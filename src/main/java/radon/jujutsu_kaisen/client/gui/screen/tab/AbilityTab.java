package radon.jujutsu_kaisen.client.gui.screen.tab;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.AbilityWidget;

public class AbilityTab extends JJKTab {
   private static final Component TITLE = Component.translatable(String.format("gui.%s.ability", "jujutsu_kaisen"));
   private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");
   private final Map<Ability, AbilityWidget> abilities = new HashMap<>();
   private final List<AbilityWidget> roots = new ArrayList<>();
   private float fade;

   public AbilityTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
      super(minecraft, screen, type, index, page, Items.ENDER_PEARL.getDefaultInstance(), TITLE, BACKGROUND, true);
      if (this.minecraft.player != null) {
         for (RegistryObject<Ability> entry : JJKAbilities.ABILITIES.getEntries()) {
            Ability ability = (Ability)entry.get();
            if (ability.isDisplayed(this.minecraft.player) && ability.getParent(this.minecraft.player) == null) {
               this.addAbilityAndChildren(ability);
            }
         }
      }
   }

   private void addAbilityAndChildren(Ability ability) {
      this.addAbility(ability);

      for (RegistryObject<Ability> entry : JJKAbilities.ABILITIES.getEntries()) {
         Ability current = (Ability)entry.get();
         if (current.isDisplayed(this.minecraft.player) && current.getParent(this.minecraft.player) == ability) {
            this.addAbilityAndChildren(current);
         }
      }
   }

   @Override
   public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
      super.drawContents(pGuiGraphics, pX, pY);
      if (this.minecraft.player != null) {
         int i = (this.screen.width - 252) / 2;
         int j = (this.screen.height - 140) / 2;
         int xOffset = i + 18;
         int yOffset = j + 27;
         ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         pGuiGraphics.drawString(
            this.minecraft.font,
            Component.translatable(String.format("gui.%s.ability.points", "jujutsu_kaisen"), new Object[]{cap.getPoints()}),
            xOffset,
            yOffset,
            16777215,
            true
         );
      }
   }

   @Override
   protected void drawCustom(GuiGraphics graphics, int x, int y) {
      for (AbilityWidget root : this.roots) {
         root.drawConnectivity(graphics, x, y, true);
         root.drawConnectivity(graphics, x, y, false);
         root.draw(graphics, x, y);
      }
   }

   @Nullable
   public AbilityWidget getAbility(Ability ability) {
      return this.abilities.get(ability);
   }

   @Override
   public void drawTooltips(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int pOffsetX, int pOffsetY) {
      super.drawTooltips(pGuiGraphics, pMouseX, pMouseY, pOffsetX, pOffsetY);
      pGuiGraphics.pose().pushPose();
      pGuiGraphics.pose().translate(pOffsetX + 9, pOffsetY + 18, 400.0F);
      RenderSystem.enableDepthTest();
      pGuiGraphics.pose().pushPose();
      pGuiGraphics.pose().translate(0.0, 0.0, -200.0);
      pGuiGraphics.fill(0, 0, 234, 113, Mth.floor(this.fade * 255.0F) << 24);
      boolean hovered = false;
      int i = Mth.floor(this.scrollX);
      int j = Mth.floor(this.scrollY);
      if (pMouseX - pOffsetX - 9 > 0 && pMouseX - pOffsetX - 9 < 234 && pMouseY - pOffsetY - 18 > 0 && pMouseY - pOffsetY - 18 < 113) {
         for (AbilityWidget widget : this.abilities.values()) {
            if (widget.isMouseOver(i, j, pMouseX - pOffsetX - 9, pMouseY - pOffsetY - 18)) {
               hovered = true;
               widget.drawHover(pGuiGraphics, i, j, this.fade, pOffsetX, pOffsetY);
               break;
            }
         }
      }

      pGuiGraphics.pose().popPose();
      if (hovered) {
         this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
      } else {
         this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
      }

      RenderSystem.disableDepthTest();
      pGuiGraphics.pose().popPose();
   }

   public void addAbility(Ability ability) {
      AbilityWidget widget = new AbilityWidget(this, this.minecraft, ability);
      if (ability.getParent(this.minecraft.player) == null) {
         this.roots.add(widget);
      }

      this.abilities.put(ability, widget);
      int i = widget.getX();
      int j = i + 28;
      int k = widget.getY();
      int l = k + 27;
      this.minX = Math.min(this.minX, i);
      this.maxX = Math.max(this.maxX, j);
      this.minY = Math.min(this.minY, k);
      this.maxY = Math.max(this.maxY, l);

      for (AbilityWidget child : this.abilities.values()) {
         child.attachToParent();
      }
   }

   @Override
   public void addWidgets() {
   }

   @Override
   public void mouseClicked(double pMouseX, double pMouseY, int pButton) {
      int i = Mth.floor(this.scrollX);
      int j = Mth.floor(this.scrollY);
      if (pMouseX > 0.0 && pMouseX < 234.0 && pMouseY > 0.0 && pMouseY < 113.0) {
         for (AbilityWidget widget : this.abilities.values()) {
            if (widget.isMouseOver(i, j, (int)pMouseX, (int)pMouseY)) {
               widget.unlock();
               break;
            }
         }
      }
   }
}
