package radon.jujutsu_kaisen.client.visual.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

public interface IOverlay {
   boolean isValid(LivingEntity var1, ClientVisualHandler.ClientData var2);

   <T extends LivingEntity> void render(
      T var1, ClientVisualHandler.ClientData var2, ResourceLocation var3, EntityModel<T> var4, PoseStack var5, MultiBufferSource var6, float var7, int var8
   );
}
