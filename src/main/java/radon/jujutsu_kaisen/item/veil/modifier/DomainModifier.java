package radon.jujutsu_kaisen.item.veil.modifier;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class DomainModifier extends Modifier {
   public DomainModifier(Modifier.Action action) {
      super(Modifier.Type.DOMAIN, action);
   }

   public DomainModifier(CompoundTag nbt) {
      super(nbt);
   }

   @Override
   public Component getComponent() {
      return Component.translatable(String.format("item.%s.veil_rod.domain.%s", "jujutsu_kaisen", this.getAction().name().toLowerCase()))
         .withStyle(this.getAction() == Modifier.Action.DENY ? ChatFormatting.DARK_RED : ChatFormatting.DARK_GREEN);
   }
}
