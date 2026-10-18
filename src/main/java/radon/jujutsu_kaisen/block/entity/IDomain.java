package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public interface IDomain {
   void setInstant(boolean var1);

   boolean isInstant();

   LivingEntity getOwner();

   default float getStrength() {
      LivingEntity owner = this.getOwner();
      if (owner == null) {
         return 0.0F;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getAbilityPower() * (owner.getHealth() / owner.getMaxHealth());
      }
   }

   AABB getBounds();

   boolean isBarrier(BlockPos var1);

   boolean isInsideBarrier(BlockPos var1);

   boolean isBarrierOrInside(BlockPos var1);

   void doSureHitEffect();

   Ability getAbility();

   DomainExpansionEntity getDomain();
}
