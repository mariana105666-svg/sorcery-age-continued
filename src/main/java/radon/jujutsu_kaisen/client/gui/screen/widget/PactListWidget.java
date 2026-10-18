package radon.jujutsu_kaisen.client.gui.screen.widget;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;
import radon.jujutsu_kaisen.client.gui.screen.tab.PactTab;

public class PactListWidget extends JJKSelectionList<Pact, PactListWidget.Entry> {
   public PactListWidget(
      JJKSelectionList.IBuilder<Pact, PactListWidget.Entry> builder,
      JJKSelectionList.ICallback<PactListWidget.Entry> callback,
      Minecraft minecraft,
      int width,
      int height,
      int x,
      int y,
      PactTab parent
   ) {
      super(builder, callback, minecraft, width, height, x, y);
   }

   @Override
   protected void renderItem(
      @NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, int pIndex, int pLeft, int pTop, int pWidth, int pHeight
   ) {
      PactListWidget.Entry e = (PactListWidget.Entry)this.getEntry(pIndex);
      e.renderBack(pGuiGraphics, pIndex, pTop, pLeft, pWidth, pHeight, pMouseX, pMouseY, Objects.equals(this.getHovered(), e), pPartialTick);
      if (this.isSelectedItem(pIndex)) {
         pGuiGraphics.renderOutline(pLeft - 2, pTop - 2, pWidth, pHeight + 3, -1);
      }

      e.render(pGuiGraphics, pIndex, pTop, pLeft, pWidth, pHeight, pMouseX, pMouseY, Objects.equals(this.getHovered(), e), pPartialTick);
   }

   @Override
   public void refreshList() {
      super.refreshList();
      this.builder.build(x$0 -> this.addEntry(x$0), x$0 -> new PactListWidget.Entry(x$0));
   }

   public class Entry extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<PactListWidget.Entry> {
      private final Pact pact;

      Entry(Pact pact) {
         this.pact = pact;
      }

      public Pact get() {
         return this.pact;
      }

      public void render(
         @NotNull GuiGraphics pGuiGraphics,
         int pIndex,
         int pTop,
         int pLeft,
         int pWidth,
         int pHeight,
         int pMouseX,
         int pMouseY,
         boolean pHovering,
         float pPartialTick
      ) {
         FormattedText formatted = FormattedText.of(this.pact.getName().getString());
         int delta = PactListWidget.this.minecraft.font.width(formatted) - pWidth;
         formatted = PactListWidget.this.minecraft.font.substrByWidth(formatted, pWidth);
         if (delta > 0) {
            String text = formatted.getString();
            formatted = FormattedText.of(String.format("%s...", text.substring(0, text.length() - 3)));
         }

         pGuiGraphics.drawString(
            PactListWidget.this.minecraft.font,
            Language.getInstance().getVisualOrder(FormattedText.composite(new FormattedText[]{formatted})),
            pLeft + 3,
            pTop + 2,
            16777215,
            false
         );
      }

      public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
         PactListWidget.this.callback.setSelected(this);
         PactListWidget.this.setSelected(this);
         return false;
      }

      @NotNull
      public Component getNarration() {
         return this.pact.getName();
      }
   }
}
