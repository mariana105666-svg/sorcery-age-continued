package radon.jujutsu_kaisen.client.render.domain;

import net.minecraft.resources.ResourceLocation;

public class MalevolentShrineRenderer extends DomainRenderer {
   private static final ResourceLocation TEXTURE = new ResourceLocation("jujutsu_kaisen", "textures/domain/malevolent_shrine.png");

   @Override
   protected ResourceLocation getTexture() {
      return TEXTURE;
   }
}
