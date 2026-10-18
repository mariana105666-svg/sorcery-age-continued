package radon.jujutsu_kaisen.network.packet.c2s;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

public class UnlockAbilityC2SPacket {
   private final ResourceLocation key;

   public UnlockAbilityC2SPacket(ResourceLocation key) {
      this.key = key;
   }

   public UnlockAbilityC2SPacket(FriendlyByteBuf buf) {
      this(buf.readResourceLocation());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeResourceLocation(this.key);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> {
         ServerPlayer sender = ctx.getSender();

         assert sender != null;

         ISorcererData cap = (ISorcererData)sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         Ability ability = JJKAbilities.getValue(this.key);
         if (ability != null) {
            if (ability.canUnlock(sender)) {
               if (!sender.getAbilities().instabuild) {
                  if (cap.getPoints() - ability.getRealPointsCost(sender) < 0) {
                     return;
                  }

                  cap.usePoints(ability.getRealPointsCost(sender));
               }

               cap.unlock(ability);
            }
         }
      });
      ctx.setPacketHandled(true);
   }
}
