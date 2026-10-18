package radon.jujutsu_kaisen.entity.sorcerer;

import java.util.List;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;

public class YujiItadoriEntity extends SorcererEntity {
   public YujiItadoriEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
      super(pType, pLevel);
   }

   @Override
   protected boolean isCustom() {
      return false;
   }

   @NotNull
   public InteractionResult mobInteract(Player pPlayer, @NotNull InteractionHand pHand) {
      ItemStack stack = pPlayer.getItemInHand(pHand);
      if (stack.is((Item)JJKItems.SUKUNA_FINGER.get())) {
         ISorcererData cap = (ISorcererData)this.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         int count = stack.getCount();
         int eaten = cap.addFingers(count);
         if (eaten > 0) {
            this.playSound(this.getEatingSound(stack), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
            stack.shrink(eaten);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
         } else {
            return InteractionResult.FAIL;
         }
      } else {
         return super.mobInteract(pPlayer, pHand);
      }
   }

   @Override
   public float getExperience() {
      return SorcererGrade.GRADE_1.getRequiredExperience();
   }

   public static Builder createAttributes() {
      return createMobAttributes()
         .add(Attributes.ATTACK_DAMAGE)
         .add(Attributes.FOLLOW_RANGE, 140.0)
         .add(Attributes.ARMOR_TOUGHNESS, 16.0)
         .add(Attributes.ARMOR, 24.0);
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return null;
   }

   @Override
   public JujutsuType getJujutsuType() {
      return JujutsuType.SORCERER;
   }

   @NotNull
   @Override
   public List<Trait> getTraits() {
      return List.of(Trait.VESSEL);
   }

   @Nullable
   @Override
   public CursedEnergyNature getNature() {
      return CursedEnergyNature.DIVERGENT;
   }

   @Override
   public void init(ISorcererData data) {
      super.init(data);
      data.setFingers(15);
   }
}
