package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;

public enum CursedEnergyNature {
   BASIC,
   ROUGH,
   LIGHTNING,
   DIVERGENT;

   public Component getName() {
      return Component.translatable(String.format("cursed_energy_nature.%s.%s", "jujutsu_kaisen", this.name().toLowerCase()));
   }
}
