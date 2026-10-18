package radon.jujutsu_kaisen.client.render.domain;

import net.minecraft.resources.ResourceLocation;

public class AuthenticMutualLoveRenderer extends DomainRenderer {
   private static final ResourceLocation TEXTURE = new ResourceLocation("jujutsu_kaisen", "textures/domain/authentic_mutual_love.png");

   @Override
   protected ResourceLocation getTexture() {
      return TEXTURE;
   }
}
