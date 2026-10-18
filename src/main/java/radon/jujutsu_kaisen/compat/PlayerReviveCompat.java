package radon.jujutsu_kaisen.compat;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.ExperienceHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import team.creative.playerrevive.api.event.PlayerBleedOutEvent;
import team.creative.playerrevive.server.PlayerReviveServer;

@EventBusSubscriber(
   modid = "playerrevive",
   bus = Bus.FORGE
)
public class PlayerReviveCompat {
   public static void load() {
   }

   public static void Kill(Player player) {
      PlayerReviveServer.getBleeding(player).forceBledOut();
   }

   public static void Revive(Player player) {
      PlayerReviveServer.revive(player);
   }

   public static boolean IsBleedingOut(Player player) {
      return PlayerReviveServer.isBleeding(player);
   }

   @SubscribeEvent
   public static void PlayerBleedOutEvent(PlayerBleedOutEvent event) {
      LivingEntity entity = event.getEntity();
      if (entity.level() instanceof ServerLevel level) {
         if (entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            LivingEntity attacker = null;
            RegistryAccess registry = level.registryAccess();
            if (event.getBleeding().getSource(registry).getEntity() instanceof LivingEntity living) {
               attacker = living;
            }

            ExperienceHandler.killExperience(entity, attacker, level);
         }
      }
   }
}
