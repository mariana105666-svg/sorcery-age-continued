package radon.jujutsu_kaisen.client.render.entity.ten_shadows;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.client.layer.RabbitEscapeLayer;
import radon.jujutsu_kaisen.entity.ten_shadows.RabbitEscapeEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RabbitEscapeRenderer extends TenShadowsRenderer<RabbitEscapeEntity> {
   public RabbitEscapeRenderer(Context renderManager) {
      super(renderManager, new DefaultedEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "rabbit_escape")));
      this.addRenderLayer(new RabbitEscapeLayer(this));
   }
}
