package radon.jujutsu_kaisen.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class JJKParticles {
   public static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "jujutsu_kaisen");
   public static RegistryObject<SimpleParticleType> BLACK_FLASH = PARTICLES.register("black_flash", () -> new SimpleParticleType(true));
   public static RegistryObject<ParticleType<LightningParticle.LightningParticleOptions>> LIGHTNING = PARTICLES.register(
      "lightning", () -> new ParticleType<LightningParticle.LightningParticleOptions>(false, LightningParticle.LightningParticleOptions.DESERIALIZER) {
         @NotNull
         public Codec<LightningParticle.LightningParticleOptions> codec() {
            return null;
         }
      }
   );
   public static RegistryObject<ParticleType<CursedSpeechParticle.CursedSpeechParticleOptions>> CURSED_SPEECH = PARTICLES.register(
      "cursed_speech",
      () -> new ParticleType<CursedSpeechParticle.CursedSpeechParticleOptions>(false, CursedSpeechParticle.CursedSpeechParticleOptions.DESERIALIZER) {
         @NotNull
         public Codec<CursedSpeechParticle.CursedSpeechParticleOptions> codec() {
            return null;
         }
      }
   );
   public static RegistryObject<ParticleType<SlicedEntityParticle.SliceParticleOptions>> SLICE = PARTICLES.register(
      "slice", () -> new ParticleType<SlicedEntityParticle.SliceParticleOptions>(false, SlicedEntityParticle.SliceParticleOptions.DESERIALIZER) {
         @NotNull
         public Codec<SlicedEntityParticle.SliceParticleOptions> codec() {
            return null;
         }
      }
   );
   public static RegistryObject<ParticleType<TravelParticle.TravelParticleOptions>> TRAVEL = PARTICLES.register(
      "travel", () -> new ParticleType<TravelParticle.TravelParticleOptions>(false, TravelParticle.TravelParticleOptions.DESERIALIZER) {
         @NotNull
         public Codec<TravelParticle.TravelParticleOptions> codec() {
            return null;
         }
      }
   );
   public static RegistryObject<ParticleType<VaporParticle.VaporParticleOptions>> VAPOR = PARTICLES.register(
      "vapor", () -> new ParticleType<VaporParticle.VaporParticleOptions>(false, VaporParticle.VaporParticleOptions.DESERIALIZER) {
         public Codec<VaporParticle.VaporParticleOptions> codec() {
            return null;
         }
      }
   );
   public static RegistryObject<ParticleType<CursedEnergyParticle.CursedEnergyParticleOptions>> CURSED_ENERGY = PARTICLES.register(
      "cursed_energy",
      () -> new ParticleType<CursedEnergyParticle.CursedEnergyParticleOptions>(false, CursedEnergyParticle.CursedEnergyParticleOptions.DESERIALIZER) {
         public Codec<CursedEnergyParticle.CursedEnergyParticleOptions> codec() {
            return null;
         }
      }
   );
   public static RegistryObject<ParticleType<MirageParticle.MirageParticleOptions>> MIRAGE = PARTICLES.register(
      "mirage", () -> new ParticleType<MirageParticle.MirageParticleOptions>(false, MirageParticle.MirageParticleOptions.DESERIALIZER) {
         public Codec<MirageParticle.MirageParticleOptions> codec() {
            return null;
         }
      }
   );
   public static RegistryObject<ParticleType<ProjectionParticle.ProjectionParticleOptions>> PROJECTION = PARTICLES.register(
      "projection", () -> new ParticleType<ProjectionParticle.ProjectionParticleOptions>(false, ProjectionParticle.ProjectionParticleOptions.DESERIALIZER) {
         public Codec<ProjectionParticle.ProjectionParticleOptions> codec() {
            return null;
         }
      }
   );
   public static RegistryObject<ParticleType<EmittingLightningParticle.EmittingLightningParticleOptions>> EMITTING_LIGHTNING = PARTICLES.register(
      "emitting_lightning",
      () -> new ParticleType<EmittingLightningParticle.EmittingLightningParticleOptions>(
         false, EmittingLightningParticle.EmittingLightningParticleOptions.DESERIALIZER
      ) {
         public Codec<EmittingLightningParticle.EmittingLightningParticleOptions> codec() {
            return null;
         }
      }
   );
   public static RegistryObject<ParticleType<FireParticle.FireParticleOptions>> FIRE = PARTICLES.register(
      "fire", () -> new ParticleType<FireParticle.FireParticleOptions>(false, FireParticle.FireParticleOptions.DESERIALIZER) {
         public Codec<FireParticle.FireParticleOptions> codec() {
            return null;
         }
      }
   );
   public static RegistryObject<ParticleType<BetterSmokeParticle.BetterSmokeParticleOptions>> SMOKE = PARTICLES.register(
      "smoke", () -> new ParticleType<BetterSmokeParticle.BetterSmokeParticleOptions>(false, BetterSmokeParticle.BetterSmokeParticleOptions.DESERIALIZER) {
         public Codec<BetterSmokeParticle.BetterSmokeParticleOptions> codec() {
            return null;
         }
      }
   );
   public static RegistryObject<SimpleParticleType> SLASH = PARTICLES.register("slash", () -> new SimpleParticleType(true));
   public static RegistryObject<SimpleParticleType> DISMANTLE = PARTICLES.register("dismantle", () -> new SimpleParticleType(true));
   public static RegistryObject<SimpleParticleType> SPIDERWEB = PARTICLES.register("spiderweb", () -> new SimpleParticleType(true));
}
