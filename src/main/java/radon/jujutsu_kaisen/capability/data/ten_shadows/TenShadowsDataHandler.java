package radon.jujutsu_kaisen.capability.data.ten_shadows;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncTenShadowsDataS2CPacket;

@EventBusSubscriber(
   modid = "jujutsu_kaisen",
   bus = Bus.FORGE
)
public class TenShadowsDataHandler {
   public static Capability<ITenShadowsData> INSTANCE = CapabilityManager.get(new CapabilityToken<ITenShadowsData>() {});

   @SubscribeEvent
   public static void onPlayerClone(Clone event) {
      Player original = event.getOriginal();
      Player player = event.getEntity();
      original.reviveCaps();
      ITenShadowsData oldCap = (ITenShadowsData)original.getCapability(INSTANCE).resolve().orElseThrow();
      ITenShadowsData newCap = (ITenShadowsData)player.getCapability(INSTANCE).resolve().orElseThrow();
      newCap.deserializeNBT(oldCap.serializeNBT());
      if (event.isWasDeath()) {
         newCap.resetAdaptations();
         if (!(Boolean)ConfigHolder.SERVER.realisticShikigami.get()) {
            newCap.revive(false);
         }
      }

      original.invalidateCaps();
   }

   @SubscribeEvent
   public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof LivingEntity entity && (entity instanceof Player || entity instanceof ISorcerer)) {
         TenShadowsDataHandler.TenShadowsDataProvider provider = new TenShadowsDataHandler.TenShadowsDataProvider();
         ITenShadowsData cap = (ITenShadowsData)provider.getCapability(INSTANCE).resolve().orElseThrow();
         cap.init(entity);
         event.addCapability(TenShadowsDataHandler.TenShadowsDataProvider.IDENTIFIER, provider);
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         ITenShadowsData cap = (ITenShadowsData)player.getCapability(INSTANCE).resolve().orElseThrow();
         PacketHandler.sendToClient(new SyncTenShadowsDataS2CPacket(cap.serializeNBT()), player);
      }
   }

   @SubscribeEvent
   public static void onPlayerChangeDimension(PlayerChangedDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         ITenShadowsData cap = (ITenShadowsData)player.getCapability(INSTANCE).resolve().orElseThrow();
         PacketHandler.sendToClient(new SyncTenShadowsDataS2CPacket(cap.serializeNBT()), player);
      }
   }

   @SubscribeEvent
   public static void onPlayerRespawn(PlayerRespawnEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         ITenShadowsData cap = (ITenShadowsData)player.getCapability(INSTANCE).resolve().orElseThrow();
         PacketHandler.sendToClient(new SyncTenShadowsDataS2CPacket(cap.serializeNBT()), player);
      }
   }

   public static class TenShadowsDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
      public static ResourceLocation IDENTIFIER = new ResourceLocation("jujutsu_kaisen", "ten_shadows_data");
      private ITenShadowsData cap = null;
      private final LazyOptional<ITenShadowsData> optional = LazyOptional.of(this::create);

      private ITenShadowsData create() {
         if (this.cap == null) {
            this.cap = new TenShadowsData();
         }

         return this.cap;
      }

      @NotNull
      public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
         return cap == TenShadowsDataHandler.INSTANCE ? this.optional.cast() : LazyOptional.empty();
      }

      public CompoundTag serializeNBT() {
         return this.create().serializeNBT();
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.create().deserializeNBT(nbt);
      }
   }
}
