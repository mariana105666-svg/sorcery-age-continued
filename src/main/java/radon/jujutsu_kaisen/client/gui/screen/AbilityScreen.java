package radon.jujutsu_kaisen.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.gui.screen.base.RadialScreen;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.CurseSummonC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.SetAbsorbedC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.SetAdditionalC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.SetStolenC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.TriggerAbilityC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.UntriggerAbilityC2SPacket;

public class AbilityScreen extends RadialScreen {
   @Override
   protected List<DisplayItem> getItems() {
      if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) {
         return List.of();
      } else if (!this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return List.of();
      } else {
         ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         List<Ability> abilities = JJKAbilities.getAbilities(this.minecraft.player);
         abilities.removeIf(ability -> ability.getMenuType() != MenuType.RADIAL);
         List<DisplayItem> items = new ArrayList<>(abilities.stream().map(DisplayItem::new).toList());
         List<AbsorbedCurse> curses = new ArrayList<>(cap.getCurses());
         curses.sort((o1, o2) -> (int)(JJKAbilities.getCurseExperience(o2) - JJKAbilities.getCurseExperience(o1)));
         items.addAll(curses.stream().map(curse -> new DisplayItem(curse, curses.indexOf(curse))).toList());
         Set<CursedTechnique> copied = cap.getCopied();
         items.addAll(copied.stream().map(technique -> new DisplayItem(DisplayItem.Type.COPIED, technique)).toList());
         Set<CursedTechnique> absorbed = cap.getAbsorbed();
         items.addAll(absorbed.stream().map(technique -> new DisplayItem(DisplayItem.Type.ABSORBED, technique)).toList());
         Set<CursedTechnique> stolen = cap.getStolen();
         items.addAll(stolen.stream().map(technique -> new DisplayItem(DisplayItem.Type.STOLEN, technique)).toList());
         return items;
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
         switch (item.type) {
            case ABILITY:
               Ability ability = item.ability;
               if (!cap.hasToggled(ability) && !cap.isChanneling(ability)) {
                  PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
               } else {
                  AbilityHandler.untrigger(this.minecraft.player, ability);
                  PacketHandler.sendToServer(new UntriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
               }
               break;
            case CURSE:
               PacketHandler.sendToServer(new CurseSummonC2SPacket(item.curse.getKey().getUUID()));
               break;
            case COPIED:
               PacketHandler.sendToServer(new SetAdditionalC2SPacket(item.copied));
               cap.setCurrentCopied(item.copied);
               break;
            case ABSORBED:
               cap.setCurrentAbsorbed(item.absorbed);
               PacketHandler.sendToServer(new SetAbsorbedC2SPacket(item.absorbed));
               break;
            case STOLEN:
               cap.setCurrentStolen(item.stolen);
               PacketHandler.sendToServer(new SetStolenC2SPacket(item.stolen));
         }
      }
   }

   @Override
   public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
      super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTicks);
      if (this.minecraft != null && this.minecraft.player != null) {
         if (this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if (cap.hasTechnique(CursedTechnique.MIMICRY)) {
               int centerX = this.width / 2;
               int centerY = this.height / 2;
               int y = centerY - 100 - 9 * 2;
               pGuiGraphics.drawCenteredString(this.font, Component.translatable(String.format("gui.%s.ability.right_click", "jujutsu_kaisen")), centerX, y, 16777215);
            }
         }
      }
   }

   public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
      if (pKeyCode == JJKKeys.SHOW_ABILITY_MENU.getKey().getValue()) {
         this.onClose();
      }

      return super.keyReleased(pKeyCode, pScanCode, pModifiers);
   }
}
