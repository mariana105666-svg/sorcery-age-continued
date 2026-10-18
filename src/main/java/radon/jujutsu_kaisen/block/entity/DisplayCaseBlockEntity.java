package radon.jujutsu_kaisen.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.item.base.CursedObjectItem;
import radon.jujutsu_kaisen.tags.JJKEntityTypeTags;
import radon.jujutsu_kaisen.util.HelperMethods;

public class DisplayCaseBlockEntity extends BlockEntity {
   private static final int SPAWN_RANGE = 8;
   private ItemStack stack = ItemStack.EMPTY;
   private int rot = 0;

   public DisplayCaseBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super((BlockEntityType)JJKBlockEntities.DISPLAY_CASE.get(), pPos, pBlockState);
   }

   @Nullable
   private static Entity getRandomCurse(Level level, float energy) {
      List<Entity> pool = new ArrayList<>();

      for (EntityType<?> type : level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE)) {
         if (type.is(JJKEntityTypeTags.SPAWNABLE_CURSE)
            && type.create(level) instanceof LivingEntity entity
            && entity instanceof ISorcerer sorcerer
            && sorcerer.getExperience() <= energy) {
            pool.add(entity);
         }
      }

      return pool.isEmpty() ? null : pool.get(HelperMethods.RANDOM.nextInt(pool.size()));
   }

   public boolean hasItem() {
      return this.stack.getItem() instanceof CursedObjectItem obj;
   }

   public float getEnergy() {
      if (this.stack.getItem() instanceof CursedObjectItem obj) {
         int index = obj.getGrade().ordinal();
         return SorcererGrade.values()[index].getRequiredExperience();
      } else {
         return 0.0F;
      }
   }

   private static boolean isRightDistanceToPlayerAndSpawnPoint(ServerLevel pLevel, ChunkAccess pChunk, MutableBlockPos pPos, double pDistance) {
      if (pDistance <= 576.0) {
         return false;
      } else {
         return pLevel.getSharedSpawnPos().closerToCenterThan(new Vec3(pPos.getX() + 0.5, pPos.getY(), pPos.getZ() + 0.5), 24.0)
            ? false
            : Objects.equals(new ChunkPos(pPos), pChunk.getPos()) || pLevel.isNaturalSpawningAllowed(pPos);
      }
   }

   private static boolean isValidSpawnPostitionForType(
      ServerLevel pLevel,
      MobCategory pCategory,
      StructureManager pStructureManager,
      ChunkGenerator pGenerator,
      SpawnerData pData,
      MutableBlockPos pPos,
      double pDistance
   ) {
      EntityType<?> type = pData.type;
      if (type.getCategory() == MobCategory.MISC) {
         return false;
      } else if (!type.canSpawnFarFromPlayer() && pDistance > type.getCategory().getDespawnDistance() * type.getCategory().getDespawnDistance()) {
         return false;
      } else if (type.canSummon() && canSpawnMobAt(pLevel, pStructureManager, pGenerator, pCategory, pData, pPos)) {
         Type spawn = SpawnPlacements.getPlacementType(type);
         if (!NaturalSpawner.isSpawnPositionOk(spawn, pLevel, pPos, type)) {
            return false;
         } else {
            return !SpawnPlacements.checkSpawnRules(type, pLevel, MobSpawnType.SPAWNER, pPos, HelperMethods.RANDOM)
               ? false
               : pLevel.noCollision(type.getAABB(pPos.getX() + 0.5, pPos.getY(), pPos.getZ() + 0.5));
         }
      } else {
         return false;
      }
   }

   private static boolean isValidPositionForMob(ServerLevel pLevel, Mob pMob, double pDistance) {
      return pDistance > pMob.getType().getCategory().getDespawnDistance() * pMob.getType().getCategory().getDespawnDistance() && pMob.removeWhenFarAway(pDistance)
         ? false
         : ForgeEventFactory.checkSpawnPosition(pMob, pLevel, MobSpawnType.SPAWNER);
   }

   private static Optional<SpawnerData> getRandomSpawnMobAt(
      ServerLevel pLevel, StructureManager pStructureManager, ChunkGenerator pGenerator, MobCategory pCategory, RandomSource pRandom, BlockPos pPos
   ) {
      Holder<Biome> holder = pLevel.getBiome(pPos);
      return pCategory == MobCategory.WATER_AMBIENT && holder.is(BiomeTags.REDUCED_WATER_AMBIENT_SPAWNS) && pRandom.nextFloat() < 0.98F
         ? Optional.empty()
         : mobsAt(pLevel, pStructureManager, pGenerator, pCategory, pPos, holder).getRandom(pRandom);
   }

   private static WeightedRandomList<SpawnerData> mobsAt(
      ServerLevel pLevel,
      StructureManager pStructureManager,
      ChunkGenerator pGenerator,
      MobCategory pCategory,
      BlockPos pPos,
      @javax.annotation.Nullable Holder<Biome> pBiome
   ) {
      return ForgeEventFactory.getPotentialSpawns(
         pLevel,
         pCategory,
         pPos,
         NaturalSpawner.isInNetherFortressBounds(pPos, pLevel, pCategory, pStructureManager)
            ? ((StructureSpawnOverride)((Structure)pStructureManager.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.FORTRESS))
                  .spawnOverrides()
                  .get(MobCategory.MONSTER))
               .spawns()
            : pGenerator.getMobsAt(pBiome != null ? pBiome : pLevel.getBiome(pPos), pStructureManager, pCategory, pPos)
      );
   }

   private static boolean canSpawnMobAt(
      ServerLevel pLevel, StructureManager pStructureManager, ChunkGenerator pGenerator, MobCategory pCategory, SpawnerData pData, BlockPos pPos
   ) {
      return mobsAt(pLevel, pStructureManager, pGenerator, pCategory, pPos, null).unwrap().contains(pData);
   }

   private static BlockPos getRandomPosWithin(Level pLevel, LevelChunk pChunk, BlockPos center, int yRange) {
      ChunkPos chunkPos = pChunk.getPos();
      int i = chunkPos.getMinBlockX() + HelperMethods.RANDOM.nextInt(16);
      int j = chunkPos.getMinBlockZ() + HelperMethods.RANDOM.nextInt(16);
      int minY = Math.max(pLevel.getMinBuildHeight(), center.getY() - yRange);
      int maxY = Math.min(pLevel.getMaxBuildHeight(), center.getY() + yRange);
      int l = HelperMethods.RANDOM.nextInt(maxY - minY + 1) + minY;
      return new BlockPos(i, l, j);
   }

   public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DisplayCaseBlockEntity pBlockEntity) {
      pBlockEntity.rotTick();
      if (!pLevel.isClientSide) {
         float energy = pBlockEntity.getEnergy();
         int centerX = pPos.getX() >> 4;
         int centerZ = pPos.getZ() >> 4;
         int displayCaseCount = 0;

         for (int x = centerX - 1; x <= centerX + 1; x++) {
            for (int z = centerZ - 1; z <= centerZ + 1; z++) {
               ChunkAccess chunk = pLevel.getChunk(x, z);

               for (BlockPos pos : chunk.getBlockEntitiesPos()) {
                  if (!pos.equals(pPos)) {
                     BlockState state = pLevel.getBlockState(pos);
                     if (state.is((Block)JJKBlocks.DISPLAY_CASE.get()) && pLevel.getBlockEntity(pos) instanceof DisplayCaseBlockEntity be && be.hasItem()) {
                        displayCaseCount++;
                     }
                  }
               }
            }
         }

         if (displayCaseCount <= 8) {
            if (pBlockEntity.hasItem() && getRandomCurse(pLevel, energy) instanceof CursedSpirit curse) {
               int rng = 64 * Math.max(1, Mth.floor(((Integer)ConfigHolder.SERVER.displayCaseSpawnRate.get()).intValue()) / (pLevel.isNight() ? 4 : 3));
               if (HelperMethods.RANDOM.nextInt(rng) == 0) {
                  AABB nearbyCurses = new AABB(pPos).inflate(16.0, 8.0, 16.0);
                  if (pLevel.getEntitiesOfClass(CursedSpirit.class, nearbyCurses).isEmpty()) {
                     EntityType<?> type = curse.getType();
                     float radius = ((Integer)ConfigHolder.SERVER.displayCaseSpawnRange.get()).intValue();
                     float offsetX = (HelperMethods.RANDOM.nextFloat() - 0.5F) * radius;
                     float offsetZ = (HelperMethods.RANDOM.nextFloat() - 0.5F) * radius;
                     int spawnCenterX = pPos.getX();
                     int spawnCenterZ = pPos.getZ();
                     int spawnX = (int)(spawnCenterX + offsetX);
                     int spawnZ = (int)(spawnCenterZ + offsetZ);
                     BlockPos posx = new BlockPos(spawnX, pPos.getY(), spawnZ);
                     if (posx.getY() >= pLevel.getMinBuildHeight() + 1) {
                        ChunkAccess chunk = pLevel.getChunk(posx);
                        StructureManager manager = ((ServerLevel)pLevel).structureManager();
                        ChunkGenerator generator = ((ServerLevel)pLevel).getChunkSource().getGenerator();
                        int i = posx.getY();
                        BlockState state = chunk.getBlockState(posx);
                        if (!state.isRedstoneConductor(chunk, posx)) {
                           MutableBlockPos mut = new MutableBlockPos();
                           int j = 0;

                           for (int k = 0; k < 3; k++) {
                              int l = posx.getX();
                              int i1 = posx.getZ();
                              int j1 = 6;
                              SpawnerData spawn = null;
                              SpawnGroupData group = null;
                              int k1 = Mth.ceil(HelperMethods.RANDOM.nextFloat() * 4.0F);
                              int l1 = 0;

                              for (int i2 = 0; i2 < k1; i2++) {
                                 l += HelperMethods.RANDOM.nextInt(j1) - HelperMethods.RANDOM.nextInt(j1);
                                 i1 += HelperMethods.RANDOM.nextInt(j1) - HelperMethods.RANDOM.nextInt(j1);
                                 mut.set(l, i, i1);
                                 double d0 = l + 0.5;
                                 double d1 = i1 + 0.5;
                                 Player player = pLevel.getNearestPlayer(d0, i, d1, -1.0, false);
                                 if (player != null) {
                                    double d2 = player.distanceToSqr(d0, i, d1);
                                    if (isRightDistanceToPlayerAndSpawnPoint((ServerLevel)pLevel, chunk, mut, d2)) {
                                       if (spawn == null) {
                                          Optional<SpawnerData> optional = getRandomSpawnMobAt(
                                             (ServerLevel)pLevel, manager, generator, type.getCategory(), HelperMethods.RANDOM, mut
                                          );
                                          if (optional.isEmpty()) {
                                             break;
                                          }

                                          spawn = optional.get();
                                          k1 = spawn.minCount + HelperMethods.RANDOM.nextInt(1 + spawn.maxCount - spawn.minCount);
                                       }

                                       if (isValidSpawnPostitionForType((ServerLevel)pLevel, type.getCategory(), manager, generator, spawn, mut, d2)) {
                                          curse.moveTo(d0, i, d1, HelperMethods.RANDOM.nextFloat() * 360.0F, 0.0F);
                                          if (isValidPositionForMob((ServerLevel)pLevel, curse, d2)) {
                                             group = curse.finalizeSpawn(
                                                (ServerLevelAccessor)pLevel, pLevel.getCurrentDifficultyAt(curse.blockPosition()), MobSpawnType.SPAWNER, group, null
                                             );
                                             j++;
                                             l1++;
                                             ((ServerLevel)pLevel).addFreshEntityWithPassengers(curse);
                                             if (j >= ForgeEventFactory.getMaxSpawnPackSize(curse)) {
                                                return;
                                             }

                                             if (curse.isMaxGroupSizeReached(l1)) {
                                                break;
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void rotTick() {
      if (this.rot != -32768) {
         this.rot++;
      }
   }

   public int getRot() {
      return this.rot;
   }

   public ItemStack getItem() {
      return this.stack;
   }

   public void setItem(ItemStack stack) {
      this.stack = stack;
      this.stack.setCount(1);
      if (this.level != null) {
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
      }

      this.setChanged();
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   protected void saveAdditional(@NotNull CompoundTag pTag) {
      super.saveAdditional(pTag);
      pTag.putInt("rotation", this.rot);
      pTag.put("stack", this.stack.save(new CompoundTag()));
   }

   public void load(@NotNull CompoundTag pTag) {
      super.load(pTag);
      this.rot = pTag.getInt("rotation");
      this.stack = ItemStack.of(pTag.getCompound("stack"));
   }
}
