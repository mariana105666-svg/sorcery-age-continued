package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.command.EnumArgument;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class PactCreationAcceptCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralCommandNode<CommandSourceStack> node = dispatcher.register(
         (LiteralArgumentBuilder)Commands.literal("pactcreationaccept")
            .then(
               Commands.argument("player", EntityArgument.entity())
                  .then(
                     Commands.argument("pact", EnumArgument.enumArgument(Pact.class))
                        .executes(
                           ctx -> accept((CommandSourceStack)ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), (Pact)ctx.getArgument("pact", Pact.class))
                        )
                  )
            )
      );
      dispatcher.register((LiteralArgumentBuilder)Commands.literal("pactcreationaccept").redirect(node));
   }

   public static int accept(CommandSourceStack stack, ServerPlayer dst, Pact pact) {
      ServerPlayer src = stack.getPlayer();
      if (src == null) {
         return 0;
      } else {
         ISorcererData dstCap = (ISorcererData)dst.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         ISorcererData srcCap = (ISorcererData)src.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (dstCap.hasRequestedPactCreation(src.getUUID(), pact)) {
            dstCap.createPact(src.getUUID(), pact);
            srcCap.createPact(dst.getUUID(), pact);
            dstCap.removePactCreationRequest(src.getUUID(), pact);
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(dstCap.serializeNBT()), dst);
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(srcCap.serializeNBT()), src);
            src.sendSystemMessage(
               Component.translatable(
                  String.format("chat.%s.pact_creation_accept", "jujutsu_kaisen"), new Object[]{pact.getName().getString().toLowerCase(), dst.getName()}
               )
            );
            dst.sendSystemMessage(
               Component.translatable(
                  String.format("chat.%s.pact_creation_accept", "jujutsu_kaisen"), new Object[]{pact.getName().getString().toLowerCase(), src.getName()}
               )
            );
            return 1;
         } else {
            src.sendSystemMessage(
               Component.translatable(
                  String.format("chat.%s.pact_failure_create", "jujutsu_kaisen"), new Object[]{dst.getName(), pact.getName().getString().toLowerCase()}
               )
            );
            return 0;
         }
      }
   }
}
