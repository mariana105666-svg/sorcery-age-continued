package radon.jujutsu_kaisen.network.packet.s2c;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.client.CameraShakeHandler;

public class CameraShakeS2CPacket {
   private final float intensity;
   private final float speed;
   private final int duration;

   public CameraShakeS2CPacket(float intensity, float speed, int duration) {
      this.intensity = intensity;
      this.speed = speed;
      this.duration = duration;
   }

   public CameraShakeS2CPacket(FriendlyByteBuf buf) {
      this(buf.readFloat(), buf.readFloat(), buf.readInt());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeFloat(this.intensity);
      buf.writeFloat(this.speed);
      buf.writeInt(this.duration);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CameraShakeHandler.shakeCamera(this.intensity, this.speed, this.duration)));
      ctx.setPacketHandled(true);
   }
}
