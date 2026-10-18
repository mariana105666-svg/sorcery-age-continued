package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.util.HelperMethods;

@EventBusSubscriber(
   modid = "jujutsu_kaisen",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public class BlinkOverlay {
   private static BlinkOverlay.FlashEvent current;
   private static ChatVisiblity previousChatVisibility;
   public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
      if (current != null) {
         float alpha = (float)current.duration / current.maxDuration;
         if (alpha >= 0.5) {
            alpha = 1.0F;
         } else {
            alpha *= 2.0F;
         }

         graphics.fill(0, 0, width, height, HelperMethods.toRGB24(0, 0, 0, (int)(255.0F * alpha)));
      }
   };

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase != Phase.START) {
         if (current != null) {
            current.duration--;
            if (current.duration <= 0) {
               current = null;
               if (previousChatVisibility != null) {
                  Minecraft mc = Minecraft.getInstance();
                  mc.options.chatVisibility().set(previousChatVisibility);
                  previousChatVisibility = null;
               }
            }
         }
      }
   }

   public static void flash(int setDuration) {
      Minecraft mc = Minecraft.getInstance();
      if (current == null) {
         previousChatVisibility = (ChatVisiblity)mc.options.chatVisibility().get();
         mc.options.chatVisibility().set(ChatVisiblity.HIDDEN);
      }

      current = new BlinkOverlay.FlashEvent(setDuration);
   }

   public static class FlashEvent {
      public int duration;
      public int maxDuration;

      public FlashEvent(int duration) {
         this.duration = duration;
         this.maxDuration = duration;
      }
   }
}
