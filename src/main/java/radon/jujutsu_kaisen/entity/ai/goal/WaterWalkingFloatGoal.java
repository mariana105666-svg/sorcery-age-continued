package radon.jujutsu_kaisen.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;

public class WaterWalkingFloatGoal extends Goal {
   private final PathfinderMob mob;

   public WaterWalkingFloatGoal(PathfinderMob pMob) {
      this.mob = pMob;
      this.setFlags(EnumSet.of(Flag.JUMP));
      pMob.getNavigation().setCanFloat(true);
   }

   public boolean canUse() {
      return true;
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      Ability ability = (Ability)JJKAbilities.CURSED_ENERGY_FLOW.get();
      boolean success = ability.shouldTrigger(this.mob, this.mob.getTarget());
      LivingEntity target = this.mob.getTarget();
      if (target != null && target.isAlive() && target.isInWater() && target.getY() < this.mob.getY() - 0.5) {
         this.mob.getNavigation().setCanFloat(false);
      } else {
         this.mob.getNavigation().setCanFloat(true);
         if ((!this.mob.isInWater() || !(this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getFluidJumpThreshold()))
            && !this.mob.isInLava()
            && !this.mob.isInFluidType((fluidType, height) -> this.mob.canSwimInFluidType(fluidType) && height > this.mob.getFluidJumpThreshold())) {
            if (success) {
               if (!JJKAbilities.hasToggled(this.mob, ability)) {
                  AbilityHandler.trigger(this.mob, ability);
               }
            } else if (JJKAbilities.hasToggled(this.mob, ability)) {
               AbilityHandler.trigger(this.mob, ability);
            }
         } else if (this.mob.getRandom().nextFloat() < 0.8F) {
            this.mob.getJumpControl().jump();
         }
      }
   }
}
