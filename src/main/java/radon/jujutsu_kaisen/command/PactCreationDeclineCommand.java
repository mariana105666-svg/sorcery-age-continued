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

public class PactCreationDeclineCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralCommandNode<CommandSourceStack> node = dispatcher.register(
         (LiteralArgumentBuilder)Commands.literal("pactcreationdecline")
            .then(
               Commands.argument("player", EntityArgument.entity())
                  .then(
                     Commands.argument("pact", EnumArgument.enumArgument(Pact.class))
                        .executes(
                           ctx -> decline(
                              (CommandSourceStack)ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), (Pact)ctx.getArgument("pact", Pact.class)
                           )
                        )
                  )
            )
      );
      dispatcher.register((LiteralArgumentBuilder)Commands.literal("pactcreationdecline").redirect(node));
   }

   public static int decline(CommandSourceStack stack, ServerPlayer dst, Pact pact) {
      ServerPlayer src = stack.getPlayer();
      if (src == null) {
         return 0;
      } else {
         ISorcererData dstCap = (ISorcererData)dst.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (dstCap.hasRequestedPactCreation(src.getUUID(), pact)) {
            dstCap.removePactCreationRequest(src.getUUID(), pact);
            dst.sendSystemMessage(
               Component.translatable(
                  String.format("chat.%s.pact_creation_decline", "jujutsu_kaisen"), new Object[]{src.getName(), pact.getName().getString().toLowerCase()}
               )
            );
            return 1;
         } else {
            src.sendSystemMessage(
               Component.translatable(
                  String.format("chat.%s.pact_creation_decline", "jujutsu_kaisen"), new Object[]{dst.getName(), pact.getName().getString().toLowerCase()}
               )
            );
            return 0;
         }
      }
   }
}
