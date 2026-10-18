package radon.jujutsu_kaisen.block.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.ForgeFlowingFluid.Properties;
import org.jetbrains.annotations.NotNull;

public abstract class ChimeraShadowGardenFluid extends ForgeFlowingFluid {
   protected ChimeraShadowGardenFluid(Properties properties) {
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

   protected void animateTick(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull FluidState pState, RandomSource pRandom) {
      if (pRandom.nextInt(20) == 0) {
         double d0 = pPos.getX() + pRandom.nextDouble();
         double d1 = pPos.getY() + 1.0;
         double d2 = pPos.getZ() + pRandom.nextDouble();
         pLevel.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
      }

      if (pRandom.nextInt(25) == 0) {
         double d0 = pPos.getX() + pRandom.nextDouble();
         double d1 = pPos.getY() + 1.0;
         double d2 = pPos.getZ() + pRandom.nextDouble();
         pLevel.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
      }
   }

   public static class Flowing extends ChimeraShadowGardenFluid {
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

   public static class Source extends ChimeraShadowGardenFluid {
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
