package radon.jujutsu_kaisen.ability.curse_manipulation;

import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

public class SummonAll extends Ability {
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
      if (!owner.level().isClientSide) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

         for (AbsorbedCurse curse : cap.getCurses()) {
            JJKAbilities.summonCurse(owner, curse, false);
         }
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return cap.getCurses().isEmpty() ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      List<AbsorbedCurse> curses = cap.getCurses();
      float cost = 0.0F;

      for (AbsorbedCurse curse : curses) {
         cost += JJKAbilities.getCurseCost(curse);
      }

      return cost;
   }

   @Override
   public float getRealCost(LivingEntity owner) {
      return this.getCost(owner);
   }
}
