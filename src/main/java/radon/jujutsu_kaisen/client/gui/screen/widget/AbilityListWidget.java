package radon.jujutsu_kaisen.client.gui.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.base.Ability;

public class AbilityListWidget extends JJKSelectionList<Ability, AbilityListWidget.Entry> {
   public AbilityListWidget(
      JJKSelectionList.IBuilder<Ability, AbilityListWidget.Entry> builder,
      JJKSelectionList.ICallback<AbilityListWidget.Entry> callback,
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
      this.builder.build(x$0 -> this.addEntry(x$0), x$0 -> new AbilityListWidget.Entry(x$0));
   }

   public class Entry extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<AbilityListWidget.Entry> {
      private final Ability ability;

      Entry(Ability ability) {
         this.ability = ability;
      }

      public Ability get() {
         return this.ability;
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
         FormattedText formatted = FormattedText.of(this.ability.getName().getString());
         int delta = AbilityListWidget.this.minecraft.font.width(formatted) - pWidth;
         formatted = AbilityListWidget.this.minecraft.font.substrByWidth(formatted, pWidth);
         if (delta > 0) {
            String text = formatted.getString();
            formatted = FormattedText.of(String.format("%s...", text.substring(0, text.length() - 3)));
         }

         pGuiGraphics.drawString(
            AbilityListWidget.this.minecraft.font,
            Language.getInstance().getVisualOrder(FormattedText.composite(new FormattedText[]{formatted})),
            pLeft + 3,
            pTop + 2,
            16777215,
            false
         );
      }

      public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
         AbilityListWidget.this.callback.setSelected(this);
         AbilityListWidget.this.setSelected(this);
         return false;
      }

      @NotNull
      public Component getNarration() {
         return this.ability.getName();
      }
   }
}
