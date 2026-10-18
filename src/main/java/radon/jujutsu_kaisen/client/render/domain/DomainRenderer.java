package radon.jujutsu_kaisen.client.render.domain;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.client.util.RenderUtil;

public abstract class DomainRenderer {
   private final List<DomainRenderLayer> layers = new ArrayList<>();

   public List<DomainRenderLayer> getLayers() {
      return this.layers;
   }

   protected void addLayer(DomainRenderLayer layer) {
      this.layers.add(layer);
   }

   public final void render(Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder builder = tesselator.getBuilder();
      builder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      builder.vertex(-100.0, -100.0, -100.0).uv(0.0F, 0.0F).endVertex();
      builder.vertex(-100.0, -100.0, 100.0).uv(0.0F, 0.5F).endVertex();
      builder.vertex(100.0, -100.0, 100.0).uv(0.33333334F, 0.5F).endVertex();
      builder.vertex(100.0, -100.0, -100.0).uv(0.33333334F, 0.0F).endVertex();
      builder.vertex(-100.0, 100.0, 100.0).uv(0.33333334F, 0.0F).endVertex();
      builder.vertex(-100.0, 100.0, -100.0).uv(0.33333334F, 0.5F).endVertex();
      builder.vertex(100.0, 100.0, -100.0).uv(0.6666667F, 0.5F).endVertex();
      builder.vertex(100.0, 100.0, 100.0).uv(0.6666667F, 0.0F).endVertex();
      builder.vertex(-100.0, 100.0, 100.0).uv(0.6666667F, 0.0F).endVertex();
      builder.vertex(-100.0, -100.0, 100.0).uv(0.6666667F, 0.5F).endVertex();
      builder.vertex(-100.0, -100.0, -100.0).uv(1.0F, 0.5F).endVertex();
      builder.vertex(-100.0, 100.0, -100.0).uv(1.0F, 0.0F).endVertex();
      builder.vertex(100.0, 100.0, -100.0).uv(0.0F, 0.5F).endVertex();
      builder.vertex(100.0, -100.0, -100.0).uv(0.0F, 1.0F).endVertex();
      builder.vertex(100.0, -100.0, 100.0).uv(0.33333334F, 1.0F).endVertex();
      builder.vertex(100.0, 100.0, 100.0).uv(0.33333334F, 0.5F).endVertex();
      builder.vertex(-100.0, 100.0, -100.0).uv(0.33333334F, 0.5F).endVertex();
      builder.vertex(-100.0, -100.0, -100.0).uv(0.33333334F, 1.0F).endVertex();
      builder.vertex(100.0, -100.0, -100.0).uv(0.6666667F, 1.0F).endVertex();
      builder.vertex(100.0, 100.0, -100.0).uv(0.6666667F, 0.5F).endVertex();
      builder.vertex(100.0, 100.0, 100.0).uv(0.6666667F, 0.5F).endVertex();
      builder.vertex(100.0, -100.0, 100.0).uv(0.6666667F, 1.0F).endVertex();
      builder.vertex(-100.0, -100.0, 100.0).uv(1.0F, 1.0F).endVertex();
      builder.vertex(-100.0, 100.0, 100.0).uv(1.0F, 0.5F).endVertex();
      RenderUtil.drawWithShader(modelViewMatrix, projectionMatrix, builder.end());
   }

   protected abstract ResourceLocation getTexture();
}
