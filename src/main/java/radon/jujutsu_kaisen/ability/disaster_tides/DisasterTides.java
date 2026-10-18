package radon.jujutsu_kaisen.ability.disaster_tides;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.effect.WaterballEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class DisasterTides extends Ability {
   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return HelperMethods.RANDOM.nextInt(5) == 0 && target != null && owner.distanceTo(target) <= 10.0 && owner.hasLineOfSight(target);
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      WaterballEntity waterball = new WaterballEntity(owner, this.getPower(owner));
      owner.level().addFreshEntity(waterball);
   }

   @Override
   public int getCooldown() {
      return 600;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 250.0F;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.WATER;
   }
}
