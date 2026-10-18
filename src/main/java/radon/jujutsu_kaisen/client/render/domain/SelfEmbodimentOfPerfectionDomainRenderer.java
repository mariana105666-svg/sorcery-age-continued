package radon.jujutsu_kaisen.client.render.domain;

import net.minecraft.resources.ResourceLocation;

public class SelfEmbodimentOfPerfectionDomainRenderer extends DomainRenderer {
   private static final ResourceLocation TEXTURE = new ResourceLocation("jujutsu_kaisen", "textures/domain/self_embodiment_of_perfection.png");

   @Override
   protected ResourceLocation getTexture() {
      return TEXTURE;
   }
}
