package radon.jujutsu_kaisen.client.render.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.effect.VolcanoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VolcanoRenderer extends GeoEntityRenderer<VolcanoEntity> {
   public VolcanoRenderer(Context renderManager) {
      super(renderManager, new DefaultedEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "volcano")));
   }

   public void preRender(
      PoseStack poseStack,
      VolcanoEntity animatable,
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
      float pitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());
      poseStack.translate(0.0, animatable.getBbHeight() / 2.0F, 0.0);
      poseStack.mulPose(Axis.YP.rotationDegrees(360.0F - yaw));
      poseStack.mulPose(Axis.XP.rotationDegrees(pitch + 90.0F));
      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
   }

   protected int getBlockLightLevel(@NotNull VolcanoEntity pEntity, @NotNull BlockPos pPos) {
      return 15;
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull VolcanoEntity animatable) {
      ResourceLocation key = super.getTextureLocation(animatable);
      return animatable.getTime() >= 15 ? new ResourceLocation(key.getNamespace(), key.getPath().replace(".png", String.format("_%s.%s", "erupt", "png"))) : key;
   }
}
