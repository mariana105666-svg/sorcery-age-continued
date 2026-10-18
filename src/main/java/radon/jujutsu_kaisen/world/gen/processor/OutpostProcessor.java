package radon.jujutsu_kaisen.world.gen.processor;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.world.gen.processor.base.CursedObjectDisplayCaseProcessor;

public class OutpostProcessor extends CursedObjectDisplayCaseProcessor {
   public static final Codec<OutpostProcessor> CODEC = Codec.unit(OutpostProcessor::new);

   @NotNull
   protected StructureProcessorType<?> getType() {
      return (StructureProcessorType<?>)JJKProcessors.OUTPOST.get();
   }
}
