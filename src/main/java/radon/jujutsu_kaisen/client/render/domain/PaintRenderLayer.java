package radon.jujutsu_kaisen.client.render.domain;

import net.minecraft.resources.ResourceLocation;

public class PaintRenderLayer extends DomainRenderLayer {
   private static final ResourceLocation TEXTURE = new ResourceLocation("jujutsu_kaisen", "textures/domain/paint.png");
   private static final int DELAY = 60;

   @Override
   public boolean shouldRender(int time) {
      return time >= 60;
   }

   @Override
   protected ResourceLocation getTexture() {
      return TEXTURE;
   }
}
