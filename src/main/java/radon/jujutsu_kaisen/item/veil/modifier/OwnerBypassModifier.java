package radon.jujutsu_kaisen.item.veil.modifier;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class OwnerBypassModifier extends Modifier {
   public OwnerBypassModifier(Modifier.Action action) {
      super(Modifier.Type.OWNER_BYPASS, action);
   }

   public OwnerBypassModifier(CompoundTag nbt) {
      super(nbt);
   }

   @Override
   public Component getComponent() {
      return Component.translatable(String.format("item.%s.veil_rod.owner_bypass.%s", "jujutsu_kaisen", this.getAction().name().toLowerCase()))
         .withStyle(this.getAction() == Modifier.Action.DENY ? ChatFormatting.DARK_RED : ChatFormatting.DARK_GREEN);
   }
}
