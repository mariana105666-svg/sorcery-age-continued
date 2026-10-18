package radon.jujutsu_kaisen.client.render.entity.curse;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.curse.ZombaCurseEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ZombaCurseRenderer extends GeoEntityRenderer<ZombaCurseEntity> {
   public ZombaCurseRenderer(Context renderManager) {
      super(renderManager, new DefaultedTurnHeadEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "zomba_curse")));
   }
}
