package radon.jujutsu_kaisen.item.veil.modifier;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class AdminModifier extends Modifier {
   public AdminModifier(Modifier.Action action) {
      super(Modifier.Type.ADMIN, action);
   }

   public AdminModifier(CompoundTag nbt) {
      super(nbt);
   }

   @Override
   public Component getComponent() {
      return Component.translatable(String.format("item.%s.veil_rod.admin", "jujutsu_kaisen")).withStyle(ChatFormatting.LIGHT_PURPLE);
   }
}
