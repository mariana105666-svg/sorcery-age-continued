package radon.jujutsu_kaisen.ability.mimicry;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class CommandPureLove extends Ability {
   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return target != null
         && !target.isDeadOrDying()
         && (
            cap.getType() == JujutsuType.CURSE || HelperMethods.RANDOM.nextInt(10) == 0 && cap.isUnlocked((Ability)JJKAbilities.RCT1.get())
               ? owner.getHealth() / owner.getMaxHealth() < 0.9F
               : owner.getHealth() / owner.getMaxHealth() < 0.4F
         );
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      if (!owner.level().isClientSide) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         RikaEntity rika = cap.getSummonByClass(RikaEntity.class);
         if (rika != null) {
            if (((Ability)JJKAbilities.SHOOT_PURE_LOVE.get()).getStatus(rika) == Ability.Status.SUCCESS) {
               rika.setOpen(153);
            }
         }
      }
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      return JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.RIKA.get()) && super.isValid(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.0F;
   }
}
