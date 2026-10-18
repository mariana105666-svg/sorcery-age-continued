package radon.jujutsu_kaisen.client.render.entity.ten_shadows;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.entity.ten_shadows.GreatSerpentEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GreatSerpentHeadRenderer extends TenShadowsRenderer<GreatSerpentEntity> {
   public GreatSerpentHeadRenderer(Context renderManager) {
      super(renderManager, new DefaultedEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "great_serpent_head")));
   }
}
