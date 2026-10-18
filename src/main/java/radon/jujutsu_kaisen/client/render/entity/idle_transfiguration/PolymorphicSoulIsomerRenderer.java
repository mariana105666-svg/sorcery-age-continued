package radon.jujutsu_kaisen.client.render.entity.idle_transfiguration;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.idle_transfiguration.PolymorphicSoulIsomerEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PolymorphicSoulIsomerRenderer extends GeoEntityRenderer<PolymorphicSoulIsomerEntity> {
   public PolymorphicSoulIsomerRenderer(Context renderManager) {
      super(renderManager, new DefaultedTurnHeadEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "polymorphic_soul_isomer")));
   }
}
