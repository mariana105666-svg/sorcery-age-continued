package radon.jujutsu_kaisen.client.gui.screen.base;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransfiguredSoul;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.ability.cursed_speech.ICursedSpeech;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.gui.screen.DisplayItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.UncopyAbilityC2SPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

public abstract class RadialScreen extends Screen {
   protected static final int RADIUS_IN = 20;
   protected static final int RADIUS_OUT = 100;
   private static final ResourceLocation HEALTH_BAR = new ResourceLocation("jujutsu_kaisen", "textures/gui/overlay/health_bar.png");
   private static final float HEALTH_BAR_SCALE = 0.3F;
   private final List<List<DisplayItem>> pages = new ArrayList<>();
   protected int hovered = -1;
   private static int page;
   private int hover;
   private long lastPageSwitchTime = 0L;
   private static final long PAGE_SWITCH_INTERVAL_MS = 500L;

   public RadialScreen() {
      super(Component.nullToEmpty(null));
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean mouseScrolled(double x, double y, double delta) {
      int i = (int)Math.signum(delta);
      if (this.pages.size() > 1 && i != 0) {
         if (this.pages.size() - 1 > page && delta < 0.0) {
            page++;
         }

         if (page > 0 && delta > 0.0) {
            page--;
         }
      }

      return false;
   }

   protected void init() {
      super.init();
      List<DisplayItem> items = this.getItems();
      int count = items.size() / 12;

      for (int i = 0; i < count; i++) {
         int index = i * 12;
         this.pages.add(items.subList(index, index + 12));
      }

      int remainder = items.size() % 12;
      if (remainder > 0) {
         int index = count * 12;
         this.pages.add(items.subList(index, index + remainder));
      }

      if (page >= this.pages.size()) {
         page = 0;
      }

      if (this.pages.isEmpty()) {
         this.onClose();
      }
   }

   @Nullable
   public List<DisplayItem> getCurrent() {
      return this.pages != null && !this.pages.isEmpty() && page >= 0 && page < this.pages.size() ? this.pages.get(page) : null;
   }

   protected abstract List<DisplayItem> getItems();

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
         float x1 = centerX + 20.0F * (float)Math.cos(angle1);
         float y1 = centerY + 20.0F * (float)Math.sin(angle1);
         float x2 = centerX + 100.0F * (float)Math.cos(angle1);
         float y2 = centerY + 100.0F * (float)Math.sin(angle1);
         float x3 = centerX + 100.0F * (float)Math.cos(angle2);
         float y3 = centerY + 100.0F * (float)Math.sin(angle2);
         float x4 = centerX + 20.0F * (float)Math.cos(angle2);
         float y4 = centerY + 20.0F * (float)Math.sin(angle2);
         Matrix4f pose = poseStack.last().pose();
         buffer.vertex(pose, x2, y2, 0.0F).color(r, g, b, a).endVertex();
         buffer.vertex(pose, x1, y1, 0.0F).color(r, g, b, a).endVertex();
         buffer.vertex(pose, x4, y4, 0.0F).color(r, g, b, a).endVertex();
         buffer.vertex(pose, x3, y3, 0.0F).color(r, g, b, a).endVertex();
      }
   }

   public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
      if (this.minecraft != null && this.minecraft.player != null) {
         ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (this.hovered >= 0 && this.getCurrent() != null && this.hovered < this.getCurrent().size()) {
            DisplayItem item = this.getCurrent().get(this.hovered);
            if (pButton == 1 && item.type == DisplayItem.Type.COPIED) {
               PacketHandler.sendToServer(new UncopyAbilityC2SPacket(item.copied));
               cap.uncopy(item.copied);
            }
         }
      }

      return super.mouseClicked(pMouseX, pMouseY, pButton);
   }

   private static void renderEntityInInventoryFollowsAngle(
      PoseStack pPoseStack, int pX, int pY, int pScale, float angleXComponent, float angleYComponent, Entity pEntity
   ) {
      Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
      Quaternionf quaternionf1 = new Quaternionf().rotateX(angleYComponent * 20.0F * (float) (Math.PI / 180.0));
      quaternionf.mul(quaternionf1);
      if (pEntity instanceof LivingEntity living) {
         living.yBodyRot = 180.0F + angleXComponent * 20.0F;
      }

      pEntity.setYRot(180.0F + angleXComponent * 40.0F);
      pEntity.setXRot(-angleYComponent * 20.0F);
      if (pEntity instanceof LivingEntity living) {
         living.yHeadRot = pEntity.getYRot();
         living.yHeadRotO = pEntity.getYRot();
      }

      renderEntityInInventory(pPoseStack, pX, pY, pScale, quaternionf, quaternionf1, pEntity);
   }

   private static void renderEntityInInventory(
      PoseStack pPoseStack, int pX, int pY, int pScale, Quaternionf p_275229_, @Nullable Quaternionf pCameraOrientation, Entity pEntity
   ) {
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      posestack.translate(0.0, 0.0, 1000.0);
      RenderSystem.applyModelViewMatrix();
      pPoseStack.pushPose();
      pPoseStack.translate(pX, pY, -950.0);
      pPoseStack.mulPoseMatrix(new Matrix4f().scaling(pScale, pScale, -pScale));
      pPoseStack.mulPose(p_275229_);
      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      if (pCameraOrientation != null) {
         pCameraOrientation.conjugate();
         dispatcher.overrideCameraOrientation(pCameraOrientation);
      }

      dispatcher.setRenderShadow(false);
      BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
      RenderSystem.runAsFancy(() -> dispatcher.render(pEntity, 0.0, 0.0, 0.0, 0.0F, 1.0F, pPoseStack, buffer, 15728880));
      buffer.endBatch();
      dispatcher.setRenderShadow(true);
      pPoseStack.popPose();
      Lighting.setupFor3DItems();
      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
   }

   public void renderBackground(GuiGraphics pGuiGraphics) {
   }

   protected boolean isActive(DisplayItem item) {
      if (this.minecraft != null && this.minecraft.player != null) {
         ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return item.type == DisplayItem.Type.ABILITY && JJKAbilities.hasToggled(this.minecraft.player, item.ability)
            || item.type == DisplayItem.Type.COPIED && cap.getCurrentCopied() == item.copied
            || item.type == DisplayItem.Type.ABSORBED && cap.getCurrentAbsorbed() == item.absorbed
            || item.type == DisplayItem.Type.STOLEN && cap.getCurrentStolen() == item.stolen;
      } else {
         return false;
      }
   }

   public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
      super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTicks);
      if (this.minecraft != null && this.minecraft.level != null && this.minecraft.player != null) {
         if (this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
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

            for (int i = 0; i < this.getCurrent().size(); i++) {
               float startAngle = this.getAngleFor(i - 0.5F);
               float endAngle = this.getAngleFor(i + 0.5F);
               DisplayItem item = this.getCurrent().get(i);
               int white = HelperMethods.toRGB24(255, 255, 255, 150);
               int black = HelperMethods.toRGB24(0, 0, 0, 150);
               int color;
               if (this.isActive(item)) {
                  color = this.hovered == i ? black : white;
               } else {
                  color = this.hovered == i ? white : black;
               }

               this.drawSlot(pGuiGraphics.pose(), buffer, centerX, centerY, startAngle, endAngle, color);
            }

            tesselator.end();
            RenderSystem.disableBlend();
            pGuiGraphics.pose().popPose();
            if (this.pages.size() > 1) {
               if (this.pages.size() - 1 > page) {
                  String symbol = ">";
                  int x = centerX + 100 + 20;
                  int y = centerY - 9;
                  pGuiGraphics.drawCenteredString(this.font, symbol, x, y, 16777215);
               }

               if (page > 0) {
                  String symbol = "<";
                  int x = centerX - 100 - 20;
                  int y = centerY - 9;
                  pGuiGraphics.drawCenteredString(this.font, symbol, x, y, 16777215);
               }
            }

            float radius = 60.0F;

            for (int i = 0; i < this.getCurrent().size(); i++) {
               float start = this.getAngleFor(i - 0.5F);
               float end = this.getAngleFor(i + 0.5F);
               float middle = (start + end) / 2.0F;
               int posX = (int)(centerX + radius * (float)Math.cos(middle));
               int posY = (int)(centerY + radius * (float)Math.sin(middle));
               DisplayItem item = this.getCurrent().get(i);
               if (this.hovered == i) {
                  List<Component> lines = new ArrayList<>();
                  if (item.type == DisplayItem.Type.ABILITY) {
                     float cost = item.ability.getRealCost(this.minecraft.player);
                     if (cost > 0.0F) {
                        lines.add(Component.translatable(String.format("gui.%s.ability_overlay.cost", "jujutsu_kaisen"), new Object[]{cost}));
                     }

                     int cooldown = item.ability.getRealCooldown(this.minecraft.player);
                     if (cooldown > 0) {
                        lines.add(
                           Component.translatable(String.format("gui.%s.ability_overlay.cooldown", "jujutsu_kaisen"), new Object[]{Math.round(cooldown / 20.0F)})
                        );
                     }

                     if (item instanceof Ability.IDurationable durationable) {
                        int duration = durationable.getRealDuration(this.minecraft.player);
                        if (duration > 0) {
                           Component durationText = Component.translatable(
                              String.format("gui.%s.ability_overlay.duration", "jujutsu_kaisen"), new Object[]{duration / 20.0F}
                           );
                           lines.add(durationText);
                        }
                     }

                     if (item instanceof ITransfiguredSoul soul) {
                        Component soulCostText = Component.translatable(
                           String.format("gui.%s.ability_overlay.soul_cost", "jujutsu_kaisen"), new Object[]{soul.getSoulCost()}
                        );
                        lines.add(soulCostText);
                     }

                     if (item instanceof ICursedSpeech speech) {
                        Component throatDamageText = Component.translatable(
                           String.format("gui.%s.ability_overlay.throat_damage", "jujutsu_kaisen"), new Object[]{speech.getThroatDamage()}
                        );
                        lines.add(throatDamageText);
                     }
                  } else if (item.type == DisplayItem.Type.CURSE) {
                     Component costText = Component.translatable(
                        String.format("gui.%s.ability_overlay.cost", "jujutsu_kaisen"), new Object[]{JJKAbilities.getCurseCost(item.curse.getKey())}
                     );
                     lines.add(costText);
                     Component experienceText = Component.translatable(
                        String.format("gui.%s.ability_overlay.experience", "jujutsu_kaisen"),
                        new Object[]{JJKAbilities.getCurseExperience(item.curse.getKey())}
                     );
                     lines.add(experienceText);
                  }

                  int x = this.width / 2;
                  int y = this.height / 2 - 9 / 2 - (lines.size() - 1) * 9;

                  for (Component line : lines) {
                     pGuiGraphics.drawCenteredString(this.font, line, x, y, 16777215);
                     y += 9;
                  }
               }

               if (item.type == DisplayItem.Type.CURSE) {
                  Entity entity = JJKAbilities.createCurse(this.minecraft.player, item.curse.getKey());
                  if (entity != null) {
                     float height = entity.getBbHeight();
                     int scale = (int)Math.max(3.0F, 10.0F - entity.getBbHeight());
                     renderEntityInInventoryFollowsAngle(pGuiGraphics.pose(), posX, (int)(posY + height * scale / 2.0F), scale, -1.0F, -0.5F, entity);
                  }
               } else if (item.ability instanceof Summon<?> summon && summon.display()) {
                  Entity entity = summon.getTypes().get(0).create(this.minecraft.level);
                  if (entity != null) {
                     float height = entity.getBbHeight();
                     int scale = (int)Math.max(3.0F, 10.0F - entity.getBbHeight());
                     renderEntityInInventoryFollowsAngle(pGuiGraphics.pose(), posX, (int)(posY + height * scale / 2.0F), scale, -1.0F, -0.5F, entity);
                  }
               } else if (item.type == DisplayItem.Type.ABILITY) {
                  int y = posY - 9 / 2;
                  pGuiGraphics.pose().pushPose();
                  pGuiGraphics.pose().scale(0.5F, 0.5F, 0.0F);
                  pGuiGraphics.pose().translate(posX, y, 0.0F);
                  pGuiGraphics.drawCenteredString(this.font, item.ability.getName(), posX, y, 16777215);
                  pGuiGraphics.pose().popPose();
               } else if (item.type == DisplayItem.Type.COPIED || item.type == DisplayItem.Type.ABSORBED || item.type == DisplayItem.Type.STOLEN) {
                  int y = posY - 9 / 2;
                  pGuiGraphics.pose().pushPose();
                  pGuiGraphics.pose().scale(0.5F, 0.5F, 0.0F);
                  pGuiGraphics.pose().translate(posX, y, 0.0F);
                  pGuiGraphics.drawCenteredString(
                     this.font,
                     item.type == DisplayItem.Type.COPIED
                        ? item.copied.getName()
                        : (item.type == DisplayItem.Type.STOLEN ? item.stolen.getName() : item.absorbed.getName()),
                     posX,
                     y,
                     11141290
                  );
                  pGuiGraphics.pose().popPose();
               }
            }
         }
      }
   }

   public void mouseMoved(double pMouseX, double pMouseY) {
      super.mouseMoved(pMouseX, pMouseY);
      int centerX = this.width / 2;
      int centerY = this.height / 2;
      double mouseAngle = Math.atan2(pMouseY - centerY, pMouseX - centerX);
      double mousePos = Math.sqrt(Math.pow(pMouseX - centerX, 2.0) + Math.pow(pMouseY - centerY, 2.0));
      if (this.getCurrent() != null && !this.getCurrent().isEmpty()) {
         float startAngle = this.getAngleFor(-0.5);
         float endAngle = this.getAngleFor(this.getCurrent().size() - 0.5F);

         while (mouseAngle < startAngle) {
            mouseAngle += (float) (Math.PI * 2);
         }

         while (mouseAngle >= endAngle) {
            mouseAngle -= (float) (Math.PI * 2);
         }

         this.hovered = -1;

         for (int i = 0; i < this.getCurrent().size(); i++) {
            float currentStart = this.getAngleFor(i - 0.5F);
            float currentEnd = this.getAngleFor(i + 0.5F);
            if (mouseAngle >= currentStart && mouseAngle < currentEnd && mousePos >= 20.0) {
               this.hovered = i;
               break;
            }
         }

         if (!(mousePos < 100.0)) {
            if (this.pages.size() > 1) {
               long now = System.currentTimeMillis();
               boolean switched = false;
               if (this.pages.size() - 1 > page
                  && pMouseX > this.width / 2.0
                  && pMouseX < this.width
                  && pMouseY > 0.0
                  && pMouseY < this.height
                  && now - this.lastPageSwitchTime >= 500L) {
                  page++;
                  this.lastPageSwitchTime = now;
                  switched = true;
               }

               if (!switched
                  && page > 0
                  && pMouseX > 0.0
                  && pMouseX < this.width / 2.0
                  && pMouseY > 0.0
                  && pMouseY < this.height
                  && now - this.lastPageSwitchTime >= 500L) {
                  page--;
                  this.lastPageSwitchTime = now;
               }

               if ((!(pMouseX < this.width / 2.0) || page <= 0) && (!(pMouseX > this.width / 2.0) || this.pages.size() - 1 <= page)) {
                  this.lastPageSwitchTime = 0L;
               }
            }
         }
      }
   }

   private float getAngleFor(double i) {
      return this.getCurrent() != null && !this.getCurrent().isEmpty()
         ? (float)((i / this.getCurrent().size() + 0.25) * (float) (Math.PI * 2) + Math.PI)
         : 0.0F;
   }
}
