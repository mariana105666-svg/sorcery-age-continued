package radon.jujutsu_kaisen.client.render.domain;

import net.minecraft.resources.ResourceLocation;

public class SweetBurningMatsuriRenderer extends DomainRenderer {
   private static final ResourceLocation TEXTURE = new ResourceLocation("jujutsu_kaisen", "textures/domain/sweet_burning_matsuri.png");

   @Override
   protected ResourceLocation getTexture() {
      return TEXTURE;
   }
}
