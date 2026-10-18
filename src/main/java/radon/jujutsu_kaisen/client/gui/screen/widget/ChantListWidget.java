package radon.jujutsu_kaisen.client.gui.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.NotNull;

public class ChantListWidget extends JJKSelectionList<String, ChantListWidget.Entry> {
   public ChantListWidget(
      JJKSelectionList.IBuilder<String, ChantListWidget.Entry> builder,
      JJKSelectionList.ICallback<ChantListWidget.Entry> callback,
      Minecraft minecraft,
      int width,
      int height,
      int x,
      int y
   ) {
      super(builder, callback, minecraft, width, height, x, y);
   }

   @Override
   public void refreshList() {
      super.refreshList();
      this.builder.build(x$0 -> this.addEntry(x$0), x$0 -> new ChantListWidget.Entry(x$0));
   }

   public class Entry extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<ChantListWidget.Entry> {
      private final String chant;

      Entry(String chant) {
         this.chant = chant;
      }

      public String get() {
         return this.chant;
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
         FormattedText formatted = FormattedText.of(this.chant);
         int delta = ChantListWidget.this.minecraft.font.width(formatted) - pWidth;
         formatted = ChantListWidget.this.minecraft.font.substrByWidth(formatted, pWidth);
         if (delta > 0) {
            String text = formatted.getString();
            formatted = FormattedText.of(String.format("%s...", text.substring(0, text.length() - 3)));
         }

         pGuiGraphics.drawString(
            ChantListWidget.this.minecraft.font,
            Language.getInstance().getVisualOrder(FormattedText.composite(new FormattedText[]{formatted})),
            pLeft + 3,
            pTop + 2,
            16777215,
            false
         );
      }

      public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
         ChantListWidget.this.callback.setSelected(this);
         ChantListWidget.this.setSelected(this);
         return false;
      }

      @NotNull
      public Component getNarration() {
         return Component.literal(this.chant);
      }
   }
}
