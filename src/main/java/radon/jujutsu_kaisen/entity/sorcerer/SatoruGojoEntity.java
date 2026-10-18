package radon.jujutsu_kaisen.entity.sorcerer;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.SorcererUtil;

public class SatoruGojoEntity extends SorcererEntity {
   private final ItemStack blindfold = new ItemStack((ItemLike)JJKItems.BLINDFOLD.get());

   public SatoruGojoEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
      super(pType, pLevel);
   }

   @Override
   protected boolean isCustom() {
      return false;
   }

   @Override
   public float getExperience() {
      return SorcererGrade.SPECIAL_GRADE.getRequiredExperience() * 3.5F;
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes()
         .add(Attributes.ATTACK_DAMAGE)
         .add(Attributes.ARMOR, 30.0)
         .add(Attributes.ARMOR_TOUGHNESS, 20.0)
         .add(Attributes.FOLLOW_RANGE, 140.0);
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return CursedTechnique.LIMITLESS;
   }

   public float getStepHeight() {
      return 5.0F;
   }

   @NotNull
   @Override
   public List<Trait> getTraits() {
      return List.of(Trait.SIX_EYES);
   }

   @Override
   public List<Ability> getUnlocked() {
      return List.of(
         (Ability)JJKAbilities.SIMPLE_DOMAIN.get(),
         (Ability)JJKAbilities.UNLIMITED_VOID.get(),
         (Ability)JJKAbilities.ZERO_POINT_TWO_SECOND_DOMAIN_EXPANSION.get(),
         (Ability)JJKAbilities.RCT1.get(),
         (Ability)JJKAbilities.RCT2.get(),
         (Ability)JJKAbilities.RCT3.get(),
         (Ability)JJKAbilities.QUICKDASH.get()
      );
   }

   @Override
   public JujutsuType getJujutsuType() {
      return JujutsuType.SORCERER;
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      LivingEntity target = this.getTarget();
      boolean wear = false;
      if (target != null && target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         ISorcererData cap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (SorcererUtil.getGrade(cap.getExperience()).ordinal() < SorcererGrade.GRADE_1.ordinal()) {
            wear = true;
         }
      } else {
         wear = true;
      }

      this.setItemSlot(EquipmentSlot.HEAD, wear ? this.blindfold : ItemStack.EMPTY);
   }
}
