package radon.jujutsu_kaisen.client.render.domain;

import net.minecraft.resources.ResourceLocation;

public class DefaultDomainRenderer extends DomainRenderer {
   private static final ResourceLocation TEXTURE = new ResourceLocation("jujutsu_kaisen", "textures/domain/domain_default.png");

   @Override
   protected ResourceLocation getTexture() {
      return TEXTURE;
   }
}
