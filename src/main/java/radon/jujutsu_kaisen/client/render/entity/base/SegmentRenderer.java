package radon.jujutsu_kaisen.client.render.entity.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.entity.base.JJKPartEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SegmentRenderer<T extends JJKPartEntity<?> & GeoAnimatable> extends GeoEntityRenderer<T> {
   public SegmentRenderer(Context renderManager, GeoModel<T> model) {
      super(renderManager, model);
   }

   public int getPackedOverlay(T animatable, float u) {
      return !(animatable.getParent() instanceof LivingEntity entity)
         ? OverlayTexture.NO_OVERLAY
         : OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
   }

   public void preRender(
      PoseStack poseStack,
      T animatable,
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
      float diff = animatable.getYRot() - animatable.yRotO;
      if (diff > 180.0F) {
         diff -= 360.0F;
      } else if (diff < -180.0F) {
         diff += 360.0F;
      }

      float yaw = animatable.yRotO + diff * partialTick;
      float pitch = Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot());
      poseStack.mulPose(Axis.YP.rotationDegrees(360.0F - yaw));
      poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
   }
}
