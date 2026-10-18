package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.effect.DisasterPlantEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class DisasterPlant extends Ability {
   public static final double RANGE = 32.0;
   public LivingEntity enemy = null;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return HelperMethods.RANDOM.nextInt(3) == 0 && target != null && this.getTarget(owner) == target;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Nullable
   private LivingEntity getTarget(LivingEntity owner) {
      LivingEntity target = RotationUtil.getExpandedLookAt(owner, 32.0);
      if (target != null) {
         return !owner.canAttack(target) ? null : target;
      } else {
         return null;
      }
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      LivingEntity target = this.enemy;
      DisasterPlantEntity plant = new DisasterPlantEntity(owner, this.getPower(owner), target);
      owner.level().addFreshEntity(plant);
      this.enemy = null;
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      LivingEntity target = this.getTarget(owner);
      this.enemy = target;
      return target == null ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public int getCooldown() {
      return 400;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 200.0F;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.PLANTS;
   }
}
