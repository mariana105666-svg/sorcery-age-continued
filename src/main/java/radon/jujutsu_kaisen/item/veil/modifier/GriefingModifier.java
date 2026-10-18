package radon.jujutsu_kaisen.item.veil.modifier;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class GriefingModifier extends Modifier {
   public GriefingModifier(Modifier.Action action) {
      super(Modifier.Type.GRIEFING, action);
   }

   public GriefingModifier(CompoundTag nbt) {
      super(nbt);
   }

   @Override
   public Component getComponent() {
      return Component.translatable(String.format("item.%s.veil_rod.griefing.%s", "jujutsu_kaisen", this.getAction().name().toLowerCase()))
         .withStyle(this.getAction() == Modifier.Action.DENY ? ChatFormatting.DARK_RED : ChatFormatting.DARK_GREEN);
   }
}
