package radon.jujutsu_kaisen.client.render.entity.ten_shadows;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.entity.ten_shadows.NueTotalityEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class NueTotalityRenderer extends TenShadowsRenderer<NueTotalityEntity> {
   public NueTotalityRenderer(Context renderManager) {
      super(renderManager, new DefaultedEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "nue_totality")));
   }
}
