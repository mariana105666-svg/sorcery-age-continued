package radon.jujutsu_kaisen.network.packet.c2s;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

public class QuestionRemovePactC2SPacket {
   private final UUID identifier;
   private final Pact pact;

   public QuestionRemovePactC2SPacket(UUID identifier, Pact pact) {
      this.identifier = identifier;
      this.pact = pact;
   }

   public QuestionRemovePactC2SPacket(FriendlyByteBuf buf) {
      this(buf.readUUID(), (Pact)buf.readEnum(Pact.class));
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeUUID(this.identifier);
      buf.writeEnum(this.pact);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(
         () -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            ISorcererData cap = (ISorcererData)sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            cap.createPactRemovalRequest(this.identifier, this.pact);
            Player player = sender.serverLevel().getPlayerByUUID(this.identifier);
            if (player != sender) {
               if (player != null) {
                  Component accept = Component.translatable(String.format("chat.%s.pact_question_accept", "jujutsu_kaisen"))
                     .withStyle(
                        Style.EMPTY
                           .withColor(ChatFormatting.GREEN)
                           .withClickEvent(
                              new ClickEvent(Action.RUN_COMMAND, String.format("/pactremovalaccept %s %s", sender.getName().getString(), this.pact.name()))
                           )
                           .withHoverEvent(
                              new HoverEvent(
                                 net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                 Component.translatable(String.format("chat.%s.pact_question_remove", "jujutsu_kaisen"))
                              )
                           )
                     );
                  Component decline = Component.translatable(String.format("chat.%s.pact_question_decline", "jujutsu_kaisen"))
                     .withStyle(
                        Style.EMPTY
                           .withColor(ChatFormatting.RED)
                           .withClickEvent(
                              new ClickEvent(Action.RUN_COMMAND, String.format("/pactremovaldecline %s %s", sender.getName().getString(), this.pact.name()))
                           )
                           .withHoverEvent(
                              new HoverEvent(
                                 net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                 Component.translatable(String.format("chat.%s.pact_question_remove", "jujutsu_kaisen"))
                              )
                           )
                     );
                  Component message = Component.translatable(
                     String.format("chat.%s.pact_question_remove", "jujutsu_kaisen"),
                     new Object[]{accept, decline, this.pact.getName().getString().toLowerCase(), sender.getName()}
                  );
                  player.sendSystemMessage(message);
               }
            }
         }
      );
      ctx.setPacketHandled(true);
   }
}
