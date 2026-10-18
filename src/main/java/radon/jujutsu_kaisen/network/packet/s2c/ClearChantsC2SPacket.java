package radon.jujutsu_kaisen.network.packet.s2c;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.client.chant.ClientChantHandler;

public class ClearChantsC2SPacket {
   public ClearChantsC2SPacket() {
   }

   public ClearChantsC2SPacket(FriendlyByteBuf ignored) {
   }

   public void encode(FriendlyByteBuf buf) {
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientChantHandler::remove));
      ctx.setPacketHandled(true);
   }
}
