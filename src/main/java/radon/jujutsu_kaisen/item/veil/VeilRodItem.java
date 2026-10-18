package radon.jujutsu_kaisen.item.veil;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.ModifierUtils;

public class VeilRodItem extends BlockItem {
   public VeilRodItem(Block pBlock, Properties pProperties) {
      super(pBlock, pProperties);
   }

   public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltip, @NotNull TooltipFlag pFlag) {
      super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
      List<Component> modifiers = new ArrayList<>();
      modifiers.add(CommonComponents.EMPTY);
      modifiers.add(Component.translatable(String.format("item.%s.veil_rod_item.modifiers", "jujutsu_kaisen")).withStyle(ChatFormatting.DARK_BLUE));
      boolean success = false;

      for (Modifier modifier : getModifiers(pStack)) {
         if (modifier.getType() != Modifier.Type.NONE) {
            modifiers.add(CommonComponents.space().append(modifier.getComponent()));
            success = true;
         }
      }

      if (success) {
         pTooltip.addAll(modifiers);
      }
   }

   public static List<Modifier> getModifiers(ItemStack stack) {
      CompoundTag nbt = stack.getOrCreateTag();
      return ModifierUtils.getModifiers(nbt.getCompound("BlockEntityTag"));
   }

   public static void setModifier(ItemStack stack, int index, Modifier modifier) {
      CompoundTag nbt = stack.getOrCreateTag();
      CompoundTag tag = nbt.getCompound("BlockEntityTag");
      ModifierUtils.setModifier(tag, index, modifier);
      nbt.put("BlockEntityTag", tag);
   }

   protected boolean updateCustomBlockEntityTag(@NotNull BlockPos pPos, @NotNull Level pLevel, @Nullable Player pPlayer, @NotNull ItemStack pStack, @NotNull BlockState pState) {
      if (pPlayer != null && pLevel.getBlockEntity(pPos) instanceof VeilRodBlockEntity be) {
         be.setOwner(pPlayer.getUUID());
         ISorcererData cap = (ISorcererData)pPlayer.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         be.setExperience(cap.getExperience());
      }

      return super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
   }
}
