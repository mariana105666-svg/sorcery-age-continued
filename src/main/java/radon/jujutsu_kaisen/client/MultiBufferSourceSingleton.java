package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.function.Consumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;

public class MultiBufferSourceSingleton {
   private static final int BUFFER_BUILDER_CAPACITY = 786432;
   private static final BufferSource bufferSource = MultiBufferSource.immediate(new BufferBuilder(786432));

   public static void use(Consumer<BufferSource> consumer) {
      consumer.accept(bufferSource);
      bufferSource.endBatch();
   }
}
