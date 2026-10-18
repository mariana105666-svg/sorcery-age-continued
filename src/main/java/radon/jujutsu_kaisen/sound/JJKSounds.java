package radon.jujutsu_kaisen.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class JJKSounds {
   public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "jujutsu_kaisen");
   public static final RegistryObject<SoundEvent> SLASH = SOUNDS.register("slash", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "slash")));
   public static RegistryObject<SoundEvent> FIRE_ARROW = SOUNDS.register(
      "fire_arrow", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "fire_arrow"))
   );
   public static RegistryObject<SoundEvent> HOLLOW_PURPLE = SOUNDS.register(
      "hollow_purple", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "hollow_purple"))
   );
   public static RegistryObject<SoundEvent> UNLIMITED_VOID = SOUNDS.register(
      "unlimited_void", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "unlimited_void"))
   );
   public static RegistryObject<SoundEvent> COLLAPSE = SOUNDS.register(
      "collapse", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "collapse"))
   );
   public static RegistryObject<SoundEvent> RATIO_RULE = SOUNDS.register(
      "ratio_rule", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "ratio_rule"))
   );
   public static RegistryObject<SoundEvent> JACOBS_STRONG_CHARGE = SOUNDS.register(
      "jacobs_strong_charge", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "jacobs_strong_charge"))
   );
   public static RegistryObject<SoundEvent> JACOBS_FIRE = SOUNDS.register(
      "jacobs_fire", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "jacobs_fire"))
   );
   public static RegistryObject<SoundEvent> JACOBS_STRONG_FIRE = SOUNDS.register(
      "jacobs_strong_fire", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "jacobs_strong_fire"))
   );
   public static RegistryObject<SoundEvent> JACOBS_CHARGE = SOUNDS.register(
      "jacobs_charge", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "jacobs_charge"))
   );
   public static RegistryObject<SoundEvent> WHEEL = SOUNDS.register("wheel", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "wheel")));
   public static RegistryObject<SoundEvent> WOLF_HOWLING = SOUNDS.register(
      "wolf_howling", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "wolf_howling"))
   );
   public static RegistryObject<SoundEvent> CURSED_SPEECH = SOUNDS.register(
      "cursed_speech", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "cursed_speech"))
   );
   public static RegistryObject<SoundEvent> CLAP = SOUNDS.register("clap", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "clap")));
   public static RegistryObject<SoundEvent> FOREST_SPIKES = SOUNDS.register(
      "forest_spikes", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "forest_spikes"))
   );
   public static RegistryObject<SoundEvent> MALEVOLENT_SHRINE = SOUNDS.register(
      "malevolent_shrine", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "malevolent_shrine"))
   );
   public static RegistryObject<SoundEvent> ELECTRICITY = SOUNDS.register(
      "electricity", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "electricity"))
   );
   public static RegistryObject<SoundEvent> CLEAVE = SOUNDS.register("cleave", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "cleave")));
   public static RegistryObject<SoundEvent> SWALLOW = SOUNDS.register("swallow", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "swallow")));
   public static RegistryObject<SoundEvent> DASH = SOUNDS.register("dash", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "dash")));
   public static RegistryObject<SoundEvent> HOLLOW_PURPLE_EXPLOSION = SOUNDS.register(
      "hollow_purple_explosion", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "hollow_purple_explosion"))
   );
   public static RegistryObject<SoundEvent> BLUE = SOUNDS.register("blue", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "blue")));
   public static RegistryObject<SoundEvent> RED_EXPLOSION = SOUNDS.register(
      "red_explosion", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "red_explosion"))
   );
   public static RegistryObject<SoundEvent> SLAM = SOUNDS.register("slam", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "slam")));
   public static RegistryObject<SoundEvent> PURE_LOVE = SOUNDS.register(
      "pure_love", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "pure_love"))
   );
   public static RegistryObject<SoundEvent> FLAME_EXPLOSION = SOUNDS.register(
      "flame_explosion", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "flame_explosion"))
   );
   public static RegistryObject<SoundEvent> THUMP = SOUNDS.register("thump", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "thump")));
   public static RegistryObject<SoundEvent> SHOOT = SOUNDS.register("shoot", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "shoot")));
   public static RegistryObject<SoundEvent> SPARK = SOUNDS.register("spark", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("jujutsu_kaisen", "spark")));
}
