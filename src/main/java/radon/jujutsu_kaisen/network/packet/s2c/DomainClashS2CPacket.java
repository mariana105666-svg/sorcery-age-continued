package radon.jujutsu_kaisen.network.packet.s2c;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.client.domain.ClientDomainClashHandler;

public class DomainClashS2CPacket {
   private final List<DomainClashS2CPacket.Entry> entries;

   public DomainClashS2CPacket(List<DomainClashS2CPacket.Entry> entries) {
      this.entries = entries;
   }

   public DomainClashS2CPacket(FriendlyByteBuf buf) {
      int count = buf.readVarInt();
      this.entries = new ArrayList<>(count);

      for (int i = 0; i < count; i++) {
         this.entries.add(new DomainClashS2CPacket.Entry(buf.readUtf(), buf.readFloat(), buf.readBoolean()));
      }
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeVarInt(this.entries.size());

      for (DomainClashS2CPacket.Entry entry : this.entries) {
         buf.writeUtf(entry.name());
         buf.writeFloat(entry.fraction());
         buf.writeBoolean(entry.self());
      }
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientDomainClashHandler.accept(this.entries)));
      ctx.setPacketHandled(true);
   }

   public record Entry(String name, float fraction, boolean self) {
   }
}
