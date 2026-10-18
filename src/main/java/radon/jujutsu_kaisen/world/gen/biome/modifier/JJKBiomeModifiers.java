package radon.jujutsu_kaisen.world.gen.biome.modifier;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries.Keys;

public class JJKBiomeModifiers {
   public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(
      Keys.BIOME_MODIFIER_SERIALIZERS, "jujutsu_kaisen"
   );
   public static final RegistryObject<Codec<? extends BiomeModifier>> MOB_SPAWN = BIOME_MODIFIERS.register("mob_spawn", () -> MobSpawnBiomeModifier.CODEC);
}
