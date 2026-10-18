package radon.jujutsu_kaisen.client.gui.overlay;

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
public class ScreenFlashOverlay {
   private static final int DEFAULT_DURATION = 20;
   private static ScreenFlashOverlay.FlashEvent current;
   public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
      if (current != null) {
         float alpha = current.duration / 20.0F;
         graphics.fill(0, 0, width, height, HelperMethods.toRGB24(255, 255, 255, (int)(255.0F * alpha)));
      }
   };

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase != Phase.START) {
         if (current != null) {
            current.duration--;
            if (current.duration <= 0) {
               current = null;
            }
         }
      }
   }

   public static void flash() {
      current = new ScreenFlashOverlay.FlashEvent(20);
   }

   public static class FlashEvent {
      public int duration;

      public FlashEvent(int duration) {
         this.duration = duration;
      }
   }
}
