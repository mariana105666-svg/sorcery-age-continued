package radon.jujutsu_kaisen.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.client.gui.screen.ShadowInventoryScreen;

public class ClientWrapper {
   @Nullable
   public static Level getLevel() {
      return Minecraft.getInstance().level;
   }

   @Nullable
   public static Player getPlayer() {
      return Minecraft.getInstance().player;
   }

   public static void setOverlayMessage(Component component, boolean animate) {
      Minecraft.getInstance().gui.setOverlayMessage(component, animate);
   }

   public static void openShadowInventory() {
      Minecraft.getInstance().setScreen(new ShadowInventoryScreen());
   }
}
