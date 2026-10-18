package radon.jujutsu_kaisen.mixin.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import radon.jujutsu_kaisen.client.VertexCapturer;
import radon.jujutsu_kaisen.client.slice.RigidBody;

@Mixin({BufferSource.class})
public abstract class BufferSourceMixin {
   @Shadow
   @Final
   protected BufferBuilder builder;

   @Shadow
   protected abstract BufferBuilder getBuilderRaw(RenderType var1);

   @Inject(
      method = {"endBatch(Lnet/minecraft/client/renderer/RenderType;)V"},
      at = {@At("HEAD")}
   )
   public void endBatch(RenderType type, CallbackInfo ci) {
      if (VertexCapturer.capture) {
         BufferBuilder builder = this.getBuilderRaw(type);
         BufferBuilderAccessor accessor = (BufferBuilderAccessor)builder;
         if (accessor.getFormat() == DefaultVertexFormat.NEW_ENTITY) {
            ByteBuffer buffer = accessor.getBuffer();
            int nextElementLimit = accessor.getNextElementByte();
            List<RigidBody.Triangle.TexVertex> vertices = new ArrayList<>();
            List<RigidBody.Triangle[]> triangles = new ArrayList<>();
            int nextElementByte = 0;

            while (nextElementByte < nextElementLimit) {
               float x = buffer.getFloat(nextElementByte);
               float y = buffer.getFloat(nextElementByte + 4);
               float z = buffer.getFloat(nextElementByte + 8);
               nextElementByte += DefaultVertexFormat.ELEMENT_POSITION.getByteSize();
               int r = buffer.get(nextElementByte) & 255;
               int g = buffer.get(nextElementByte + 1) & 255;
               int b = buffer.get(nextElementByte + 2) & 255;
               int a = buffer.get(nextElementByte + 3) & 255;
               nextElementByte += DefaultVertexFormat.ELEMENT_COLOR.getByteSize();
               float u = buffer.getFloat(nextElementByte);
               float v = buffer.getFloat(nextElementByte + 4);
               nextElementByte += DefaultVertexFormat.ELEMENT_UV0.getByteSize();
               nextElementByte += DefaultVertexFormat.ELEMENT_UV1.getByteSize();
               nextElementByte += DefaultVertexFormat.ELEMENT_UV2.getByteSize();
               nextElementByte += DefaultVertexFormat.ELEMENT_NORMAL.getByteSize();
               nextElementByte += DefaultVertexFormat.ELEMENT_PADDING.getByteSize();
               vertices.add(new RigidBody.Triangle.TexVertex(new Vec3(x, y, z), u, v, ARGB32.color(a, r, g, b)));
               if (vertices.size() == 24) {
                  RigidBody.Triangle[] current = new RigidBody.Triangle[12];

                  for (int i = 0; i < vertices.size(); i += 4) {
                     RigidBody.Triangle.TexVertex v0 = vertices.get(i);
                     RigidBody.Triangle.TexVertex v1 = vertices.get(i + 1);
                     RigidBody.Triangle.TexVertex v2 = vertices.get(i + 2);
                     RigidBody.Triangle.TexVertex v3 = vertices.get(i + 3);
                     float[] uv = new float[]{v0.u, v0.v, v1.u, v1.v, v2.u, v2.v};
                     int[] color = new int[]{v0.color, v1.color, v2.color};
                     current[i / 2] = new RigidBody.Triangle(v0.pos, v1.pos, v2.pos, uv, color);
                     uv = new float[]{v2.u, v2.v, v3.u, v3.v, v0.u, v0.v};
                     color = new int[]{v2.color, v3.color, v0.color};
                     current[i / 2 + 1] = new RigidBody.Triangle(v2.pos, v3.pos, v0.pos, uv, color);
                  }

                  triangles.add(current);
                  vertices.clear();
               }
            }

            VertexCapturer.captured.add(new VertexCapturer.Capture(type, triangles));
         }
      }
   }
}
