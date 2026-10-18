package radon.jujutsu_kaisen.client.domain;

import java.util.List;
import net.minecraft.client.Minecraft;
import radon.jujutsu_kaisen.network.packet.s2c.DomainClashS2CPacket;

public class ClientDomainClashHandler {
   private static final int TIMEOUT = 10;
   private static List<DomainClashS2CPacket.Entry> entries = List.of();
   private static long updated = Long.MIN_VALUE;

   public static void accept(List<DomainClashS2CPacket.Entry> incoming) {
      entries = incoming;
      Minecraft mc = Minecraft.getInstance();
      updated = mc.level == null ? Long.MIN_VALUE : mc.level.getGameTime();
   }

   public static List<DomainClashS2CPacket.Entry> getEntries() {
      Minecraft mc = Minecraft.getInstance();
      return mc.level != null && mc.level.getGameTime() - updated <= 10L ? entries : List.of();
   }
}
