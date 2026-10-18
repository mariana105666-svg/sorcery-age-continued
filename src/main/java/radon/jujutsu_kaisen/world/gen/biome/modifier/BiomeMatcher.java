package radon.jujutsu_kaisen.world.gen.biome.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;

public record BiomeMatcher(Optional<List<HolderSet<Biome>>> ifAll, Optional<List<HolderSet<Biome>>> ifAny, Optional<List<HolderSet<Biome>>> excluding)
   implements Predicate<Holder<Biome>> {
   public static final Codec<BiomeMatcher> CODEC = RecordCodecBuilder.create(
      codec -> codec.group(
            Biome.LIST_CODEC.listOf().optionalFieldOf("if_all").forGetter(BiomeMatcher::ifAll),
            Biome.LIST_CODEC.listOf().optionalFieldOf("if_any").forGetter(BiomeMatcher::ifAny),
            Biome.LIST_CODEC.listOf().optionalFieldOf("excluding").forGetter(BiomeMatcher::excluding)
         )
         .apply(codec, BiomeMatcher::new)
   );

   public boolean test(Holder<Biome> biome) {
      if (this.ifAll.isPresent()) {
         for (HolderSet<Biome> biomes : this.ifAll.get()) {
            if (!biomes.contains(biome)) {
               return false;
            }
         }
      }

      if (this.ifAny.isPresent()) {
         boolean found = false;

         for (HolderSet<Biome> biomesx : this.ifAny.get()) {
            if (biomesx.contains(biome)) {
               found = true;
               break;
            }
         }

         if (!found) {
            return false;
         }
      }

      if (this.excluding.isPresent()) {
         for (HolderSet<Biome> biomesxx : this.excluding.get()) {
            if (biomesxx.contains(biome)) {
               return false;
            }
         }
      }

      return true;
   }
}
