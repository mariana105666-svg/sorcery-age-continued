package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class JJKKeys {
   public static String KEY_CATEGORY_JUJUTSU_KAISEN = String.format("key.category.%s", "jujutsu_kaisen");
   public static KeyMapping ACTIVATE_MELEE_MENU = createKeyMapping("activate_melee_menu", 342);
   public static KeyMapping ACTIVATE_J2TSU_MENU = createKeyMapping("activate_j2tsu_menu", 86);
   public static KeyMapping ACTIVATE_J2TSU = createKeyMapping("activate_j2tsu", 89);
   public static KeyMapping ACTIVATE_ABILITY = createKeyMapping("activate_ability", 82);
   public static KeyMapping ACTIVATE_RCT_OR_HEAL = createKeyMapping("activate_rct_or_heal", 88);
   public static KeyMapping OPEN_INVENTORY_CURSE = createKeyMapping("open_inventory_curse", 78);
   public static KeyMapping ACTIVATE_CURSED_ENERGY_SHIELD = createKeyMapping("activate_cursed_energy_shield", 90);
   public static KeyMapping SHOW_ABILITY_MENU = createKeyMapping("show_ability_menu", 67);
   public static KeyMapping SHOW_DOMAIN_MENU = createKeyMapping("show_domain_menu", 66);
   public static KeyMapping DASH = createKeyMapping("dash", 71);
   public static KeyMapping QUICKDASH = createKeyMapping("quickdash", InputConstants.UNKNOWN.getValue());
   public static KeyMapping WATERWALK = createKeyMapping("waterwalk", 76);
   public static KeyMapping OPEN_JUJUTSU_MENU = createKeyMapping("open_jujutsu_menu", 80);
   public static KeyMapping INCREASE_OUTPUT = createKeyMapping("increase_output", 265);
   public static KeyMapping DECREASE_OUTPUT = createKeyMapping("decrease_output", 264);
   public static KeyMapping QUICK_VEIL = createKeyMapping("quick_veil", 78);

   private static KeyMapping createKeyMapping(String name, int keyCode) {
      return new KeyMapping(String.format("key.%s.%s", "jujutsu_kaisen", name), keyCode, KEY_CATEGORY_JUJUTSU_KAISEN);
   }
}
