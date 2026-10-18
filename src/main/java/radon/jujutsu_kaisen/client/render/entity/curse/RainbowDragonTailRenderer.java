package radon.jujutsu_kaisen.client.render.entity.curse;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.client.render.entity.base.SegmentRenderer;
import radon.jujutsu_kaisen.entity.curse.RainbowDragonSegmentEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class RainbowDragonTailRenderer extends SegmentRenderer<RainbowDragonSegmentEntity> {
   public RainbowDragonTailRenderer(Context renderManager) {
      super(renderManager, new DefaultedEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "rainbow_dragon_tail")));
   }
}
