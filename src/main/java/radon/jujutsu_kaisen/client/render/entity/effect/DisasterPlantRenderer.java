package radon.jujutsu_kaisen.client.render.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import radon.jujutsu_kaisen.entity.effect.DisasterPlantEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DisasterPlantRenderer extends GeoEntityRenderer<DisasterPlantEntity> {
   public DisasterPlantRenderer(Context renderManager) {
      super(renderManager, new DefaultedEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "disaster_plant")));
   }

   public void preRender(
      PoseStack poseStack,
      DisasterPlantEntity animatable,
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
      poseStack.mulPose(Axis.YN.rotationDegrees(yaw));
      poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
      GeoBone parent = (GeoBone)model.getBone("cursed_buds").orElseThrow();
      List<GeoBone> buds = parent.getChildBones();

      for (int i = 0; i < 15; i++) {
         buds.get(i).setHidden(i < 15 - animatable.getBudCount());
      }

      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
   }
}
