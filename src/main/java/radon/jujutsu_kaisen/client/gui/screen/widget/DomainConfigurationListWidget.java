package radon.jujutsu_kaisen.client.gui.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.sorcerer.DomainConfiguration;

public class DomainConfigurationListWidget extends JJKSelectionList<DomainConfiguration, DomainConfigurationListWidget.Entry> {
   public DomainConfigurationListWidget(
      JJKSelectionList.IBuilder<DomainConfiguration, DomainConfigurationListWidget.Entry> builder,
      JJKSelectionList.ICallback<DomainConfigurationListWidget.Entry> callback,
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
      this.builder.build(x$0 -> this.addEntry(x$0), x$0 -> new DomainConfigurationListWidget.Entry(x$0));
   }

   public class Entry extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<DomainConfigurationListWidget.Entry> {
      private final DomainConfiguration config;

      Entry(DomainConfiguration config) {
         this.config = config;
      }

      public DomainConfiguration get() {
         return this.config;
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
         FormattedText formatted = FormattedText.of(this.config.getName().getString());
         int delta = DomainConfigurationListWidget.this.minecraft.font.width(formatted) - pWidth;
         formatted = DomainConfigurationListWidget.this.minecraft.font.substrByWidth(formatted, pWidth);
         if (delta > 0) {
            String text = formatted.getString();
            formatted = FormattedText.of(String.format("%s...", text.substring(0, text.length() - 3)));
         }

         pGuiGraphics.drawString(
            DomainConfigurationListWidget.this.minecraft.font,
            Language.getInstance().getVisualOrder(FormattedText.composite(new FormattedText[]{formatted})),
            pLeft + 3,
            pTop + 2,
            16777215,
            false
         );
      }

      public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
         DomainConfigurationListWidget.this.callback.setSelected(this);
         DomainConfigurationListWidget.this.setSelected(this);
         return false;
      }

      @NotNull
      public Component getNarration() {
         return this.config.getName();
      }
   }
}
