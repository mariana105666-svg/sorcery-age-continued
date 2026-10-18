package radon.jujutsu_kaisen.block.domain;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;

public class FakeSkyBlock extends DomainBlock implements EntityBlock {
   public FakeSkyBlock(Properties pProperties) {
      super(pProperties);
   }

   @NotNull
   public RenderShape getRenderShape(@NotNull BlockState pState) {
      return RenderShape.INVISIBLE;
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
      return ((BlockEntityType)JJKBlockEntities.FAKE_SKY.get()).create(pPos, pState);
   }
}
