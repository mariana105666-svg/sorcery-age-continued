package radon.jujutsu_kaisen.client.render.entity.curse;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.client.layer.ScissorCurseOpenLayer;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnnaEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ScissorCurseRenderer extends GeoEntityRenderer<KuchisakeOnnaEntity> {
   public ScissorCurseRenderer(Context renderManager) {
      super(renderManager, new DefaultedEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "kuchisake_onna")));
      this.addRenderLayer(new ScissorCurseOpenLayer(this));
   }
}
