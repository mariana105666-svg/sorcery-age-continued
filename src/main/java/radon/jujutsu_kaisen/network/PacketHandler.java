package radon.jujutsu_kaisen.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.network.simple.SimpleChannel;
import radon.jujutsu_kaisen.network.packet.c2s.AbilityRightClickC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.AddBindingVowC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.AddChantC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.ChangeOutputC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.CommandableTargetC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.CreateVeilC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.CurseSummonC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.JumpInputListenerC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.KuchisakeOnnaAnswerC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.NyoiStaffSummonLightningC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.OpenInventoryCurseC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.QuestionCreatePactC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.QuestionRemovePactC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.QuickVeilC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.RemoveBindingVowC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.RemoveChantC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.RequestCostC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.RequestVisualDataC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.RightClickInputListenerC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.SetAbsorbedC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.SetAdditionalC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.SetCursedEnergyColorC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.SetDomainConfigurationC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.SetDomainSizeC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.SetQuickVeilC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.SetSizeC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.SetStolenC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.SetTojiBountyC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.ShadowInventoryTakeC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.TriggerAbilityC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.UncopyAbilityC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.UnlockAbilityC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.UnstealAbilityC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.UntriggerAbilityC2SPacket;
import radon.jujutsu_kaisen.network.packet.s2c.AddChantS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.BlinkS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.CameraShakeS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.ClearChantsC2SPacket;
import radon.jujutsu_kaisen.network.packet.s2c.DomainClashS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.EquipAbilityS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.OpenVeilConfigurationS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.ReceiveVisualDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.RemoveChantS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.ResurrectionS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.ScreenFlashS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SetCostS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SetFrequencyS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncMouthS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncTenShadowsDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncVisualDataS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.TriggerAbilityS2CPacket;

public class PacketHandler {
   private static SimpleChannel INSTANCE;
   private static int packetId = 0;

   private static int id() {
      return packetId++;
   }

