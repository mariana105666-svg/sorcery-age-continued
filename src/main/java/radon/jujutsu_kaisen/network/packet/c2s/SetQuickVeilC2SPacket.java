package radon.jujutsu_kaisen.network.packet.c2s;

import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

public class SetQuickVeilC2SPacket {
   private final CompoundTag settings;

   public SetQuickVeilC2SPacket(CompoundTag settings) {
      this.settings = settings;
   }

   public SetQuickVeilC2SPacket(FriendlyByteBuf buf) {
      this.settings = buf.readNbt();
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeNbt(this.settings);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         ServerPlayer sender = ctx.getSender();
         if (sender != null && this.settings != null) {
            ISorcererData cap = (ISorcererData)sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
            if (cap != null) {
               cap.setQuickVeil(this.settings);
            }
         }
      });
      ctx.setPacketHandled(true);
   }
}
