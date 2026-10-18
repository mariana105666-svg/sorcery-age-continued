package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FakeSkyBlockEntity extends DomainBlockEntity {
   public FakeSkyBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super((BlockEntityType<?>)JJKBlockEntities.FAKE_SKY.get(), pPos, pBlockState);
   }
}
