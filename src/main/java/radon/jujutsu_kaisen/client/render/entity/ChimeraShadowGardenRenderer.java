package radon.jujutsu_kaisen.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import radon.jujutsu_kaisen.entity.ChimeraShadowGardenEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ChimeraShadowGardenRenderer extends GeoEntityRenderer<ChimeraShadowGardenEntity> {
   public ChimeraShadowGardenRenderer(Context renderManager) {
      super(renderManager, new DefaultedEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "chimera_shadow_garden")));
   }

   public void preRender(
      PoseStack poseStack,
      ChimeraShadowGardenEntity animatable,
      BakedGeoModel model,
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
      float yaw = Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot());
      poseStack.mulPose(Axis.YN.rotationDegrees(yaw));
      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
   }
}
