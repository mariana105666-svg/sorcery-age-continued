package radon.jujutsu_kaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class GuaranteedBlackFlashCommand {
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("jjkblackflash").requires(src -> src.hasPermission(2)))
            .then(
               ((RequiredArgumentBuilder)Commands.argument("enabled", BoolArgumentType.bool())
                     .executes(
                        ctx -> set((CommandSourceStack)ctx.getSource(), self((CommandSourceStack)ctx.getSource()), BoolArgumentType.getBool(ctx, "enabled"))
                     ))
                  .then(
                     Commands.argument("targets", EntityArgument.players())
                        .executes(
                           ctx -> set((CommandSourceStack)ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"), BoolArgumentType.getBool(ctx, "enabled"))
                        )
                  )
            )
      );
   }

   private static Collection<ServerPlayer> self(CommandSourceStack source) throws CommandSyntaxException {
      return List.of(source.getPlayerOrException());
   }

   private static int set(CommandSourceStack source, Collection<ServerPlayer> targets, boolean enabled) {
      int changed = 0;

      for (ServerPlayer player : targets) {
         ISorcererData cap = (ISorcererData)player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         if (cap != null) {
            cap.setGuaranteedBlackFlash(enabled);
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
            changed++;
         }
      }

      if (changed == 0) {
         source.sendFailure(Component.translatable(String.format("commands.%s.black_flash.failed", "jujutsu_kaisen")));
         return 0;
      } else {
         int count = changed;
         source.sendSuccess(
            () -> Component.translatable(String.format("commands.%s.black_flash.%s", "jujutsu_kaisen", enabled ? "enabled" : "disabled"), new Object[]{count}),
            true
         );
         return changed;
      }
   }
}
