package radon.jujutsu_kaisen.entity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.base.ITemporaryBlockEntity;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.block.entity.DomainSkyBlockEntity;
import radon.jujutsu_kaisen.block.entity.IBarrier;
import radon.jujutsu_kaisen.block.entity.IDomainBarrier;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.RotationUtil;

public class DomainBarrierEntity extends Entity implements IDomainBarrier {
   private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(DomainBarrierEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_RADIUS = SynchedEntityData.defineId(DomainBarrierEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_MAX_HEALTH = SynchedEntityData.defineId(DomainBarrierEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_HEALTH = SynchedEntityData.defineId(DomainBarrierEntity.class, EntityDataSerializers.FLOAT);
   private static final float STRENGTH = 120.0F;
   private static final int MAX_BLOCKS_PER_TICK = 4096;
   private boolean instant;
   public static final int OFFSET = 5;
   private int lastRadius = -1;
   private final List<DomainExpansionEntity> clashers = new ArrayList<>();
   private final Map<UUID, Vec3> positions = new HashMap<>();
   private static final Map<Integer, List<Vec3i>> sphereCache = new HashMap<>();
   private List<BlockPos> toRun = new ArrayList<>();
   private int invulnTick;
   private static final int CHECK_INTERVAL = 20;
   private final Map<BlockPos, DomainBarrierEntity.BlockPlacementTask> blockTasks = new HashMap<>();

   @Nullable
   public DomainBarrierEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public DomainBarrierEntity(LivingEntity creator, Vec3 pos, int radius) {
      super((EntityType)JJKEntities.DOMAIN_BARRIER.get(), creator.level());
      this.moveTo(pos.x, pos.y, pos.z, RotationUtil.getTargetAdjustedYRot(creator), RotationUtil.getTargetAdjustedXRot(creator));
      this.entityData.set(DATA_RADIUS, radius);
      ISorcererData cap = (ISorcererData)creator.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
      Float mult = 1.0F;
      if (cap != null) {
         mult = mult * cap.getAbilityPower();
      }

      this.setMaxHealth(120.0F * mult);
      this.setHealth((Float)this.entityData.get(DATA_MAX_HEALTH));
   }

   public float getMaxHealth() {
      return (Float)this.entityData.get(DATA_MAX_HEALTH);
   }

   private void setMaxHealth(float maxHealth) {
      this.entityData.set(DATA_MAX_HEALTH, maxHealth);
   }

   public float getHealth() {
      return (Float)this.entityData.get(DATA_HEALTH);
   }

   private static List<Vec3i> getSphereOffsets(int radius) {
      return sphereCache.computeIfAbsent(radius, r -> {
         List<Vec3i> list = new ArrayList<>();

         for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
               for (int z = -r; z <= r; z++) {
                  if (x * x + y * y + z * z <= r * r) {
                     list.add(new Vec3i(x, y, z));
                  }
               }
            }
         }

         return list;
      });
   }

   private void setHealth(float health) {
      this.entityData.set(DATA_HEALTH, Mth.clamp(health, 0.0F, this.getMaxHealth()));
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_TIME, 0);
      this.entityData.define(DATA_MAX_HEALTH, 0.0F);
      this.entityData.define(DATA_HEALTH, 0.0F);
      this.entityData.define(DATA_RADIUS, 0);
   }

   public int getTime() {
      return (Integer)this.entityData.get(DATA_TIME);
   }

   public void setTime(int time) {
      this.entityData.set(DATA_TIME, time);
   }

   @Override
   public int getRadius() {
      return (Integer)this.entityData.get(DATA_RADIUS);
   }

   public void setRadius(int radius) {
      this.entityData.set(DATA_RADIUS, radius);
   }

