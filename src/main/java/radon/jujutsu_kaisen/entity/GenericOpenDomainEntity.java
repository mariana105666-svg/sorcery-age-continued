package radon.jujutsu_kaisen.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.base.ITemporaryBlockEntity;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.block.entity.IDomainBarrier;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public class GenericOpenDomainEntity extends OpenDomainExpansionEntity {
   private static final int TICKS_PER_RING = 2;
   public static final int MAX_RADIUS = 96;
   private static final int MAX_GROUND_SEARCH = 40;
   private final List<BlockPos> replacedPositions = new ArrayList<>();
   private List<List<BlockPos>> rings;
   private int nextRing;
   private List<Block> floorPalette;
   private boolean formed;

   public GenericOpenDomainEntity(EntityType<?> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public GenericOpenDomainEntity(LivingEntity owner, DomainExpansion ability, int radius, int height) {
      super((EntityType<?>)JJKEntities.GENERIC_OPEN_DOMAIN.get(), owner, ability, radius, height);
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("next_ring", this.nextRing);
      pCompound.putBoolean("formed", this.formed);
      ListTag positionsList = new ListTag();

      for (BlockPos pos : this.replacedPositions) {
         positionsList.add(NbtUtils.writeBlockPos(pos));
      }

      pCompound.put("replaced_positions", positionsList);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.nextRing = pCompound.getInt("next_ring");
      this.formed = pCompound.getBoolean("formed");
      this.replacedPositions.clear();

      for (Tag tag : pCompound.getList("replaced_positions", 10)) {
         this.replacedPositions.add(NbtUtils.readBlockPos((CompoundTag)tag));
      }
   }

   private boolean isWaveDone() {
      return this.nextRing > this.getRadius();
   }

   public boolean isCompleted() {
      return this.formed;
   }

   @Override
   public void reexpand() {
      this.rings = null;
      this.nextRing = 0;
   }

   @Override
   public DomainExpansionEntity checkSureHitEffect() {
      return !this.isCompleted() ? null : super.checkSureHitEffect();
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         LivingEntity owner = this.getOwner();
         if (owner != null) {
            if (this.isWaveDone()) {
               this.formed = true;
            } else {
               if (this.rings == null) {
                  this.rings = this.scanSurface();
               }

               if (this.getTime() % 2 == 0) {
                  this.placeRing(this.nextRing++);
               }
            }
         }
      }
   }

   private List<List<BlockPos>> scanSurface() {
      int radius = this.getRadius();
      BlockPos origin = this.blockPosition();
      ServerLevel level = (ServerLevel)this.level();
      List<List<BlockPos>> result = new ArrayList<>();

      for (int i = 0; i <= radius; i++) {
         result.add(new ArrayList<>());
      }

      for (int dx = -radius; dx <= radius; dx++) {
         for (int dz = -radius; dz <= radius; dz++) {
            int distSqr = dx * dx + dz * dz;
            if (distSqr <= radius * radius) {
               int ring = (int)Math.round(Math.sqrt(distSqr));
               if (ring <= radius) {
                  BlockPos column = new BlockPos(origin.getX() + dx, origin.getY(), origin.getZ() + dz);
                  if (level.isLoaded(column)) {
                     BlockPos surface = this.findGround(level, level.getHeightmapPos(Types.WORLD_SURFACE, column).below());
                     if (surface != null) {
                        result.get(ring).add(surface);
                     }
                  }
               }
            }
         }
      }

      return result;
   }

   @Nullable
   private BlockPos findGround(ServerLevel level, BlockPos start) {
      MutableBlockPos pos = start.mutable();
      boolean grown = false;

      for (int i = 0; i < 40; i++) {
         if (!level.isInWorldBounds(pos)) {
            return null;
         }

         BlockState state = level.getBlockState(pos);
         if (!state.getFluidState().isEmpty()) {
            return pos.immutable();
         }

         if (state.is(BlockTags.LEAVES)) {
            if (state.hasProperty(LeavesBlock.PERSISTENT) && !(Boolean)state.getValue(LeavesBlock.PERSISTENT)) {
               grown = true;
               pos.move(Direction.DOWN);
               continue;
            }
         } else if (grown && state.is(BlockTags.LOGS)) {
            pos.move(Direction.DOWN);
            continue;
         }

         if (!state.isAir() && !state.getCollisionShape(level, pos).isEmpty()) {
            return pos.immutable();
         }

         pos.move(Direction.DOWN);
      }

      return null;
   }

   private void placeRing(int ring) {
      if (this.rings != null && ring < this.rings.size()) {
         ServerLevel level = (ServerLevel)this.level();

         for (BlockPos pos : this.rings.get(ring)) {
            this.placeOpenDomainBlock(pos, level);
         }
      }
   }

   private Block pickFloorBlock() {
      List<Block> palette = this.getFloorPalette();
      return palette.get(this.random.nextInt(palette.size()));
   }

   private List<Block> getFloorPalette() {
      if (this.floorPalette != null) {
         return this.floorPalette;
      } else {
         this.floorPalette = this.resolveFloorPalette();
         return this.floorPalette;
      }
   }

   private List<Block> resolveFloorPalette() {
      if (this.ability == null) {
         return List.of((Block)JJKBlocks.DOMAIN.get());
      } else {
         List<Block> bottom = this.ability.getBottomFloorBlocks();
         if (!bottom.isEmpty()) {
            return bottom;
         } else {
            List<Block> floor = this.ability.getFloorBlocks();
            if (!floor.isEmpty()) {
               return floor;
            } else {
               List<Block> fill = this.ability.getFillBlocks();
               return !fill.isEmpty() && !this.isSky(fill.get(0)) ? fill : List.of((Block)JJKBlocks.DOMAIN.get());
            }
         }
      }
   }

   private boolean isSky(Block block) {
      return block == JJKBlocks.DOMAIN_SKY.get() || block == JJKBlocks.FAKE_SKY.get();
   }

   private void placeOpenDomainBlock(BlockPos pos, ServerLevel level) {
      if (!this.isRemoved()) {
         if (level.isInWorldBounds(pos)) {
            BlockState current = level.getBlockState(pos);
            if (!current.isAir() && !current.is(Blocks.BEDROCK)) {
               if (!VeilHandler.isProtected(level, pos)) {
                  for (IDomainBarrier barrier : VeilHandler.getDomainBarriers(level, pos)) {
                     if (barrier != this) {
                        return;
                     }
                  }

                  BlockEntity existing = level.getBlockEntity(pos);
                  if (existing instanceof DomainBlockEntity dbe) {
                     UUID identifier = dbe.getIdentifier();
                     if (identifier != null && identifier.equals(this.getUUID())) {
                        return;
                     }
                  }

                  CompoundTag saved = null;
                  if (existing instanceof ITemporaryBlockEntity tmp) {
                     current = tmp.getOriginal();
                     saved = tmp.getSaved();
                  } else if (existing != null) {
                     saved = existing.saveWithFullMetadata();
                  }

                  if (current != null && !current.isAir()) {
                     if (existing != null) {
                        level.removeBlockEntity(pos);
                     }

                     level.setBlock(pos, this.pickFloorBlock().defaultBlockState(), 50);
                     if (level.getBlockEntity(pos) instanceof DomainBlockEntity dbex) {
                        dbex.create(this.getUUID(), 0, current, saved, this.getUUID());
                        this.replacedPositions.add(pos.immutable());
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void remove(@NotNull RemovalReason pReason) {
      if (!this.level().isClientSide) {
         ServerLevel level = (ServerLevel)this.level();

         for (BlockPos pos : this.replacedPositions) {
            if (level.getBlockEntity(pos) instanceof DomainBlockEntity dbe) {
               UUID identifier = dbe.getIdentifier();
               if (identifier != null && identifier.equals(this.getUUID())) {
                  dbe.destroy();
               }
            }
         }

         this.replacedPositions.clear();
      }

      this.rings = null;
      super.remove(pReason);
   }
}
