package radon.jujutsu_kaisen.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.MouseScrollingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.client.gui.MeleeMenuType;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.config.ConfigHolder;

@EventBusSubscriber(
   modid = "jujutsu_kaisen",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public class MouseHandler {
   @SubscribeEvent
   public static void onMouseScroll(MouseScrollingEvent event) {
      double delta = event.getScrollDelta();
      int i = (int)Math.signum(delta);
      if (i != 0) {
         if (ConfigHolder.CLIENT.meleeMenuType.get() == MeleeMenuType.SCROLL) {
            if (JJKKeys.ACTIVATE_MELEE_MENU.isDown() && AbilityOverlay.scroll(i)) {
               event.setCanceled(true);
            }
         }
      }
   }
}
