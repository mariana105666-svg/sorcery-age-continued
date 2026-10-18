package radon.jujutsu_kaisen.client.render.domain;

import net.minecraft.resources.ResourceLocation;

public class TimeCellMoonPalaceDomainRenderer extends DomainRenderer {
   private static final ResourceLocation TEXTURE = new ResourceLocation("jujutsu_kaisen", "textures/domain/time_cell_moon_palace.png");

   @Override
   protected ResourceLocation getTexture() {
      return TEXTURE;
   }
}
