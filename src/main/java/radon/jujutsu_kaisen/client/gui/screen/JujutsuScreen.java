package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.gui.screen.tab.AbilityTab;
import radon.jujutsu_kaisen.client.gui.screen.tab.BindingVowTab;
import radon.jujutsu_kaisen.client.gui.screen.tab.ChantTab;
import radon.jujutsu_kaisen.client.gui.screen.tab.CursedEnergyColorTab;
import radon.jujutsu_kaisen.client.gui.screen.tab.DomainCustomizationTab;
import radon.jujutsu_kaisen.client.gui.screen.tab.JJKTab;
import radon.jujutsu_kaisen.client.gui.screen.tab.JJKTabType;
import radon.jujutsu_kaisen.client.gui.screen.tab.PactTab;
import radon.jujutsu_kaisen.client.gui.screen.tab.QuickVeilTab;
import radon.jujutsu_kaisen.client.gui.screen.tab.StatsTab;

public class JujutsuScreen extends Screen {
   private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
   public static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");
   public static final int WINDOW_WIDTH = 252;
   public static final int WINDOW_HEIGHT = 140;
   private static final int WINDOW_INSIDE_X = 9;
   private static final int WINDOW_INSIDE_Y = 18;
   public static final int WINDOW_INSIDE_WIDTH = 234;
   public static final int WINDOW_INSIDE_HEIGHT = 113;
   public static final int BACKGROUND_TILE_WIDTH = 16;
   public static final int BACKGROUND_TILE_HEIGHT = 16;
   private final List<JJKTab> tabs = new ArrayList<>();
   @Nullable
   private JJKTab selectedTab;
   private static int tabPage;
   private static int maxPages;
   private boolean isScrolling;

   public JujutsuScreen() {
      super(GameNarrator.NO_TITLE);
   }

   private void setSelectedTab(@NotNull JJKTab tab) {
      if (this.selectedTab != null) {
         for (GuiEventListener widget : this.selectedTab.getRenderables()) {
            this.removeWidget(widget);
         }

         this.selectedTab.removeWidgets();
      }

      this.selectedTab = tab;
      this.selectedTab.addWidgets();
   }

   public void removeWidgets() {
      if (this.selectedTab != null) {
         for (GuiEventListener widget : this.selectedTab.getRenderables()) {
            this.removeWidget(widget);
         }
      }
   }

   public void tick() {
      super.tick();
      if (this.selectedTab != null) {
         this.selectedTab.tick();
      }
   }

   @NotNull
   public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(@NotNull T pWidget) {
      return (T)super.addRenderableWidget(pWidget);
   }

