package radon.jujutsu_kaisen.world.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class JJKDimensions {
   public static final ResourceKey<Level> LIMBO_KEY = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("jujutsu_kaisen", "limbo"));
   public static final ResourceKey<DimensionType> LIMBO_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, LIMBO_KEY.registry());
}
