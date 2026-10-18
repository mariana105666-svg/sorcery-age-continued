package radon.jujutsu_kaisen.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.gui.screen.base.RadialScreen;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.TriggerAbilityC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.UntriggerAbilityC2SPacket;

public class DomainScreen extends RadialScreen {
   @Override
   protected List<DisplayItem> getItems() {
      if (this.minecraft != null && this.minecraft.level != null && this.minecraft.player != null) {
         List<Ability> abilities = JJKAbilities.getAbilities(this.minecraft.player);
         abilities.removeIf(ability -> ability.getMenuType() != MenuType.DOMAIN);
         return new ArrayList<>(abilities.stream().map(DisplayItem::new).toList());
      } else {
         return List.of();
      }
   }

   public void onClose() {
      super.onClose();
      if (this.hovered != -1) {
         if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) {
            return;
         }

         ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         DisplayItem item = this.getCurrent().get(this.hovered);
         Ability ability = item.ability;
         if (!cap.hasToggled(ability) && !cap.isChanneling(ability)) {
            PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
         } else {
            AbilityHandler.untrigger(this.minecraft.player, ability);
            PacketHandler.sendToServer(new UntriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
         }
      }
   }

   public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
      if (pKeyCode == JJKKeys.SHOW_DOMAIN_MENU.getKey().getValue()) {
         this.onClose();
      }

      return super.keyReleased(pKeyCode, pScanCode, pModifiers);
   }
}
