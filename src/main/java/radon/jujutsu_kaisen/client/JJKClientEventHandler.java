package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.AddLayers;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.InputEvent.InteractionKeyMappingTriggered;
import net.minecraftforge.client.event.RegisterColorHandlersEvent.Block;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.VeilBlock;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.client.gui.overlay.BlinkOverlay;
import radon.jujutsu_kaisen.client.gui.overlay.CursedEnergyOverlay;
import radon.jujutsu_kaisen.client.gui.overlay.DomainClashOverlay;
import radon.jujutsu_kaisen.client.gui.overlay.ScreenFlashOverlay;
import radon.jujutsu_kaisen.client.gui.overlay.SixEyesOverlay;
import radon.jujutsu_kaisen.client.layer.JJKOverlayLayer;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.client.model.entity.AoiTodoModel;
import radon.jujutsu_kaisen.client.model.entity.CursedEnergyBlastModel;
import radon.jujutsu_kaisen.client.model.entity.HajimeKashimoModel;
import radon.jujutsu_kaisen.client.model.entity.MakiZeninModel;
import radon.jujutsu_kaisen.client.model.entity.MegumiFushiguroModel;
import radon.jujutsu_kaisen.client.model.entity.NaoyaZeninModel;
import radon.jujutsu_kaisen.client.model.entity.RyuIshigoriModel;
import radon.jujutsu_kaisen.client.model.entity.SatoruGojoModel;
import radon.jujutsu_kaisen.client.model.entity.SuguruGetoModel;
import radon.jujutsu_kaisen.client.model.entity.TogeInumakiModel;
import radon.jujutsu_kaisen.client.model.entity.TojiFushiguroModel;
import radon.jujutsu_kaisen.client.model.entity.YujiItadoriModel;
import radon.jujutsu_kaisen.client.model.entity.YutaOkkotsuModel;
import radon.jujutsu_kaisen.client.particle.BetterSmokeParticle;
import radon.jujutsu_kaisen.client.particle.BlackFlashParticle;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.CursedSpeechParticle;
import radon.jujutsu_kaisen.client.particle.DismantleParticle;
import radon.jujutsu_kaisen.client.particle.EmittingLightningParticle;
import radon.jujutsu_kaisen.client.particle.FireParticle;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.MirageParticle;
import radon.jujutsu_kaisen.client.particle.ProjectionParticle;
import radon.jujutsu_kaisen.client.particle.SlashParticle;
import radon.jujutsu_kaisen.client.particle.SlicedEntityParticle;
import radon.jujutsu_kaisen.client.particle.SpiderwebParticle;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.client.particle.VaporParticle;
import radon.jujutsu_kaisen.client.render.EmptyRenderer;
import radon.jujutsu_kaisen.client.render.block.DisplayCaseRenderer;
import radon.jujutsu_kaisen.client.render.block.DomainSkyBlockRenderer;
import radon.jujutsu_kaisen.client.render.block.FakeSkyRenderer;
import radon.jujutsu_kaisen.client.render.entity.ChimeraShadowGardenRenderer;
import radon.jujutsu_kaisen.client.render.entity.EmberInsectsFlightRenderer;
import radon.jujutsu_kaisen.client.render.entity.LimboCloneRenderer;
import radon.jujutsu_kaisen.client.render.entity.MalevolentShrineClosedRenderer;
import radon.jujutsu_kaisen.client.render.entity.MalevolentShrineRenderer;
import radon.jujutsu_kaisen.client.render.entity.NyoiStaffRenderer;
import radon.jujutsu_kaisen.client.render.entity.SelfEmbodimentOfPerfectionRenderer;
import radon.jujutsu_kaisen.client.render.entity.TimeCellMoonPalaceRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.AbsorbedPlayerRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.BirdCurseRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.CyclopsCurseRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.DagonRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.DinoCurseRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.FelineCurseRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.FingerBearerRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.FishCurseRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.FuglyCurseRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.HanamiRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.JogoRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.JogoatRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.RainbowDragonHeadRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.RikaRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.RugbyFieldCurseRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.ScissorCurseRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.WormCurseHeadRenderer;
import radon.jujutsu_kaisen.client.render.entity.curse.ZombaCurseRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.AirFrameRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.BlackFlashRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.BodyRepelRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.CursedEnergyBlastRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.CursedEnergyBombRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.DisasterPlantRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.ElectricBeamRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.FireBeamRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.ForestDashRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.ForestRootsRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.ForestSpikeRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.ForestWaveRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.GraniteBlastRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.HollowPurpleExplosionRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.HollowWickerBasketRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.JacobsLadderRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.LightningRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.MaximumOutputJacobsLadderRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.MeteorRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.PiercingWaterRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.ProjectionFrameRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.PureLoveRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.ScissorRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.SkyStrikeRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.SpiderwebRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.VolcanoRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.WaterTorrentRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.WaterballRenderer;
import radon.jujutsu_kaisen.client.render.entity.effect.WoodSegmentRenderer;
import radon.jujutsu_kaisen.client.render.entity.idle_transfiguration.PolymorphicSoulIsomerRenderer;
import radon.jujutsu_kaisen.client.render.entity.idle_transfiguration.TransfiguredSoulLargeRenderer;
import radon.jujutsu_kaisen.client.render.entity.idle_transfiguration.TransfiguredSoulNormalRenderer;
import radon.jujutsu_kaisen.client.render.entity.idle_transfiguration.TransfiguredSoulSmallRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.CursedBudRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.DismantleRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.EelGrappleRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.EelShikigamiRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.EmberInsectRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.FilmGaugeRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.FireArrowRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.FireballRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.LavaRockRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.MaximumUzumakiRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.MiniUzumakiRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.PiranhaShikigamiRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.RedRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.SharkShikigamiRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.ThrownChainProjectileRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.ToadTongueRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.TransfiguredSoulRenderer;
import radon.jujutsu_kaisen.client.render.entity.projectile.WorldSlashRenderer;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.AoiTodoRenderer;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.HajimeKashimoRenderer;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.HeianSukunaRenderer;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.MakiZeninRenderer;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.MegumiFushiguroRenderer;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.NaoyaZeninRenderer;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.RyuIshigoriRenderer;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.SatoruGojoRenderer;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.SuguruGetoRenderer;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.SukunaRenderer;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.TogeInumakiRenderer;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.TojiFushiguroRenderer;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.YujiItadoriRenderer;
import radon.jujutsu_kaisen.client.render.entity.sorcerer.YutaOkkotsuRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.AgitoRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.DivineDogRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.DivineDogTotalityRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.GreatSerpentHeadRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.MahoragaRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.MaxElephantRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.NueRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.NueTotalityRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.PiercingBullRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.RabbitEscapeRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.ToadRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.TranquilDeerRenderer;
import radon.jujutsu_kaisen.client.render.entity.ten_shadows.WheelRenderer;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.NyoiStaffEntity;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnnaEntity;
import radon.jujutsu_kaisen.mixin.client.IItemInHandRendererAccessor;
import radon.jujutsu_kaisen.mixin.client.IPlayerModelAccessor;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.AbilityRightClickC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.CommandableTargetC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.KuchisakeOnnaAnswerC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.NyoiStaffSummonLightningC2SPacket;
import radon.jujutsu_kaisen.tags.JJKItemTags;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class JJKClientEventHandler {
   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE,
      value = {Dist.CLIENT}
   )
   public static class JJKClientEventHandlerForgeEvents {
      @SubscribeEvent
      public static <T extends Item & GeoItem> void onRenderHand(RenderHandEvent event) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.player != null && mc.player.getItemInHand(event.getHand()).isEmpty()) {
            if (mc.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
               ISorcererData cap = (ISorcererData)mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               boolean translated = false;

               for (Ability ability : cap.getToggled()) {
                  if (ability instanceof ITransformation transformation) {
                     HumanoidArm arm = event.getHand() == InteractionHand.MAIN_HAND && mc.player.getMainArm() == HumanoidArm.RIGHT
                        ? HumanoidArm.RIGHT
                        : HumanoidArm.LEFT;
                     if (transformation.getBodyPart() == ITransformation.Part.RIGHT_ARM && arm == HumanoidArm.RIGHT
                        || transformation.getBodyPart() == ITransformation.Part.LEFT_ARM && arm == HumanoidArm.LEFT) {
                        PlayerRenderer renderer = (PlayerRenderer)mc.getEntityRenderDispatcher().getRenderer(mc.player);
                        PlayerModel<AbstractClientPlayer> model = (PlayerModel<AbstractClientPlayer>)renderer.getModel();
                        GeoArmorRenderer<T> armor = (GeoArmorRenderer<T>)ForgeHooksClient.getArmorModel(
                           mc.player, transformation.getItem().getDefaultInstance(), EquipmentSlot.CHEST, model
                        );
                        VertexConsumer consumer = event.getMultiBufferSource()
                           .getBuffer(RenderType.armorCutoutNoCull(armor.getTextureLocation((T)transformation.getItem())));
                        float f = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
                        if (!translated) {
                           model.attackTime = mc.player.getAttackAnim(event.getPartialTick());
                           model.riding = mc.player.isPassenger() && mc.player.getVehicle() != null && mc.player.getVehicle().shouldRiderSit();
                           model.young = mc.player.isBaby();
                           if (model.attackTime == 0.0F) {
                              model.rightArmPose = ArmPose.EMPTY;
                              model.leftArmPose = ArmPose.EMPTY;
                              model.setupAnim(mc.player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                           }

                           event.getPoseStack().translate(f * 0.125F, -0.125F, 0.0F);
                           translated = true;
                        }

                        float swing = arm == HumanoidArm.RIGHT ? mc.player.getAttackAnim(event.getPartialTick()) : 0.0F;
                        float equip = 1.0F
                           - Mth.lerp(
                              event.getPartialTick(),
                              ((IItemInHandRendererAccessor)mc.gameRenderer.itemInHandRenderer).getOMainHandHeightAccessor(),
                              ((IItemInHandRendererAccessor)mc.gameRenderer.itemInHandRenderer).getMainHandHeightAccessor()
                           );
                        IClientItemExtensions extensions = IClientItemExtensions.of(transformation.getItem());
                        boolean custom = extensions.applyForgeHandTransform(
                           event.getPoseStack(), mc.player, arm, transformation.getItem().getDefaultInstance(), event.getPartialTick(), equip, swing
                        );
                        event.getPoseStack().pushPose();
                        if (!custom) {
                           float f1 = Mth.sqrt(swing);
                           float f2 = -0.3F * Mth.sin(f1 * (float) Math.PI);
                           float f3 = 0.4F * Mth.sin(f1 * (float) (Math.PI * 2));
                           float f4 = -0.4F * Mth.sin(swing * (float) Math.PI);
                           event.getPoseStack().translate(f * (f2 + 0.64000005F), f3 - 0.6F + equip * -0.6F, f4 - 0.71999997F);
                           event.getPoseStack().mulPose(Axis.YP.rotationDegrees(f * 45.0F));
                           float f5 = Mth.sin(swing * swing * (float) Math.PI);
                           float f6 = Mth.sin(f1 * (float) Math.PI);
                           event.getPoseStack().mulPose(Axis.YP.rotationDegrees(f * f6 * 70.0F));
                           event.getPoseStack().mulPose(Axis.ZP.rotationDegrees(f * f5 * -20.0F));
                           event.getPoseStack().translate(f * -1.0F, 3.6F, 3.5F);
                           event.getPoseStack().mulPose(Axis.ZP.rotationDegrees(f * 120.0F));
                           event.getPoseStack().mulPose(Axis.XP.rotationDegrees(200.0F));
                           event.getPoseStack().mulPose(Axis.YP.rotationDegrees(f * -135.0F));
                           event.getPoseStack().translate(f * 5.6F, 0.0F, 0.0F);
                           if (((IPlayerModelAccessor)model).getSlimAccessor()) {
                              event.getPoseStack().translate(f * 0.0546875F, 0.0F, 0.0F);
                           }
                        }

                        armor.renderToBuffer(event.getPoseStack(), consumer, event.getPackedLight(), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                        event.getPoseStack().popPose();
                        event.setCanceled(custom);
                     }
                  }
               }
            }
         }
      }

      @SubscribeEvent
      public static void onPlayerMouseClick(InteractionKeyMappingTriggered event) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.player != null) {
            MobEffectInstance instance = mc.player.getEffect((MobEffect)JJKEffects.STUN.get());
            ISorcererData cap = (ISorcererData)mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
            if (cap != null) {
               if (cap.hasToggled((Ability)JJKAbilities.HOLLOW_WICKER_BASKET.get()) && !cap.hasTrait(Trait.PERFECT_BODY)
                  || instance != null && instance.getAmplifier() > 0
                  || mc.player.hasEffect((MobEffect)JJKEffects.UNLIMITED_VOID.get())) {
                  event.setCanceled(true);
                  event.setSwingHand(false);
               }
            }
         }
      }

      @SubscribeEvent
      public static void onPlayerMouseClick(net.minecraftforge.client.event.InputEvent.MouseButton.Pre event) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.player != null) {
            if (event.getAction() == 1 && event.getButton() == 1) {
               if (mc.options.keyShift.isDown()) {
                  if (RotationUtil.getLookAtHit(mc.player, 64.0) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target) {
                     PacketHandler.sendToServer(new CommandableTargetC2SPacket(target.getUUID()));
                  }
               } else {
                  NyoiStaffEntity target = RotationUtil.getNyoiLookAt(mc.player, 100.0);
                  if (target != null) {
                     PacketHandler.sendToServer(new NyoiStaffSummonLightningC2SPacket(target.getUUID()));
                  } else {
                     ISorcererData cap = (ISorcererData)mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                     cap.onRightClick(mc.player);
                     PacketHandler.sendToServer(new AbilityRightClickC2SPacket());
                  }
               }
            }
         }
      }

      @SubscribeEvent
      public static void onRenderLevelStage(RenderLevelStageEvent event) {
         if (event.getStage() == Stage.AFTER_TRIPWIRE_BLOCKS) {
            FakeSkyHandler.renderSky(event.getPoseStack(), event.getPartialTick(), event.getProjectionMatrix());
         }
      }

      @SubscribeEvent
      public static void onRenderLivingPre(Pre<?, ?> event) {
         Minecraft mc = Minecraft.getInstance();

         assert mc.player != null;

         LivingEntity target = event.getEntity();
         ClientVisualHandler.ClientData data = ClientVisualHandler.get(target);
         if (data != null) {
            if (data.traits.contains(Trait.HEAVENLY_RESTRICTION)) {
               if (!(Minecraft.getInstance().getCameraEntity() instanceof LivingEntity viewer)) {
                  return;
               }

               if (JJKAbilities.hasTrait(viewer, Trait.HEAVENLY_RESTRICTION)) {
                  return;
               }

               if (target != viewer) {
                  Vec3 look = RotationUtil.getTargetAdjustedLookAngle(viewer);
                  Vec3 start = viewer.getEyePosition();
                  Vec3 result = target.getEyePosition().subtract(start);
                  double angle = Math.acos(look.normalize().dot(result.normalize()));
                  double threshold = 1.0;
                  if (!target.getItemInHand(InteractionHand.MAIN_HAND).is(JJKItemTags.CURSED_TOOL)
                     || !target.getItemInHand(InteractionHand.OFF_HAND).is(JJKItemTags.CURSED_TOOL)) {
                     threshold = 0.9;
                  }

                  if (angle > threshold) {
                     event.setCanceled(true);
                  }
               }
            }
         }
      }

      @SubscribeEvent
      public static void onClientChat(ClientChatEvent event) {
         Minecraft mc = Minecraft.getInstance();

         assert mc.level != null && mc.player != null;

         for (KuchisakeOnnaEntity curse : mc.level.getEntitiesOfClass(KuchisakeOnnaEntity.class, AABB.ofSize(mc.player.position(), 64.0, 64.0, 64.0))) {
            curse.getCurrent().ifPresent(identifier -> {
               event.setCanceled(true);
               mc.player.sendSystemMessage(Component.literal(String.format("<%s> %s", mc.player.getName().getString(), event.getMessage())));
               PacketHandler.sendToServer(new KuchisakeOnnaAnswerC2SPacket(curse.getUUID()));
            });
         }
      }
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.MOD,
      value = {Dist.CLIENT}
   )
   public static class JJKClientEventHandlerModEvents {
      @SubscribeEvent
      public static void onRegisterBlockColors(Block event) {
         event.register(
            (pState, pLevel, pPos, pTintIndex) -> ((DyeColor)pState.getValue(VeilBlock.COLOR)).getMapColor().col,
            new net.minecraft.world.level.block.Block[]{(net.minecraft.world.level.block.Block)JJKBlocks.VEIL.get()}
         );
         event.register(
            (pState, pLevel, pPos, pTintIndex) -> GrassColor.getDefaultColor(),
            new net.minecraft.world.level.block.Block[]{(net.minecraft.world.level.block.Block)JJKBlocks.SHINING_SEA_OF_FLOWERS_FLOOR.get()}
         );
      }

      @SubscribeEvent
      public static void onRegisterPlayerLayers(AddLayers event) {
         if (event.getSkin("default") instanceof PlayerRenderer renderer) {
            renderer.addLayer(new JJKOverlayLayer(renderer));
         }

         if (event.getSkin("slim") instanceof PlayerRenderer renderer) {
            renderer.addLayer(new JJKOverlayLayer(renderer));
         }
      }

      @SubscribeEvent
      public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
         event.register(JJKKeys.ACTIVATE_MELEE_MENU);
         event.register(JJKKeys.ACTIVATE_J2TSU_MENU);
         event.register(JJKKeys.ACTIVATE_ABILITY);
         event.register(JJKKeys.ACTIVATE_J2TSU);
         event.register(JJKKeys.ACTIVATE_RCT_OR_HEAL);
         event.register(JJKKeys.OPEN_INVENTORY_CURSE);
         event.register(JJKKeys.ACTIVATE_CURSED_ENERGY_SHIELD);
         event.register(JJKKeys.SHOW_ABILITY_MENU);
         event.register(JJKKeys.SHOW_DOMAIN_MENU);
         event.register(JJKKeys.DASH);
         event.register(JJKKeys.QUICKDASH);
         event.register(JJKKeys.OPEN_JUJUTSU_MENU);
         event.register(JJKKeys.INCREASE_OUTPUT);
         event.register(JJKKeys.DECREASE_OUTPUT);
         event.register(JJKKeys.QUICK_VEIL);
      }

      @SubscribeEvent
      public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
         event.registerAboveAll("ability_overlay", AbilityOverlay.OVERLAY);
         event.registerAboveAll("cursed_energy_overlay", CursedEnergyOverlay.OVERLAY);
         event.registerAboveAll("six_eyes_overlay", SixEyesOverlay.OVERLAY);
         event.registerAboveAll("screen_flash_overlay", ScreenFlashOverlay.OVERLAY);
         event.registerAboveAll("blink_overlay", BlinkOverlay.OVERLAY);
         event.registerAboveAll("domain_clash_overlay", DomainClashOverlay.OVERLAY);
      }

      @SubscribeEvent
      public static void onRegisterLayers(RegisterLayerDefinitions event) {
         event.registerLayerDefinition(CursedEnergyBlastModel.LAYER, CursedEnergyBlastModel::createBodyLayer);
         event.registerLayerDefinition(TojiFushiguroModel.LAYER, SkinModel::createBodyLayer);
         event.registerLayerDefinition(TojiFushiguroModel.INNER_LAYER, SkinModel::createInnerLayer);
         event.registerLayerDefinition(TojiFushiguroModel.OUTER_LAYER, SkinModel::createOuterLayer);
         event.registerLayerDefinition(SatoruGojoModel.LAYER, SkinModel::createBodyLayer);
         event.registerLayerDefinition(SatoruGojoModel.INNER_LAYER, SkinModel::createInnerLayer);
         event.registerLayerDefinition(SatoruGojoModel.OUTER_LAYER, SkinModel::createOuterLayer);
         event.registerLayerDefinition(YutaOkkotsuModel.LAYER, SkinModel::createBodyLayer);
         event.registerLayerDefinition(YutaOkkotsuModel.INNER_LAYER, SkinModel::createInnerLayer);
         event.registerLayerDefinition(YutaOkkotsuModel.OUTER_LAYER, SkinModel::createOuterLayer);
         event.registerLayerDefinition(MegumiFushiguroModel.LAYER, SkinModel::createBodyLayer);
         event.registerLayerDefinition(MegumiFushiguroModel.INNER_LAYER, SkinModel::createInnerLayer);
         event.registerLayerDefinition(MegumiFushiguroModel.OUTER_LAYER, SkinModel::createOuterLayer);
         event.registerLayerDefinition(YujiItadoriModel.LAYER, SkinModel::createBodyLayer);
         event.registerLayerDefinition(YujiItadoriModel.INNER_LAYER, SkinModel::createInnerLayer);
         event.registerLayerDefinition(YujiItadoriModel.OUTER_LAYER, SkinModel::createOuterLayer);
         event.registerLayerDefinition(TogeInumakiModel.LAYER, SkinModel::createBodyLayer);
         event.registerLayerDefinition(TogeInumakiModel.INNER_LAYER, SkinModel::createInnerLayer);
         event.registerLayerDefinition(TogeInumakiModel.OUTER_LAYER, SkinModel::createOuterLayer);
         event.registerLayerDefinition(SuguruGetoModel.LAYER, SkinModel::createBodyLayer);
         event.registerLayerDefinition(SuguruGetoModel.INNER_LAYER, SkinModel::createInnerLayer);
         event.registerLayerDefinition(SuguruGetoModel.OUTER_LAYER, SkinModel::createOuterLayer);
         event.registerLayerDefinition(NaoyaZeninModel.LAYER, SkinModel::createBodyLayer);
         event.registerLayerDefinition(NaoyaZeninModel.INNER_LAYER, SkinModel::createInnerLayer);
         event.registerLayerDefinition(NaoyaZeninModel.OUTER_LAYER, SkinModel::createOuterLayer);
         event.registerLayerDefinition(RyuIshigoriModel.LAYER, SkinModel::createBodyLayer);
         event.registerLayerDefinition(RyuIshigoriModel.INNER_LAYER, SkinModel::createInnerLayer);
         event.registerLayerDefinition(RyuIshigoriModel.OUTER_LAYER, SkinModel::createOuterLayer);
         event.registerLayerDefinition(HajimeKashimoModel.LAYER, SkinModel::createBodyLayer);
         event.registerLayerDefinition(HajimeKashimoModel.INNER_LAYER, SkinModel::createInnerLayer);
         event.registerLayerDefinition(HajimeKashimoModel.OUTER_LAYER, SkinModel::createOuterLayer);
         event.registerLayerDefinition(MakiZeninModel.LAYER, SkinModel::createBodyLayer);
         event.registerLayerDefinition(MakiZeninModel.INNER_LAYER, SkinModel::createInnerLayer);
         event.registerLayerDefinition(MakiZeninModel.OUTER_LAYER, SkinModel::createOuterLayer);
         event.registerLayerDefinition(AoiTodoModel.LAYER, SkinModel::createBodyLayer);
         event.registerLayerDefinition(AoiTodoModel.INNER_LAYER, SkinModel::createInnerLayer);
         event.registerLayerDefinition(AoiTodoModel.OUTER_LAYER, SkinModel::createOuterLayer);
      }

      @SubscribeEvent
      public static void onRegisterRenderers(RegisterRenderers event) {
         event.registerBlockEntityRenderer((BlockEntityType)JJKBlockEntities.DOMAIN_SKY.get(), pContext -> new DomainSkyBlockRenderer());
         event.registerBlockEntityRenderer((BlockEntityType)JJKBlockEntities.FAKE_SKY.get(), FakeSkyRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.JOGO.get(), JogoRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.JOGOAT.get(), JogoatRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.DAGON.get(), DagonRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.HANAMI.get(), HanamiRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.RUGBY_FIELD_CURSE.get(), RugbyFieldCurseRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.FISH_CURSE.get(), FishCurseRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.CYCLOPS_CURSE.get(), CyclopsCurseRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.KUCHISAKE_ONNA.get(), ScissorCurseRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.ZOMBA_CURSE.get(), ZombaCurseRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.WORM_CURSE.get(), WormCurseHeadRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.FELINE_CURSE.get(), FelineCurseRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.FUGLY_CURSE.get(), FuglyCurseRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.BIRD_CURSE.get(), BirdCurseRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.FINGER_BEARER.get(), FingerBearerRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.RAINBOW_DRAGON.get(), RainbowDragonHeadRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.DINO_CURSE.get(), DinoCurseRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.ABSORBED_PLAYER.get(), AbsorbedPlayerRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.LIMBO_CLONE.get(), LimboCloneRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.SUKUNA.get(), SukunaRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.HEIAN_SUKUNA.get(), HeianSukunaRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.TOJI_FUSHIGURO.get(), TojiFushiguroRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.SATORU_GOJO.get(), SatoruGojoRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.SUGURU_GETO.get(), SuguruGetoRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.YUTA_OKKOTSU.get(), YutaOkkotsuRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.MEGUMI_FUSHIGURO.get(), MegumiFushiguroRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.NAOYA_ZENIN.get(), NaoyaZeninRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.YUJI_ITADORI.get(), YujiItadoriRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.TOGE_INUMAKI.get(), TogeInumakiRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.MAKI_ZENIN.get(), MakiZeninRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.HAJIME_KASHIMO.get(), HajimeKashimoRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.RYU_ISHIGORI.get(), RyuIshigoriRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.AOI_TODO.get(), AoiTodoRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.RIKA.get(), RikaRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.MAHORAGA.get(), MahoragaRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.DIVINE_DOG_WHITE.get(), DivineDogRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.DIVINE_DOG_BLACK.get(), DivineDogRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.TOAD.get(), ToadRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.TOAD_TONGUE.get(), ToadTongueRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.TOAD_FUSION.get(), ToadRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.RABBIT_ESCAPE.get(), RabbitEscapeRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.NUE.get(), NueRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.NUE_TOTALITY.get(), NueTotalityRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.GREAT_SERPENT.get(), GreatSerpentHeadRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.MAX_ELEPHANT.get(), MaxElephantRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.DIVINE_DOG_TOTALITY.get(), DivineDogTotalityRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.PIERCING_BULL.get(), PiercingBullRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.TRANQUIL_DEER.get(), TranquilDeerRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.AGITO.get(), AgitoRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.TRANSFIGURED_SOUL_SMALL.get(), TransfiguredSoulSmallRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.TRANSFIGURED_SOUL_NORMAL.get(), TransfiguredSoulNormalRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.TRANSFIGURED_SOUL_LARGE.get(), TransfiguredSoulLargeRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.POLYMORPHIC_SOUL_ISOMER.get(), PolymorphicSoulIsomerRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.TOAD_TONGUE.get(), ToadTongueRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.WHEEL.get(), WheelRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.RED.get(), RedRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.BLUE.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.HOLLOW_PURPLE.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.CLOSED_DOMAIN_EXPANSION.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.VEIL.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.DOMAIN_BARRIER.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.GENERIC_OPEN_DOMAIN.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.INCOMPLETE_DOMAIN.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.DISMANTLE.get(), DismantleRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.WORLD_SLASH.get(), WorldSlashRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.MALEVOLENT_SHRINE.get(), MalevolentShrineRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.MALEVOLENT_SHRINE_CLOSED.get(), MalevolentShrineClosedRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.FIRE_ARROW.get(), FireArrowRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.PURE_LOVE.get(), PureLoveRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.FIREBALL.get(), FireballRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.EMBER_INSECT.get(), EmberInsectRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.VOLCANO.get(), VolcanoRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.METEOR.get(), MeteorRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.THROWN_CHAIN.get(), ThrownChainProjectileRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.CHIMERA_SHADOW_GARDEN.get(), EmptyRenderer::new);
         event.registerBlockEntityRenderer((BlockEntityType)JJKBlockEntities.DISPLAY_CASE.get(), DisplayCaseRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.SCISSOR.get(), ScissorRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.PIERCING_WATER.get(), PiercingWaterRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.JUJUTSU_LIGHTNING.get(), LightningBoltRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.JACOBS_LADDER.get(), JacobsLadderRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.MAXIMUM_OUTPUT_JACOBS_LADDER.get(), MaximumOutputJacobsLadderRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.SKY_STRIKE.get(), SkyStrikeRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.MAXIMUM_UZUMAKI.get(), MaximumUzumakiRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.MINI_UZUMAKI.get(), MiniUzumakiRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.HOLLOW_PURPLE_EXPLOSION.get(), HollowPurpleExplosionRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.WATERBALL.get(), WaterballRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.CHIMERA_SHADOW_GARDEN.get(), ChimeraShadowGardenRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.EEL_SHIKIGAMI.get(), EelShikigamiRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.SHARK_SHIKIGAMI.get(), SharkShikigamiRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.PIRANHA_SHIKIGAMI.get(), PiranhaShikigamiRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.SIMPLE_DOMAIN.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.HOLLOW_WICKER_BASKET.get(), HollowWickerBasketRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.WATER_TORRENT.get(), WaterTorrentRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.FOREST_SPIKE.get(), ForestSpikeRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.WOOD_SEGMENT.get(), WoodSegmentRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.WOOD_SHIELD_SEGMENT.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.WOOD_SHIELD.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.CURSED_BUD.get(), CursedBudRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.FOREST_WAVE.get(), ForestWaveRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.LAVA_ROCK.get(), LavaRockRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.LIGHTNING.get(), LightningRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.PROJECTION_FRAME.get(), ProjectionFrameRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.FOREST_ROOTS.get(), ForestRootsRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.FILM_GAUGE.get(), FilmGaugeRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.TIME_CELL_MOON_PALACE.get(), TimeCellMoonPalaceRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.DISASTER_PLANT.get(), DisasterPlantRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.SELF_EMBODIMENT_OF_PERFECTION.get(), SelfEmbodimentOfPerfectionRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.BLACk_FLASH.get(), BlackFlashRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.NYOI_STAFF.get(), NyoiStaffRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.EMBER_INSECT_FLIGHT.get(), EmberInsectsFlightRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.AIR_FRAME.get(), AirFrameRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.FIRE_BEAM.get(), FireBeamRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.FOREST_DASH.get(), ForestDashRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.CURSED_ENERGY_IMBUED_ITEM.get(), ItemEntityRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.CURSED_ENERGY_BOMB.get(), CursedEnergyBombRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.ELECTRIC_BEAM.get(), ElectricBeamRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.GRANITE_BLAST.get(), GraniteBlastRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.GRANITE_BLAST_VOLLEY.get(), GraniteBlastRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.MAXIMUM_GRANITE_BEAM.get(), GraniteBlastRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.GRANITE_RING.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.CURSED_ENERGY_BLAST.get(), CursedEnergyBlastRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.EEL_GRAPPLE.get(), EelGrappleRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.TRANSFIGURED_SOUL.get(), TransfiguredSoulRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.BODY_REPEL.get(), BodyRepelRenderer::new);
         event.registerEntityRenderer((EntityType)JJKEntities.SPIDERWEB.get(), SpiderwebRenderer::new);
      }

      @SubscribeEvent
      public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
         event.registerSpriteSet((ParticleType)JJKParticles.BLACK_FLASH.get(), BlackFlashParticle.Provider::new);
         event.registerSpriteSet((ParticleType)JJKParticles.VAPOR.get(), VaporParticle.Provider::new);
         event.registerSpriteSet((ParticleType)JJKParticles.TRAVEL.get(), TravelParticle.Provider::new);
         event.registerSpriteSet((ParticleType)JJKParticles.CURSED_ENERGY.get(), CursedEnergyParticle.Provider::new);
         event.registerSpriteSet((ParticleType)JJKParticles.MIRAGE.get(), MirageParticle.Provider::new);
         event.registerSpriteSet((ParticleType)JJKParticles.PROJECTION.get(), ProjectionParticle.Provider::new);
         event.registerSpriteSet((ParticleType)JJKParticles.LIGHTNING.get(), LightningParticle.Provider::new);
         event.registerSpriteSet((ParticleType)JJKParticles.CURSED_SPEECH.get(), CursedSpeechParticle.Provider::new);
         event.registerSpriteSet((ParticleType)JJKParticles.SLASH.get(), SlashParticle.Provider::new);
         event.registerSpriteSet((ParticleType)JJKParticles.DISMANTLE.get(), DismantleParticle.Provider::new);
         event.registerSpriteSet((ParticleType)JJKParticles.SPIDERWEB.get(), SpiderwebParticle.Provider::new);
         event.registerSpriteSet((ParticleType)JJKParticles.EMITTING_LIGHTNING.get(), EmittingLightningParticle.Provider::new);
         event.registerSpriteSet((ParticleType)JJKParticles.FIRE.get(), FireParticle.Provider::new);
         event.registerSpriteSet((ParticleType)JJKParticles.SMOKE.get(), BetterSmokeParticle.Provider::new);
         event.registerSpriteSet((ParticleType)JJKParticles.SLICE.get(), SlicedEntityParticle.Provider::new);
      }
   }
}
