package radon.jujutsu_kaisen.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.entity.DurationBlockEntity;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;

public class FakeWaterDurationBlock extends LiquidBlock implements EntityBlock {
   public FakeWaterDurationBlock(Supplier<? extends FlowingFluid> pFluid, Properties pProperties) {
      super(pFluid, pProperties);
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
      return ((BlockEntityType)JJKBlockEntities.DURATION.get()).create(pPos, pState);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      @NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType
   ) {
      return pLevel.isClientSide
         ? null
         : JJKBlocks.createTickerHelper(pBlockEntityType, (BlockEntityType)JJKBlockEntities.DURATION.get(), DurationBlockEntity::tick);
   }
}
