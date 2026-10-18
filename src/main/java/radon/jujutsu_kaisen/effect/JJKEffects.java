package radon.jujutsu_kaisen.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.effect.base.JJKEffect;

public class JJKEffects {
   public static DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "jujutsu_kaisen");
   public static RegistryObject<MobEffect> STUN = EFFECTS.register("stun", () -> new JJKEffect(MobEffectCategory.NEUTRAL, 16777215));
   public static RegistryObject<MobEffect> STAGGER = EFFECTS.register("stagger", () -> new JJKEffect(MobEffectCategory.NEUTRAL, 16777200));
   public static RegistryObject<MobEffect> DOMAINSTUN = EFFECTS.register("domainstun", () -> new JJKEffect(MobEffectCategory.NEUTRAL, 16777200));
   public static RegistryObject<MobEffect> UNLIMITED_VOID = EFFECTS.register("unlimited_void", () -> new JJKEffect(MobEffectCategory.HARMFUL, 0));
   public static RegistryObject<MobEffect> CURSED_BUD = EFFECTS.register("cursed_bud", () -> new CursedBudEffect(MobEffectCategory.HARMFUL, 65280));
   public static RegistryObject<MobEffect> INVISIBILITY = EFFECTS.register("invisibility", () -> new JJKEffect(MobEffectCategory.BENEFICIAL, 65280));
   public static RegistryObject<MobEffect> TRANSFIGURED_SOUL = EFFECTS.register("transfigured_soul", () -> new JJKEffect(MobEffectCategory.HARMFUL, 16777215));
}
