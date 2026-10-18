package radon.jujutsu_kaisen.ability.base;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.util.RotationUtil;

public interface IRMBAttack extends IRMBAble {
   double MELEE_RANGE = 5.0;

   void perform(LivingEntity var1, LivingEntity var2);

   default double getRange() {
      return 5.0;
   }

   default boolean canUse(LivingEntity owner) {
      return !owner.hasEffect((MobEffect)JJKEffects.STAGGER.get());
   }

   default LivingEntity hitEntity(LivingEntity owner) {
      return RotationUtil.getLookAtHit(owner, this.getRange()) instanceof EntityHitResult ehr && ehr.getEntity() instanceof LivingEntity target ? target : null;
   }

   @Override
   default boolean onRightClick(LivingEntity owner) {
      if (!this.canUse(owner)) {
         return false;
      } else {
         LivingEntity target = this.hitEntity(owner);
         if (target == null) {
            return false;
         } else if (!owner.canAttack(target)) {
            return false;
         } else {
            owner.swing(InteractionHand.OFF_HAND);
            if (!owner.level().isClientSide) {
               this.perform(owner, target);
            }

            return true;
         }
      }
   }
}
