package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import radon.jujutsu_kaisen.util.EntityUtil;

public class Switch extends Ability {
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
         return cap.getType() != JujutsuType.CURSE && !cap.isUnlocked((Ability)JJKAbilities.RCT1.get())
            ? owner.getHealth() / owner.getMaxHealth() < 0.3F
            : owner.getHealth() / owner.getMaxHealth() < 0.8F;
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
      if (!owner.level().isClientSide) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         SukunaEntity sukuna;
         if ((sukuna = cap.getSummonByClass(SukunaEntity.class)) != null) {
            cap.removeSummon(sukuna);
            sukuna.discard();
         } else {
            sukuna = new SukunaEntity(owner, cap.getFingers(), true);
            EntityUtil.convertTo(owner, sukuna, true, false);
            cap.addSummon(sukuna);
         }
      }
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return cap.hasTrait(Trait.VESSEL) && cap.getFingers() > 0;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.0F;
   }

   @Override
   public int getCooldown() {
      return 600;
   }
}
