package radon.jujutsu_kaisen.item.base;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;

public abstract class CursedToolItem extends SwordItem {
   public CursedToolItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
      super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
   }

   public abstract SorcererGrade getGrade();

   @NotNull
   public Component getName(@NotNull ItemStack pStack) {
      MutableComponent name = super.getName(pStack).copy();
      return name.withStyle(ChatFormatting.DARK_PURPLE);
   }

   public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
      pTooltipComponents.add(Component.translatable(String.format("%s.desc", this.getDescriptionId())));
      pTooltipComponents.add(
         Component.translatable(
            String.format("item.%s.grade", "jujutsu_kaisen"), new Object[]{this.getGrade().getName().copy().withStyle(ChatFormatting.DARK_RED)}
         )
      );
   }
}
