package radon.jujutsu_kaisen.client.render.entity.idle_transfiguration;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.model.base.DefaultedTurnHeadEntityGeoModel;
import radon.jujutsu_kaisen.entity.idle_transfiguration.TransfiguredSoulNormalEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TransfiguredSoulNormalRenderer extends GeoEntityRenderer<TransfiguredSoulNormalEntity> {
   public TransfiguredSoulNormalRenderer(Context renderManager) {
      super(renderManager, new DefaultedTurnHeadEntityGeoModel(new ResourceLocation("jujutsu_kaisen", "transfigured_soul_normal")));
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull TransfiguredSoulNormalEntity animatable) {
      ResourceLocation key = super.getTextureLocation(animatable);
      return new ResourceLocation(key.getNamespace(), key.getPath().replace(".png", String.format("_%s.%s", animatable.getVariant().ordinal() + 1, "png")));
   }
}
