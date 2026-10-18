package radon.jujutsu_kaisen.client.gui.screen.tab;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.veil.QuickVeilSettings;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;

public class QuickVeilTab extends JJKTab {
   private static final Component TITLE = Component.translatable(String.format("gui.%s.quick_veil", "jujutsu_kaisen"));
   private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");
   private static final int INSIDE_X = 9;
   private static final int INSIDE_Y = 18;
   private static final int COLUMNS = 2;
   private static final int SPACING = 2;
   private static final int ROW_HEIGHT = 14;
   private static final int BUTTON_WIDTH = 116;
   private final List<Button> options = new ArrayList<>();
   private final List<Button> adminOnly = new ArrayList<>();
   private ForgeSlider sizeSlider;

   public QuickVeilTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
      super(minecraft, screen, type, index, page, Items.BARRIER.getDefaultInstance(), TITLE, BACKGROUND, false);
   }

   private boolean canUseAdmin() {
      if (this.minecraft != null && this.minecraft.player != null) {
         ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         return cap != null && cap.hasAdminVeil();
      } else {
         return false;
      }
   }

   @Override
   public void tick() {
      if (this.sizeSlider != null) {
         QuickVeilSettings.setSize(this.sizeSlider.getValueInt());
      }
   }

   @Override
   public void addWidgets() {
      this.options.clear();
      this.adminOnly.clear();
      int left = (this.screen.width - 252) / 2 + 9;
      int top = (this.screen.height - 140) / 2 + 18;
      Modifier.Type[] rules = QuickVeilSettings.RULES;

      for (int i = 0; i < rules.length; i++) {
         Modifier.Type type = rules[i];
         int x = left + i % 2 * 118;
         int y = top + i / 2 * 14;
         Button rule = Button.builder(this.ruleLabel(type), button -> {
            QuickVeilSettings.cycle(type);
            button.setMessage(this.ruleLabel(type));
         }).bounds(x, y, 116, 13).build();
         this.addRenderableWidget(rule);
         if (type != Modifier.Type.CURSE && type != Modifier.Type.SORCERER) {
            this.options.add(rule);
         }
      }

      int extrasY = top + (rules.length + 2 - 1) / 2 * 14;
      Button transparent = Button.builder(this.transparentLabel(), button -> {
         QuickVeilSettings.setTransparent(!QuickVeilSettings.isTransparent());
         button.setMessage(this.transparentLabel());
      }).bounds(left, extrasY, 116, 13).build();
      this.addRenderableWidget(transparent);
      Button color = Button.builder(this.colorLabel(), button -> {
         QuickVeilSettings.cycleColor();
         button.setMessage(this.colorLabel());
      }).bounds(left + 116 + 2, extrasY, 116, 13).build();
      this.addRenderableWidget(color);
      this.options.add(color);
      int sizeY = extrasY + 14;
      this.sizeSlider = new ForgeSlider(
         left,
         sizeY,
         234,
         13,
         Component.translatable(String.format("gui.%s.veil_configuration.size", "jujutsu_kaisen")).copy().append(": "),
         Component.empty(),
         ((Integer)ConfigHolder.SERVER.minimumVeilSize.get()).intValue(),
         ((Integer)ConfigHolder.SERVER.maximumVeilSize.get()).intValue(),
         Mth.clamp(QuickVeilSettings.getSize(), (Integer)ConfigHolder.SERVER.minimumVeilSize.get(), (Integer)ConfigHolder.SERVER.maximumVeilSize.get()),
         true
      );
      this.addRenderableWidget(this.sizeSlider);
      if (this.canUseAdmin()) {
         int adminY = sizeY + 14;
         this.addRenderableWidget(Button.builder(this.adminLabel(), button -> {
            QuickVeilSettings.setAdmin(!QuickVeilSettings.isAdmin());
            button.setMessage(this.adminLabel());
            this.refreshOptions();
         }).bounds(left, adminY, 234, 13).build());
         int adminRulesY = adminY + 14;
         Modifier.Type[] adminRules = QuickVeilSettings.ADMIN_RULES;

         for (int ix = 0; ix < adminRules.length; ix++) {
            Modifier.Type type = adminRules[ix];
            int x = left + ix % 2 * 118;
            int y = adminRulesY + ix / 2 * 14;
            Button rule = Button.builder(this.ruleLabel(type), button -> {
               QuickVeilSettings.cycle(type);
               button.setMessage(this.ruleLabel(type));
            }).bounds(x, y, 116, 13).build();
            this.addRenderableWidget(rule);
            this.adminOnly.add(rule);
         }
      }

      this.refreshOptions();
   }

   private void refreshOptions() {
      boolean admin = QuickVeilSettings.isAdmin();

      for (Button option : this.options) {
         option.active = !admin;
      }

      for (Button option : this.adminOnly) {
         option.visible = admin;
      }
   }

   private Component ruleLabel(Modifier.Type type) {
      Modifier.Action action = QuickVeilSettings.get(type);
      Component name = Component.translatable(String.format("gui.%s.veil_configuration.%s", "jujutsu_kaisen", type.name().toLowerCase()));
      Component value = Component.translatable(String.format("gui.%s.veil_configuration.action.%s", "jujutsu_kaisen", action.name().toLowerCase()));

      ChatFormatting formatting = switch (action) {
         case ALLOW -> ChatFormatting.GREEN;
         case DENY -> ChatFormatting.RED;
         case NONE -> ChatFormatting.GRAY;
      };
      return Component.empty().append(name).append(": ").append(value.copy().withStyle(formatting));
   }

   private Component transparentLabel() {
      boolean transparent = QuickVeilSettings.isTransparent();
      Component name = Component.translatable(String.format("gui.%s.veil_configuration.transparent", "jujutsu_kaisen"));
      Component value = Component.translatable(String.format("gui.%s.veil_configuration.action.%s", "jujutsu_kaisen", transparent ? "allow" : "none"));
      return Component.empty().append(name).append(": ").append(value.copy().withStyle(transparent ? ChatFormatting.GREEN : ChatFormatting.GRAY));
   }

   private Component colorLabel() {
      int color = QuickVeilSettings.getColor();
      Component name = Component.translatable(String.format("gui.%s.veil_configuration.color", "jujutsu_kaisen"));
      Component value = color < 0
         ? Component.translatable(String.format("gui.%s.veil_configuration.action.none", "jujutsu_kaisen"))
         : Component.literal(DyeColor.values()[color].getName());
      return Component.empty().append(name).append(": ").append(value.copy().withStyle(color < 0 ? ChatFormatting.GRAY : ChatFormatting.AQUA));
   }

   private Component adminLabel() {
      boolean admin = QuickVeilSettings.isAdmin();
      Component name = Component.translatable(String.format("gui.%s.veil_configuration.admin", "jujutsu_kaisen"));
      Component value = Component.translatable(String.format("gui.%s.veil_configuration.action.%s", "jujutsu_kaisen", admin ? "allow" : "none"));
      return Component.empty().append(name).append(": ").append(value.copy().withStyle(admin ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY));
   }
}
