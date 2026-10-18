package radon.jujutsu_kaisen.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class JJKEntityTypeTags {
   public static final TagKey<EntityType<?>> SPAWNABLE_CURSE = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jujutsu_kaisen", "spawnable_curse"));
}
