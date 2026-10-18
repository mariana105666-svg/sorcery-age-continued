package radon.jujutsu_kaisen.client.visual.visual;

import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.client.visual.base.IVisual;

public class PerfectBodyVisual implements IVisual {
   private static final int MAX_MOUTH_FRAMES = 4;

   public static void onChant(UUID identifier) {
      ClientVisualHandler.ClientData data = ClientVisualHandler.get(identifier);
      if (data != null) {
         data.mouth++;
      }
   }

   @Override
   public boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData data) {
      return data.mouth > 0 && entity.level().getGameTime() % 5L == 0L;
   }

   @Override
   public void tick(LivingEntity entity, ClientVisualHandler.ClientData data) {
      if (++data.mouth >= 4) {
         data.mouth = 0;
      }
   }
}
