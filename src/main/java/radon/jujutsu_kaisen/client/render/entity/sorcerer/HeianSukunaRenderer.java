package radon.jujutsu_kaisen.client.render.entity.sorcerer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import radon.jujutsu_kaisen.client.layer.HeianSukunaEyesLayer;
import radon.jujutsu_kaisen.client.layer.HeianSukunaMouthLayer;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.sorcerer.HeianSukunaEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class HeianSukunaRenderer extends GeoEntityRenderer<HeianSukunaEntity> {
   private static final String LEFT_HAND = "left_hand_1";
   private static final String RIGHT_HAND = "right_hand_1";
   protected ItemStack mainHandItem;
   protected ItemStack offhandItem;

   public HeianSukunaRenderer(Context renderManager) {
      super(renderManager, new DefaultedTurnHeadEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "heian_sukuna")));
      this.addRenderLayer(new HeianSukunaEyesLayer(this));
      this.addRenderLayer(
         new BlockAndItemGeoLayer<HeianSukunaEntity>(this) {
            @Nullable
            protected ItemStack getStackForBone(GeoBone bone, HeianSukunaEntity animatable) {
               String var3 = bone.getName();

               return switch (var3) {
                  case "left_hand_1" -> animatable.isLeftHanded() ? HeianSukunaRenderer.this.mainHandItem : HeianSukunaRenderer.this.offhandItem;
                  case "right_hand_1" -> animatable.isLeftHanded() ? HeianSukunaRenderer.this.offhandItem : HeianSukunaRenderer.this.mainHandItem;
                  default -> null;
               };
            }

            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, HeianSukunaEntity animatable) {
               String var4 = bone.getName();

               return switch (var4) {
                  case "left_hand_1", "right_hand_1" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                  default -> ItemDisplayContext.NONE;
               };
            }

            protected void renderStackForBone(
               PoseStack poseStack,
               GeoBone bone,
               ItemStack stack,
               HeianSukunaEntity animatable,
               MultiBufferSource bufferSource,
               float partialTick,
               int packedLight,
               int packedOverlay
            ) {
               if (stack == HeianSukunaRenderer.this.mainHandItem) {
                  poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                  if (stack.getItem() instanceof ShieldItem) {
                     poseStack.translate(0.0F, 0.125F, -0.25F);
                  }
               } else if (stack == HeianSukunaRenderer.this.offhandItem) {
                  poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                  if (stack.getItem() instanceof ShieldItem) {
                     poseStack.translate(0.0F, 0.125F, 0.25F);
                     poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                  }
               }

               super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
         }
      );
      this.addRenderLayer(new HeianSukunaMouthLayer(this));
   }

   public void preRender(
      PoseStack poseStack,
      HeianSukunaEntity animatable,
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
      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
      this.mainHandItem = animatable.getMainHandItem();
      this.offhandItem = animatable.getOffhandItem();
   }
}
