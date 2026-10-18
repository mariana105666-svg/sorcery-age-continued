package radon.jujutsu_kaisen.client.gui.overlay;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.gui.MeleeMenuType;
import radon.jujutsu_kaisen.client.gui.screen.JutwotsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.MeleeScreen;
import radon.jujutsu_kaisen.config.ConfigHolder;

public class AbilityOverlay {
   private static int selected;
   private static List<Ability> abilities = new ArrayList<>();
   public static IGuiOverlay OVERLAY = (gui, graphics, partialTicks, width, height) -> {
      Minecraft mc = gui.getMinecraft();
      if (mc.player != null) {
         if (ConfigHolder.CLIENT.meleeMenuType.get() == MeleeMenuType.SCROLL) {
            abilities = JJKAbilities.getAbilities(mc.player);
            abilities.removeIf(abilityx -> abilityx.getMenuType() != MenuType.MELEE);
            if (!abilities.isEmpty()) {
               int index = getIndex();
               Ability ability = abilities.get(index);
               render(gui, graphics, width, height, ability);
            }
         } else if (ConfigHolder.CLIENT.meleeMenuType.get() == MeleeMenuType.TOGGLE) {
            Ability selected = MeleeScreen.getSelected();
            if (selected == null) {
               return;
            }

            if (!selected.isValid(mc.player) || !JJKAbilities.getAbilities(mc.player).contains(selected)) {
               selected = null;
            }

            render(gui, graphics, width, height, selected);
         }

         Ability selectedx = JutwotsuScreen.getSelected();
         if (selectedx != null) {
            if (!selectedx.isValid(mc.player) || !JJKAbilities.getAbilities(mc.player).contains(selectedx)) {
               selectedx = null;
            }

            render(gui, graphics, width, height - 30, selectedx);
         }
      }
   };

   public static boolean scroll(int direction) {
      int i = -((int)Math.signum((float)direction));
      int count = abilities.size();
      if (count == 0) {
         return false;
      } else {
         selected += i;

         while (selected < 0) {
            selected += count;
         }

         while (selected >= count) {
            selected -= count;
         }

         return true;
      }
   }

   @Nullable
   public static Ability getSelected() {
      if (ConfigHolder.CLIENT.meleeMenuType.get() == MeleeMenuType.SCROLL) {
         if (abilities.isEmpty()) {
            return null;
         }

         int index = getIndex();
         if (abilities.size() > index) {
            return abilities.get(index);
         }
      } else if (ConfigHolder.CLIENT.meleeMenuType.get() == MeleeMenuType.TOGGLE) {
         return MeleeScreen.getSelected();
      }

      return null;
   }

   @Nullable
   public static Ability getSelected2() {
      return JutwotsuScreen.getSelected();
   }

   private static int getIndex() {
      int count = abilities.size();
      int index = selected;

      while (index < 0) {
         index += count;
      }

      while (index >= count) {
         index -= count;
      }

      return index;
   }

   private static void render(ForgeGui gui, GuiGraphics graphics, int width, int height, Ability ability) {
      Minecraft mc = gui.getMinecraft();
      if (mc.player != null && ability != null) {
         if (mc.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            List<Component> lines = new ArrayList<>();
            Component nameText = Component.translatable(String.format("gui.%s.ability_overlay.name", "jujutsu_kaisen"), new Object[]{ability.getName()});
            lines.add(nameText);
            float cost = ability.getRealCost(mc.player);
            if (cost > 0.0F) {
               lines.add(Component.translatable(String.format("gui.%s.ability_overlay.cost", "jujutsu_kaisen"), new Object[]{cost}));
            }

            int cooldown = ability.getRealCooldown(mc.player);
            if (cooldown > 0) {
               lines.add(Component.translatable(String.format("gui.%s.ability_overlay.cooldown", "jujutsu_kaisen"), new Object[]{Math.round(cooldown / 20.0F)}));
            }

            if (ability instanceof Ability.IDurationable durationable) {
               int duration = durationable.getRealDuration(mc.player);
               if (duration > 0) {
                  Component durationText = Component.translatable(
                     String.format("gui.%s.ability_overlay.duration", "jujutsu_kaisen"), new Object[]{duration / 20.0F}
                  );
                  lines.add(durationText);
               }
            }

            int offset = 0;

            for (Component line : lines) {
               if (mc.font.width(line) > offset) {
                  offset = mc.font.width(line);
               }
            }

            int x = width - 20 - offset;
            int y = height - 20 - (lines.size() * 9 + 2);

            for (Component linex : lines) {
               graphics.drawString(gui.getFont(), linex, x, y, 16777215);
               y += 9;
            }
         }
      }
   }
}
