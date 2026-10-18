package radon.jujutsu_kaisen.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.item.armor.ArmBladeItem;
import radon.jujutsu_kaisen.item.armor.BlindfoldItem;
import radon.jujutsu_kaisen.item.armor.GunItem;
import radon.jujutsu_kaisen.item.armor.HorseLegsItem;
import radon.jujutsu_kaisen.item.armor.InstantSpiritBodyOfDistortedKillingItem;
import radon.jujutsu_kaisen.item.armor.InventoryCurseItem;
import radon.jujutsu_kaisen.item.armor.JJKArmorMaterial;
import radon.jujutsu_kaisen.item.armor.MythicalBeastAmberItem;
import radon.jujutsu_kaisen.item.armor.WingsItem;
import radon.jujutsu_kaisen.item.cursed_object.CursedCompassItem;
import radon.jujutsu_kaisen.item.cursed_object.CursedEyeOfEnderItem;
import radon.jujutsu_kaisen.item.cursed_object.CursedMusicDiscItem;
import radon.jujutsu_kaisen.item.cursed_object.CursedTotemItem;
import radon.jujutsu_kaisen.item.cursed_object.SukunaFingerItem;
import radon.jujutsu_kaisen.item.cursed_tool.ChainOfAThousandMilesItem;
import radon.jujutsu_kaisen.item.cursed_tool.DragonBoneItem;
import radon.jujutsu_kaisen.item.cursed_tool.GreenHandleKatana;
import radon.jujutsu_kaisen.item.cursed_tool.HitenStaffItem;
import radon.jujutsu_kaisen.item.cursed_tool.InvertedSpearOfHeavenItem;
import radon.jujutsu_kaisen.item.cursed_tool.KamutokeDaggerItem;
import radon.jujutsu_kaisen.item.cursed_tool.NyoiStaffItem;
import radon.jujutsu_kaisen.item.cursed_tool.PlayfulCloudItem;
import radon.jujutsu_kaisen.item.cursed_tool.PolearmStaffItem;
import radon.jujutsu_kaisen.item.cursed_tool.RedHandleKatana;
import radon.jujutsu_kaisen.item.cursed_tool.SlaughterDemonItem;
import radon.jujutsu_kaisen.item.cursed_tool.SplitSoulKatanaItem;
import radon.jujutsu_kaisen.item.cursed_tool.SteelGauntletItem;
import radon.jujutsu_kaisen.item.veil.VeilRodItem;

