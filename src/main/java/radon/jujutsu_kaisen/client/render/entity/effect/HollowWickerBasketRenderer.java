package radon.jujutsu_kaisen.client.render.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.client.JJKRenderTypes;
import radon.jujutsu_kaisen.entity.HollowWickerBasketEntity;

public class HollowWickerBasketRenderer extends EntityRenderer<HollowWickerBasketEntity> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("jujutsu_kaisen", "textures/entity/hollow_wicker_basket.png");

   public HollowWickerBasketRenderer(Context ctx) {
      super(ctx);
   }

   public void render(HollowWickerBasketEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      poseStack.pushPose();
      poseStack.translate(0.0, entity.getBbHeight() * 0.9, 0.0);
      float radius = 1.25F;
      int stacks = 64;
      int slices = 64;
      float tileU = 4.0F;
      float tileV = 4.0F;
      VertexConsumer consumer = buffer.getBuffer(JJKRenderTypes.hollow_wicker_basket(TEXTURE));
      float[][][] positions = new float[65][65][3];
      float[][][] uvs = new float[65][65][2];

      for (int i = 0; i <= 64; i++) {
         float phi = (float) Math.PI * i / 64.0F;
         float v = i / 64.0F * 4.0F;

         for (int j = 0; j <= 64; j++) {
            float theta = (float)((Math.PI * 2) * j / 64.0);
            float u = j / 64.0F * 4.0F;
            float x = (float)(radius * Math.sin(phi) * Math.cos(theta));
            float y = (float)(radius * Math.cos(phi));
            float z = (float)(radius * Math.sin(phi) * Math.sin(theta));
            positions[i][j][0] = x;
            positions[i][j][1] = y;
            positions[i][j][2] = z;
            uvs[i][j][0] = u;
            uvs[i][j][1] = v;
         }
      }

      Pose pose = poseStack.last();
      Matrix4f posMat = pose.pose();
      Matrix3f normalMat = pose.normal();
      float alpha = 0.2F;

      for (int i = 0; i < 64; i++) {
         for (int j = 0; j < 64; j++) {
            float[] v1 = positions[i][j];
            float[] v2 = positions[i][j + 1];
            float[] v3 = positions[i + 1][j + 1];
            float[] v4 = positions[i + 1][j];
            float[] uv1 = uvs[i][j];
            float[] uv2 = uvs[i][j + 1];
            float[] uv3 = uvs[i + 1][j + 1];
            float[] uv4 = uvs[i + 1][j];
            addQuadDualSided(consumer, posMat, normalMat, v1, uv1, v2, uv2, v3, uv3, v4, uv4, 0.2F, packedLight, radius);
         }
      }

      poseStack.popPose();
      super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
   }

   private static void putVertex(VertexConsumer consumer, Matrix4f mat, Matrix3f normalMat, float[] pos, float[] uv, float[] normal, float alpha, int light) {
      consumer.vertex(mat, pos[0], pos[1], pos[2])
         .color(1.0F, 1.0F, 1.0F, alpha)
         .uv(uv[0], uv[1])
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(light)
         .normal(normalMat, normal[0], normal[1], normal[2])
         .endVertex();
   }

   private static void addQuadDualSided(
      VertexConsumer consumer,
      Matrix4f mat,
      Matrix3f normalMat,
      float[] v1,
      float[] uv1,
      float[] v2,
      float[] uv2,
      float[] v3,
      float[] uv3,
      float[] v4,
      float[] uv4,
      float alpha,
      int light,
      float radius
   ) {
      float[] n1 = normalize(v1, radius);
      float[] n2 = normalize(v2, radius);
      float[] n3 = normalize(v3, radius);
      float[] n4 = normalize(v4, radius);
      putVertex(consumer, mat, normalMat, v1, uv1, n1, alpha, light);
      putVertex(consumer, mat, normalMat, v2, uv2, n2, alpha, light);
      putVertex(consumer, mat, normalMat, v3, uv3, n3, alpha, light);
      putVertex(consumer, mat, normalMat, v4, uv4, n4, alpha, light);
      float shrink = 0.999F;
      float[] v1i = new float[]{v1[0] * 0.999F, v1[1] * 0.999F, v1[2] * 0.999F};
      float[] v2i = new float[]{v2[0] * 0.999F, v2[1] * 0.999F, v2[2] * 0.999F};
      float[] v3i = new float[]{v3[0] * 0.999F, v3[1] * 0.999F, v3[2] * 0.999F};
      float[] v4i = new float[]{v4[0] * 0.999F, v4[1] * 0.999F, v4[2] * 0.999F};
      putVertex(consumer, mat, normalMat, v4i, uv4, negate(n4), alpha, light);
      putVertex(consumer, mat, normalMat, v3i, uv3, negate(n3), alpha, light);
      putVertex(consumer, mat, normalMat, v2i, uv2, negate(n2), alpha, light);
      putVertex(consumer, mat, normalMat, v1i, uv1, negate(n1), alpha, light);
   }

   private static float[] normalize(float[] pos, float radius) {
      float x = pos[0];
      float y = pos[1];
      float z = pos[2];
      float len = (float)Math.sqrt(x * x + y * y + z * z);
      return len == 0.0F ? new float[]{0.0F, 1.0F, 0.0F} : new float[]{x / len, y / len, z / len};
   }

   private static float[] negate(float[] v) {
      return new float[]{-v[0], -v[1], -v[2]};
   }

   public ResourceLocation getTextureLocation(HollowWickerBasketEntity entity) {
      return TEXTURE;
   }
}
