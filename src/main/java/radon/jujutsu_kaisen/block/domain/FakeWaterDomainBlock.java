package radon.jujutsu_kaisen.block.domain;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;

public class FakeWaterDomainBlock extends LiquidBlock implements EntityBlock {
   public FakeWaterDomainBlock(Supplier<? extends FlowingFluid> pFluid, Properties pProperties) {
      super(pFluid, pProperties);
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
      return ((BlockEntityType)JJKBlockEntities.DOMAIN.get()).create(pPos, pState);
   }
}
