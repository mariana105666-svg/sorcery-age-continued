package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.effect.CursedEnergyBlastEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class CursedEnergyBlast extends Ability {
   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target == null) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getTechnique() == null
            && HelperMethods.RANDOM.nextInt(5) == 0
            && owner.hasLineOfSight(target)
            && owner.distanceTo(target) <= CursedEnergyBlastEntity.RANGE / 2.0F;
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

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      CursedEnergyBlastEntity blast = new CursedEnergyBlastEntity(owner, this.getPower(owner));
      owner.level().addFreshEntity(blast);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 125.0F;
   }

   @Override
   public int getCooldown() {
      return 300;
   }

   @Override
   public int getPointsCost() {
      return (Integer)ConfigHolder.SERVER.ceBlastCost.get();
   }

   @Override
   public boolean canUnlock(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return cap.getType() == JujutsuType.CURSE && cap.hasTrait(Trait.CURSED_WOMB) && super.canUnlock(owner);
   }

   @Nullable
   @Override
   public Ability getParent(LivingEntity owner) {
      return (Ability)JJKAbilities.CURSED_ENERGY_FLOW.get();
   }

   @Override
   public Vec2 getDisplayCoordinates() {
      return new Vec2(3.0F, 3.0F);
   }

   @Override
   public boolean isDisplayed(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return cap.getType() == JujutsuType.CURSE && cap.hasTrait(Trait.CURSED_WOMB) && cap.checkWombAwakened() && super.isDisplayed(owner);
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return cap.getType() == JujutsuType.CURSE && cap.hasTrait(Trait.CURSED_WOMB) && cap.checkWombAwakened() && super.isValid(owner);
   }
}
