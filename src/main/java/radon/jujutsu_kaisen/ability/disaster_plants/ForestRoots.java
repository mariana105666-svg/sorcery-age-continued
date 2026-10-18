package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.chant.ChantHandler;
import radon.jujutsu_kaisen.entity.effect.ForestRootsEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class ForestRoots extends Ability {
   public static final double RANGE = 22.0;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return HelperMethods.RANDOM.nextInt(5) == 0 && target != null && owner.hasLineOfSight(target);
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      float output = ChantHandler.getOutput(owner, this) * 1.2F;

      for (Entity entity : owner.level().getEntities(owner, AABB.ofSize(owner.position(), 22.0 * output, 22.0 * output, 22.0 * output))) {
         if (entity instanceof LivingEntity living && owner.canAttack(living) && entity.onGround()) {
            owner.level().addFreshEntity(new ForestRootsEntity(owner, this.getPower(owner), living, output));
         }
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 250.0F;
   }

   @Override
   public int getCooldown() {
      return 400;
   }
}
