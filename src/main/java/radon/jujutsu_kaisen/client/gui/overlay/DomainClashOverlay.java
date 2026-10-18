package radon.jujutsu_kaisen.client.gui.overlay;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import radon.jujutsu_kaisen.client.domain.ClientDomainClashHandler;
import radon.jujutsu_kaisen.network.packet.s2c.DomainClashS2CPacket;

public class DomainClashOverlay {
   private static final int WIDTH = 182;
   private static final int HEIGHT = 5;
   private static final int TOP = 8;
   private static final int GAP = 2;
   private static final int BACKGROUND = -1610612736;
   private static final int BORDER = -16777216;
   private static final int SELF = -11141121;
   private static final int ENEMY = -43691;
   public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
      Minecraft mc = gui.getMinecraft();
      if (mc.player != null && !mc.options.hideGui) {
         List<DomainClashS2CPacket.Entry> entries = ClientDomainClashHandler.getEntries();
         if (entries.size() >= 2) {
            int count = entries.size();
            int left = (width - 182) / 2;
            int slot = 182 / count;
            int centre = left + 91;
            graphics.fill(left - 1, 7, left + 182 + 1, 14, -16777216);
            graphics.fill(left, 8, left + 182, 13, -1610612736);

            for (int i = 0; i < count; i++) {
               DomainClashS2CPacket.Entry entry = entries.get(i);
               int slotLeft = left + i * slot + (i > 0 ? 2 : 0);
               int slotRight = left + (i + 1) * slot - (i < count - 1 ? 2 : 0);
               int filled = Math.round((slotRight - slotLeft) * Math.max(0.0F, Math.min(1.0F, entry.fraction())));
               int colour = entry.self() ? -11141121 : -43691;
               if (slotRight <= centre) {
                  graphics.fill(slotRight - filled, 8, slotRight, 13, colour);
               } else if (slotLeft >= centre) {
                  graphics.fill(slotLeft, 8, slotLeft + filled, 13, colour);
               } else {
                  int mid = (slotLeft + slotRight) / 2;
                  graphics.fill(mid - filled / 2, 8, mid + filled / 2, 13, colour);
               }
            }

            drawNames(graphics, mc, entries, left, slot, count);
         }
      }
   };

   private static void drawNames(GuiGraphics graphics, Minecraft mc, List<DomainClashS2CPacket.Entry> entries, int left, int slot, int count) {
      int y = 16;

      for (int i = 0; i < count; i++) {
         DomainClashS2CPacket.Entry entry = entries.get(i);
         Component name = Component.literal(entry.name());
         int x = left + i * slot + slot / 2;
         graphics.drawCenteredString(mc.font, name, x, y, entry.self() ? -11141121 : -43691);
      }
   }
}
