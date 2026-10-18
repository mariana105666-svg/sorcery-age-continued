package radon.jujutsu_kaisen.ability.boogie_woogie;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.projectile.CursedEnergyImbuedItemProjectile;

public class CEThrow extends Ability {
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

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      if (!owner.level().isClientSide) {
         ItemStack stack = owner.getItemInHand(InteractionHand.MAIN_HAND);
         CursedEnergyImbuedItemProjectile item = new CursedEnergyImbuedItemProjectile(owner, stack.copyWithCount(1));
         owner.level().addFreshEntity(item);
         stack.shrink(1);
         SwapOthers.setTarget(owner, item);
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      return owner.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 10.0F;
   }
}
