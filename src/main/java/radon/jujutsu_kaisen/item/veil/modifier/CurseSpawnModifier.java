package radon.jujutsu_kaisen.item.veil.modifier;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class CurseSpawnModifier extends Modifier {
   public CurseSpawnModifier(Modifier.Action action) {
      super(Modifier.Type.CURSE_SPAWN, action);
   }

   public CurseSpawnModifier(CompoundTag nbt) {
      super(nbt);
   }

   @Override
   public Component getComponent() {
      return Component.translatable(String.format("item.%s.veil_rod.curse_spawn.%s", "jujutsu_kaisen", this.getAction().name().toLowerCase()))
         .withStyle(this.getAction() == Modifier.Action.DENY ? ChatFormatting.DARK_RED : ChatFormatting.DARK_GREEN);
   }
}
