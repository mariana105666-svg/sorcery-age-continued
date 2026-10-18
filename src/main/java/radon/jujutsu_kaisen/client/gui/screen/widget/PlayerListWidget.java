package radon.jujutsu_kaisen.client.gui.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.NotNull;

public class PlayerListWidget extends JJKSelectionList<PlayerInfo, PlayerListWidget.Entry> {
   public PlayerListWidget(
      JJKSelectionList.IBuilder<PlayerInfo, PlayerListWidget.Entry> builder,
      JJKSelectionList.ICallback<PlayerListWidget.Entry> callback,
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
      this.builder.build(x$0 -> this.addEntry(x$0), x$0 -> new PlayerListWidget.Entry(x$0));
   }

   public class Entry extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<PlayerListWidget.Entry> {
      private final PlayerInfo player;

      Entry(PlayerInfo player) {
         this.player = player;
      }

      public PlayerInfo get() {
         return this.player;
      }

      public void render(
         GuiGraphics pGuiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pHovering, float pPartialTick
      ) {
         pGuiGraphics.drawString(
            PlayerListWidget.this.minecraft.font,
            Language.getInstance()
               .getVisualOrder(
                  FormattedText.composite(
                     new FormattedText[]{
                        PlayerListWidget.this.minecraft
                           .font
                           .substrByWidth(Component.literal(this.player.getProfile().getName()), PlayerListWidget.this.width)
                     }
                  )
               ),
            pLeft + 3,
            pTop + 2,
            16777215,
            false
         );
      }

      public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
         PlayerListWidget.this.callback.setSelected(this);
         PlayerListWidget.this.setSelected(this);
         return false;
      }

      @NotNull
      public Component getNarration() {
         return Component.literal(this.player.getProfile().getName());
      }
   }
}
