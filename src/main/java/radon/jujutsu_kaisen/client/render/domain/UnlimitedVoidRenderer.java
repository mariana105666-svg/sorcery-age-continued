package radon.jujutsu_kaisen.client.render.domain;

import net.minecraft.resources.ResourceLocation;

public class UnlimitedVoidRenderer extends DomainRenderer {
   private static final ResourceLocation TEXTURE = new ResourceLocation("jujutsu_kaisen", "textures/domain/unlimited_void.png");

   public UnlimitedVoidRenderer() {
      this.addLayer(new PaintRenderLayer());
   }

   @Override
   protected ResourceLocation getTexture() {
      return TEXTURE;
   }
}
