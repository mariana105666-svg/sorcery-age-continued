package radon.jujutsu_kaisen.entity.base;

import java.util.List;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.SorcererUtil;

public interface ISorcerer {
   boolean hasMeleeAttack();

   boolean hasArms();

   boolean canJump();

   boolean canChant();

   float getExperience();

   default float getMaxEnergy() {
      return 0.0F;
   }

   default int getCursedEnergyColor() {
      return -1;
   }

   default SorcererGrade getGrade() {
      Entity entity = (Entity)this;
      if (!entity.isAddedToWorld()) {
         return SorcererUtil.getGrade(this.getExperience());
      } else {
         ISorcererData cap = (ISorcererData)entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return SorcererUtil.getGrade(cap.getExperience());
      }
   }

   @Nullable
   CursedTechnique getTechnique();

   @Nullable
   default CursedTechnique getAdditional() {
      return null;
   }

   @NotNull
   default List<Trait> getTraits() {
      return List.of();
   }

   @Nullable
   default CursedEnergyNature getNature() {
      return CursedEnergyNature.BASIC;
   }

   @NotNull
   default List<Ability> getCustom() {
      return List.of();
   }

   default List<Ability> getUnlocked() {
      return List.of();
   }

   JujutsuType getJujutsuType();

   default void init(ISorcererData data) {
      data.setExperience(this.getExperience());
      data.setTechnique(this.getTechnique());
      data.setAdditional(this.getAdditional());
      data.setNature(this.getNature());
      data.addTraits(this.getTraits());
      data.setType(this.getJujutsuType());
      data.unlockAll(this.getUnlocked());
      if (this.getMaxEnergy() > 0.0F) {
         data.setMaxEnergy(this.getMaxEnergy());
      }

      data.setEnergy(data.getMaxEnergy());
      if (this.getCursedEnergyColor() != -1) {
         data.setCursedEnergyColor(this.getCursedEnergyColor());
      }

      if (this.canChant()) {
         for (Ability ability : JJKAbilities.getAbilities((LivingEntity)this)) {
            if ((ability.isTechnique() || ability.isChantable()) && ability.isScalable((LivingEntity)this)) {
               Set<String> chants = HelperMethods.getRandomWordCombo(5);

               while (!data.isChantsAvailable(chants)) {
                  chants = HelperMethods.getRandomWordCombo(5);
               }

               data.addChants(ability, chants);
            }
         }
      }
   }
}
