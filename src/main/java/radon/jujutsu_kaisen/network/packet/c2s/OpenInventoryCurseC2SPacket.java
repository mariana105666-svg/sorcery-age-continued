package radon.jujutsu_kaisen.network.packet.c2s;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;
import radon.jujutsu_kaisen.util.CuriosUtil;

public class OpenInventoryCurseC2SPacket {
   public OpenInventoryCurseC2SPacket() {
   }

   public OpenInventoryCurseC2SPacket(FriendlyByteBuf ignored) {
   }

   public void encode(FriendlyByteBuf ignored) {
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         ServerPlayer sender = ctx.getSender();

         assert sender != null;

         if (sender.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof InventoryCurseItem item) {
            sender.openMenu(item);
         } else if (CuriosUtil.findSlot(sender, "body").getItem() instanceof InventoryCurseItem item) {
            sender.openMenu(item);
         }
      });
      ctx.setPacketHandled(true);
   }
}
