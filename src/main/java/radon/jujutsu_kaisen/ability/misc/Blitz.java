package radon.jujutsu_kaisen.ability.misc;

import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
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

public class Blitz extends Ability {
   private static final double RANGE = 40.0;
   public static int DURATION = 10;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target != null && !target.isDeadOrDying()) {
         return owner.hasLineOfSight(target) && !(owner.distanceTo(target) > 40.0) ? HelperMethods.RANDOM.nextInt(6) == 0 : false;
      } else {
         return false;
      }
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   private Vec3 getTarget(LivingEntity owner) {
      Vec3 start = owner.getEyePosition();
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      Vec3 end = start.add(look.scale(40.0));
      HitResult result = RotationUtil.getHitResult(owner, start, end);
      return result.getType() == Type.MISS ? end : result.getLocation();
   }

   @Override
   public void run(LivingEntity owner) {
      Vec3 look2 = RotationUtil.getTargetAdjustedLookAngle(owner).scale(5.0);
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      Vec3 target = this.getTarget(owner);
      Vec3 ogPos = owner.position();
      Vec3 oldV = owner.getDeltaMovement();
      owner.moveTo(target);
      owner.setDeltaMovement(oldV);
      if (owner.level() instanceof ServerLevel level) {
         JJKEffectUtil.addEffect(owner, new MobEffectInstance((MobEffect)JJKEffects.INVISIBILITY.get(), 5, 0, false, false, false));
         level.sendParticles(new MirageParticle.MirageParticleOptions(owner.getId()), ogPos.x, ogPos.y, ogPos.z, 0, 0.0, 0.0, 0.0, 1.0);
         double var22 = ogPos.distanceTo(target);
         owner.level().playSound(null, target.x, target.y, target.z, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.MASTER, 0.8F, 1.0F);
         owner.level().playSound(null, target.x, target.y, target.z, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.MASTER, 2.0F, 0.7F);
         Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
         level.sendParticles(ParticleTypes.FLASH, ogPos.x, ogPos.y + 1.0, ogPos.z, 0, 0.0, 0.0, 0.0, 1.0);
         level.sendParticles(ParticleTypes.SONIC_BOOM, ogPos.x, ogPos.y + 1.0, ogPos.z, 0, 0.0, 0.0, 0.0, 1.0);
         Vec3 pos = owner.getEyePosition().add(look);
         owner.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_SMALL_FALL, SoundSource.MASTER, 1.0F, 1.0F);
         owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.DASH.get(), SoundSource.MASTER, 1.0F, 2.0F);
         ArrayList<String> targets = new ArrayList<>();

         for (double i = 0.0; i < var22; i += 0.2) {
            Vec3 cPos = ogPos.lerp(target, 1.0 / var22 * i).add(0.0, 1.0, 0.0);
            level.sendParticles(
               ParticleTypes.ELECTRIC_SPARK, cPos.x, cPos.y, cPos.z, 0, look2.x / 4.0, look2.y / 4.0, look2.z / 4.0, 0.0
            );

            for (LivingEntity entity : owner.level().getEntitiesOfClass(LivingEntity.class, AABB.ofSize(cPos, 8.0, 8.0, 8.0), entityx -> entityx != owner)) {
               boolean found = false;

               for (String enemy : targets) {
                  if (Objects.equals(enemy, entity.getStringUUID())) {
                     found = true;
                  }
               }

               if (!found) {
                  targets.add(entity.getStringUUID());
                  Vec3 center = entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0);
                  level.sendParticles(ParticleTypes.FLASH, center.x, center.y, center.z, 0, 1.0, 0.0, 0.0, 1.0);
                  entity.level().playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);
                  if (owner instanceof Player player) {
                     player.attack(entity);
                  } else {
                     owner.doHurtTarget(entity);
                  }

                  entity.invulnerableTime = 0;
                  float newDMG = 8.0F;
                  if (!(owner instanceof Player player)) {
                     newDMG /= 1.65F;
                  }

                  if (entity.hurt(
                     owner instanceof Player player ? owner.damageSources().playerAttack(player) : owner.damageSources().mobAttack(owner),
                     newDMG * 1.45F * this.getPower(owner)
                  )) {
                     entity.setDeltaMovement(look.scale(1.0F * (1.0F + this.getPower(owner) * 0.1F) * 2.0F).multiply(1.0, 0.25, 1.0));
                  }
               }
            }
         }

         for (int i = 0; i < 4; i++) {
            cap.delayTickEvent(
               () -> {
                  for (int a = 0; a < 5; a++) {
                     double xPos = owner.getX()
                        + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.25F)
                        - owner.getLookAngle().scale(0.35).x;
                     double yPos = owner.getY() + HelperMethods.RANDOM.nextDouble() * owner.getBbHeight();
                     double zPos = owner.getZ()
                        + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.25F)
                        - owner.getLookAngle().scale(0.35).z;
                     level.sendParticles(ParticleTypes.ELECTRIC_SPARK, xPos, yPos, zPos, 0, look2.x / 4.0, look2.y / 4.0, look2.z / 4.0, 0.0);
                  }
               },
               i
            );
         }
      }
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return !(owner instanceof ISorcerer sorcerer && (!sorcerer.hasMeleeAttack() || !sorcerer.hasArms()))
         && cap.hasTrait(Trait.HEAVENLY_RESTRICTION)
         && super.isValid(owner);
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      return owner.isUsingItem() ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION) ? 0.0F : 15.0F;
   }

   @Override
   public int getCooldown() {
      return 560;
   }

   @Override
   public boolean isMelee() {
      return true;
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
