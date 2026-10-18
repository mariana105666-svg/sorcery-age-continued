package radon.jujutsu_kaisen.world.gen.biome.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.BiomeModifier.Phase;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;

public record MobSpawnBiomeModifier(BiomeMatcher matcher, List<SpawnerData> spawners) implements BiomeModifier {
   public static final Codec<MobSpawnBiomeModifier> CODEC = RecordCodecBuilder.create(
      codec -> codec.group(
            BiomeMatcher.CODEC.fieldOf("biomes").forGetter(MobSpawnBiomeModifier::matcher),
            SpawnerData.CODEC.listOf().fieldOf("spawners").forGetter(MobSpawnBiomeModifier::spawners)
         )
         .apply(codec, MobSpawnBiomeModifier::new)
   );

   public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
      if (phase == Phase.ADD) {
         if (this.matcher.test(biome)) {
            this.spawners.forEach(data -> builder.getMobSpawnSettings().addSpawn(data.type.getCategory(), data));
         }
      }
   }

   public Codec<? extends BiomeModifier> codec() {
      return CODEC;
   }
}
