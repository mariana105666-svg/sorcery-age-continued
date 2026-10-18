package radon.jujutsu_kaisen.client.render.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.entity.effect.JacobsLadderEntity;

public class JacobsLadderRenderer extends EntityRenderer<JacobsLadderEntity> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("jujutsu_kaisen", "textures/entity/sky_strike.png");
   private static final float TEXTURE_WIDTH = 208.0F;
   private static final float TEXTURE_HEIGHT = 32.0F;
   private static final float BEAM_MIN_U = 0.84615386F;
   private static final float BEAM_MAX_U = 1.0F;
   private static final float PIXEL_SCALE = 0.0625F;
   private static final int MAX_HEIGHT = 256;
   private static final float DRAW_FADE_IN_RATE = 2.0F;
   private static final float DRAW_FADE_IN_POINT = 0.5F;
   private static final float DRAW_OPACITY_MULTIPLIER = 0.7F;
   private static final float RING_RADIUS = 1.6F;
   private static final int RING_FRAME_SIZE = 16;
   private static final int RING_FRAME_COUNT = 6;
   private static final int BEAM_FRAME_COUNT = 31;
   private static final float BEAM_DRAW_START_RADIUS = 2.0F;
   private static final float BEAM_DRAW_END_RADIUS = 0.25F;
   private static final float BEAM_STRIKE_RADIUS = 1.0F;

   public JacobsLadderRenderer(Context pContext) {
      super(pContext);
   }

   private static void vertex(Matrix4f matrix4f, Pose pose, VertexConsumer consumer, float x, float y, float z, float u, float v, float alpha, int packedLight) {
      consumer.vertex(matrix4f, x, y, z)
         .color(1.0F, 1.0F, 1.0F, alpha)
         .uv(u, v)
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(packedLight)
         .normal(pose.normal(), 0.0F, 1.0F, 0.0F)
         .endVertex();
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull JacobsLadderEntity pEntity) {
      return TEXTURE;
   }

   public void render(
      JacobsLadderEntity pEntity, float pEntityYaw, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight
   ) {
      float maxY = (float)(256.0 - pEntity.getY());
      if (!(maxY < 0.0F)) {
         boolean isStriking = pEntity.isStriking(pPartialTick);
         pPoseStack.pushPose();
         float scale = Math.max(1.5F, pEntity.getPower() * 0.2F);
         pPoseStack.scale(scale, scale, scale);
         VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(pEntity)));
         if (isStriking && pEntity.isAlive()) {
            this.drawStrike(pEntity, maxY, pPartialTick, pPoseStack, consumer, pPackedLight);
         }

         pPoseStack.popPose();
      }
   }

   private void drawStrike(JacobsLadderEntity entity, float maxY, float partialTicks, PoseStack poseStack, VertexConsumer builder, int packedLightIn) {
      float drawTime = entity.getStrikeDrawTime(partialTicks);
      float strikeTime = entity.getStrikeDamageTime(partialTicks);
      boolean drawing = entity.isStrikeDrawing(partialTicks);
      float opacity = drawing && drawTime < 0.5F ? drawTime * 2.0F : 1.0F;
      if (drawing) {
         opacity *= 0.7F;
      }

      this.drawRing(drawing, drawTime, strikeTime, opacity, poseStack, builder, packedLightIn);
      poseStack.mulPose(Axis.YP.rotationDegrees(-Minecraft.getInstance().gameRenderer.getMainCamera().getYRot()));
      this.drawBeam(drawing, drawTime, strikeTime, opacity, maxY, poseStack, builder, packedLightIn);
   }

   private void drawRing(boolean drawing, float drawTime, float strikeTime, float opacity, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
      this.drawRing(drawing, drawTime, strikeTime, opacity, poseStack, consumer, packedLight, 1.6F);
   }

   private void drawRing(
      boolean drawing, float drawTime, float strikeTime, float opacity, PoseStack poseStack, VertexConsumer consumer, int packedLight, float radius
   ) {
      int frame = (int)((drawing ? drawTime : strikeTime) * 7.0F);
      if (frame > 6) {
         frame = 6;
      }

      float minU = frame * 16 / 208.0F;
      float maxU = minU + 0.07692308F;
      float minV = drawing ? 0.0F : 0.5F;
      float maxV = minV + 0.5F;
      float offset = 0.0625F * radius * (frame % 2);
      Pose pose = poseStack.last();
      Matrix4f matrix4f = pose.pose();
      vertex(matrix4f, pose, consumer, -radius + offset, 0.0F, -radius + offset, minU, minV, opacity, packedLight);
      vertex(matrix4f, pose, consumer, -radius + offset, 0.0F, radius + offset, minU, maxV, opacity, packedLight);
      vertex(matrix4f, pose, consumer, radius + offset, 0.0F, radius + offset, maxU, maxV, opacity, packedLight);
      vertex(matrix4f, pose, consumer, radius + offset, 0.0F, -radius + offset, maxU, minV, opacity, packedLight);
   }

   private void drawBeam(
      boolean drawing, float drawTime, float strikeTime, float opacity, float maxY, PoseStack poseStack, VertexConsumer builder, int packedLight
   ) {
      int frame = drawing ? 0 : (int)(strikeTime * 32.0F);
      if (frame > 31) {
         frame = 31;
      }

      float radius = 1.0F;
      if (drawing) {
         radius = -1.75F * drawTime + 2.0F;
      }

      float minV = frame / 32.0F;
      float maxV = (frame + 1.0F) / 32.0F;
      Pose pose = poseStack.last();
      Matrix4f matrix4f = pose.pose();
      vertex(matrix4f, pose, builder, -radius, 0.0F, 0.0F, 0.84615386F, minV, opacity, packedLight);
      vertex(matrix4f, pose, builder, -radius, maxY, 0.0F, 0.84615386F, maxV, opacity, packedLight);
      vertex(matrix4f, pose, builder, radius, maxY, 0.0F, 1.0F, maxV, opacity, packedLight);
      vertex(matrix4f, pose, builder, radius, 0.0F, 0.0F, 1.0F, minV, opacity, packedLight);
   }
}
