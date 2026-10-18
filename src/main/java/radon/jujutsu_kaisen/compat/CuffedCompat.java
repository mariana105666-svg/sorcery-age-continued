package radon.jujutsu_kaisen.compat;

import com.lazrproductions.cuffed.cap.provider.RestrainableCapabilityProvider;
import net.minecraft.world.entity.player.Player;

public class CuffedCompat {
   public static void load() {
   }

   public static boolean headRestrained(Player player) {
      return player.getCapability(RestrainableCapabilityProvider.CAP).map(cap -> cap.headRestrained()).orElse(false);
   }

   public static boolean legsRestrained(Player player) {
      return player.getCapability(RestrainableCapabilityProvider.CAP).map(cap -> cap.legsRestrained()).orElse(false);
   }

   public static boolean armsRestrained(Player player) {
      return player.getCapability(RestrainableCapabilityProvider.CAP).map(cap -> cap.armsRestrained()).orElse(false);
   }
}
