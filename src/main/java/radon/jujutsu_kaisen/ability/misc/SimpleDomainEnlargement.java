package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;

public class SimpleDomainEnlargement extends Ability implements Ability.IChannelened {
   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   @Override
   public void run(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      SimpleDomainEntity domain = cap.getSummonByClass(SimpleDomainEntity.class);
      if (domain != null) {
         domain.enlarge();
      }
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      if (!JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.SIMPLE_DOMAIN.get())) {
         return false;
      } else {
         if (!owner.level().isClientSide) {
            ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            SimpleDomainEntity domain = cap.getSummonByClass(SimpleDomainEntity.class);
            if (domain == null) {
               return false;
            }

            if (!domain.canEnlarge()) {
               return false;
            }
         }

         return super.isValid(owner);
      }
   }

   @Override
   public int getCooldown() {
      return 100;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 4.0F;
   }

   @Nullable
   @Override
   public Ability getParent(LivingEntity owner) {
      return (Ability)JJKAbilities.SIMPLE_DOMAIN.get();
   }

   @Override
   public int getPointsCost() {
      return (Integer)ConfigHolder.SERVER.simpleDomainEnlargementCost.get();
   }

   @Override
   public Vec2 getDisplayCoordinates() {
      return new Vec2(4.0F, 4.0F);
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public boolean isDisplayed(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      CursedTechnique technique = cap.getTechnique();
      CursedEnergyNature nature = cap.getNature();
      return ((Boolean)ConfigHolder.SERVER.newShadowStyleForAll.get() || technique == CursedTechnique.TECHNIQUELESS) && super.isDisplayed(owner);
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.DOMAIN;
   }
}
