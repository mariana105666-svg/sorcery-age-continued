package radon.jujutsu_kaisen.block.domain;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.DomainSkyBlockEntity;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;

public class DomainSkyBlock extends DomainBlock {
   public DomainSkyBlock(Properties pProperties) {
      super(pProperties);
   }

   @NotNull
   public RenderShape getRenderShape(@NotNull BlockState pState) {
      return RenderShape.INVISIBLE;
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
      return ((BlockEntityType)JJKBlockEntities.DOMAIN_SKY.get()).create(pPos, pState);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      @NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType
   ) {
      return pLevel.isClientSide
         ? null
         : JJKBlocks.createTickerHelper(pBlockEntityType, (BlockEntityType)JJKBlockEntities.DOMAIN_SKY.get(), DomainSkyBlockEntity::tick);
   }
}
