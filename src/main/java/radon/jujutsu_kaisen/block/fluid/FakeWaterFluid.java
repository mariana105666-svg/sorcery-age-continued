package radon.jujutsu_kaisen.block.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.ForgeFlowingFluid.Properties;
import org.jetbrains.annotations.NotNull;

public abstract class FakeWaterFluid extends ForgeFlowingFluid {
   protected FakeWaterFluid(Properties properties) {
      super(properties);
   }

   protected boolean canSpreadTo(
      @NotNull BlockGetter pLevel,
      @NotNull BlockPos pFromPos,
      @NotNull BlockState pFromBlockState,
      @NotNull Direction pDirection,
      @NotNull BlockPos pToPos,
      @NotNull BlockState pToBlockState,
      @NotNull FluidState pToFluidState,
      @NotNull Fluid pFluid
   ) {
      return false;
   }

   public static class Flowing extends FakeWaterFluid {
      public Flowing(Properties properties) {
         super(properties);
         this.registerDefaultState((FluidState)((FluidState)this.getStateDefinition().any()).setValue(LEVEL, 7));
      }

      protected void createFluidStateDefinition(@NotNull Builder<Fluid, FluidState> builder) {
         super.createFluidStateDefinition(builder);
         builder.add(new Property[]{LEVEL});
      }

      public int getAmount(FluidState state) {
         return (Integer)state.getValue(LEVEL);
      }

      public boolean isSource(@NotNull FluidState state) {
         return false;
      }
   }

   public static class Source extends FakeWaterFluid {
      public Source(Properties properties) {
         super(properties);
      }

      public int getAmount(@NotNull FluidState state) {
         return 8;
      }

      public boolean isSource(@NotNull FluidState state) {
         return true;
      }
   }
}
