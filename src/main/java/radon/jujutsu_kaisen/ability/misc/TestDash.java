package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.MirageParticle;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class TestDash extends Ability {
   public static final double RANGE = 80.0;
   private static final float DASH = 2.0F;
   private static final float MAX_DASH = 3.0F;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target == null ? false : owner.hasLineOfSight(target) && owner.distanceTo(target) <= getRange(owner);
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      return !canDash(owner) ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   private static boolean canDash(LivingEntity owner) {
      if (owner.hasEffect((MobEffect)JJKEffects.STUN.get())) {
         return false;
      } else {
         boolean collision = false;
         AABB bounds = owner.getBoundingBox();
         Cursor3D cursor = new Cursor3D(
            Mth.floor(bounds.minX - 1.0E-7) - 2,
            Mth.floor(bounds.minY - 1.0E-7) - 2,
            Mth.floor(bounds.minZ - 1.0E-7) - 2,
            Mth.floor(bounds.maxX + 1.0E-7) + 2,
            Mth.floor(bounds.maxY + 1.0E-7) + 2,
            Mth.floor(bounds.maxZ + 1.0E-7) + 2
         );

         while (cursor.advance()) {
            int i = cursor.nextX();
            int j = cursor.nextY();
            int k = cursor.nextZ();
            int l = cursor.getNextType();
            if (l != 3) {
               BlockState state = owner.level().getBlockState(new BlockPos(i, j, k));
               if (!state.isAir()) {
                  collision = true;
                  break;
               }
            }
         }

         return collision || owner.getXRot() >= 15.0F;
      }
   }

   private static float getRange(LivingEntity owner) {
      return (float)(80.0 * (JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION) ? 1.5F : 1.0F));
   }

   private Vec3 getTarget(LivingEntity owner) {
      Vec3 start = owner.getEyePosition();
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      if (owner instanceof Player) {
         Minecraft instance = Minecraft.getInstance();
         look = instance.player.getLookAngle().multiply(80.0, 0.0, 80.0).normalize();
         Vec3 forwards = new Vec3(look.x, 0.1, look.z);
         Vec3 backwards = new Vec3(-look.x, 0.1, -look.z);
         Vec3 left = new Vec3(look.z, 0.1, -look.x);
         Vec3 right = new Vec3(-look.z, 0.1, look.x);
         Vec3 forwardsLeft = forwards.add(left).scale(0.5);
         Vec3 forwardsRight = forwards.add(right).scale(0.5);
         Vec3 backwardsLeft = backwards.add(left).scale(0.5);
         Vec3 backwardsRight = backwards.add(right).scale(0.5);
         look = forwards;
         if (instance.player.input.leftImpulse > 0.0F && instance.player.input.forwardImpulse > 0.0F) {
            look = forwardsLeft;
         } else if (instance.player.input.leftImpulse < 0.0F && instance.player.input.forwardImpulse > 0.0F) {
            look = forwardsRight;
         } else if (instance.player.input.leftImpulse > 0.0F && instance.player.input.forwardImpulse < 0.0F) {
            look = backwardsLeft;
         } else if (instance.player.input.leftImpulse < 0.0F && instance.player.input.forwardImpulse < 0.0F) {
            look = backwardsRight;
         } else if (instance.player.input.leftImpulse > 0.0F) {
            look = left;
         } else if (instance.player.input.leftImpulse < 0.0F) {
            look = right;
         } else if (instance.player.input.forwardImpulse < 0.0F) {
            look = backwards;
         }
      }

      return look;
   }

   @Override
   public void run(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         if (canDash(owner)) {
            ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if (cap.getSpeedStacks() > 0 || cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
               owner.level()
                  .playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.DASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
               JJKEffectUtil.addEffect(owner, new MobEffectInstance((MobEffect)JJKEffects.INVISIBILITY.get(), 5, 0, false, false, false));
               level.sendParticles(
                  new MirageParticle.MirageParticleOptions(owner.getId()), owner.getX(), owner.getY(), owner.getZ(), 0, 0.0, 0.0, 0.0, 1.0
               );
            }

            HitResult hit = RotationUtil.getLookAtHit(owner, getRange(owner));
            float power = Math.min(3.0F, 2.0F * (1.0F + this.getPower(owner) * 0.1F));
            Vec3 target = this.getTarget(owner);
            Vec3 velocity = target.normalize().scale(power);
            Vec3 look = target;
            velocity = velocity.multiply(new Vec3(0.7, 1.0, 0.7));
            if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
               velocity = velocity.multiply(new Vec3(1.2, 1.0, 1.2)).add(new Vec3(0.0, 0.05, 0.0));
            }

            velocity = velocity.add(new Vec3(0.0, 0.2, 0.0));
            if (!owner.onGround() && owner.level().getBlockState(owner.blockPosition()).getFluidState().isEmpty()) {
               velocity = velocity.add(new Vec3(0.0, -0.75, 0.0));
            }

            owner.setDeltaMovement(velocity);
            owner.hurtMarked = true;
            Vec3 pos = owner.position();

            for (int i = 0; i < 32; i++) {
               double xPos = owner.getX()
                  + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.25F)
                  - owner.getLookAngle().scale(0.35).x;
               double yPos = owner.getY() + HelperMethods.RANDOM.nextDouble() * owner.getBbHeight();
               double zPos = owner.getZ()
                  + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.25F)
                  - owner.getLookAngle().scale(0.35).z;
               double theta = HelperMethods.RANDOM.nextDouble() * 2.0 * Math.PI;
               double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
               double r = HelperMethods.RANDOM.nextDouble() * 0.2;
               double x = r * Math.sin(phi) * Math.cos(theta);
               double y = r * Math.sin(phi) * Math.sin(theta);
               double z = r * Math.cos(phi);
               Vec3 speed = look.add(x, y, z).reverse();
               level.sendParticles(ParticleTypes.CLOUD, xPos, yPos, zPos, 0, speed.x, speed.y, speed.z, 1.0);
            }
         }
      }
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      return !(owner instanceof ISorcerer sorcerer && !sorcerer.canJump()) && super.isValid(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.0F;
   }

   @Override
   public int getCooldown() {
      return 10;
   }

   @Override
   public int getRealCooldown(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return cap.hasTrait(Trait.HEAVENLY_RESTRICTION) ? 4 : super.getRealCooldown(owner);
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.NONE;
   }
}
