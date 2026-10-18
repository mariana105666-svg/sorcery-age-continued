package radon.jujutsu_kaisen.client.render.entity.ten_shadows;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.ten_shadows.TranquilDeerEntity;

public class TranquilDeerRenderer extends TenShadowsRenderer<TranquilDeerEntity> {
   public TranquilDeerRenderer(Context renderManager) {
      super(renderManager, new DefaultedTurnHeadEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "tranquil_deer")));
   }
}