   public boolean hurt(@NotNull DamageSource pSource, float pAmount, LivingEntity entity, BlockPos pos) {
      if (this.invulnTick != 0 && this.getTime() - this.invulnTick < 5) {
         return false;
      } else {
         this.invulnTick = this.getTime();
         System.out.println("hurting1");
         System.out.println("max hp: " + this.getMaxHealth());
         System.out.println("hp remaining: " + this.getHealth());
         this.setHealth(this.getHealth() - pAmount);
         float ratio = this.getHealth() / this.getMaxHealth();
         System.out.println("ratio: " + ratio);
         if (ratio == 0.0F) {
            entity.level().playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GLASS_BREAK, SoundSource.MASTER, 2.0F, 1.0F);
         } else {
            float pitch = 1.0F + (float)Math.pow(1.0F - ratio, 2.1) * 1.25F;
            entity.level().playSound(null, pos, (SoundEvent)JJKSounds.THUMP.get(), SoundSource.MASTER, 1.5F + 0.1F * (ratio / 2.0F), pitch);
         }

         System.out.println(this.getHealth());
         return true;
      }
   }

   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      pCompound.putInt("time", this.getTime());
      pCompound.putInt("radius", this.getRadius());
      pCompound.putFloat("max_health", this.getMaxHealth());
      pCompound.putFloat("health", this.getHealth());
      ListTag positionsTag = new ListTag();

      for (Entry<UUID, Vec3> entry : this.positions.entrySet()) {
         CompoundTag nbt = new CompoundTag();
         nbt.putUUID("identifier", entry.getKey());
         Vec3 position = entry.getValue();
         nbt.putDouble("pos_x", position.x);
         nbt.putDouble("pos_y", position.y);
         nbt.putDouble("pos_z", position.z);
         positionsTag.add(nbt);
      }
   }

   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      this.setTime(pCompound.getInt("time"));
      this.setRadius(pCompound.getInt("radius"));
      this.setMaxHealth(pCompound.getFloat("max_health"));
      this.setHealth(pCompound.getFloat("health"));

      for (Tag key : pCompound.getList("positions", 10)) {
         CompoundTag nbt = (CompoundTag)key;
         this.positions.put(nbt.getUUID("identifier"), new Vec3(nbt.getDouble("pos_x"), nbt.getDouble("pos_y"), nbt.getDouble("pos_z")));
      }
   }

   public boolean isCompleted() {
      int radius = this.getRadius();
      return this.getTime() >= radius * 2;
   }

   @Override
   public boolean isBarrier(BlockPos pos) {
      int radius = this.getRadius();
      BlockPos center = BlockPos.containing(this.position().add(0.0, radius, 0.0));
      BlockPos relative = pos.subtract(center);
      return Math.sqrt(relative.distSqr(Vec3i.ZERO)) >= radius - 1 && Math.sqrt(relative.distSqr(Vec3i.ZERO)) <= radius;
   }

   @Override
   public boolean isInsideBarrier(BlockPos pos) {
      int radius = this.getRadius();
      BlockPos center = BlockPos.containing(this.position().add(0.0, radius, 0.0));
      BlockPos relative = pos.subtract(center);
      return Math.sqrt(relative.distSqr(Vec3i.ZERO)) < radius - 1;
   }

   @Override
   public boolean isBarrierOrInside(BlockPos pos) {
      return this.isBarrier(pos) || this.isInsideBarrier(pos);
   }

   @Override
   public AABB getBounds() {
      return this.getBoundingBox();
   }

   @NotNull
   public Vec3 getDeltaMovement() {
      return Vec3.ZERO;
   }

   protected boolean updateInWaterStateAndDoFluidPushing() {
      return false;
   }

   private void check() {
      int radius = this.getRadius();
      BlockPos center = BlockPos.containing(this.position().add(0.0, radius, 0.0));
      Map<Direction, Integer> counts = new EnumMap<>(Direction.class);
      Map<Direction, Integer> totals = new EnumMap<>(Direction.class);

      for (Direction direction : Direction.values()) {
         counts.put(direction, 0);
         totals.put(direction, 0);
      }

      for (Vec3i off : getSphereOffsets(radius)) {
         double distance = off.getX() * off.getX() + off.getY() * off.getY() + off.getZ() * off.getZ();
         if (!(distance < (radius - 2) * (radius - 2)) && !(distance > radius * radius)) {
            BlockPos pos = center.offset(off);
            if (this.level().isInWorldBounds(pos) && !this.level().getBlockState(pos).is(Blocks.BEDROCK)) {
               boolean intact = this.level().getBlockEntity(pos) instanceof DomainBlockEntity;
               if (off.getY() > 0) {
                  totals.merge(Direction.UP, 1, Integer::sum);
                  if (intact) {
                     counts.merge(Direction.UP, 1, Integer::sum);
                  }
               }

               if (off.getY() < 0) {
                  totals.merge(Direction.DOWN, 1, Integer::sum);
                  if (intact) {
                     counts.merge(Direction.DOWN, 1, Integer::sum);
                  }
               }

               if (off.getZ() > 0) {
                  totals.merge(Direction.SOUTH, 1, Integer::sum);
                  if (intact) {
                     counts.merge(Direction.SOUTH, 1, Integer::sum);
                  }
               }

               if (off.getZ() < 0) {
                  totals.merge(Direction.NORTH, 1, Integer::sum);
                  if (intact) {
                     counts.merge(Direction.NORTH, 1, Integer::sum);
                  }
               }

               if (off.getX() > 0) {
                  totals.merge(Direction.EAST, 1, Integer::sum);
                  if (intact) {
                     counts.merge(Direction.EAST, 1, Integer::sum);
                  }
               }

               if (off.getX() < 0) {
                  totals.merge(Direction.WEST, 1, Integer::sum);
                  if (intact) {
                     counts.merge(Direction.WEST, 1, Integer::sum);
                  }
               }
            }
         }
      }

      for (Direction direction : Direction.values()) {
         int total = totals.get(direction);
         if (total != 0 && (float)counts.get(direction).intValue() / total < 0.75F && !this.isRemoved()) {
            this.discardClashers();
            this.discard();
            return;
         }
      }
   }

   public void onAddedToWorld() {
      super.onAddedToWorld();
      if (!this.level().isClientSide) {
         VeilHandler.barrier(this.level().dimension(), this.getUUID());
      }
   }

   @Override
   public List<DomainExpansionEntity> getClashers() {
      return this.clashers;
   }

   @NotNull
   public EntityDimensions getDimensions(@NotNull Pose pPose) {
      int radius = this.getRadius() * 2;
      return EntityDimensions.fixed(radius, radius);
   }

   protected void onBelowWorld() {
   }

   public boolean getShellBalance() {
      int balanceCount = 0;

      for (DomainExpansionEntity clasher : this.clashers) {
         if (clasher.getShellBalance()) {
            balanceCount++;
         }
      }

      return balanceCount * 2 > this.clashers.size();
   }

   public void scheduleBlockPlacement(BlockPos pos, int delay, Runnable action) {
      DomainBarrierEntity.BlockPlacementTask task = new DomainBarrierEntity.BlockPlacementTask(delay, action);
      this.blockTasks.put(pos, task);
   }

   public void registerClasher(DomainExpansionEntity clasher) {
      if (clasher != null) {
         if (!this.clashers.contains(clasher)) {
            this.clashers.add(clasher);
         }

         if (!this.level().isClientSide) {
            this.createBarrier(false, clasher);
         }
      }
   }

   public void unregisterClasher(DomainExpansionEntity clasher, boolean collapse) {
      if (clasher != null) {
         this.clashers.remove(clasher);
         if (collapse && !this.level().isClientSide) {
            this.createBarrier(false, clasher, true);
         }
      }
   }

   private void discardClashers() {
      for (DomainExpansionEntity clasher : new ArrayList<>(this.clashers)) {
         if (clasher != null && !clasher.isRemoved() && !(clasher instanceof OpenDomainExpansionEntity)) {
            clasher.discard();
         }
      }
   }

   public void remove(@NotNull RemovalReason pReason) {
      super.remove(pReason);
      this.blockTasks.clear();
      int radius = this.getRadius();
      ServerLevel serverLevel = (ServerLevel)this.level();
      BlockPos center = BlockPos.containing(this.position().add(0.0, radius, 0.0));

      for (Vec3i off : getSphereOffsets(radius)) {
         double distance = off.getX() * off.getX() + off.getY() * off.getY() + off.getZ() * off.getZ();
         if (!(distance > radius * radius)) {
            BlockPos pos = center.offset(off);
            if (serverLevel.getBlockEntity(pos) instanceof DomainBlockEntity domainBe) {
               UUID id = domainBe.getIdentifier();
               if (id != null && id.equals(this.getUUID())) {
                  domainBe.destroy();
               }
            }
         }
      }

      Set<IBarrier> domains = VeilHandler.getBarriers((ServerLevel)this.level(), this.getBounds());

      for (Entry<UUID, Vec3> entry : this.positions.entrySet()) {
         UUID identifier = entry.getKey();
         boolean stillWithin = false;
         Entity entity = ((ServerLevel)this.level()).getEntity(identifier);
         if (entity != null && this.isInsideBarrier(entity.blockPosition())) {
            Vec3 pos = entry.getValue();

            for (IBarrier domain : domains) {
               if (domain != this && domain instanceof DomainBarrierEntity closed && closed.isInsideBarrier(entity.blockPosition())) {
                  stillWithin = true;
                  break;
               }
            }

            if (!stillWithin) {
               System.out.println("positionback");
               entity.teleportTo(pos.x, pos.y, pos.z);
            }
         }
      }
   }

   private void createBlock(int delay, BlockPos pos, int radius, double distance, BlockEntity existing, DomainExpansionEntity winner) {
      if (!(distance > radius * radius)) {
         if (this.level().isInWorldBounds(pos)) {
            if (existing instanceof DomainBlockEntity dbe && !(dbe instanceof DomainSkyBlockEntity)) {
               UUID owner = dbe.getPidentifier();
               if (owner != null && owner.equals(winner.getUUID())) {
                  return;
               }
            }

            BlockState state = this.level().getBlockState(pos);
            if (state == null
               || !state.is(Blocks.BEDROCK)
                  && (
                     !this.isCompleted()
                        || (!state.isAir() || state.is((Block)JJKBlocks.DOMAIN_AIR.get())) && !state.is((Block)JJKBlocks.DOMAIN.get())
                  )) {
               if (!this.isRemoved()) {
                  if (VeilHandler.getAdminVeil(this.level(), pos) == null) {
                     CompoundTag saved = null;
                     if (existing != null && !(existing instanceof ITemporaryBlockEntity)) {
                        saved = existing.saveWithFullMetadata();
                     }

                     DomainExpansion domain = (DomainExpansion)winner.getAbility();
                     List<Block> blocks = domain.getBlocks();
                     List<Block> fill = domain.getFillBlocks();
                     List<Block> floor = domain.getFloorBlocks();
                     List<Block> bottomfloor = domain.getBottomFloorBlocks();
                     List<Block> decoration = domain.getDecorationBlocks();
                     Block block = (Block)JJKBlocks.DOMAIN_AIR.get();
                     BlockPos center = BlockPos.containing(this.position().add(0.0, radius, 0.0));
                     if (distance >= (radius - 1) * (radius - 1)) {
                        block = (Block)JJKBlocks.DOMAIN.get();
                     } else if (pos.getY() < center.getY() - 1) {
                        if (!bottomfloor.isEmpty()) {
                           block = bottomfloor.get(this.random.nextInt(bottomfloor.size()));
                        } else if (!floor.isEmpty()) {
                           block = floor.get(this.random.nextInt(floor.size()));
                        } else {
                           block = fill.get(this.random.nextInt(fill.size()));
                        }
                     } else if (pos.getY() < center.getY()) {
                        block = floor.isEmpty() ? fill.get(this.random.nextInt(fill.size())) : floor.get(this.random.nextInt(floor.size()));
                     } else if (pos.getY() < center.getY()) {
                        block = bottomfloor.isEmpty() ? fill.get(this.random.nextInt(fill.size())) : floor.get(this.random.nextInt(floor.size()));
                     } else if (distance >= (radius - 2) * (radius - 2)) {
                        block = blocks.get(this.random.nextInt(blocks.size()));
                     } else if (!decoration.isEmpty() && pos.getY() == center.getY()) {
                        block = decoration.get(this.random.nextInt(decoration.size()));
                     }

                     if (JJKBlocks.DOMAIN_SKY.get() == block && this.level().getBlockEntity(pos) instanceof DomainSkyBlockEntity skybe) {
                        skybe.setDomain(JJKAbilities.getKey(winner.getAbility()));
                     } else {
                        BlockEntity be = ((ServerLevel)this.level()).getBlockEntity(pos);
                        if (be != null) {
                           ((ServerLevel)this.level()).removeBlockEntity(pos);
                        }

                        boolean success = this.level().setBlock(pos, block.defaultBlockState(), 50);
                        if (existing instanceof ITemporaryBlockEntity tmp) {
                           state = tmp.getOriginal();
                           saved = tmp.getSaved();
                        }

                        if (this.level().getBlockEntity(pos) instanceof DomainBlockEntity dbex) {
                           dbex.create(this.uuid, delay, state, saved, winner.getUUID());
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private Map<DomainExpansionEntity, Double> getClasherAngles(BlockPos center) {
      Map<DomainExpansionEntity, Double> result = new LinkedHashMap<>();
      List<Entry<DomainExpansionEntity, Double>> tmp = new ArrayList<>();

      for (DomainExpansionEntity clasher : this.clashers) {
         if (clasher != null) {
            Vec3 pos = clasher.position();
            Vec3 centerVec = center.getCenter();
            double dx = pos.x - centerVec.x;
            double dz = pos.z - centerVec.z;
            double angle = Math.toDegrees(Math.atan2(dz, dx));
            if (angle < 0.0) {
               angle += 360.0;
            }

            tmp.add(Map.entry(clasher, angle));
         }
      }

      int n = tmp.size();
      if (n == 0) {
         return result;
      } else {
         tmp.sort((a, bx) -> Double.compare(a.getValue(), (Double)bx.getValue()));
         if (n == 1) {
            result.put(tmp.get(0).getKey(), tmp.get(0).getValue());
            return result;
         } else if (n == 2) {
            DomainExpansionEntity a = tmp.get(0).getKey();
            DomainExpansionEntity b = tmp.get(1).getKey();
            Vec3 centerVec = center.getCenter();
            Vec3 pa = a.position().subtract(centerVec).multiply(1.0, 0.0, 1.0);
            Vec3 pb = b.position().subtract(centerVec).multiply(1.0, 0.0, 1.0);
            double abAngle = Math.toDegrees(Math.atan2(pb.z - pa.z, pb.x - pa.x));
            if (abAngle < 0.0) {
               abAngle += 360.0;
            }

            double bCenter = abAngle + 180.0;
            if (bCenter >= 360.0) {
               bCenter -= 360.0;
            }

            result.put(a, bCenter);
            result.put(b, abAngle);
            return result;
         } else {
            double anglePer = 360.0 / n;
            double sumX = 0.0;
            double sumY = 0.0;

            for (int i = 0; i < n; i++) {
               double theta = tmp.get(i).getValue();
               double raw = theta - i * anglePer;
               raw = ((raw + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;
               double rad = Math.toRadians(raw);
               sumX += Math.cos(rad);
               sumY += Math.sin(rad);
            }

            double avgRad = Math.atan2(sumY, sumX);
            double avgDeg = Math.toDegrees(avgRad);
            if (avgDeg < 0.0) {
               avgDeg += 360.0;
            }

            for (int i = 0; i < n; i++) {
               DomainExpansionEntity clasherx = tmp.get(i).getKey();
               double centerAngle = (i * anglePer + avgDeg) % 360.0;
               if (centerAngle < 0.0) {
                  centerAngle += 360.0;
               }

               result.put(clasherx, centerAngle);
            }

            return result;
         }
      }
   }

   private static double circularDistanceDeg(double a, double b) {
      double diff = Math.abs(a - b) % 360.0;
      return Math.min(diff, 360.0 - diff);
   }

   private DomainExpansionEntity getWinnerForBlock(BlockPos blockPos, BlockPos center, Map<DomainExpansionEntity, Double> clasherCenters) {
      if (clasherCenters.isEmpty()) {
         return null;
      } else if (clasherCenters.size() == 1) {
         return clasherCenters.keySet().iterator().next();
      } else {
         Vec3 blockDir = blockPos.getCenter().subtract(center.getCenter()).multiply(1.0, 0.0, 1.0);
         double blockAngle = Math.toDegrees(Math.atan2(blockDir.z, blockDir.x));
         if (blockAngle < 0.0) {
            blockAngle += 360.0;
         }

         DomainExpansionEntity best = null;
         double bestDist = Double.MAX_VALUE;

         for (Entry<DomainExpansionEntity, Double> entry : clasherCenters.entrySet()) {
            double centerAngle = entry.getValue() % 360.0;
            if (centerAngle < 0.0) {
               centerAngle += 360.0;
            }

            double dist = circularDistanceDeg(blockAngle, centerAngle);
            if (dist < bestDist) {
               bestDist = dist;
               best = entry.getKey();
            }
         }

         return best;
      }
   }

   private void createBarrier(boolean instant) {
      this.createBarrier(instant, null);
   }

   private void createBarrier(boolean instant, @Nullable DomainExpansionEntity leadClasher) {
      this.createBarrier(instant, leadClasher, false);
   }

   private void createBarrier(boolean instant, @Nullable DomainExpansionEntity leadClasher, boolean lose) {
      int radius = this.getRadius();
      BlockPos center = BlockPos.containing(this.position().add(0.0, radius, 0.0));
      Map<DomainExpansionEntity, Double> clasherAngles = this.getClasherAngles(center);
      List<DomainExpansionEntity> currentClashers = new ArrayList<>(this.clashers);
      if (clasherAngles.isEmpty()) {
         if (!this.isRemoved()) {
            this.discardClashers();
            this.discard();
         }
      } else {
         Vec3 behind;
         if (leadClasher != null) {
            if (lose) {
               Vec3 clasherPos = leadClasher.position();
               Vec3 centerpos = this.position().add(0.0, radius, 0.0);
               Vec3 dirToClasher = centerpos.subtract(clasherPos).normalize();
               behind = centerpos.add(dirToClasher.scale(radius - 5));
            } else {
               Vec3 clasherPos = leadClasher.position();
               Vec3 centerPos = this.position().add(0.0, radius, 0.0);
               Vec3 dirToClasher = clasherPos.subtract(centerPos).normalize();
               behind = centerPos.add(dirToClasher.scale(radius - 5));
            }
         } else {
            Vec3 direction = this.getLookAngle();
            behind = this.position().subtract(direction.scale(radius - 5)).add(0.0, radius, 0.0);
         }

         for (Vec3i off : getSphereOffsets(radius)) {
            BlockPos pos = center.offset(off);
            double distance = off.getX() * off.getX() + off.getY() * off.getY() + off.getZ() * off.getZ();
            if (this.level().isInWorldBounds(pos)) {
               int delay = (int)Math.round(pos.getCenter().distanceTo(behind)) / 2 + 1;
               if (instant) {
                  BlockEntity existing = this.level().getBlockEntity(pos);
                  DomainExpansionEntity winner = this.getWinnerForBlock(pos, center, clasherAngles);
                  if (winner != null) {
                     if (existing instanceof DomainBlockEntity var17) {
                        ;
                     }

                     this.createBlock(radius - delay, pos, radius, distance, existing, winner);
                  }
               } else {
                  this.scheduleBlockPlacement(pos, delay, () -> {
                     boolean same = this.clashers.size() == currentClashers.size() && this.clashers.containsAll(currentClashers);
                     DomainExpansionEntity winnerx = null;
                     if (!same) {
                        winnerx = this.getWinnerForBlock(pos, center, this.getClasherAngles(center));
                     } else {
                        winnerx = this.getWinnerForBlock(pos, center, clasherAngles);
                     }

                     BlockEntity existingx = this.level().getBlockEntity(pos);
                     if (winnerx != null) {
                        this.createBlock(radius - delay, pos, radius, distance, existingx, winnerx);
                     }
                  });
               }
            }
         }
      }
   }

   private void applyPreStun() {
      if (!this.level().isClientSide) {
         int radius = this.getRadius();
         BlockPos center = BlockPos.containing(this.position().add(0.0, radius, 0.0));

         for (LivingEntity entity : this.level()
            .getEntitiesOfClass(
               LivingEntity.class,
               new AABB(
                  center.getX() - radius,
                  center.getY() - radius,
                  center.getZ() - radius,
                  center.getX() + radius,
                  center.getY() + radius,
                  center.getZ() + radius
               ),
               entityx -> this.isInsideBarrier(entityx.blockPosition())
            )) {
            JJKEffectUtil.addEffect(entity, new MobEffectInstance((MobEffect)JJKEffects.DOMAINSTUN.get(), 30, 1, false, false, false));
            JJKEffectUtil.addEffect(entity, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 6, false, false, false));
         }
      }
   }

   public int getClasherSize() {
      int i = 0;

      for (DomainExpansionEntity clasher : this.clashers) {
         if (!(clasher instanceof OpenDomainExpansionEntity)) {
            i++;
         }
      }

      return i;
   }

   @Override
   public DomainExpansionEntity checkSureHitEffect() {
      if (!this.isCompleted()) {
         return null;
      } else {
         DomainExpansionEntity remainingEntity = null;
         int remainingDomains = 0;

         for (DomainExpansionEntity domain : this.clashers) {
            if (domain != null && !domain.isRemoved() && domain.isAlive()) {
               if (domain.getDomain().shouldCollapse(domain.getStrength())) {
                  domain.getDomain().discard();
               } else if (domain.getSureHitToggled() && !domain.isClashing()) {
                  if (++remainingDomains > 1) {
                     return null;
                  }

                  remainingEntity = domain;
               }
            }
         }

         return remainingEntity;
      }
   }

   public void tick() {
      super.tick();
      this.setTime(this.getTime() + 1);
      if (!this.level().isClientSide) {
         int radius = this.getRadius();
         if (!this.isCompleted() && !this.isRemoved()) {
            BlockPos center = BlockPos.containing(this.position().add(0.0, radius, 0.0));

            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBounds(), entityx -> this.isInsideBarrier(entityx.blockPosition()))) {
               if (!this.positions.containsKey(entity.getUUID())) {
                  this.positions.put(entity.getUUID(), entity.position());
               }

               if (entity.getY() < center.getY()) {
                  entity.teleportTo(entity.getX(), center.getY(), entity.getZ());
                  Vec3 motion = entity.getDeltaMovement();
                  if (motion.y < 0.0) {
                     entity.setDeltaMovement(motion.x, 0.0, motion.z);
                  }

                  entity.fallDistance = 0.0F;
               }
            }
         }

         DomainExpansionEntity sureHit = this.checkSureHitEffect();
         if (sureHit != null) {
            sureHit.doSureHitEffect();
         }

         if (this.isCompleted()) {
            if (!this.level().isClientSide) {
               AABB bounds = this.getBounds();
               Map<DomainExpansionEntity, Double> clasherAngles = this.getClasherAngles(
                  BlockPos.containing(this.position().add(0.0, this.getRadius(), 0.0))
               );

               for (DomainExpansionEntity clasher : this.clashers) {
                  if (clasher != null) {
                     ParticleOptions particle = ((DomainExpansion)clasher.getAbility()).getEnvironmentParticle();
                     if (particle != null) {
                        for (BlockPos pos : BlockPos.randomBetweenClosed(
                           this.random,
                           16,
                           (int)bounds.minX,
                           (int)bounds.minY,
                           (int)bounds.minZ,
                           (int)bounds.maxX,
                           (int)bounds.maxY,
                           (int)bounds.maxZ
                        )) {
                           DomainExpansionEntity winner = this.getWinnerForBlock(
                              pos, BlockPos.containing(this.position().add(0.0, this.getRadius(), 0.0)), clasherAngles
                           );
                           if (winner == clasher) {
                              Vec3 center = pos.getCenter();
                              ((ServerLevel)this.level()).sendParticles(particle, center.x, center.y, center.z, 0, 0.0, 0.0, 0.0, 0.0);
                           }
                        }
                     }
                  }
               }
            }

            if (this.getTime() % 20 == 0) {
               this.check();
            }
         } else if (this.getTime() == 1) {
            this.applyPreStun();
            this.refreshDimensions();
         }

         if (!this.blockTasks.isEmpty()) {
            int placedThisTick = 0;

            for (Entry<BlockPos, DomainBarrierEntity.BlockPlacementTask> entry : this.blockTasks.entrySet()) {
               DomainBarrierEntity.BlockPlacementTask task = entry.getValue();
               task.ticks--;
               if (task.ticks <= 0) {
                  this.toRun.add(entry.getKey());
               }
            }

            for (BlockPos posx : this.toRun) {
               if (placedThisTick >= 4096) {
                  break;
               }

               DomainBarrierEntity.BlockPlacementTask task = this.blockTasks.remove(posx);
               task.action.run();
               placedThisTick++;
            }

            this.toRun.clear();
         }

         if (this.getClasherSize() == 0 && !this.isRemoved()) {
            this.discard();
         }
      }
   }

   private static class BlockPlacementTask {
      int ticks;
      Runnable action;

      BlockPlacementTask(int ticks, Runnable action) {
         this.ticks = ticks;
         this.action = action;
      }
   }
}
