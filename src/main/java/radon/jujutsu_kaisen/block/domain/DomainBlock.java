package radon.jujutsu_kaisen.block.domain;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.DomainBarrierEntity;

public class DomainBlock extends Block implements EntityBlock {
   public DomainBlock(Properties pProperties) {
      super(pProperties);
   }

   @NotNull
   public VoxelShape getCollisionShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
      if (pContext instanceof EntityCollisionContext ctx) {
         Entity entity = ctx.getEntity();
         if (entity != null && entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData cap = (ISorcererData)entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if ((
                  cap.hasTrait(Trait.HEAVENLY_RESTRICTION)
                     || entity instanceof LivingEntity living && JJKAbilities.hasToggled(living, (Ability)JJKAbilities.BARRIER_TRAVEL.get())
               )
               && !pContext.isAbove(Shapes.block(), pPos, true)) {
               return Shapes.empty();
            }
         }
      }

      return super.getCollisionShape(pState, pLevel, pPos, pContext);
   }

   public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
      Entity exploder = explosion.getExploder();
      if (exploder != null && level instanceof ServerLevel && level.getBlockEntity(pos) instanceof DomainBlockEntity be) {
         UUID identifier = be.getIdentifier();
         if (identifier != null
            && ((ServerLevel)level).getEntity(identifier) instanceof DomainBarrierEntity domain
            && domain.isInsideBarrier(exploder.blockPosition())) {
            return 3600000.8F;
         }
      }

      return super.getExplosionResistance(state, level, pos, explosion);
   }

   public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
      return SoundType.GLASS;
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
      return ((BlockEntityType)JJKBlockEntities.DOMAIN.get()).create(pPos, pState);
   }
}
