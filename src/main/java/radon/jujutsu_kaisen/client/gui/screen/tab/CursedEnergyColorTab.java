package radon.jujutsu_kaisen.client.gui.screen.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.SetCursedEnergyColorC2SPacket;

public class CursedEnergyColorTab extends JJKTab {
   private static final Component TITLE = Component.translatable(String.format("gui.%s.cursed_energy_color", "jujutsu_kaisen"));
   private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");
   private ForgeSlider rSlider;
   private ForgeSlider gSlider;
   private ForgeSlider bSlider;
   private float oldR;
   private float oldG;
   private float oldB;

   public CursedEnergyColorTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
      super(minecraft, screen, type, index, page, Items.RED_DYE.getDefaultInstance(), TITLE, BACKGROUND, false);
   }

   @Override
   public void tick() {
      float r = (float)this.rSlider.getValue();
      float g = (float)this.gSlider.getValue();
      float b = (float)this.bSlider.getValue();
      if (r != this.oldR || g != this.oldG || b != this.oldB) {
         if (this.minecraft != null && this.minecraft.player != null) {
            int color = ARGB32.color(255, Math.round(r), Math.round(g), Math.round(b));
            ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            PacketHandler.sendToServer(new SetCursedEnergyColorC2SPacket(color));
            cap.setCursedEnergyColor(color);
         }

         this.oldR = r;
         this.oldG = g;
         this.oldB = b;
      }
   }

   @Override
   public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
      super.drawContents(pGuiGraphics, pX, pY);
      int i = (this.screen.width - 252) / 2;
      int j = (this.screen.height - 140) / 2;
      int centerY = j + 70;
      pGuiGraphics.drawString(
         this.minecraft.font,
         Component.translatable(String.format("gui.%s.cursed_energy_color.red", "jujutsu_kaisen")),
         i + 71,
         centerY - 32 - 9 - 2,
         16777215,
         true
      );
      pGuiGraphics.drawString(
         this.minecraft.font,
         Component.translatable(String.format("gui.%s.cursed_energy_color.green", "jujutsu_kaisen")),
         i + 71,
         centerY - 9 - 2,
         16777215,
         true
      );
      pGuiGraphics.drawString(
         this.minecraft.font,
         Component.translatable(String.format("gui.%s.cursed_energy_color.blue", "jujutsu_kaisen")),
         i + 71,
         centerY + 32 - 9 - 2,
         16777215,
         true
      );
   }

   @Override
   public void addWidgets() {
      if (this.minecraft != null && this.minecraft.player != null) {
         ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         Vector3f color = Vec3.fromRGB24(cap.getCursedEnergyColor()).toVector3f();
         int i = (this.screen.width - 252) / 2;
         int j = (this.screen.height - 140) / 2;
         int centerY = j + 70;
         this.rSlider = new ForgeSlider(i + 71, centerY - 32, 110, 16, Component.empty(), Component.empty(), 0.0, 255.0, color.x * 255.0F, 0.1, 0, true);
         this.addRenderableWidget(this.rSlider);
         this.gSlider = new ForgeSlider(i + 71, centerY, 110, 16, Component.empty(), Component.empty(), 0.0, 255.0, color.y * 255.0F, 0.1, 0, true);
         this.addRenderableWidget(this.gSlider);
         this.bSlider = new ForgeSlider(i + 71, centerY + 32, 110, 16, Component.empty(), Component.empty(), 0.0, 255.0, color.z * 255.0F, 0.1, 0, true);
         this.addRenderableWidget(this.bSlider);
      }
   }
}
