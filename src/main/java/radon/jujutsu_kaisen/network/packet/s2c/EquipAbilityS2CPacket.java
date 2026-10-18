package radon.jujutsu_kaisen.network.packet.s2c;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.client.ClientEquipHandler;

public class EquipAbilityS2CPacket {
   public static final ResourceLocation NOTHING = new ResourceLocation("jujutsu_kaisen", "nothing");
   private final ResourceLocation key;

   public EquipAbilityS2CPacket(ResourceLocation key) {
      this.key = key;
   }

   public EquipAbilityS2CPacket(FriendlyByteBuf buf) {
      this.key = buf.readResourceLocation();
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeResourceLocation(this.key);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientEquipHandler.equip(this.key)));
      ctx.setPacketHandled(true);
   }
}
