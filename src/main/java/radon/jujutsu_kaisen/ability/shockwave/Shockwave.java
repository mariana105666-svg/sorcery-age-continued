package radon.jujutsu_kaisen.ability.shockwave;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Shockwave extends Ability {
   private static final double RADIUS = 15.0;
   private static final float DAMAGE = 14.0F;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return true;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target != null && !target.isDeadOrDying() && owner.distanceTo(target) <= 15.0;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);
         ISorcererData var6 = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         int current = 1;

         while (current < 15.0) {
            current++;
            int radius = current;
            var6.delayTickEvent(
               () -> {
                  for (double phi = 0.0; phi < Math.PI * 2; phi += 0.5) {
                     double x = owner.getX() + radius * Math.cos(phi);
                     double y = owner.getY();
                     double z = owner.getZ() + radius * Math.sin(phi);
                     level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 0, 0.0, HelperMethods.RANDOM.nextDouble(), 0.0, 1.0);
                  }

                  for (Entity entity : owner.level().getEntities(owner, AABB.ofSize(owner.position(), radius, radius, radius))) {
                     if (entity instanceof LivingEntity living
                        && owner.canAttack(living)
                        && !(entity.distanceTo(owner) > 15.0)
                        && entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), 14.0F * this.getPower(owner))) {
                        entity.setDeltaMovement(entity.position().subtract(owner.position()));
                     }
                  }
               },
               current
            );
         }
      }
   }

   @Override
   public int getCooldown() {
      return 100;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 50.0F;
   }
}
