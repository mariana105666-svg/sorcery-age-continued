package radon.jujutsu_kaisen.client.render.entity.ten_shadows;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogEntity;

public class DivineDogRenderer extends TenShadowsRenderer<DivineDogEntity> {
   public DivineDogRenderer(Context renderManager) {
      super(renderManager, new DefaultedTurnHeadEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "divine_dog")));
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull DivineDogEntity animatable) {
      ResourceLocation key = super.getTextureLocation(animatable);
      return new ResourceLocation(
         key.getNamespace(), key.getPath().replace(".png", String.format("_%s.%s", animatable.getVariant().name().toLowerCase(), "png"))
      );
   }
}
