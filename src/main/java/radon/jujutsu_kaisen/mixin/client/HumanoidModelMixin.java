package radon.jujutsu_kaisen.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.client.visual.overlay.PerfectBodyOverlay;

@Mixin({HumanoidModel.class})
public class HumanoidModelMixin<T extends LivingEntity> {
   @Shadow
   @Final
   public ModelPart leftArm;
   @Shadow
   @Final
   public ModelPart rightArm;
   @Shadow
   public ArmPose rightArmPose;
   @Shadow
   public ArmPose leftArmPose;

   @Inject(
      method = {"setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"},
      at = {@At("TAIL")}
   )
   public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci) {
      ClientVisualHandler.ClientData data = ClientVisualHandler.get(pEntity);
      if (data != null) {
         if (pEntity instanceof LivingEntity) {
            if (PerfectBodyOverlay.shouldRenderExtraArms(pEntity, data)) {
               if (this.rightArmPose == ArmPose.EMPTY || this.rightArmPose == ArmPose.ITEM) {
                  this.rightArm.xRot = this.rightArm.xRot + (this.rightArm.xRot * 0.5F - (float) (Math.PI / 10));
                  this.rightArm.zRot = this.rightArm.zRot - (this.rightArm.zRot * 0.5F - (float) (Math.PI / 10));
               }

               if (this.leftArmPose == ArmPose.EMPTY || this.leftArmPose == ArmPose.ITEM) {
                  this.leftArm.xRot = this.leftArm.xRot + (this.leftArm.xRot * 0.5F - (float) (Math.PI / 10));
                  this.leftArm.zRot = this.leftArm.zRot + (this.leftArm.zRot * 0.5F - (float) (Math.PI / 10));
               }
            }
         }
      }
   }
}
