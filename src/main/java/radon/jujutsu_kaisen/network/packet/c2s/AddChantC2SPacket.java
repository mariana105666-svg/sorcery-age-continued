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
import radon.jujutsu_kaisen.chant.ServerChantHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.util.HelperMethods;

public class AddChantC2SPacket {
   private final ResourceLocation key;
   private final String chant;

   public AddChantC2SPacket(ResourceLocation key, String chant) {
      this.key = key;
      this.chant = chant;
   }

   public AddChantC2SPacket(FriendlyByteBuf buf) {
      this(buf.readResourceLocation(), buf.readUtf());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeResourceLocation(this.key);
      buf.writeUtf(this.chant);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(
         () -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            if (this.chant.length() <= (Integer)ConfigHolder.SERVER.maximumChantLength.get()
               && this.chant.length() >= (Integer)ConfigHolder.SERVER.minimumChantLength.get()) {
               Ability ability = JJKAbilities.getValue(this.key);
               if (ability != null && ability.isScalable(sender) && (ability.isTechnique() || ability.isChantable())) {
                  ISorcererData cap = (ISorcererData)sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                  String text = this.chant.toLowerCase();
                  if (!text.isEmpty() && !text.isBlank()) {
                     for (String chant : cap.getFirstChants(ability)) {
                        if (HelperMethods.strcmp(chant, text) < (Double)ConfigHolder.SERVER.chantSimilarityThreshold.get()) {
                           return;
                        }
                     }
                  }

                  if (cap.getFirstChants(ability).size() != (Integer)ConfigHolder.SERVER.maximumChantCount.get()
                     && !text.isEmpty()
                     && !text.isBlank()
                     && !cap.hasChant(ability, text)) {
                     cap.addChant(ability, text);
                     ServerChantHandler.clearMessages(sender);
                  }
               }
            }
         }
      );
      ctx.setPacketHandled(true);
   }
}
