package radon.jujutsu_kaisen.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.entity.JJKBlockEntities;
import radon.jujutsu_kaisen.block.entity.VeilBlockEntity;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;

public class VeilBlock extends Block implements EntityBlock {
   public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);
   public static final BooleanProperty TRANSPARENT = BooleanProperty.create("transparent");

   public VeilBlock(Properties pProperties) {
      super(pProperties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(COLOR, DyeColor.BLACK)).setValue(TRANSPARENT, false));
   }

   public boolean propagatesSkylightDown(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos) {
      return true;
   }

   @NotNull
   public RenderShape getRenderShape(@NotNull BlockState pState) {
      return pState.getValue(TRANSPARENT) ? RenderShape.INVISIBLE : super.getRenderShape(pState);
   }

   @NotNull
   public VoxelShape getCollisionShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
      if (pContext instanceof EntityCollisionContext ctx) {
         if (!(pLevel.getBlockEntity(pPos) instanceof VeilBlockEntity be)) {
            return super.getCollisionShape(pState, pLevel, pPos, pContext);
         }

         Entity entity = ctx.getEntity();
         if (entity != null) {
            if (entity instanceof LivingEntity living
               && (JJKAbilities.hasTrait(living, Trait.HEAVENLY_RESTRICTION) || JJKAbilities.hasToggled(living, (Ability)JJKAbilities.BARRIER_TRAVEL.get()))
               && !pContext.isAbove(Shapes.block(), pPos, true)) {
               return Shapes.empty();
            }

            if (entity instanceof Projectile projectile) {
               entity = projectile.getOwner();
            }

            return VeilHandler.isWhitelisted(pPos, entity) && !pContext.isAbove(Shapes.block(), pPos, true) ? Shapes.empty() : Shapes.block();
         }
      }

      return super.getCollisionShape(pState, pLevel, pPos, pContext);
   }

   public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (!level.isClientSide) {
         if (level.getBlockEntity(pos) instanceof VeilBlockEntity veil) {
            Entity var8 = entity;
            if (entity instanceof Projectile projectile && projectile.getOwner() != null) {
               var8 = projectile.getOwner();
            }

            if (!(
               var8 instanceof LivingEntity living
                  && (JJKAbilities.hasTrait(living, Trait.HEAVENLY_RESTRICTION) || JJKAbilities.hasToggled(living, (Ability)JJKAbilities.BARRIER_TRAVEL.get()))
            )) {
               boolean allowed = VeilHandler.isWhitelisted(pos, var8);
               if (!allowed) {
                  entity.teleportTo(entity.xOld, entity.yOld, entity.zOld);
               }
            }
         }
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
      return ((BlockEntityType)JJKBlockEntities.VEIL.get()).create(pPos, pState);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{COLOR, TRANSPARENT});
   }
}
