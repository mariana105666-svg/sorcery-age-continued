package radon.jujutsu_kaisen.ability.mythical_beast_amber;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.CursedSpeechParticle;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class EMFBlast extends Ability {
   private static final double RANGE = 30.0;
   private static final double RADIUS = 2.5;
   private static final float DAMAGE = 20.0F;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return getEntities(owner).contains(target) && HelperMethods.RANDOM.nextInt(5) == 0 && target != null && owner.hasLineOfSight(target);
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      return JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.MYTHICAL_BEAST_AMBER.get()) && super.isValid(owner);
   }

   @Override
   public boolean isDisplayed(LivingEntity owner) {
      return JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.MYTHICAL_BEAST_AMBER.get()) && super.isDisplayed(owner);
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   private static List<Entity> getEntities(LivingEntity owner) {
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      Vec3 src = owner.getEyePosition();
      AABB bounds = AABB.ofSize(src, 1.0, 1.0, 1.0).expandTowards(look.scale(30.0)).inflate(2.5);
      return owner.level().getEntities(owner, bounds, entity -> !(entity instanceof LivingEntity living && !owner.canAttack(living)));
   }

   private void spawnParticles(Entity entity, LivingEntity owner) {
      double x = entity.getX();
      double y = entity.getY() + entity.getBbHeight() / 2.0F;
      double z = entity.getZ();

      for (int i = 0; i < 50; i++) {
         double scale = HelperMethods.RANDOM.nextDouble() * 0.95 + 0.95;
         Vec3 speed = new Vec3(HelperMethods.RANDOM.nextGaussian(), HelperMethods.RANDOM.nextGaussian(), HelperMethods.RANDOM.nextGaussian()).normalize().scale(scale);
         double offsetX = x + speed.x;
         double offsetY = y + speed.y;
         double offsetZ = z + speed.z;
         ((ServerLevel)owner.level())
            .sendParticles(
               new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), 0.2F, 2),
               offsetX,
               offsetY,
               offsetZ,
               0,
               0.0,
               0.0,
               0.0,
               0.0
            );
      }
   }

   @Override
   public void run(LivingEntity owner) {
      if (!owner.level().isClientSide) {
         Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
         Vec3 src = owner.getEyePosition();

         for (int i = 1; i < 37.0; i++) {
            Vec3 dst = src.add(look.scale(i));
            ((ServerLevel)owner.level())
               .sendParticles(
                  new CursedSpeechParticle.CursedSpeechParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), (float)(src.distanceTo(dst) * 0.5)),
                  dst.x,
                  dst.y,
                  dst.z,
                  0,
                  0.0,
                  0.0,
                  0.0,
                  1.0
               );
         }

         owner.level()
            .playSound(
               null,
               src.x,
               src.y,
               src.z,
               (SoundEvent)JJKSounds.SPARK.get(),
               SoundSource.MASTER,
               2.0F,
               0.8F + HelperMethods.RANDOM.nextFloat() * 0.2F
            );
         owner.level()
            .playSound(
               null,
               src.x,
               src.y,
               src.z,
               (SoundEvent)JJKSounds.ELECTRICITY.get(),
               SoundSource.MASTER,
               2.0F,
               0.8F + HelperMethods.RANDOM.nextFloat() * 0.2F
            );

         for (Entity entity : getEntities(owner)) {
            if (entity instanceof LivingEntity living && !JJKAbilities.hasToggled(living, (Ability)JJKAbilities.INFINITY.get())) {
               ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               cap.delayTickEvent(
                  () -> {
                     if (entity != null && entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), 20.0F * this.getPower(owner))) {
                        Vec3 center = entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0);
                        ((ServerLevel)owner.level())
                           .sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, 1.0, 0.0, 0.0, 1.0);
                        ((ServerLevel)owner.level())
                           .sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 0, 1.0, 0.0, 0.0, 1.0);
                        ((ServerLevel)owner.level())
                           .sendParticles(
                              new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), 0.2F, 3),
                              center.x,
                              center.y,
                              center.z,
                              0,
                              0.0,
                              0.0,
                              0.0,
                              0.0
                           );
                        owner.level()
                           .playSound(
                              null,
                              center.x,
                              center.y,
                              center.z,
                              SoundEvents.GENERIC_EXPLODE,
                              SoundSource.BLOCKS,
                              4.0F,
                              (1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.2F) * 0.7F
                           );
                        owner.level()
                           .playSound(
                              null,
                              center.x,
                              center.y,
                              center.z,
                              SoundEvents.LIGHTNING_BOLT_IMPACT,
                              SoundSource.BLOCKS,
                              4.0F,
                              (1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.2F) * 0.7F
                           );
                        float radius = 5.0F;
                        int minX = Mth.floor(entity.getX() - radius - 1.0);
                        int maxX = Mth.floor(entity.getX() + radius + 1.0);
                        int minY = Mth.floor(entity.getY() - radius - 1.0);
                        int maxY = Mth.floor(entity.getY() + radius + 1.0);
                        int minZ = Mth.floor(entity.getZ() - radius - 1.0);
                        int maxZ = Mth.floor(entity.getZ() + radius + 1.0);

                        for (int x = minX; x <= maxX; x++) {
                           for (int y = minY; y <= maxY; y++) {
                              for (int z = minZ; z <= maxZ; z++) {
                                 double distance = (x - entity.getX()) * (x - entity.getX())
                                    + (y - entity.getY()) * (y - entity.getY())
                                    + (z - entity.getZ()) * (z - entity.getZ());
                                 if (distance <= radius * radius) {
                                    BlockPos pos = new BlockPos(x, y, z);
                                    if (HelperMethods.isDestroyable(owner.level(), owner, pos)) {
                                       owner.level().destroyBlock(pos, false);
                                    }
                                 }
                              }
                           }
                        }

                        this.spawnParticles(entity, owner);
                        entity.hurtMarked = true;
                        JJKEffectUtil.addEffect(living, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), 30, 0, false, false, false));
                     }
                  },
                  10
               );
            }
         }
      }
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 150.0F;
   }

   @Override
   public int getCooldown() {
      return 320;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.CURSED_SPEECH;
   }
}
