package radon.jujutsu_kaisen.client;

import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.Type;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "jujutsu_kaisen",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public class CameraShakeHandler {
   @Nullable
   private static CameraShakeHandler.ShakeEvent current;

   @SubscribeEvent
   public static void onComputeCameraAngles(ComputeCameraAngles event) {
      if (current != null) {
         float time = (float)(event.getCamera().getEntity().tickCount + event.getPartialTick());
         float shakeX = Mth.cos(time * current.speed) * current.intensity;
         float shakeY = Mth.sin(time * current.speed) * current.intensity;
         event.setPitch(event.getPitch() + shakeX);
         event.setYaw(event.getYaw() + shakeY);
      }
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.type == Type.CLIENT) {
         if (event.phase != Phase.START) {
            if (current != null) {
               current.duration--;
               if (current.duration <= 0) {
                  current = null;
               }
            }
         }
      }
   }

   public static void shakeCamera(float intensity, float speed, int duration) {
      current = new CameraShakeHandler.ShakeEvent(intensity, speed, duration);
   }

   public static class ShakeEvent {
      public float intensity;
      public float speed;
      public int duration;

      public ShakeEvent(float intensity, float speed, int duration) {
         this.intensity = intensity;
         this.speed = speed;
         this.duration = duration;
      }
   }
}
