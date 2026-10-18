package radon.jujutsu_kaisen.client.particle;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jetbrains.annotations.NotNull;

public class JJKParticleRenderTypes {
   public static ParticleRenderType GLOW = new ParticleRenderType() {
      public void begin(BufferBuilder buffer, @NotNull TextureManager manager) {
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         buffer.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator tesselator) {
         tesselator.end();
         RenderSystem.defaultBlendFunc();
      }
   };
   public static ParticleRenderType ADDITIVE = new ParticleRenderType() {
      public void begin(BufferBuilder buffer, @NotNull TextureManager manager) {
         RenderSystem.depthMask(true);
         RenderSystem.enableBlend();
         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.blendFunc(SourceFactor.ONE, DestFactor.ONE);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         buffer.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator tesselator) {
         tesselator.end();
         RenderSystem.defaultBlendFunc();
      }
   };
   public static ParticleRenderType TRANSLUCENT = new ParticleRenderType() {
      public void begin(BufferBuilder buffer, @NotNull TextureManager manager) {
         RenderSystem.depthMask(true);
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         buffer.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator tesselator) {
         tesselator.end();
      }
   };
   public static ParticleRenderType CUSTOM = new ParticleRenderType() {
      public void begin(@NotNull BufferBuilder buffer, @NotNull TextureManager manager) {
         buffer.begin(Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
      }

      public void end(Tesselator tesselator) {
         tesselator.end();
      }
   };
}
