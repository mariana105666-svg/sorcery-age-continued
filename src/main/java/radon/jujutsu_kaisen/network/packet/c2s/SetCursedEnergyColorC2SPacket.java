package radon.jujutsu_kaisen.network.packet.c2s;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

public class SetCursedEnergyColorC2SPacket {
   private final int cursedEnergyColor;

   public SetCursedEnergyColorC2SPacket(int cursedEnergyColor) {
      this.cursedEnergyColor = cursedEnergyColor;
   }

   public SetCursedEnergyColorC2SPacket(FriendlyByteBuf buf) {
      this(buf.readInt());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.cursedEnergyColor);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         ServerPlayer sender = ctx.getSender();

         assert sender != null;

         ISorcererData cap = (ISorcererData)sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         cap.setCursedEnergyColor(this.cursedEnergyColor);
      });
      ctx.setPacketHandled(true);
   }
}
