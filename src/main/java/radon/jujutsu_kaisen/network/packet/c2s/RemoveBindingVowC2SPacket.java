package radon.jujutsu_kaisen.network.packet.c2s;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.capability.data.sorcerer.BindingVow;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

public class RemoveBindingVowC2SPacket {
   private final BindingVow vow;

   public RemoveBindingVowC2SPacket(BindingVow vow) {
      this.vow = vow;
   }

   public RemoveBindingVowC2SPacket(FriendlyByteBuf buf) {
      this((BindingVow)buf.readEnum(BindingVow.class));
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeEnum(this.vow);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         ServerPlayer sender = ctx.getSender();

         assert sender != null;

         ISorcererData cap = (ISorcererData)sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (cap.isCooldownDone(this.vow)) {
            cap.removeBindingVow(this.vow);
            cap.addBindingVowCooldown(this.vow);
         }
      });
      ctx.setPacketHandled(true);
   }
}
