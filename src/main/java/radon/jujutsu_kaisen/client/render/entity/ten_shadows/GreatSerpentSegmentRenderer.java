package radon.jujutsu_kaisen.client.render.entity.ten_shadows;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.client.render.entity.base.SegmentRenderer;
import radon.jujutsu_kaisen.entity.ten_shadows.GreatSerpentEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.GreatSerpentSegmentEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GreatSerpentSegmentRenderer extends SegmentRenderer<GreatSerpentSegmentEntity> {
   public GreatSerpentSegmentRenderer(Context renderManager) {
      super(renderManager, new DefaultedEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "great_serpent_segment")));
   }

   public void actuallyRender(
      PoseStack poseStack,
      GreatSerpentSegmentEntity animatable,
      BakedGeoModel model,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      float red,
      float green,
      float blue,
      float alpha
   ) {
      if (((GreatSerpentEntity)animatable.getParent()).isClone()) {
         super.actuallyRender(
            poseStack,
            animatable,
            model,
            renderType,
            bufferSource,
            buffer,
            isReRender,
            partialTick,
            packedLight,
            packedOverlay,
            TenShadowsRenderer.COLOR.x,
            TenShadowsRenderer.COLOR.y,
            TenShadowsRenderer.COLOR.z,
            alpha
         );
      } else {
         super.actuallyRender(
            poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha
         );
      }
   }
}