public class JJKItems {
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "jujutsu_kaisen");
   public static RegistryObject<Item> INVERTED_SPEAR_OF_HEAVEN = ITEMS.register(
      "inverted_spear_of_heaven", () -> new InvertedSpearOfHeavenItem(JJKTiers.CURSED_TOOL, 5, -2.8F, new Properties())
   );
   public static RegistryObject<Item> PLAYFUL_CLOUD = ITEMS.register(
      "playful_cloud", () -> new PlayfulCloudItem(JJKTiers.CURSED_TOOL, 8, -2.8F, new Properties())
   );
   public static RegistryObject<Item> SPLIT_SOUL_KATANA = ITEMS.register(
      "split_soul_katana", () -> new SplitSoulKatanaItem(JJKTiers.CURSED_TOOL, 7, -2.8F, new Properties())
   );
   public static RegistryObject<Item> DRAGON_BONE = ITEMS.register("dragon_bone", () -> new DragonBoneItem(JJKTiers.CURSED_TOOL, 6, -2.8F, new Properties()));
   public static RegistryObject<Item> CHAIN_OF_A_THOUSAND_MILES = ITEMS.register(
      "chain_of_a_thousand_miles", () -> new ChainOfAThousandMilesItem(JJKTiers.CURSED_TOOL, 0, 0.0F, new Properties())
   );
   public static RegistryObject<Item> NYOI_STAFF = ITEMS.register("nyoi_staff", () -> new NyoiStaffItem(JJKTiers.CURSED_TOOL, 6, -2.8F, new Properties()));
   public static RegistryObject<Item> SLAUGHTER_DEMON = ITEMS.register(
      "slaughter_demon", () -> new SlaughterDemonItem(JJKTiers.CURSED_TOOL, 4, -2.0F, new Properties())
   );
   public static RegistryObject<Item> KAMUTOKE_DAGGER = ITEMS.register(
      "kamutoke_dagger", () -> new KamutokeDaggerItem(JJKTiers.CURSED_TOOL, 4, -2.8F, new Properties())
   );
   public static RegistryObject<Item> HITEN_STAFF = ITEMS.register("hiten_staff", () -> new HitenStaffItem(JJKTiers.CURSED_TOOL, 4, -2.8F, new Properties()));
   public static RegistryObject<Item> POLEARM_STAFF = ITEMS.register(
      "polearm_staff", () -> new PolearmStaffItem(JJKTiers.CURSED_TOOL, 4, -2.4F, new Properties())
   );
   public static RegistryObject<Item> STEEL_GAUNTLET = ITEMS.register(
      "steel_gauntlet", () -> new SteelGauntletItem(JJKTiers.CURSED_TOOL, 4, 0.0F, new Properties())
   );
   public static RegistryObject<Item> GREEN_HANDLE_KATANA = ITEMS.register(
      "green_handle_katana", () -> new GreenHandleKatana(JJKTiers.CURSED_TOOL, 6, -2.8F, new Properties())
   );
   public static RegistryObject<Item> RED_HANDLE_KATANA = ITEMS.register(
      "red_handle_katana", () -> new RedHandleKatana(JJKTiers.CURSED_TOOL, 6, -2.8F, new Properties())
   );
   public static RegistryObject<Item> JET_BLACK_SHADOW_SWORD = ITEMS.register(
      "jet_black_shadow_sword", () -> new JetBlackShadowSwordItem(JJKTiers.CURSED_TOOL, 6, -2.8F, new Properties())
   );
   public static RegistryObject<ArmorItem> INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING = ITEMS.register(
      "instant_body_of_distorted_killing",
      () -> new InstantSpiritBodyOfDistortedKillingItem(JJKItems.JJKArmorMaterials.CUSTOM_MODEL, Type.CHESTPLATE, new Properties())
   );
   public static RegistryObject<ArmorItem> ARM_BLADE = ITEMS.register(
      "arm_blade", () -> new ArmBladeItem(JJKItems.JJKArmorMaterials.CUSTOM_MODEL, Type.CHESTPLATE, new Properties())
   );
   public static RegistryObject<ArmorItem> GUN = ITEMS.register(
      "gun", () -> new GunItem(JJKItems.JJKArmorMaterials.CUSTOM_MODEL, Type.CHESTPLATE, new Properties())
   );
   public static RegistryObject<ArmorItem> HORSE_LEGS = ITEMS.register(
      "horse_legs", () -> new HorseLegsItem(JJKItems.JJKArmorMaterials.CUSTOM_MODEL, Type.LEGGINGS, new Properties())
   );
   public static RegistryObject<ArmorItem> WINGS = ITEMS.register(
      "wings", () -> new WingsItem(JJKItems.JJKArmorMaterials.CUSTOM_MODEL, Type.CHESTPLATE, new Properties())
   );
   public static RegistryObject<ArmorItem> MYTHICAL_BEAST_AMBER = ITEMS.register(
      "mythical_beast_amber", () -> new MythicalBeastAmberItem(JJKItems.JJKArmorMaterials.CUSTOM_MODEL, Type.CHESTPLATE, new Properties())
   );
   public static RegistryObject<ArmorItem> INVENTORY_CURSE = ITEMS.register(
      "inventory_curse", () -> new InventoryCurseItem(JJKItems.JJKArmorMaterials.INVENTORY_CURSE, Type.CHESTPLATE, new Properties())
   );
   public static RegistryObject<Item> BLINDFOLD = ITEMS.register(
      "blindfold", () -> new BlindfoldItem(JJKItems.JJKArmorMaterials.BLINDFOLD, Type.HELMET, new Properties())
   );
   public static RegistryObject<Item> DISPLAY_CASE = ITEMS.register(
      "display_case", () -> new DisplayCaseItem((Block)JJKBlocks.DISPLAY_CASE.get(), new Properties())
   );
   public static RegistryObject<Item> SUKUNA_FINGER = ITEMS.register(
      "sukuna_finger", () -> new SukunaFingerItem(new Properties().food(JJKFoods.CURSED_OBJECT).stacksTo(20))
   );
   public static RegistryObject<Item> CURSED_TOTEM = ITEMS.register(
      "cursed_totem", () -> new CursedTotemItem(new Properties().food(JJKFoods.CURSED_OBJECT))
   );
   public static RegistryObject<Item> CURSED_COMPASS = ITEMS.register(
      "cursed_compass", () -> new CursedCompassItem(new Properties().food(JJKFoods.CURSED_OBJECT))
   );
   public static RegistryObject<Item> CURSED_MUSIC_DISC = ITEMS.register(
      "cursed_music_disc", () -> new CursedMusicDiscItem(new Properties().food(JJKFoods.CURSED_OBJECT))
   );
   public static RegistryObject<Item> CURSED_EYE_OF_ENDER = ITEMS.register(
      "cursed_eye_of_ender", () -> new CursedEyeOfEnderItem(new Properties().food(JJKFoods.CURSED_OBJECT))
   );
   public static RegistryObject<Item> CURSED_SPIRIT_ORB = ITEMS.register(
      "cursed_spirit_orb", () -> new CursedSpiritOrbItem(new Properties().food(JJKFoods.CURSED_OBJECT))
   );
   public static RegistryObject<Item> TRANSFIGURED_SOUL = ITEMS.register(
      "transfigured_soul", () -> new TransfiguredSoulItem(new Properties().food(JJKFoods.TRANSFIGURED_SOUL))
   );
   public static RegistryObject<Item> SORCERER_FLESH = ITEMS.register(
      "sorcerer_flesh", () -> new SorcererFleshItem(new Properties().food(JJKFoods.SORCERER_FLESH))
   );
   public static RegistryObject<Item> CURSE_FLESH = ITEMS.register("curse_flesh", () -> new CurseFleshItem(new Properties().food(JJKFoods.CURSE_FLESH)));
   public static RegistryObject<Item> MERGED_FLESH = ITEMS.register("merged_flesh", () -> new MergedFleshItem(new Properties().food(JJKFoods.MERGED_FLESH)));
   public static RegistryObject<Item> VEIL_ROD = ITEMS.register("veil_rod", () -> new VeilRodItem((Block)JJKBlocks.VEIL_ROD.get(), new Properties()));
   public static RegistryObject<Item> ALTAR = ITEMS.register("altar", () -> new BlockItem((Block)JJKBlocks.ALTAR.get(), new Properties()));
   public static RegistryObject<Item> LIMBO = ITEMS.register("limbo", () -> new BlockItem((Block)JJKBlocks.LIMBO.get(), new Properties()));
   public static RegistryObject<ForgeSpawnEggItem> TOJI_FUSHIGURO_SPAWN_EGG = ITEMS.register(
      "toji_fushiguro_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.TOJI_FUSHIGURO, 2960685, 16711422, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> SATORU_GOJO_SPAWN_EGG = ITEMS.register(
      "satoru_gojo_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.SATORU_GOJO, 16777215, 53503, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> YUTA_OKKOTSU_SPAWN_EGG = ITEMS.register(
      "yuta_okkotsu_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.YUTA_OKKOTSU, 16777215, 1973790, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> MEGUMI_FUSHIGURO_SPAWN_EGG = ITEMS.register(
      "megumi_fushiguro_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.MEGUMI_FUSHIGURO, 2764093, 6641716, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> YUJI_IDATORI_SPAWN_EGG = ITEMS.register(
      "yuji_itadori_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.YUJI_ITADORI, 13999515, 7357993, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> TOGE_INUMAKI_SPAWN_EGG = ITEMS.register(
      "toge_inumaki_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.TOGE_INUMAKI, 14867660, 7826320, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> SUGURU_GETO_SPAWN_EGG = ITEMS.register(
      "suguru_geto_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.SUGURU_GETO, 2105403, 11375684, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> NAOYA_ZENIN_SPAWN_EGG = ITEMS.register(
      "naoya_zenin_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.NAOYA_ZENIN, 13547899, 10648400, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> HAJIME_KASHIMO_SPAWN_EGG = ITEMS.register(
      "hajime_kashimo_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.HAJIME_KASHIMO, 12839901, 5546890, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> MAKI_ZENIN_SPAWN_EGG = ITEMS.register(
      "maki_zenin_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.MAKI_ZENIN, 2309183, 9405805, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> AOI_TODO_SPAWN_EGG = ITEMS.register(
      "aoi_todo_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.AOI_TODO, 14786662, 9203264, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> JOGO_SPAWN_EGG = ITEMS.register(
      "jogo_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.JOGO, 11842884, 921104, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> JOGOAT_SPAWN_EGG = ITEMS.register(
      "jogoat_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.JOGOAT, 2960685, 53503, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> DAGON_SPAWN_EGG = ITEMS.register(
      "dagon_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.DAGON, 10369857, 14074019, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> HANAMI_SPAWN_EGG = ITEMS.register(
      "hanami_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.HANAMI, 16645371, 7931152, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> RUGBY_FIELD_CURSE_SPAWN_EGG = ITEMS.register(
      "rugby_field_curse_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.RUGBY_FIELD_CURSE, 6463408, 13087851, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> FISH_CURSE_SPAWN_EGG = ITEMS.register(
      "fish_curse_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.FISH_CURSE, 1579553, 2435381, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> CYCLOPS_CURSE_SPAWN_EGG = ITEMS.register(
      "cyclops_curse_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.CYCLOPS_CURSE, 12501189, 12037306, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> KUCHISAKE_ONNA_SPAWN_EGG = ITEMS.register(
      "kuchisake_onna_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.KUCHISAKE_ONNA, 12830408, 16446688, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> ZOMBA_CURSE_SPAWN_EGG = ITEMS.register(
      "zomba_curse_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.ZOMBA_CURSE, 7515827, 12694405, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> WORM_CURSE_SPAWN_EGG = ITEMS.register(
      "worm_curse_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.WORM_CURSE, 13289407, 4948821, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> FELINE_CURSE_SPAWN_EGG = ITEMS.register(
      "feline_curse_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.FELINE_CURSE, 7952685, 4726541, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> FUGLY_CURSE_SPAWN_EGG = ITEMS.register(
      "fugly_curse_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.FUGLY_CURSE, 14144203, 6580314, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> BIRD_CURSE_SPAWN_EGG = ITEMS.register(
      "bird_curse_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.BIRD_CURSE, 13081428, 13086087, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> FINGER_BEARER_SPAWN_EGG = ITEMS.register(
      "finger_bearer_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.FINGER_BEARER, 12045525, 3160664, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> RAINBOW_DRAGON_SPAWN_EGG = ITEMS.register(
      "rainbow_dragon_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.RAINBOW_DRAGON, 16777215, 16771673, new Properties())
   );
   public static RegistryObject<ForgeSpawnEggItem> DINO_CURSE_SPAWN_EGG = ITEMS.register(
      "dino_curse_spawn_egg", () -> new ForgeSpawnEggItem(JJKEntities.DINO_CURSE, 2965596, 12056318, new Properties())
   );

   public static class JJKArmorMaterials {
      public static JJKArmorMaterial CUSTOM_MODEL = new JJKArmorMaterial(
         "custom_model", 0, new int[]{0, 0, 0, 0}, 0, SoundEvents.EMPTY, 0.0F, 0.0F, () -> Ingredient.EMPTY
      );
      public static JJKArmorMaterial INVENTORY_CURSE = new JJKArmorMaterial(
         "inventory_curse", 0, new int[]{0, 0, 0, 0}, 0, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.EMPTY
      );
      public static JJKArmorMaterial BLINDFOLD = new JJKArmorMaterial(
         "blindfold", 0, new int[]{0, 0, 0, 0}, 0, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.EMPTY
      );
   }
}
