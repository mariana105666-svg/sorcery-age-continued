package radon.jujutsu_kaisen.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class JJKCreativeTabs {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "jujutsu_kaisen");
   public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register(
      "main",
      () -> CreativeModeTab.builder()
         .icon(() -> new ItemStack((ItemLike)JJKItems.INVERTED_SPEAR_OF_HEAVEN.get()))
         .title(Component.translatable("creative_mode_tab.jujutsu_kaisen.main"))
         .displayItems((pParameters, pOutput) -> {
            pOutput.accept((ItemLike)JJKItems.INVERTED_SPEAR_OF_HEAVEN.get());
            pOutput.accept((ItemLike)JJKItems.PLAYFUL_CLOUD.get());
            pOutput.accept((ItemLike)JJKItems.SPLIT_SOUL_KATANA.get());
            pOutput.accept((ItemLike)JJKItems.DRAGON_BONE.get());
            pOutput.accept((ItemLike)JJKItems.CHAIN_OF_A_THOUSAND_MILES.get());
            pOutput.accept((ItemLike)JJKItems.NYOI_STAFF.get());
            pOutput.accept((ItemLike)JJKItems.SLAUGHTER_DEMON.get());
            pOutput.accept((ItemLike)JJKItems.KAMUTOKE_DAGGER.get());
            pOutput.accept((ItemLike)JJKItems.HITEN_STAFF.get());
            pOutput.accept((ItemLike)JJKItems.POLEARM_STAFF.get());
            pOutput.accept((ItemLike)JJKItems.STEEL_GAUNTLET.get());
            pOutput.accept((ItemLike)JJKItems.GREEN_HANDLE_KATANA.get());
            pOutput.accept((ItemLike)JJKItems.JET_BLACK_SHADOW_SWORD.get());
            pOutput.accept((ItemLike)JJKItems.INVENTORY_CURSE.get());
            pOutput.accept((ItemLike)JJKItems.BLINDFOLD.get());
            pOutput.accept((ItemLike)JJKItems.TOJI_FUSHIGURO_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.SATORU_GOJO_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.YUTA_OKKOTSU_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.MEGUMI_FUSHIGURO_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.YUJI_IDATORI_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.TOGE_INUMAKI_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.SUGURU_GETO_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.NAOYA_ZENIN_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.HAJIME_KASHIMO_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.MAKI_ZENIN_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.AOI_TODO_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.JOGO_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.JOGOAT_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.DAGON_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.HANAMI_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.RUGBY_FIELD_CURSE_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.FISH_CURSE_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.CYCLOPS_CURSE_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.KUCHISAKE_ONNA_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.ZOMBA_CURSE_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.WORM_CURSE_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.FELINE_CURSE_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.FUGLY_CURSE_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.BIRD_CURSE_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.FINGER_BEARER_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.RAINBOW_DRAGON_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.DINO_CURSE_SPAWN_EGG.get());
            pOutput.accept((ItemLike)JJKItems.DISPLAY_CASE.get());
            pOutput.accept((ItemLike)JJKItems.ALTAR.get());
            pOutput.accept((ItemLike)JJKItems.VEIL_ROD.get());
            pOutput.accept((ItemLike)JJKItems.SUKUNA_FINGER.get());
            pOutput.accept((ItemLike)JJKItems.CURSED_TOTEM.get());
            pOutput.accept((ItemLike)JJKItems.CURSED_COMPASS.get());
            pOutput.accept((ItemLike)JJKItems.CURSED_MUSIC_DISC.get());
            pOutput.accept((ItemLike)JJKItems.CURSED_EYE_OF_ENDER.get());
            pOutput.accept((ItemLike)JJKItems.SORCERER_FLESH.get());
            pOutput.accept((ItemLike)JJKItems.CURSE_FLESH.get());
            pOutput.accept((ItemLike)JJKItems.MERGED_FLESH.get());
         })
         .build()
   );
}
