package radon.jujutsu_kaisen.network.packet.c2s;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.entity.base.IRightClickInputListener;

public class RightClickInputListenerC2SPacket {
   private final boolean down;

   public RightClickInputListenerC2SPacket(boolean down) {
      this.down = down;
   }

   public RightClickInputListenerC2SPacket(FriendlyByteBuf buf) {
      this(buf.readBoolean());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeBoolean(this.down);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         ServerPlayer sender = ctx.getSender();

         assert sender != null;

         if (sender.getVehicle() instanceof IRightClickInputListener listener) {
            listener.setDown(this.down);
         }
      });
      ctx.setPacketHandled(true);
   }
}
