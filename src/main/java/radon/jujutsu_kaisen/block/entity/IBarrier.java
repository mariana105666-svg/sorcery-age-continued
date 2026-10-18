package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public interface IBarrier {
   Level level();

   default boolean isAffected(BlockPos pos) {
      return this.isInsideBarrier(pos);
   }

   boolean isBarrier(BlockPos var1);

   boolean isInsideBarrier(BlockPos var1);

   boolean isBarrierOrInside(BlockPos var1);

   AABB getBounds();
}
