package radon.jujutsu_kaisen.chant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.ability.AbilityStopEvent;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.AddChantS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.ClearChantsC2SPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncMouthS2CPacket;

public class ServerChantHandler {
   private static final Map<UUID, Integer> timers = new HashMap<>();
   private static final Map<UUID, List<String>> messages = new HashMap<>();
   private static final int CLEAR_INTERVAL = 200;

   public static List<String> getMessages(LivingEntity owner) {
      return messages.getOrDefault(owner.getUUID(), List.of());
   }

   public static void clearMessages(LivingEntity owner) {
      messages.remove(owner.getUUID());
      if (owner instanceof ServerPlayer player) {
         PacketHandler.sendToClient(new ClearChantsC2SPacket(), player);
      }
   }

   public static boolean onChant(LivingEntity owner, String word) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      Ability ability = cap.getAbility(word);
      if (ability == null) {
         return false;
      } else {
         UUID id = owner.getUUID();
         if (messages.containsKey(id) && !messages.get(id).isEmpty()) {
            List<String> current = messages.get(id);
            String firstWord = current.get(0);
            Ability currentAbility = cap.getAbility(firstWord);
            if (currentAbility != null && currentAbility != ability) {
               clearMessages(owner);
            }
         }

         if (!messages.containsKey(id)) {
            messages.put(id, new ArrayList<>());
         }

         List<String> chants = new ArrayList<>(cap.getFirstChants(ability));
         List<String> latest = messages.get(id);
         int index = 0;
         Iterator<String> iter = chants.iterator();

         for (String chant : latest) {
            if (!iter.hasNext() || !chant.equals(iter.next())) {
               break;
            }

            index++;
         }

         if (index < chants.size() && chants.get(index).equals(word)) {
            messages.get(id).add(word);
            if (owner instanceof ServerPlayer player) {
               PacketHandler.sendToClient(new AddChantS2CPacket(word), player);
            }

            timers.put(id, 200);
            if (owner instanceof ServerPlayer player) {
               PacketHandler.sendToClient(
                  new SetOverlayMessageS2CPacket(
                     Component.translatable(
                        String.format("chat.%s.chant", "jujutsu_kaisen"),
                        new Object[]{ability.getName().copy(), ChantHandler.getOutput(owner, ability) * 100.0F}
                     ),
                     false
                  ),
                  player
               );
            }

            if (cap.hasTrait(Trait.PERFECT_BODY)) {
               PacketHandler.broadcast(new SyncMouthS2CPacket(id));
            }

            return true;
         } else {
            return true;
         }
      }
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class ChantHandlerForgeEvents {
      @SubscribeEvent
      public static void onServerTick(ServerTickEvent event) {
         Iterator<Entry<UUID, Integer>> iter = ServerChantHandler.timers.entrySet().iterator();

         while (iter.hasNext()) {
            Entry<UUID, Integer> entry = iter.next();
            if (!ServerChantHandler.messages.containsKey(entry.getKey())) {
               iter.remove();
            } else {
               Entity owner = null;
               Iterator cap = event.getServer().getAllLevels().iterator();

               while (true) {
                  if (cap.hasNext()) {
                     ServerLevel level = (ServerLevel)cap.next();
                     if ((owner = level.getEntity(entry.getKey())) == null) {
                        continue;
                     }
                  }

                  if (owner != null) {
                     ISorcererData capx = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                     Ability ability = capx.getAbility(new LinkedHashSet<>(ServerChantHandler.messages.get(entry.getKey())));
                     if (!capx.isChanneling(ability)) {
                        int remaining = entry.getValue();
                        if (remaining > 0) {
                           ServerChantHandler.timers.put(entry.getKey(), --remaining);
                        } else {
                           ServerChantHandler.messages.remove(entry.getKey());
                           iter.remove();
                           if (owner instanceof ServerPlayer player) {
                              PacketHandler.sendToClient(new ClearChantsC2SPacket(), player);
                           }
                        }
                     }
                  }
                  break;
               }
            }
         }
      }

      @SubscribeEvent
      public static void onAbilityTrigger(AbilityTriggerEvent.Post event) {
         if (!event.getEntity().level().isClientSide) {
            Ability ability = event.getAbility();
            if (ChantHandler.isChanted(event.getEntity(), ability) && ability.getActivationType(event.getEntity()) == Ability.ActivationType.INSTANT) {
               LivingEntity owner = event.getEntity();
               ServerChantHandler.messages.remove(owner.getUUID());
               if (event.getEntity() instanceof ServerPlayer player) {
                  PacketHandler.sendToClient(new ClearChantsC2SPacket(), player);
               }
            }
         }
      }

      @SubscribeEvent
      public static void onAbilityStop(AbilityStopEvent event) {
         if (!event.getEntity().level().isClientSide) {
            Ability ability = event.getAbility();
            if (ChantHandler.isChanted(event.getEntity(), ability)) {
               LivingEntity owner = event.getEntity();
               ServerChantHandler.messages.remove(owner.getUUID());
               if (event.getEntity() instanceof ServerPlayer player) {
                  PacketHandler.sendToClient(new ClearChantsC2SPacket(), player);
               }
            }
         }
      }

      @SubscribeEvent
      public static void onServerChat(ServerChatEvent event) {
         ServerPlayer sender = event.getPlayer();
         String message = event.getRawText();
         ServerLevel level = sender.serverLevel();
         sender.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasSilenced()) {
               event.setCanceled(true);
               sender.displayClientMessage(Component.literal("You are silenced and cannot speak!"), true);
            } else {
               boolean sent = ServerChantHandler.onChant(sender, message.toLowerCase());
               if (sent) {
                  event.setCanceled(true);
                  double radius = 256.0;
                  sender.sendSystemMessage(Component.literal("<" + sender.getName().getString() + "> " + message));

                  for (ServerPlayer other : level.players()) {
                     if (sender != other && other.distanceTo(sender) <= radius && other.level().dimension().equals(sender.level().dimension())) {
                        other.sendSystemMessage(Component.literal("<" + sender.getName().getString() + "> " + message));
                     }
                  }
               }
            }
         });
      }
   }
}
