package radon.jujutsu_kaisen.network.packet.c2s;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

public class ChangeOutputC2SPacket {
   public static final int INCREASE = 1;
   public static final int DECREASE = -1;
   private final int direction;

   public ChangeOutputC2SPacket(int direction) {
      this.direction = direction;
   }

   public ChangeOutputC2SPacket(FriendlyByteBuf buf) {
      this(buf.readInt());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.direction);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         ServerPlayer sender = ctx.getSender();

         assert sender != null;

         ISorcererData cap = (ISorcererData)sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (this.direction == 1) {
            cap.increaseOutput();
         } else {
            cap.decreaseOutput();
         }
      });
      ctx.setPacketHandled(true);
   }
}
