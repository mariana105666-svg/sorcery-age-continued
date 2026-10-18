package radon.jujutsu_kaisen.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

public class CursedEnergyOverlay {
   public static ResourceLocation BAR = new ResourceLocation("jujutsu_kaisen", "textures/gui/overlay/energy_bar.png");
   public static ResourceLocation BLACK_FLASH_BAR = new ResourceLocation("jujutsu_kaisen", "textures/gui/overlay/blackflash_bar.png");
   public static ResourceLocation BACKGROUND = new ResourceLocation("jujutsu_kaisen", "textures/gui/overlay/energy_bar_background.png");
   private static final int BAR_WIDTH = 236;
   private static final int BAR_HEIGHT = 42;
   private static final float BAR_SCALE = 0.5F;
   private static final int FILL_TRIM = 1;
   private static final int BOTTOM_MARGIN = 8;
   private static final int LABEL_GAP = 6;
   private static final int READOUT_INSET = 29;
   private static final int DRAWN_WIDTH = Math.round(118.0F);
   private static final int DRAWN_HEIGHT = Math.round(21.0F);
   private static final float SCALE = 0.6F;
   public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
      Minecraft mc = gui.getMinecraft();
      if (mc.player != null) {
         if (mc.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData cap = (ISorcererData)mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            graphics.pose().pushPose();
            graphics.pose().scale(0.6F, 0.6F, 0.6F);
            List<Component> above = new ArrayList<>();
            above.add(
               Component.translatable(String.format("gui.%s.cursed_energy_overlay.output", "jujutsu_kaisen"), new Object[]{Math.round(cap.getOutput() * 100.0F)})
            );
            above.add(Component.translatable(String.format("gui.%s.cursed_energy_overlay.experience", "jujutsu_kaisen"), new Object[]{cap.getExperience()}));
            if (cap.hasTechnique(CursedTechnique.IDLE_TRANSFIGURATION)) {
               above.add(
                  Component.translatable(
                     String.format("gui.%s.cursed_energy_overlay.transfigured_souls", "jujutsu_kaisen"), new Object[]{cap.getTransfiguredSouls()}
                  )
               );
            }

            List<Component> below = new ArrayList<>();
            int burnout = cap.getBurnout();
            if (burnout > 0) {
               below.add(
                  Component.translatable(String.format("gui.%s.cursed_energy_overlay.burnout", "jujutsu_kaisen"), new Object[]{Math.round(burnout / 20.0F)})
                     .withStyle(ChatFormatting.DARK_RED)
               );
            }

            for (Ability ability : cap.getToggled()) {
               if (ability instanceof Ability.IAttack) {
                  int cooldown = cap.getRemainingCooldown(ability);
                  if (cooldown > 0) {
                     below.add(
                        Component.translatable(
                           String.format("gui.%s.cursed_energy_overlay.cooldown", "jujutsu_kaisen"),
                           new Object[]{ability.getName(), Math.round(cooldown / 20.0F)}
                        )
                     );
                  }
               }
            }

            Ability channeled = cap.getChanneled();
            if (channeled instanceof Ability.IAttack) {
               int cooldown = cap.getRemainingCooldown(channeled);
               if (cooldown > 0) {
                  below.add(
                     Component.translatable(
                        String.format("gui.%s.cursed_energy_overlay.cooldown", "jujutsu_kaisen"),
                        new Object[]{channeled.getName(), Math.round(cooldown / 20.0F)}
                     )
                  );
               }
            }

            int lineHeight = 9;
            int total = DRAWN_HEIGHT + (below.isEmpty() ? 0 : lineHeight + below.size() * lineHeight);
            int aboveY = height - 8 - total;
            int textX = 20 + DRAWN_WIDTH + 6;
            int textY = aboveY + Math.max(0, (DRAWN_HEIGHT - above.size() * (lineHeight - 1)) / 2);

            for (Component line : above) {
               graphics.drawString(gui.getFont(), line, Math.round(textX * 1.6666666F), Math.round(textY * 1.6666666F), 16777215);
               textY += 9 - 1;
            }

            graphics.pose().popPose();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            graphics.pose().pushPose();
            graphics.pose().scale(0.5F, 0.5F, 1.0F);
            int barX = Math.round(40.0F);
            int barY = Math.round(aboveY / 0.5F);
            if (cap.getEnergy() > 0.0F) {
               float fraction = Math.min(1.0F, cap.getEnergy() / cap.getMaxEnergy());
               int filled = Math.min(235, Math.round(fraction * 236.0F));
               if (filled > 0) {
                  ResourceLocation bar = cap.isInZone() ? BLACK_FLASH_BAR : CursedEnergyBarTexture.get(cap.getCursedEnergyColor());
                  graphics.blit(bar, barX, barY, 0.0F, 0.0F, filled, 41, 236, 42);
               }
            }

            graphics.blit(BACKGROUND, barX, barY, 0.0F, 0.0F, 236, 42, 236, 42);
            graphics.pose().popPose();
            graphics.pose().pushPose();
            graphics.pose().scale(0.6F, 0.6F, 0.6F);
            if (cap.getEnergy() > 0.0F) {
               float readoutY = aboveY + (DRAWN_HEIGHT - 9.0F * 0.6F) / 2.0F;
               graphics.drawString(
                  gui.getFont(),
                  String.format("%.1f / %.1f", cap.getEnergy(), cap.getMaxEnergy()),
                  Math.round(48.333332F),
                  Math.round(readoutY * 1.6666666F),
                  16777215
               );
            }

            aboveY += DRAWN_HEIGHT;
            int belowY = aboveY + 9;

            for (Component line : below) {
               graphics.drawString(gui.getFont(), line, Math.round(33.333332F), Math.round(belowY * 1.6666666F), 16777215);
               belowY += 9;
            }

            graphics.pose().popPose();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   };
}
