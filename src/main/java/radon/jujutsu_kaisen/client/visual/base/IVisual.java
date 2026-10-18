package radon.jujutsu_kaisen.client.visual.base;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

public interface IVisual {
   boolean isValid(LivingEntity var1, ClientVisualHandler.ClientData var2);

   void tick(LivingEntity var1, ClientVisualHandler.ClientData var2);
}
