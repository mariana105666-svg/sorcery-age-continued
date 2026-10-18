package radon.jujutsu_kaisen.ability.ratio;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.EmittingLightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Collapse extends Ability implements Ability.IChannelened, Ability.IDurationable {
   private static final int RANGE = 10;
   private static final int DELAY = 20;
   private static final float DAMAGE = 10.0F;
   private static final int DURATION = 25;
   private static final float RADIUS = 3.0F;
   private static final float EXPLOSIVE_POWER = 3.0F;
   private static final float MAX_EXPLOSIVE_POWER = 10.0F;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target == null || !owner.hasLineOfSight(target)) {
         return false;
      } else {
         return JJKAbilities.isChanneling(owner, this) ? HelperMethods.RANDOM.nextInt(5) != 0 : HelperMethods.RANDOM.nextInt(3) == 0;
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   @Override
   public void run(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         ISorcererData var16 = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         Vec3 pos = owner.position();
         int index = this.getCharge(owner);
         float scale = 1.5F;

         for (int player = 0; player < 12.0F * scale; player++) {
            double x = owner.getX()
               + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.5F * scale)
               - owner.getLookAngle().scale(0.35).x;
            double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * owner.getBbHeight();
            double z = owner.getZ()
               + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.5F * scale)
               - owner.getLookAngle().scale(0.35).z;
            double speed = owner.getBbHeight() * 0.3F * HelperMethods.RANDOM.nextDouble();
            level.sendParticles(
               new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.FALLING_BLOSSOM_EMOTION, owner.getBbWidth() * 0.5F, 0.2F, 16),
               x,
               y,
               z,
               0,
               0.0,
               speed,
               0.0,
               1.0
            );
         }

         if (index == 12 && owner instanceof ServerPlayer player) {
            owner.level()
               .playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.COLLAPSE.get(), SoundSource.MASTER, 1.0F, 1.0F);
         }
      }
   }

   @Override
   public void onStop(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         Vec3 var27 = owner.getEyePosition();
         Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
         Vec3 end = var27.add(look.scale(10.0));
         HitResult result = RotationUtil.getHitResult(owner, var27, end);
         if (result.getType() == Type.BLOCK) {
            int index = this.getCharge(owner);
            owner.swing(InteractionHand.MAIN_HAND);
            Vec3 center = owner.position().add(0.0, owner.getBbHeight() / 2.0F, 0.0);

            for (int i = 0; i < 12; i++) {
               double theta = HelperMethods.RANDOM.nextDouble() * Math.PI * 2.0;
               double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
               Vec3 direction = new Vec3(Math.sin(phi) * Math.cos(theta), Math.sin(phi) * Math.sin(theta), Math.cos(phi));
               Vec3 offset = center.add(direction.multiply(owner.getBbWidth() / 2.0F, owner.getBbHeight() / 2.0F, owner.getBbWidth() / 2.0F));
               level.sendParticles(
                  new EmittingLightningParticle.EmittingLightningParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), direction, 6.0F, 1, true),
                  offset.x,
                  offset.y,
                  offset.z,
                  0,
                  0.0,
                  0.0,
                  0.0,
                  0.0
               );
            }

            Vec3 realpos = result.getLocation();
            if (index >= 17 && index < 25) {
               ExplosionHandler.spawn(
                  owner.level().dimension(),
                  realpos,
                  Math.min(11.0F, 3.0F * this.getPower(owner) * 1.1F),
                  20,
                  10.0F + this.getPower(owner) * 0.1F,
                  owner,
                  JJKDamageSources.indirectJujutsuAttack(owner, owner, (Ability)JJKAbilities.COLLAPSE.get()),
                  false
               );
               BlockHitResult hit = this.getBlockHit(owner, 10.0);
               BlockPos blocked = hit.getBlockPos();
               AABB bounds = new AABB(
                  blocked.getX() - 1.0,
                  blocked.getY() - 1.0,
                  blocked.getZ() - 1.0,
                  blocked.getX() + 1.0,
                  blocked.getY() + 1.0,
                  blocked.getZ() + 1.0
               );
               double centerX = bounds.getCenter().x;
               double centerY = bounds.getCenter().y;
               double centerZ = bounds.getCenter().z;

               for (int x = (int)bounds.minX; x <= bounds.maxX; x++) {
                  for (int y = (int)bounds.minY; y <= bounds.maxY; y++) {
                     for (int z = (int)bounds.minZ; z <= bounds.maxZ; z++) {
                        BlockPos blocker = new BlockPos(x, y, z);
                        BlockState state = owner.level().getBlockState(blocker);
                        double distance = Math.sqrt(Math.pow(x - centerX, 2.0) + Math.pow(y - centerY, 2.0) + Math.pow(z - centerZ, 2.0));
                        if (distance <= 3.0 && HelperMethods.isDestroyable(owner.level(), owner, blocker) && owner.level().destroyBlock(blocker, false)) {
                           FallingBlockEntity entity = FallingBlockEntity.fall(owner.level(), blocker, state);
                           entity.noPhysics = true;
                        }
                     }
                  }
               }
            } else if (index < 25 || index >= 25) {
               ExplosionHandler.spawn(
                  owner.level().dimension(),
                  realpos,
                  Math.min(7.5F, 3.0F * this.getPower(owner) * 0.75F),
                  20,
                  10.0F + this.getPower(owner) * 0.1F,
                  owner,
                  JJKDamageSources.indirectJujutsuAttack(owner, owner, (Ability)JJKAbilities.COLLAPSE.get()),
                  false
               );
            }
         }
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      return super.isTriggerable(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 13.0F;
   }

   @Override
   public int getCooldown() {
      return 280;
   }

   @Override
   public int getDuration() {
      return 5;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.SLASHING;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