   public static void register() {
      INSTANCE = ChannelBuilder.named(new ResourceLocation("jujutsu_kaisen", "main"))
         .networkProtocolVersion(() -> "1.0")
         .clientAcceptedVersions(x -> true)
         .serverAcceptedVersions(x -> true)
         .simpleChannel();
      INSTANCE.messageBuilder(SyncSorcererDataS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(SyncSorcererDataS2CPacket::new)
         .encoder(SyncSorcererDataS2CPacket::encode)
         .consumerMainThread(SyncSorcererDataS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(SyncTenShadowsDataS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(SyncTenShadowsDataS2CPacket::new)
         .encoder(SyncTenShadowsDataS2CPacket::encode)
         .consumerMainThread(SyncTenShadowsDataS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(TriggerAbilityS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(TriggerAbilityS2CPacket::new)
         .encoder(TriggerAbilityS2CPacket::encode)
         .consumerMainThread(TriggerAbilityS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(TriggerAbilityC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(TriggerAbilityC2SPacket::new)
         .encoder(TriggerAbilityC2SPacket::encode)
         .consumerMainThread(TriggerAbilityC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(OpenInventoryCurseC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(OpenInventoryCurseC2SPacket::new)
         .encoder(OpenInventoryCurseC2SPacket::encode)
         .consumerMainThread(OpenInventoryCurseC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(CameraShakeS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(CameraShakeS2CPacket::new)
         .encoder(CameraShakeS2CPacket::encode)
         .consumerMainThread(CameraShakeS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(CommandableTargetC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(CommandableTargetC2SPacket::new)
         .encoder(CommandableTargetC2SPacket::encode)
         .consumerMainThread(CommandableTargetC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(SetOverlayMessageS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(SetOverlayMessageS2CPacket::new)
         .encoder(SetOverlayMessageS2CPacket::encode)
         .consumerMainThread(SetOverlayMessageS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(JumpInputListenerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(JumpInputListenerC2SPacket::new)
         .encoder(JumpInputListenerC2SPacket::encode)
         .consumerMainThread(JumpInputListenerC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(RightClickInputListenerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(RightClickInputListenerC2SPacket::new)
         .encoder(RightClickInputListenerC2SPacket::encode)
         .consumerMainThread(RightClickInputListenerC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(SetSizeC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(SetSizeC2SPacket::new)
         .encoder(SetSizeC2SPacket::encode)
         .consumerMainThread(SetSizeC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(SetFrequencyS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(SetFrequencyS2CPacket::new)
         .encoder(SetFrequencyS2CPacket::encode)
         .consumerMainThread(SetFrequencyS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(SetCostS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(SetCostS2CPacket::new)
         .encoder(SetCostS2CPacket::encode)
         .consumerMainThread(SetCostS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(RequestCostC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(RequestCostC2SPacket::new)
         .encoder(RequestCostC2SPacket::encode)
         .consumerMainThread(RequestCostC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(SetTojiBountyC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(SetTojiBountyC2SPacket::new)
         .encoder(SetTojiBountyC2SPacket::encode)
         .consumerMainThread(SetTojiBountyC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(KuchisakeOnnaAnswerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(KuchisakeOnnaAnswerC2SPacket::new)
         .encoder(KuchisakeOnnaAnswerC2SPacket::encode)
         .consumerMainThread(KuchisakeOnnaAnswerC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(ShadowInventoryTakeC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(ShadowInventoryTakeC2SPacket::new)
         .encoder(ShadowInventoryTakeC2SPacket::encode)
         .consumerMainThread(ShadowInventoryTakeC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(CurseSummonC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(CurseSummonC2SPacket::new)
         .encoder(CurseSummonC2SPacket::encode)
         .consumerMainThread(CurseSummonC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(SetAdditionalC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(SetAdditionalC2SPacket::new)
         .encoder(SetAdditionalC2SPacket::encode)
         .consumerMainThread(SetAdditionalC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(SetStolenC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(SetStolenC2SPacket::new)
         .encoder(SetStolenC2SPacket::encode)
         .consumerMainThread(SetStolenC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(SetAbsorbedC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(SetAbsorbedC2SPacket::new)
         .encoder(SetAbsorbedC2SPacket::encode)
         .consumerMainThread(SetAbsorbedC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(RequestVisualDataC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(RequestVisualDataC2SPacket::new)
         .encoder(RequestVisualDataC2SPacket::encode)
         .consumerMainThread(RequestVisualDataC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(ReceiveVisualDataS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(ReceiveVisualDataS2CPacket::new)
         .encoder(ReceiveVisualDataS2CPacket::encode)
         .consumerMainThread(ReceiveVisualDataS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(SetDomainSizeC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(SetDomainSizeC2SPacket::new)
         .encoder(SetDomainSizeC2SPacket::encode)
         .consumerMainThread(SetDomainSizeC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(ScreenFlashS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(ScreenFlashS2CPacket::new)
         .encoder(ScreenFlashS2CPacket::encode)
         .consumerMainThread(ScreenFlashS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(BlinkS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(BlinkS2CPacket::new)
         .encoder(BlinkS2CPacket::toBytes)
         .consumerMainThread(BlinkS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(ResurrectionS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(ResurrectionS2CPacket::new)
         .encoder(ResurrectionS2CPacket::encode)
         .consumerMainThread(ResurrectionS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(QuestionCreatePactC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(QuestionCreatePactC2SPacket::new)
         .encoder(QuestionCreatePactC2SPacket::encode)
         .consumerMainThread(QuestionCreatePactC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(SetDomainConfigurationC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(SetDomainConfigurationC2SPacket::new)
         .encoder(SetDomainConfigurationC2SPacket::encode)
         .consumerMainThread(SetDomainConfigurationC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(AddBindingVowC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(AddBindingVowC2SPacket::new)
         .encoder(AddBindingVowC2SPacket::encode)
         .consumerMainThread(AddBindingVowC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(RemoveBindingVowC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(RemoveBindingVowC2SPacket::new)
         .encoder(RemoveBindingVowC2SPacket::encode)
         .consumerMainThread(RemoveBindingVowC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(UnlockAbilityC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(UnlockAbilityC2SPacket::new)
         .encoder(UnlockAbilityC2SPacket::encode)
         .consumerMainThread(UnlockAbilityC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(AddChantC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(AddChantC2SPacket::new)
         .encoder(AddChantC2SPacket::encode)
         .consumerMainThread(AddChantC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(RemoveChantC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(RemoveChantC2SPacket::new)
         .encoder(RemoveChantC2SPacket::encode)
         .consumerMainThread(RemoveChantC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(ChangeOutputC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(ChangeOutputC2SPacket::new)
         .encoder(ChangeOutputC2SPacket::encode)
         .consumerMainThread(ChangeOutputC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(UncopyAbilityC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(UncopyAbilityC2SPacket::new)
         .encoder(UncopyAbilityC2SPacket::encode)
         .consumerMainThread(UncopyAbilityC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(UnstealAbilityC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(UnstealAbilityC2SPacket::new)
         .encoder(UnstealAbilityC2SPacket::encode)
         .consumerMainThread(UnstealAbilityC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(SyncVisualDataS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(SyncVisualDataS2CPacket::new)
         .encoder(SyncVisualDataS2CPacket::encode)
         .consumerMainThread(SyncVisualDataS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(SyncMouthS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(SyncMouthS2CPacket::new)
         .encoder(SyncMouthS2CPacket::encode)
         .consumerMainThread(SyncMouthS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(AbilityRightClickC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(AbilityRightClickC2SPacket::new)
         .encoder(AbilityRightClickC2SPacket::encode)
         .consumerMainThread(AbilityRightClickC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(QuestionRemovePactC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(QuestionRemovePactC2SPacket::new)
         .encoder(QuestionRemovePactC2SPacket::encode)
         .consumerMainThread(QuestionRemovePactC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(NyoiStaffSummonLightningC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(NyoiStaffSummonLightningC2SPacket::new)
         .encoder(NyoiStaffSummonLightningC2SPacket::encode)
         .consumerMainThread(NyoiStaffSummonLightningC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(SetCursedEnergyColorC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(SetCursedEnergyColorC2SPacket::new)
         .encoder(SetCursedEnergyColorC2SPacket::encode)
         .consumerMainThread(SetCursedEnergyColorC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(UntriggerAbilityC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(UntriggerAbilityC2SPacket::new)
         .encoder(UntriggerAbilityC2SPacket::encode)
         .consumerMainThread(UntriggerAbilityC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(AddChantS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(AddChantS2CPacket::new)
         .encoder(AddChantS2CPacket::encode)
         .consumerMainThread(AddChantS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(RemoveChantS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(RemoveChantS2CPacket::new)
         .encoder(RemoveChantS2CPacket::encode)
         .consumerMainThread(RemoveChantS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(ClearChantsC2SPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(ClearChantsC2SPacket::new)
         .encoder(ClearChantsC2SPacket::encode)
         .consumerMainThread(ClearChantsC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(DomainClashS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(DomainClashS2CPacket::new)
         .encoder(DomainClashS2CPacket::encode)
         .consumerMainThread(DomainClashS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(OpenVeilConfigurationS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(OpenVeilConfigurationS2CPacket::new)
         .encoder(OpenVeilConfigurationS2CPacket::encode)
         .consumerMainThread(OpenVeilConfigurationS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(CreateVeilC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(CreateVeilC2SPacket::new)
         .encoder(CreateVeilC2SPacket::encode)
         .consumerMainThread(CreateVeilC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(EquipAbilityS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
         .decoder(EquipAbilityS2CPacket::new)
         .encoder(EquipAbilityS2CPacket::encode)
         .consumerMainThread(EquipAbilityS2CPacket::handle)
         .add();
      INSTANCE.messageBuilder(SetQuickVeilC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(SetQuickVeilC2SPacket::new)
         .encoder(SetQuickVeilC2SPacket::encode)
         .consumerMainThread(SetQuickVeilC2SPacket::handle)
         .add();
      INSTANCE.messageBuilder(QuickVeilC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
         .decoder(QuickVeilC2SPacket::new)
         .encoder(QuickVeilC2SPacket::encode)
         .consumerMainThread(QuickVeilC2SPacket::handle)
         .add();
   }

   public static <MSG> void broadcast(MSG message) {
      INSTANCE.send(PacketDistributor.ALL.noArg(), message);
   }

   public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
      INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
   }

   public static <MSG> void sendTracking(MSG message, Entity entity) {
      INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
   }

   public static void sendTrackingAndSelf(Object msg, Entity entity) {
      INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
   }

   public static <MSG> void sendToServer(MSG message) {
      INSTANCE.sendToServer(message);
   }
}
