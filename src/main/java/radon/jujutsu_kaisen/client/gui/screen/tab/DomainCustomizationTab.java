package radon.jujutsu_kaisen.client.gui.screen.tab;

import java.util.EnumMap;
import java.util.Map;
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
import net.minecraftforge.client.gui.widget.ForgeSlider;
import radon.jujutsu_kaisen.capability.data.sorcerer.DomainConfiguration;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.ValueType;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.DomainConfigurationListWidget;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.SetDomainConfigurationC2SPacket;

public class DomainCustomizationTab extends JJKTab {
   private static final Component TITLE = Component.translatable(String.format("gui.%s.domain_customization", "jujutsu_kaisen"));
   private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");
   private float oldSize;
   private Button enable;
   private Button disable;
   private MultiLineTextWidget description;
   @Nullable
   private DomainConfigurationListWidget.Entry config;
   private final Map<DomainConfiguration, ForgeSlider> sliders = new EnumMap<>(DomainConfiguration.class);

   public DomainCustomizationTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
      super(minecraft, screen, type, index, page, Items.NETHER_STAR.getDefaultInstance(), TITLE, BACKGROUND, false);
   }

   @Override
   public void tick() {
      if (this.config != null) {
         if (this.config.get().getType() == ValueType.FLOAT) {
            ForgeSlider slider = this.sliders.get(this.config.get());
            if (slider != null) {
               float size = (float)slider.getValue();
               if (size != this.oldSize) {
                  if (this.minecraft != null && this.minecraft.player != null && this.config != null && this.config.get().getType() == ValueType.FLOAT) {
                     ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                     PacketHandler.sendToServer(new SetDomainConfigurationC2SPacket(this.config.get(), size));
                     cap.setDomainConfig(this.config.get(), size);
                  }

                  this.oldSize = size;
               }
            }
         }
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
         this.minecraft.font,
         Component.translatable(String.format("gui.%s.domain_customization.configurations", "jujutsu_kaisen")),
         xOffset,
         yOffset,
         16777215,
         true
      );
      if (this.config != null && this.config.get().getType() != ValueType.BOOLEAN) {
         pGuiGraphics.drawString(
            this.minecraft.font,
            Component.translatable(String.format("domain_configuration.%s.%s", "jujutsu_kaisen", this.config.get().getRawName())),
            xOffset + 86,
            yOffset + 9 + 58,
            16777215,
            true
         );
      }
   }

   private void refreshToggleButtons(@Nullable ISorcererData cap) {
      if (cap != null && this.config != null) {
         if (this.config.get().getType() == ValueType.BOOLEAN) {
            boolean value = cap.<Boolean>getDomainConfig(this.config.get());
            this.enable.active = !value;
            this.disable.active = value;
         } else {
            this.enable.active = false;
            this.disable.active = false;
         }
      }
   }

   public void setSelectedDomainConfiguration(DomainConfigurationListWidget.Entry entry) {
      this.config = entry;
      ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
      if (this.config.get().getType() == ValueType.BOOLEAN) {
         this.enable.visible = true;
         this.disable.visible = true;
         this.enable.setMessage(Component.translatable("domain_configuration.%s.%s.add".formatted("jujutsu_kaisen", this.config.get().getRawName())));
         this.disable.setMessage(Component.translatable("domain_configuration.%s.%s.remove".formatted("jujutsu_kaisen", this.config.get().getRawName())));
         this.refreshToggleButtons(cap);

         for (ForgeSlider slider : this.sliders.values()) {
            slider.visible = false;
            slider.active = false;
         }
      } else if (this.config.get().getType() == ValueType.FLOAT) {
         this.oldSize = Float.NaN;
         this.enable.visible = false;
         this.disable.visible = false;

         for (ForgeSlider slider : this.sliders.values()) {
            slider.visible = false;
            slider.active = false;
         }

         DomainConfiguration cfg = this.config.get();
         ForgeSlider slider = this.sliders.get(cfg);
         if (slider != null) {
            slider.visible = true;
            slider.active = true;
         }
      }

      this.description.setMessage(entry.get().getDescription());
   }

   public <T extends Entry<T>> void buildDomainConfigurationList(Consumer<T> consumer, Function<DomainConfiguration, T> result) {
      for (DomainConfiguration config : DomainConfiguration.values()) {
         consumer.accept(result.apply(config));
      }
   }

   @Override
   public void addWidgets() {
      if (this.minecraft != null && this.minecraft.player != null) {
         int i = (this.screen.width - 252) / 2;
         int j = (this.screen.height - 140) / 2;
         int xOffset = i + 18;
         int yOffset = j + 27;
         ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         if (cap != null) {
            this.addRenderableWidget(
               new DomainConfigurationListWidget(
                  this::buildDomainConfigurationList, this::setSelectedDomainConfiguration, this.minecraft, 78, 85, xOffset, yOffset + 9 + 1
               )
            );
            this.enable = Button.builder(Component.translatable(String.format("gui.%s.binding_vow.add", "jujutsu_kaisen")), pButton -> {
               if (this.config != null) {
                  PacketHandler.sendToServer(new SetDomainConfigurationC2SPacket(this.config.get(), true));
                  cap.setDomainConfig(this.config.get(), true);
                  this.refreshToggleButtons(cap);
               }
            }).size(61, 20).pos(xOffset + 86, yOffset + 9 + 66).build();
            this.addRenderableWidget(this.enable);
            this.disable = Button.builder(Component.translatable(String.format("gui.%s.binding_vow.remove", "jujutsu_kaisen")), pButton -> {
               if (this.config != null) {
                  PacketHandler.sendToServer(new SetDomainConfigurationC2SPacket(this.config.get(), false));
                  cap.setDomainConfig(this.config.get(), false);
                  this.refreshToggleButtons(cap);
               }
            }).size(61, 20).pos(xOffset + 155, yOffset + 9 + 66).build();
            this.disable.visible = false;
            this.disable.active = false;
            this.enable.visible = false;
            this.enable.active = false;
            this.addRenderableWidget(this.disable);

            for (DomainConfiguration cfg : DomainConfiguration.values()) {
               if (cfg.getType() == ValueType.FLOAT) {
                  float min = (Float)cfg.getMinimumValue();
                  float max = (Float)cfg.getMaximumValue();
                  float value = cap.<Float>getDomainConfig(cfg);
                  ForgeSlider slider = new ForgeSlider(
                     xOffset + 86, yOffset + 9 + 68, 130, 16, Component.empty(), Component.empty(), min, max, value, 0.1, 0, true
                  );
                  slider.visible = false;
                  slider.active = false;
                  this.sliders.put(cfg, slider);
                  this.addRenderableWidget(slider);
               }
            }

            this.description = new MultiLineTextWidget(xOffset + 86, yOffset + 9 + 1, Component.empty(), this.minecraft.font).setMaxWidth(129);
            this.addRenderableWidget(this.description);
         }
      }
   }
}
