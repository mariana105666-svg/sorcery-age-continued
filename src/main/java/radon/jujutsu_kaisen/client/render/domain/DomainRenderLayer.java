package radon.jujutsu_kaisen.client.render.domain;

import net.minecraft.resources.ResourceLocation;

public abstract class DomainRenderLayer {
   public abstract boolean shouldRender(int var1);

   protected abstract ResourceLocation getTexture();
}
