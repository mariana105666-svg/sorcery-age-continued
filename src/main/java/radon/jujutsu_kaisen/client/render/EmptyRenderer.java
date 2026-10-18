package radon.jujutsu_kaisen.client.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EmptyRenderer<T extends Entity> extends EntityRenderer<T> {
   public EmptyRenderer(Context renderManager) {
      super(renderManager);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull T pEntity) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
