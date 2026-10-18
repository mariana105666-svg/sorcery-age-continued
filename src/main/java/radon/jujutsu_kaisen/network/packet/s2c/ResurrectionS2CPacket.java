package radon.jujutsu_kaisen.network.packet.s2c;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.ResurrectionHandler;

public class ResurrectionS2CPacket {
   private final float health;
   private final int src;

   public ResurrectionS2CPacket(int src, float health) {
      this.src = src;
      this.health = health;
   }

   public ResurrectionS2CPacket(FriendlyByteBuf buf) {
      this(buf.readInt(), buf.readFloat());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.src);
      buf.writeFloat(this.health);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ResurrectionHandler.handle(this.src, this.health)));
      ctx.setPacketHandled(true);
   }
}
