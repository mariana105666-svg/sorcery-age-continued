package radon.jujutsu_kaisen.client.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.entity.projectile.EelGrappleProjectile;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EelGrappleRenderer extends GeoEntityRenderer<EelGrappleProjectile> {
   public EelGrappleRenderer(Context renderManager) {
      super(renderManager, new DefaultedEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "eel_grapple")));
   }

   public void preRender(
      PoseStack poseStack,
      EelGrappleProjectile animatable,
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
      if (animatable.getOwner() instanceof LivingEntity owner) {
         poseStack.translate(0.0F, animatable.getBbHeight() / 2.0F, 0.0F);
         Vec3 var22 = getPosition(owner, owner.getBbHeight() / 2.0F, partialTick);
         Vec3 projectilePos = getPosition(animatable, animatable.getBbHeight() / 2.0F, partialTick);
         Vec3 relative = var22.subtract(projectilePos);
         float f0 = (float)relative.length();
         relative = relative.normalize();
         float f1 = (float)Math.acos(relative.y);
         float f2 = (float)Math.atan2(relative.z, relative.x);
         poseStack.mulPose(Axis.YP.rotationDegrees(((float) (Math.PI / 2) - f2) * (180.0F / (float)Math.PI) - 180.0F));
         poseStack.mulPose(Axis.XN.rotationDegrees(f1 * (180.0F / (float)Math.PI) - 90.0F));
         GeoBone tail = (GeoBone)this.model.getBone("tail").orElseThrow();
         tail.setScaleZ(f0);
         super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
      }
   }

   private static Vec3 getPosition(Entity entity, double yOffset, float pPartialTick) {
      double d0 = entity.xOld + (entity.getX() - entity.xOld) * pPartialTick;
      double d1 = yOffset + entity.yOld + (entity.getY() - entity.yOld) * pPartialTick;
      double d2 = entity.zOld + (entity.getZ() - entity.zOld) * pPartialTick;
      return new Vec3(d0, d1, d2);
   }
}
