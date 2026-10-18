package radon.jujutsu_kaisen.client.gui.screen.tab;

import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList.Entry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import radon.jujutsu_kaisen.capability.data.sorcerer.BindingVow;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.BindingVowListWidget;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.AddBindingVowC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.RemoveBindingVowC2SPacket;

public class BindingVowTab extends JJKTab {
   private static final Component TITLE = Component.translatable(String.format("gui.%s.binding_vow", "jujutsu_kaisen"));
   private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");
   private Button add;
   private Button remove;
   private MultiLineTextWidget description;
   @Nullable
   private BindingVowListWidget.Entry vow;

   public BindingVowTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
      super(minecraft, screen, type, index, page, Items.ENCHANTED_GOLDEN_APPLE.getDefaultInstance(), TITLE, BACKGROUND, false);
   }

   public void setSelectedBindingVow(BindingVowListWidget.Entry entry) {
      this.vow = entry;
      this.description.setMessage(entry.get().getDescription());
   }

   public <T extends Entry<T>> void buildBindingVowList(Consumer<T> consumer, Function<BindingVow, T> result) {
      for (BindingVow vow : BindingVow.values()) {
         consumer.accept(result.apply(vow));
      }
   }

   @Override
   public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
      super.drawContents(pGuiGraphics, pX, pY);
      int i = (this.screen.width - 252) / 2;
      int j = (this.screen.height - 140) / 2;
      int xOffset = i + 18;
      int yOffset = j + 27;
      pGuiGraphics.drawString(
         this.minecraft.font, Component.translatable(String.format("gui.%s.binding_vow.binding_vows", "jujutsu_kaisen")), xOffset, yOffset, 16777215, true
      );
      if (this.minecraft.player != null) {
         ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (this.vow != null && !cap.isCooldownDone(this.vow.get())) {
            int seconds = cap.getRemainingCooldown(this.vow.get()) / 20;
            int minutes = seconds / 60;
            int remaining = seconds - minutes * 60;
            if (seconds > 60) {
               pGuiGraphics.drawString(
                  this.minecraft.font,
                  Component.translatable(String.format("gui.%s.binding_vow.cooldown.minutes", "jujutsu_kaisen"), new Object[]{minutes, remaining}),
                  xOffset + 86,
                  yOffset + 9 + 1 - 9 + 1,
                  16777215,
                  true
               );
            } else if (seconds % 60 != 0) {
               pGuiGraphics.drawString(
                  this.minecraft.font,
                  Component.translatable(String.format("gui.%s.binding_vow.cooldown.seconds", "jujutsu_kaisen"), new Object[]{remaining}),
                  xOffset + 86,
                  yOffset + 9 + 1 - 9 + 1,
                  16777215,
                  true
               );
            }
         }
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.minecraft.player != null) {
         ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         this.add.active = this.vow != null && !cap.hasBindingVow(this.vow.get()) && cap.isCooldownDone(this.vow.get());
         this.remove.active = this.vow != null && cap.hasBindingVow(this.vow.get()) && cap.isCooldownDone(this.vow.get());
      }
   }

   @Override
   public void addWidgets() {
      if (this.minecraft != null && this.minecraft.player != null) {
         int i = (this.screen.width - 252) / 2;
         int j = (this.screen.height - 140) / 2;
         int xOffset = i + 18;
         int yOffset = j + 27;
         this.addRenderableWidget(
            new BindingVowListWidget(this::buildBindingVowList, this::setSelectedBindingVow, this.minecraft, 78, 85, xOffset, yOffset + 9 + 1)
         );
         this.add = Button.builder(Component.translatable(String.format("gui.%s.binding_vow.add", "jujutsu_kaisen")), pButton -> {
            if (this.vow != null) {
               ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               PacketHandler.sendToServer(new AddBindingVowC2SPacket(this.vow.get()));
               cap.addBindingVow(this.vow.get());
               this.add.active = !cap.hasBindingVow(this.vow.get());
               this.remove.active = cap.hasBindingVow(this.vow.get());
               cap.addBindingVowCooldown(this.vow.get());
            }
         }).size(61, 20).pos(xOffset + 86, yOffset + 9 + 66).build();
         this.addRenderableWidget(this.add);
         this.remove = Button.builder(Component.translatable(String.format("gui.%s.binding_vow.remove", "jujutsu_kaisen")), pButton -> {
            if (this.vow != null) {
               ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               PacketHandler.sendToServer(new RemoveBindingVowC2SPacket(this.vow.get()));
               cap.removeBindingVow(this.vow.get());
               this.add.active = !cap.hasBindingVow(this.vow.get());
               this.remove.active = cap.hasBindingVow(this.vow.get());
               cap.addBindingVowCooldown(this.vow.get());
            }
         }).size(61, 20).pos(xOffset + 155, yOffset + 9 + 66).build();
         this.addRenderableWidget(this.remove);
         this.description = new MultiLineTextWidget(xOffset + 86, yOffset + 9 + 1, Component.empty(), this.minecraft.font).setMaxWidth(129);
         this.addRenderableWidget(this.description);
      }
   }
}
