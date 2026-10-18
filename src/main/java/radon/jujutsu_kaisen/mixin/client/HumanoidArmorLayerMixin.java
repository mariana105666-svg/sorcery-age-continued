package radon.jujutsu_kaisen.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;

@Mixin({HumanoidArmorLayer.class})
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {
   @Shadow
   protected abstract void setPartVisibility(A var1, EquipmentSlot var2);

   @Shadow
   protected abstract A getArmorModel(EquipmentSlot var1);

   @Shadow
   protected abstract Model getArmorModelHook(T var1, ItemStack var2, EquipmentSlot var3, A var4);

   @Shadow
   protected abstract boolean usesInnerModel(EquipmentSlot var1);

   @Shadow
   public abstract ResourceLocation getArmorResource(Entity var1, ItemStack var2, EquipmentSlot var3, @Nullable String var4);

   @Shadow
   protected abstract void renderModel(
      PoseStack var1, MultiBufferSource var2, int var3, ArmorItem var4, Model var5, boolean var6, float var7, float var8, float var9, ResourceLocation var10
   );

   @Inject(
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V"},
      at = {@At("HEAD")}
   )
   public void render(
      PoseStack pPoseStack,
      MultiBufferSource pBuffer,
      int pPackedLight,
      T pLivingEntity,
      float pLimbSwing,
      float pLimbSwingAmount,
      float pPartialTicks,
      float pAgeInTicks,
      float pNetHeadYaw,
      float pHeadPitch,
      CallbackInfo ci
   ) {
      ClientVisualHandler.ClientData data = ClientVisualHandler.get(pLivingEntity);
      if (data != null) {
         Map<Ability, ITransformation.Part> parts = new HashMap<>();

         for (Ability ability : data.toggled) {
            if (ability instanceof ITransformation transformation && transformation.isReplacement()) {
               parts.put(ability, transformation.getBodyPart());
            }
         }

         Set<EquipmentSlot> hidden = new HashSet<>();

         for (Ability abilityx : data.toggled) {
            if (abilityx instanceof ITransformation transformation && transformation.isReplacement()) {
               for (Entry<Ability, ITransformation.Part> entry : parts.entrySet()) {
                  if (entry.getKey() != abilityx && (entry.getValue() == ITransformation.Part.BODY || entry.getValue() == transformation.getBodyPart())) {
                     hidden.add(transformation.getBodyPart().getSlot());
                     break;
                  }
               }
            }
         }

         for (Ability abilityxx : data.toggled) {
            if (abilityxx instanceof ITransformation transformation) {
               for (EquipmentSlot slot : EquipmentSlot.values()) {
                  if (transformation.getBodyPart().getSlot() == slot || !hidden.contains(slot)) {
                     A model = this.getArmorModel(slot);
                     ((HumanoidModel)((RenderLayer)(Object)this).getParentModel()).copyPropertiesTo(model);
                     this.setPartVisibility(model, slot);
                     pPoseStack.pushPose();
                     if ((transformation.getBodyPart() == ITransformation.Part.RIGHT_ARM || transformation.getBodyPart() == ITransformation.Part.LEFT_ARM)
                        && ((RenderLayer)(Object)this).getParentModel() instanceof PlayerModel<?> player
                        && ((IPlayerModelAccessor)player).getSlimAccessor()) {
                        float translation = transformation.getSlimTranslation();
                        pPoseStack.translate(transformation.getBodyPart() == ITransformation.Part.RIGHT_ARM ? translation : -translation, 0.0F, 0.0F);
                     }

                     Model armor = this.getArmorModelHook(pLivingEntity, transformation.getItem().getDefaultInstance(), slot, model);
                     this.renderModel(
                        pPoseStack,
                        pBuffer,
                        pPackedLight,
                        (ArmorItem)transformation.getItem().asItem(),
                        armor,
                        this.usesInnerModel(slot),
                        1.0F,
                        1.0F,
                        1.0F,
                        this.getArmorResource(pLivingEntity, transformation.getItem().getDefaultInstance(), slot, null)
                     );
                     pPoseStack.popPose();
                  }
               }
            }
         }
      }
   }

   @Inject(
      method = {"renderArmorPiece"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;setPartVisibility(Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/world/entity/EquipmentSlot;)V",
         shift = Shift.AFTER
      )}
   )
   public void renderArmorPiece(
      PoseStack pPoseStack, MultiBufferSource pBuffer, T pLivingEntity, EquipmentSlot pSlot, int pPackedLight, A pModel, CallbackInfo ci
   ) {
      ClientVisualHandler.ClientData data = ClientVisualHandler.get(pLivingEntity);
      if (data != null) {
         for (Ability ability : data.toggled) {
            if (ability instanceof ITransformation transformation && transformation.isReplacement()) {
               switch (transformation.getBodyPart()) {
                  case HEAD:
                     pModel.head.visible = false;
                     pModel.hat.visible = false;
                     break;
                  case BODY:
                     pModel.setAllVisible(false);
                     break;
                  case RIGHT_ARM:
                     pModel.rightArm.visible = false;
                     break;
                  case LEFT_ARM:
                     pModel.leftArm.visible = false;
                     break;
                  case LEGS:
                     pModel.rightLeg.visible = false;
                     pModel.leftLeg.visible = false;
               }
            }
         }
      }
   }
}
