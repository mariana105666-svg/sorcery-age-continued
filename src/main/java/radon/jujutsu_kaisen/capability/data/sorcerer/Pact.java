package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;

public enum Pact {
   FRIENDS,
   INVULNERABILITY,
   TECHNIQUE;

   public Component getName() {
      return Component.translatable(String.format("pact.%s.%s", "jujutsu_kaisen", this.name().toLowerCase()));
   }

   public Component getDescription() {
      return Component.translatable(String.format("pact.%s.%s.description", "jujutsu_kaisen", this.name().toLowerCase()));
   }
}
