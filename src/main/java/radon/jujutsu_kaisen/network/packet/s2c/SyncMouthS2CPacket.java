package radon.jujutsu_kaisen.network.packet.s2c;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.client.visual.visual.PerfectBodyVisual;

public class SyncMouthS2CPacket {
   private final UUID src;

   public SyncMouthS2CPacket(UUID src) {
      this.src = src;
   }

   public SyncMouthS2CPacket(FriendlyByteBuf buf) {
      this(buf.readUUID());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeUUID(this.src);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PerfectBodyVisual.onChant(this.src)));
      ctx.setPacketHandled(true);
   }
}
