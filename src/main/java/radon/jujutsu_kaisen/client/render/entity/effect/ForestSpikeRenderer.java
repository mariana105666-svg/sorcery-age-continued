package radon.jujutsu_kaisen.client.render.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.entity.effect.ForestSpikeEntity;

public class ForestSpikeRenderer extends EntityRenderer<ForestSpikeEntity> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("jujutsu_kaisen", "textures/entity/forest_spike.png");
   private static final int TEXTURE_WIDTH = 16;
   private static final int TEXTURE_HEIGHT = 32;
   private static final float WIDTH = 1.0F;
   private static final float HEIGHT = 2.0F;

   public ForestSpikeRenderer(Context pContext) {
      super(pContext);
   }

   public void render(
      @NotNull ForestSpikeEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight
   ) {
      pPoseStack.pushPose();
      pPoseStack.translate(0.0F, 0.0F, 0.0F);
      float yaw = Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot());
      float pitch = Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot());
      pPoseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
      pPoseStack.mulPose(Axis.YP.rotationDegrees(yaw));
      pPoseStack.mulPose(Axis.XP.rotationDegrees(pitch + 90.0F));
      pPoseStack.translate(0.0F, -1.5F, 0.0F);
      float age = pEntity.getTime() + pPartialTick;
      float maxScale = 1.0F;
      float popDuration = 6.0F;
      float verticalScale = age < popDuration ? age / popDuration * maxScale : maxScale;
      pPoseStack.translate(0.0F, 1.0F, 0.0F);
      pPoseStack.scale(1.0F, verticalScale, 1.0F);
      pPoseStack.translate(0.0F, -1.0F, 0.0F);

      for (int i = 0; i < 2; i++) {
         pPoseStack.mulPose(Axis.YP.rotationDegrees(i * 90.0F));
         RenderType type = RenderType.entityCutoutNoCull(TEXTURE);
         VertexConsumer consumer = pBuffer.getBuffer(type);
         Pose pose = pPoseStack.last();
         float minU = 0.0F;
         float minV = 0.0F;
         float maxU = minU + 1.0F;
         float maxV = minV + 1.0F;
         Matrix4f matrix4f = pose.pose();
         Matrix3f matrix3f = pose.normal();
         this.drawVertex(matrix4f, matrix3f, consumer, -1.0F, -2.0F, 0.0F, minU, minV, pPackedLight);
         this.drawVertex(matrix4f, matrix3f, consumer, -1.0F, 2.0F, 0.0F, minU, maxV, pPackedLight);
         this.drawVertex(matrix4f, matrix3f, consumer, 1.0F, 2.0F, 0.0F, maxU, maxV, pPackedLight);
         this.drawVertex(matrix4f, matrix3f, consumer, 1.0F, -2.0F, 0.0F, maxU, minV, pPackedLight);
      }

      pPoseStack.popPose();
   }

   private void drawVertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer consumer, float x, float y, float z, float u, float v, int packedLight) {
      consumer.vertex(matrix4f, x, y, z)
         .color(1.0F, 1.0F, 1.0F, 1.0F)
         .uv(u, v)
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(packedLight)
         .normal(matrix3f, 0.0F, 1.0F, 0.0F)
         .endVertex();
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull ForestSpikeEntity pEntity) {
      return TEXTURE;
   }
}