   public static boolean hasQuickVeil() {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         return cap != null && cap.isUnlocked((Ability)JJKAbilities.QUICK_VEIL_CONFIG.get());
      }
   }

   protected void init() {
      this.tabs.clear();
      this.selectedTab = null;
      int index = 0;
      this.tabs.add(new StatsTab(this.minecraft, this, JJKTabType.ABOVE, 0, 0));
      this.tabs.add(new DomainCustomizationTab(this.minecraft, this, JJKTabType.ABOVE, ++index % JJKTabType.MAX_TABS, index / JJKTabType.MAX_TABS));
      this.tabs.add(new PactTab(this.minecraft, this, JJKTabType.ABOVE, ++index % JJKTabType.MAX_TABS, index / JJKTabType.MAX_TABS));
      this.tabs.add(new BindingVowTab(this.minecraft, this, JJKTabType.ABOVE, ++index % JJKTabType.MAX_TABS, index / JJKTabType.MAX_TABS));
      this.tabs.add(new AbilityTab(this.minecraft, this, JJKTabType.ABOVE, ++index % JJKTabType.MAX_TABS, index / JJKTabType.MAX_TABS));
      this.tabs.add(new ChantTab(this.minecraft, this, JJKTabType.ABOVE, ++index % JJKTabType.MAX_TABS, index / JJKTabType.MAX_TABS));
      this.tabs.add(new CursedEnergyColorTab(this.minecraft, this, JJKTabType.ABOVE, ++index % JJKTabType.MAX_TABS, index / JJKTabType.MAX_TABS));
      if (hasQuickVeil()) {
         this.tabs.add(new QuickVeilTab(this.minecraft, this, JJKTabType.ABOVE, ++index % JJKTabType.MAX_TABS, index / JJKTabType.MAX_TABS));
      }

      this.setSelectedTab(this.tabs.get(0));
      if (this.tabs.size() > JJKTabType.MAX_TABS) {
         int guiLeft = (this.width - 252) / 2;
         int guiTop = (this.height - 140) / 2;
         this.addRenderableWidget(
            Button.builder(Component.literal("<"), b -> tabPage = Math.max(tabPage - 1, 0)).pos(guiLeft, guiTop - 50).size(20, 20).build()
         );
         this.addRenderableWidget(
            Button.builder(Component.literal(">"), b -> tabPage = Math.min(tabPage + 1, maxPages))
               .pos(guiLeft + 252 - 20, guiTop - 50)
               .size(20, 20)
               .build()
         );
         maxPages = this.tabs.size() / JJKTabType.MAX_TABS;
      }
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      if (this.selectedTab != null) {
         int i = (this.width - 252) / 2;
         int j = (this.height - 140) / 2;
         this.selectedTab.mouseClicked(pMouseX - i - 9.0, pMouseY - j - 18.0, pButton);
      }

      if (pButton == 0) {
         int i = (this.width - 252) / 2;
         int j = (this.height - 140) / 2;

         for (JJKTab tab : this.tabs) {
            if (tab.getPage() == tabPage && tab.isMouseOver(i, j, pMouseX, pMouseY)) {
               this.setSelectedTab(tab);
               break;
            }
         }
      }

      return super.mouseClicked(pMouseX, pMouseY, pButton);
   }

   public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (this.selectedTab != null) {
         this.selectedTab.keyPressed(pKeyCode, pScanCode, pModifiers);
      }

      return super.keyPressed(pKeyCode, pScanCode, pModifiers);
   }

   public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
      int i = (this.width - 252) / 2;
      int j = (this.height - 140) / 2;
      this.renderBackground(pGuiGraphics);
      if (maxPages != 0) {
         Component page = Component.literal(String.format("%d / %d", tabPage + 1, maxPages + 1));
         int width = this.font.width(page);
         pGuiGraphics.drawString(this.font, page.getVisualOrderText(), i + 126 - width / 2, j - 44, -1);
      }

      this.renderInside(pGuiGraphics, pMouseX, pMouseY, i, j);
      this.renderWindow(pGuiGraphics, i, j);
      this.renderTooltips(pGuiGraphics, pMouseX, pMouseY, i, j);
      super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
   }

   private void renderInside(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int pOffsetX, int pOffsetY) {
      if (this.selectedTab != null) {
         this.selectedTab.drawContents(pGuiGraphics, pOffsetX + 9, pOffsetY + 18);
      }
   }

   public void renderWindow(GuiGraphics pGuiGraphics, int pOffsetX, int pOffsetY) {
      RenderSystem.enableBlend();
      pGuiGraphics.blit(WINDOW_LOCATION, pOffsetX, pOffsetY, 0, 0, 252, 140);

      for (JJKTab tab : this.tabs) {
         if (tab.getPage() == tabPage) {
            tab.drawTab(pGuiGraphics, pOffsetX, pOffsetY, tab == this.selectedTab);
         }
      }

      for (JJKTab tabx : this.tabs) {
         if (tabx.getPage() == tabPage) {
            tabx.drawIcon(pGuiGraphics, pOffsetX, pOffsetY);
         }
      }
   }

   private void renderTooltips(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int pOffsetX, int pOffsetY) {
      if (this.selectedTab != null) {
         this.selectedTab.drawTooltips(pGuiGraphics, pMouseX, pMouseY, pOffsetX, pOffsetY);
      }

      for (JJKTab JJKTab : this.tabs) {
         if (JJKTab.getPage() == tabPage && JJKTab.isMouseOver(pOffsetX, pOffsetY, pMouseX, pMouseY)) {
            pGuiGraphics.renderTooltip(this.font, JJKTab.getTitle(), pMouseX, pMouseY);
         }
      }
   }

   public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
      if (pButton != 0) {
         this.isScrolling = false;
         return false;
      } else {
         if (!this.isScrolling) {
            this.isScrolling = true;
         } else if (this.selectedTab != null) {
            this.selectedTab.scroll(pDragX, pDragY);
         }

         return true;
      }
   }
}
