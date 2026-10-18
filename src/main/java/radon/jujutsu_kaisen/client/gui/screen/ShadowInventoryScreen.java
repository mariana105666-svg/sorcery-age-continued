package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.ShadowInventoryTakeC2SPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

public class ShadowInventoryScreen extends Screen {
   private static final int RADIUS_IN = 50;
   private static final int RADIUS_OUT = 100;
   private final List<ItemStack> items = new ArrayList<>();
   private int hovered = -1;

   public ShadowInventoryScreen() {
      super(Component.nullToEmpty(null));
   }

   public boolean isPauseScreen() {
      return false;
   }

   protected void init() {
      super.init();
      if (this.minecraft != null) {
         List<ItemStack> inventory = this.getItems();
         if (inventory != null) {
            this.items.addAll(inventory);
            if (this.items.isEmpty()) {
               this.onClose();
            }
         }
      }
   }

   @Nullable
   private List<ItemStack> getItems() {
      if (this.minecraft == null || this.minecraft.player == null) {
         return null;
      } else if (!this.minecraft.player.getCapability(TenShadowsDataHandler.INSTANCE).isPresent()) {
         return null;
      } else {
         ITenShadowsData cap = (ITenShadowsData)this.minecraft.player.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getShadowInventory();
      }
   }

   private void drawSlot(PoseStack poseStack, BufferBuilder buffer, float centerX, float centerY, float startAngle, float endAngle, int color) {
      float angle = endAngle - startAngle;
      float precision = 0.0069444445F;
      int sections = Math.max(1, Mth.ceil(angle / precision));
      angle = endAngle - startAngle;
      int r = color >> 16 & 0xFF;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      int a = color >> 24 & 0xFF;
      float slice = angle / sections;

      for (int i = 0; i < sections; i++) {
         float angle1 = startAngle + i * slice;
         float angle2 = startAngle + (i + 1) * slice;
         float x1 = centerX + 50.0F * (float)Math.cos(angle1);
         float y1 = centerY + 50.0F * (float)Math.sin(angle1);
         float x2 = centerX + 100.0F * (float)Math.cos(angle1);
         float y2 = centerY + 100.0F * (float)Math.sin(angle1);
         float x3 = centerX + 100.0F * (float)Math.cos(angle2);
         float y3 = centerY + 100.0F * (float)Math.sin(angle2);
         float x4 = centerX + 50.0F * (float)Math.cos(angle2);
         float y4 = centerY + 50.0F * (float)Math.sin(angle2);
         Matrix4f pose = poseStack.last().pose();
         buffer.vertex(pose, x2, y2, 0.0F).color(r, g, b, a).endVertex();
         buffer.vertex(pose, x1, y1, 0.0F).color(r, g, b, a).endVertex();
         buffer.vertex(pose, x4, y4, 0.0F).color(r, g, b, a).endVertex();
         buffer.vertex(pose, x3, y3, 0.0F).color(r, g, b, a).endVertex();
      }
   }

   public void onClose() {
      if (this.hovered != -1) {
         PacketHandler.sendToServer(new ShadowInventoryTakeC2SPacket(this.hovered));
      }

      super.onClose();
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && this.hovered != -1) {
         PacketHandler.sendToServer(new ShadowInventoryTakeC2SPacket(this.hovered));
         this.items.remove(this.hovered);
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public static void drawCenteredString(@NotNull GuiGraphics pGuiGraphics, Font pFont, Component pText, int pX, int pY, int pColor) {
      FormattedCharSequence sql = pText.getVisualOrderText();
      pGuiGraphics.drawString(pFont, sql, pX - pFont.width(sql) / 2, pY, pColor);
   }

   public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
      super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTicks);
      if (this.minecraft != null && this.minecraft.level != null && this.minecraft.player != null) {
         int centerX = this.width / 2;
         int centerY = this.height / 2;
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         pGuiGraphics.pose().pushPose();
         Tesselator tesselator = Tesselator.getInstance();
         BufferBuilder buffer = tesselator.getBuilder();
         buffer.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

         for (int i = 0; i < this.items.size(); i++) {
            float startAngle = this.getAngleFor(i - 0.5F);
            float endAngle = this.getAngleFor(i + 0.5F);
            int white = HelperMethods.toRGB24(255, 255, 255, 150);
            int black = HelperMethods.toRGB24(0, 0, 0, 150);
            this.drawSlot(pGuiGraphics.pose(), buffer, centerX, centerY, startAngle, endAngle, this.hovered == i ? white : black);
         }

         tesselator.end();
         RenderSystem.disableBlend();
         pGuiGraphics.pose().popPose();
         float radius = 75.0F;

         for (int i = 0; i < this.items.size(); i++) {
            float start = this.getAngleFor(i - 0.5F);
            float end = this.getAngleFor(i + 0.5F);
            float middle = (start + end) / 2.0F;
            int posX = (int)(centerX + radius * (float)Math.cos(middle));
            int posY = (int)(centerY + radius * (float)Math.sin(middle));
            ItemStack stack = this.items.get(i);
            if (this.hovered == i) {
               int x = this.width / 2;
               int y = this.height / 2 - 9 / 2;
               drawCenteredString(pGuiGraphics, this.font, stack.getHoverName(), x, y, 16777215);
            }

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(-8.0F, -8.0F, 0.0F);
            pGuiGraphics.renderFakeItem(stack, posX, posY);
            pGuiGraphics.renderItemDecorations(this.font, stack, posX, posY);
            pGuiGraphics.pose().popPose();
         }
      }
   }

   public void mouseMoved(double pMouseX, double pMouseY) {
      super.mouseMoved(pMouseX, pMouseY);
      int centerX = this.width / 2;
      int centerY = this.height / 2;
      double mouseAngle = Math.atan2(pMouseY - centerY, pMouseX - centerX);
      double mousePos = Math.sqrt(Math.pow(pMouseX - centerX, 2.0) + Math.pow(pMouseY - centerY, 2.0));
      if (this.items.size() > 0) {
         float startAngle = this.getAngleFor(-0.5);
         float endAngle = this.getAngleFor(this.items.size() - 0.5F);

         while (mouseAngle < startAngle) {
            mouseAngle += (float) (Math.PI * 2);
         }

         while (mouseAngle >= endAngle) {
            mouseAngle -= (float) (Math.PI * 2);
         }
      }

      this.hovered = -1;

      for (int i = 0; i < this.items.size(); i++) {
         float startAngle = this.getAngleFor(i - 0.5F);
         float endAngle = this.getAngleFor(i + 0.5F);
         if (mouseAngle >= startAngle && mouseAngle < endAngle && mousePos >= 50.0 && mousePos < 100.0) {
            this.hovered = i;
            break;
         }
      }
   }

   private float getAngleFor(double i) {
      return this.items.isEmpty() ? 0.0F : (float)((i / this.items.size() + 0.25) * (float) (Math.PI * 2) + Math.PI);
   }
}
