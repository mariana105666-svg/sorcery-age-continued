package radon.jujutsu_kaisen.entity.sorcerer;

import java.util.List;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
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
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;

public class YutaOkkotsuEntity extends SorcererEntity {
   public YutaOkkotsuEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
      super(pType, pLevel);
   }

   @Override
   protected boolean isCustom() {
      return false;
   }

   @Override
   public float getExperience() {
      return SorcererGrade.SPECIAL_GRADE.getRequiredExperience() * 2.0F;
   }

   public static Builder createAttributes() {
      return createMobAttributes()
         .add(Attributes.ATTACK_DAMAGE)
         .add(Attributes.FOLLOW_RANGE, 140.0)
         .add(Attributes.ARMOR_TOUGHNESS, 15.0)
         .add(Attributes.ARMOR, 20.0);
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return CursedTechnique.MIMICRY;
   }

   @NotNull
   @Override
   public List<Ability> getCustom() {
      return List.of((Ability)JJKAbilities.OUTPUT_RCT.get());
   }

   @NotNull
   @Override
   public List<Trait> getTraits() {
      return List.of(Trait.RCT_OUTPUT);
   }

   @Override
   public List<Ability> getUnlocked() {
      return List.of(
         (Ability)JJKAbilities.SIMPLE_DOMAIN.get(),
         (Ability)JJKAbilities.RCT1.get(),
         (Ability)JJKAbilities.RCT2.get(),
         (Ability)JJKAbilities.RCT3.get(),
         (Ability)JJKAbilities.OUTPUT_RCT.get(),
         (Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get()
      );
   }

   @Override
   public JujutsuType getJujutsuType() {
      return JujutsuType.SORCERER;
   }

   @Override
   public void init(ISorcererData data) {
      super.init(data);
      data.copy(CursedTechnique.CURSED_SPEECH);
      data.setAdditionalEnergy(data.getMaxEnergy() * 0.5F);
   }

   @Override
   public void onAddedToWorld() {
      super.onAddedToWorld();
      this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack((ItemLike)JJKItems.GREEN_HANDLE_KATANA.get()));
   }
}
