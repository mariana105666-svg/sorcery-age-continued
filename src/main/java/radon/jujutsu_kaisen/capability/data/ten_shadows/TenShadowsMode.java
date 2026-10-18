package radon.jujutsu_kaisen.capability.data.ten_shadows;

import net.minecraft.network.chat.Component;

public enum TenShadowsMode {
   SUMMON,
   ABILITY;

   public Component getName() {
      return Component.translatable(String.format("ten_shadows_mode.%s.%s", "jujutsu_kaisen", this.name().toLowerCase()));
   }
}
