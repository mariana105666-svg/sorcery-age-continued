package radon.jujutsu_kaisen.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.client.particle.SlicedEntityParticle;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = "jujutsu_kaisen",
   bus = Bus.FORGE
)
public class SlicedEntityRenderer {
   @SubscribeEvent
   public static void onRenderLevelStage(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_ENTITIES) {
         for (SlicedEntityParticle sliced : SlicedEntityParticle.getAll()) {
            sliced.actuallyRender(event.getPoseStack(), event.getPartialTick());
         }
      }
   }
}
