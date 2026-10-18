package radon.jujutsu_kaisen.client.render.entity.curse;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.client.render.entity.base.SegmentRenderer;
import radon.jujutsu_kaisen.entity.curse.WormCurseSegmentEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class WormCurseSegmentRenderer extends SegmentRenderer<WormCurseSegmentEntity> {
   public WormCurseSegmentRenderer(Context renderManager) {
      super(renderManager, new DefaultedEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "worm_curse_segment")));
   }
}
