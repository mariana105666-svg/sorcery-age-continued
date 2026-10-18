package radon.jujutsu_kaisen.network.packet.c2s;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.TriggerAbilityS2CPacket;

public class TriggerAbilityC2SPacket {
   private final ResourceLocation key;

   public TriggerAbilityC2SPacket(ResourceLocation key) {
      this.key = key;
   }

   public TriggerAbilityC2SPacket(FriendlyByteBuf buf) {
      this(buf.readResourceLocation());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeResourceLocation(this.key);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(
         () -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            Ability ability = JJKAbilities.getValue(this.key);
            Ability.Status status;
            if ((status = AbilityHandler.trigger(sender, ability)) == Ability.Status.SUCCESS) {
               PacketHandler.sendToClient(new TriggerAbilityS2CPacket(JJKAbilities.getKey(ability)), sender);
            } else {
               ISorcererData cap = (ISorcererData)sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
               if (cap == null) {
                  return;
               }

               switch (status) {
                  case ENERGY:
                     PacketHandler.sendToClient(
                        new SetOverlayMessageS2CPacket(Component.translatable(String.format("ability.%s.fail.energy", "jujutsu_kaisen")), false), sender
                     );
                     break;
                  case COOLDOWN:
                     PacketHandler.sendToClient(
                        new SetOverlayMessageS2CPacket(
                           Component.translatable(
                              String.format("ability.%s.fail.cooldown", "jujutsu_kaisen"), new Object[]{Math.max(1, cap.getRemainingCooldown(ability) / 20)}
                           ),
                           false
                        ),
                        sender
                     );
                     break;
                  case BURNOUT:
                     PacketHandler.sendToClient(
                        new SetOverlayMessageS2CPacket(
                           Component.translatable(String.format("ability.%s.fail.burnout", "jujutsu_kaisen"), new Object[]{Math.max(1, cap.getBurnout() / 20)}),
                           false
                        ),
                        sender
                     );
                     break;
                  case DISABLE:
                     PacketHandler.sendToClient(
                        new SetOverlayMessageS2CPacket(Component.translatable(String.format("ability.%s.fail.disable", "jujutsu_kaisen")), false), sender
                     );
                     break;
                  case FAILURE:
                     PacketHandler.sendToClient(
                        new SetOverlayMessageS2CPacket(Component.translatable(String.format("ability.%s.fail.failure", "jujutsu_kaisen")), false), sender
                     );
                     break;
                  case CHANT:
                     PacketHandler.sendToClient(
                        new SetOverlayMessageS2CPacket(Component.translatable(String.format("ability.%s.fail.chant", "jujutsu_kaisen")), false), sender
                     );
                     break;
                  case THROAT:
                     PacketHandler.sendToClient(
                        new SetOverlayMessageS2CPacket(
                           Component.translatable(String.format("ability.%s.fail.throat", "jujutsu_kaisen"), new Object[]{Math.max(1, cap.getThroatDamage() / 20)}),
                           false
                        ),
                        sender
                     );
                     break;
                  case EMPTYINV:
                     PacketHandler.sendToClient(
                        new SetOverlayMessageS2CPacket(Component.translatable(String.format("ability.%s.fail.emptyinv", "jujutsu_kaisen")), false), sender
                     );
                  case SILENCED:
                  case DISARMED:
                  case UNUSABLE:
                  case SUCCESS:
               }
            }
         }
      );
      ctx.setPacketHandled(true);
   }
}
