package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class SoulRestoration extends Ability {
   public static final float RANGE = 5.0F;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Nullable
   private LivingEntity getTarget(LivingEntity owner) {
      if (!(RotationUtil.getLookAtHit(owner, 5.0) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target)) {
         return null;
      } else {
         return !owner.canAttack(target) ? null : target;
      }
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      if (owner.level() instanceof ServerLevel level) {
         LivingEntity target = this.getTarget(owner);
         if (target != null) {
            target.setHealth(target.getMaxHealth());
            int count = 8 + (int)(target.getBbWidth() * target.getBbHeight()) * 16;

            for (int i = 0; i < count; i++) {
               double x = target.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5) * (target.getBbWidth() * 2.0F) - target.getLookAngle().scale(0.35).x;
               double y = target.getY() + HelperMethods.RANDOM.nextDouble() * target.getBbHeight();
               double z = target.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5) * (target.getBbWidth() * 2.0F) - target.getLookAngle().scale(0.35).z;
               level.sendParticles(ParticleTypes.SOUL, x, y, z, 0, 0.0, HelperMethods.RANDOM.nextDouble() * 0.1, 0.0, 1.0);
            }
         }
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      LivingEntity target = this.getTarget(owner);
      return target != null && target.isAlive() ? -(target.getMaxHealth() - target.getHealth()) * 2.0F : 0.0F;
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      LivingEntity target = this.getTarget(owner);
      return target == null ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
