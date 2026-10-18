package radon.jujutsu_kaisen.network.packet.c2s;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.menu.VeilRodMenu;

public class SetSizeC2SPacket {
   private final int size;

   public SetSizeC2SPacket(int size) {
      this.size = size;
   }

   public SetSizeC2SPacket(FriendlyByteBuf buf) {
      this(buf.readInt());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.size);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         ServerPlayer sender = ctx.getSender();

         assert sender != null;

         if (sender.containerMenu instanceof VeilRodMenu menu) {
            if (!menu.stillValid(sender)) {
               return;
            }

            menu.setSize(Mth.clamp(this.size, (Integer)ConfigHolder.SERVER.minimumVeilSize.get(), (Integer)ConfigHolder.SERVER.maximumVeilSize.get()));
         }
      });
      ctx.setPacketHandled(true);
   }
}
