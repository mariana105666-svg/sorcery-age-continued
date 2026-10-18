package radon.jujutsu_kaisen.client.gui.screen.tab;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.ObjectSelectionList.Entry;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.JJKSelectionList;

public abstract class JJKTab {
   protected final Minecraft minecraft;
   protected final JujutsuScreen screen;
   private final JJKTabType type;
   private final int index;
   private final ItemStack icon;
   private final Component title;
   private final int page;
   private final ResourceLocation background;
   private final boolean scrollable;
   protected double scrollX;
   protected double scrollY;
   protected int minX = Integer.MAX_VALUE;
   protected int minY = Integer.MAX_VALUE;
   protected int maxX = Integer.MIN_VALUE;
   protected int maxY = Integer.MIN_VALUE;
   private boolean centered;
   private final List<GuiEventListener> widgets = new ArrayList<>();

   public JJKTab(
      Minecraft minecraft,
      JujutsuScreen screen,
      JJKTabType type,
      int index,
      int page,
      ItemStack icon,
      Component title,
      ResourceLocation background,
      boolean scrollable
   ) {
      this.minecraft = minecraft;
      this.screen = screen;
      this.type = type;
      this.index = index;
      this.page = page;
      this.icon = icon;
      this.title = title;
      this.background = background;
      this.scrollable = scrollable;
   }

   public JujutsuScreen getScreen() {
      return this.screen;
   }

   public void scroll(double pDragX, double pDragY) {
      if (this.scrollable) {
         if (this.maxX - this.minX > 234) {
            this.scrollX = Mth.clamp(this.scrollX + pDragX, -(this.maxX - 234), -Math.min(0.0, (double)this.minX));
         }

         if (this.maxY - this.minY > 113) {
            this.scrollY = Mth.clamp(this.scrollY + pDragY, -(this.maxY - 113), -Math.min(0.0, (double)this.minY));
         }
      }
   }

   public void tick() {
   }

   public List<GuiEventListener> getRenderables() {
      return this.widgets;
   }

   public abstract void addWidgets();

   public void removeWidgets() {
      this.screen.removeWidgets();
      this.widgets.clear();
   }

   protected <T extends GuiEventListener & Renderable & NarratableEntry> void addRenderableWidget(@NotNull T pWidget) {
      this.widgets.add(pWidget);
      this.screen.addRenderableWidget(pWidget);
   }

   public int getPage() {
      return this.page;
   }

   public JJKTabType getType() {
      return this.type;
   }

   public int getIndex() {
      return this.index;
   }

   public Component getTitle() {
      return this.title;
   }

   public void drawTab(GuiGraphics pGuiGraphics, int pOffsetX, int pOffsetY, boolean pIsSelected) {
      this.type.draw(pGuiGraphics, pOffsetX, pOffsetY, pIsSelected, this.index);
   }

   public void drawIcon(GuiGraphics pGuiGraphics, int pOffsetX, int pOffsetY) {
      this.type.drawIcon(pGuiGraphics, pOffsetX, pOffsetY, this.index, this.icon);
   }

   public void mouseClicked(double pMouseX, double pMouseY, int pButton) {
   }

   public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
      if (!this.centered) {
         this.scrollX = 117.0 - (this.maxX + this.minX) / 2.0;
         this.scrollY = 56.0 - (this.maxY + this.minY) / 2.0;
         this.centered = true;
      }

      pGuiGraphics.enableScissor(pX, pY, pX + 234, pY + 113);
      pGuiGraphics.pose().pushPose();
      pGuiGraphics.pose().translate(pX, pY, 0.0F);
      int i = Mth.floor(this.scrollX);
      int j = Mth.floor(this.scrollY);
      int k = i % 16;
      int l = j % 16;

      for (int i1 = -1; i1 <= 15; i1++) {
         for (int j1 = -1; j1 <= 8; j1++) {
            pGuiGraphics.blit(this.background, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
         }
      }

      this.drawCustom(pGuiGraphics, i, j);
      pGuiGraphics.pose().popPose();
      pGuiGraphics.disableScissor();
   }

   protected void drawCustom(GuiGraphics graphics, int x, int y) {
   }

   public void drawTooltips(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int pWidth, int pHeight) {
      for (GuiEventListener widget : this.widgets) {
         if (widget instanceof JJKSelectionList<?, ?> list && list.isMouseOver(pMouseX, pMouseY)) {
            Entry<?> entry = list.getMouseOver(pMouseX, pMouseY);
            if (entry != null) {
               pGuiGraphics.renderTooltip(this.minecraft.font, entry.getNarration(), pMouseX, pMouseY);
               break;
            }
         }
      }
   }

   public boolean isMouseOver(int pOffsetX, int pOffsetY, double pMouseX, double pMouseY) {
      return this.type.isMouseOver(pOffsetX, pOffsetY, this.index, pMouseX, pMouseY);
   }

   public void keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
   }
}
