package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;

public enum Trait {
   SIX_EYES,
   HEAVENLY_RESTRICTION,
   VESSEL,
   RCT_OUTPUT,
   INCARNATED,
   PERFECT_BODY,
   CURSED_WOMB,
   DEATH_PAINTING,
   PRODIGY,
   SIMURIAN,
   RAW_OUTPUT;

   public Component getName() {
      return Component.translatable(String.format("trait.%s.%s", "jujutsu_kaisen", this.name().toLowerCase()));
   }

   public String getRawName() {
      return this.name().toLowerCase();
   }
}
