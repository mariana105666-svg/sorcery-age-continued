package radon.jujutsu_kaisen.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.gui.screen.base.RadialScreen;

public class JutwotsuScreen extends RadialScreen {
   @Nullable
   private static Ability selected;

   public static void select(@Nullable Ability ability) {
      selected = ability;
   }

   @Nullable
   public static Ability getSelected() {
      return selected;
   }

   @Override
   protected boolean isActive(DisplayItem item) {
      return getSelected() == item.ability || super.isActive(item);
   }

   @Override
   protected List<DisplayItem> getItems() {
      if (this.minecraft != null && this.minecraft.level != null && this.minecraft.player != null) {
         List<Ability> abilities = JJKAbilities.getAbilities(this.minecraft.player);
         abilities.removeIf(ability -> ability.getMenuType() != MenuType.J2TSU);
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

         DisplayItem item = this.getCurrent().get(this.hovered);
         selected = item.ability;
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
      if (pKeyCode == JJKKeys.ACTIVATE_J2TSU_MENU.getKey().getValue()) {
         this.onClose();
      }

      return super.keyReleased(pKeyCode, pScanCode, pModifiers);
   }
}
