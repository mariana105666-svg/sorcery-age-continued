package radon.jujutsu_kaisen.network.packet.s2c;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.client.gui.overlay.ScreenFlashOverlay;

public class ScreenFlashS2CPacket {
   public ScreenFlashS2CPacket() {
   }

   public ScreenFlashS2CPacket(FriendlyByteBuf ignored) {
      this();
   }

   public void encode(FriendlyByteBuf ignored) {
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ScreenFlashOverlay::flash));
      ctx.setPacketHandled(true);
   }
}
