package radon.jujutsu_kaisen.client.gui.screen.tab;

import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList.Entry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.AbilityListWidget;
import radon.jujutsu_kaisen.client.gui.screen.widget.ChantListWidget;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.AddChantC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.RemoveChantC2SPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

public class ChantTab extends JJKTab {
   private static final Component TITLE = Component.translatable(String.format("gui.%s.chant", "jujutsu_kaisen"));
   private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");
   private ChantListWidget chants;
   private EditBox text;
   private Button add;
   private Button remove;
   @Nullable
   private AbilityListWidget.Entry ability;
   @Nullable
   private ChantListWidget.Entry chant;

   public ChantTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
      super(minecraft, screen, type, index, page, Items.ENCHANTED_BOOK.getDefaultInstance(), TITLE, BACKGROUND, false);
   }

   public void setSelectedAbility(AbilityListWidget.Entry entry) {
      this.ability = entry;
      this.chants.refreshList();
   }

   public <T extends Entry<T>> void buildAbilityList(Consumer<T> consumer, Function<Ability, T> result) {
      if (this.minecraft.player != null) {
         for (Ability ability : JJKAbilities.getAbilities(this.minecraft.player)) {
            if (ability.isScalable(this.minecraft.player) && (ability.isTechnique() || ability.isChantable())) {
               consumer.accept(result.apply(ability));
            }
         }
      }
   }

   public void setSelectedChant(ChantListWidget.Entry entry) {
      this.chant = entry;
   }

   public <T extends Entry<T>> void buildChantList(Consumer<T> consumer, Function<String, T> result) {
      if (this.ability != null && this.minecraft.player != null) {
         ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         cap.getFirstChants(this.ability.get()).forEach(chant -> consumer.accept(result.apply(chant)));
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
         this.minecraft.font, Component.translatable(String.format("gui.%s.chant.techniques", "jujutsu_kaisen")), xOffset, yOffset, 16777215, true
      );
      pGuiGraphics.drawString(
         this.minecraft.font, Component.translatable(String.format("gui.%s.chant.chants", "jujutsu_kaisen")), xOffset + 77, yOffset, 16777215, true
      );
      pGuiGraphics.drawString(
         this.minecraft.font, Component.translatable(String.format("gui.%s.chant.chant", "jujutsu_kaisen")), xOffset + 154, yOffset, 16777215, true
      );
   }

   @Override
   public void tick() {
      super.tick();
      if (this.minecraft.player != null) {
         ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         String text = this.text.getValue().toLowerCase();
         boolean unique = true;
         if (!text.isEmpty() && !text.isBlank()) {
            if (text.length() < (Integer)ConfigHolder.SERVER.minimumChantLength.get()) {
               unique = false;
            } else {
               for (ChantListWidget.Entry chant : this.chants.children()) {
                  if (HelperMethods.strcmp(chant.get(), text) < (Double)ConfigHolder.SERVER.chantSimilarityThreshold.get()) {
                     unique = false;
                     break;
                  }
               }
            }
         }

         this.add.active = unique
            && this.chants.children().size() < (Integer)ConfigHolder.SERVER.maximumChantCount.get()
            && !text.isEmpty()
            && !text.isBlank()
            && this.ability != null
            && !cap.hasChant(this.ability.get(), text);
         this.remove.active = this.ability != null && this.chant != null;
      }
   }

   @Override
   public void addWidgets() {
      if (this.minecraft.player != null) {
         int i = (this.screen.width - 252) / 2;
         int j = (this.screen.height - 140) / 2;
         int xOffset = i + 18;
         int yOffset = j + 27;
         this.addRenderableWidget(new AbilityListWidget(this::buildAbilityList, this::setSelectedAbility, this.minecraft, 67, 85, xOffset, yOffset + 9 + 1));
         this.chants = new ChantListWidget(this::buildChantList, this::setSelectedChant, this.minecraft, 67, 85, xOffset + 77, yOffset + 9 + 1);
         this.addRenderableWidget(this.chants);
         this.text = new EditBox(this.minecraft.font, xOffset + 154, yOffset + 9 + 2, 62, 20, Component.empty());
         this.text.setMaxLength((Integer)ConfigHolder.SERVER.maximumChantLength.get());
         this.addRenderableWidget(this.text);
         this.add = Button.builder(Component.translatable(String.format("gui.%s.chant.add", "jujutsu_kaisen")), pButton -> {
            String text = this.text.getValue();
            if (this.ability != null) {
               ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               PacketHandler.sendToServer(new AddChantC2SPacket(JJKAbilities.getKey(this.ability.get()), text));
               cap.addChant(this.ability.get(), text);
               this.chants.refreshList();
            }
         }).size(62, 20).pos(xOffset + 154, yOffset + 9 + 40).build();
         this.addRenderableWidget(this.add);
         this.remove = Button.builder(Component.translatable(String.format("gui.%s.chant.remove", "jujutsu_kaisen")), pButton -> {
            if (this.ability != null && this.chant != null) {
               ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               PacketHandler.sendToServer(new RemoveChantC2SPacket(JJKAbilities.getKey(this.ability.get()), this.chant.get()));
               cap.removeChant(this.ability.get(), this.chant.get());
               this.chants.refreshList();
            }
         }).size(62, 20).pos(xOffset + 154, yOffset + 9 + 66).build();
         this.addRenderableWidget(this.remove);
      }
   }

   @Override
   public void keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
      if (this.add.active && pKeyCode == 257) {
         this.add.onPress();
      } else if (this.remove.active && pKeyCode == 261) {
         this.remove.onPress();
      }
   }
}
