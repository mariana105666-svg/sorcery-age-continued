package radon.jujutsu_kaisen.client.render.entity.curse;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.client.layer.RikaOpenLayer;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RikaRenderer extends GeoEntityRenderer<RikaEntity> {
   public RikaRenderer(Context renderManager) {
      super(renderManager, new DefaultedTurnHeadEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "rika")));
      this.addRenderLayer(new RikaOpenLayer(this));
   }
}
