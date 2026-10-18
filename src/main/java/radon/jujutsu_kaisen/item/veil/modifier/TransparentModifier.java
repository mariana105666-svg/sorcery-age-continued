package radon.jujutsu_kaisen.item.veil.modifier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class TransparentModifier extends Modifier {
   public TransparentModifier(Modifier.Action action) {
      super(Modifier.Type.TRANSPARENT, action);
   }

   public TransparentModifier(CompoundTag nbt) {
      super(nbt);
   }

   @Override
   public Component getComponent() {
      return Component.translatable(String.format("item.%s.veil_rod.transparent", "jujutsu_kaisen"));
   }
}
