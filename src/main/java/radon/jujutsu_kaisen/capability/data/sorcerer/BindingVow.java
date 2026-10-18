package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;

public enum BindingVow {
   OVERTIME,
   RECOIL,
   CHANTER,
   RISK;

   public Component getName() {
      return Component.translatable(String.format("binding_vow.%s.%s", "jujutsu_kaisen", this.name().toLowerCase()));
   }

   public Component getDescription() {
      return Component.translatable(String.format("binding_vow.%s.%s.description", "jujutsu_kaisen", this.name().toLowerCase()));
   }
}
