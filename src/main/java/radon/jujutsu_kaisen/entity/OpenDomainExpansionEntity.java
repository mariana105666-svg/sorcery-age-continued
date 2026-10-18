package radon.jujutsu_kaisen.entity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.entity.IDomainBarrier;
import radon.jujutsu_kaisen.block.entity.IOpenDomainBarrier;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public abstract class OpenDomainExpansionEntity extends DomainExpansionEntity implements IOpenDomainBarrier {
   private static final EntityDataAccessor<Integer> DATA_RADIUS = SynchedEntityData.defineId(OpenDomainExpansionEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_HEIGHT = SynchedEntityData.defineId(OpenDomainExpansionEntity.class, EntityDataSerializers.INT);
   private final List<DomainExpansionEntity> clashers = new ArrayList<>();

   public OpenDomainExpansionEntity(EntityType<?> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public OpenDomainExpansionEntity(EntityType<?> pType, LivingEntity owner, DomainExpansion ability, int radius, int height) {
      super(pType, owner, ability);
      Vec3 pos = owner.position().subtract(RotationUtil.getTargetAdjustedLookAngle(owner).multiply(this.getBbWidth() / 2.0F, 0.0, this.getBbWidth() / 2.0F));
      this.moveTo(pos.x, pos.y, pos.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));
      this.entityData.set(DATA_RADIUS, radius);
      this.entityData.set(DATA_HEIGHT, height);
      this.registerClasher(this);

      for (IDomainBarrier domain : VeilHandler.getDomainBarriers((ServerLevel)owner.level(), owner.blockPosition())) {
         if (domain instanceof DomainBarrierEntity barrier) {
            barrier.registerClasher(this);
         }
      }
   }

   public void push(@NotNull Entity pEntity) {
   }

   @Override
   public List<DomainExpansionEntity> getClashers() {
      return this.clashers;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public void registerClasher(DomainExpansionEntity clasher) {
      if (clasher != null) {
         if (!this.clashers.contains(clasher)) {
            this.clashers.add(clasher);
         }
      }
   }

   public void unregisterClasher(DomainExpansionEntity clasher) {
      if (clasher != null) {
         this.clashers.remove(clasher);
      }
   }

   @Override
   public boolean isBarrier(BlockPos pos) {
      return this.isInsideBarrier(pos);
   }

   @Override
   public boolean isBarrierOrInside(BlockPos pos) {
      return this.isBarrier(pos);
   }

   @Override
   public DomainExpansionEntity checkSureHitEffect() {
      if (!this.getSureHitToggled()) {
         return null;
      } else if (this.isClashing()) {
         return null;
      } else {
         for (IDomainBarrier domain : VeilHandler.getDomainBarriers((ServerLevel)this.level(), this.getBounds())) {
            if (domain != this && domain.isInsideBarrier(this.blockPosition())) {
               for (DomainExpansionEntity d : domain.getClashers()) {
                  if (d != this && this.shouldCollapse(d.getStrength())) {
                     this.discard();
                  } else if (d != this) {
                     return null;
                  }
               }
            }
         }

         return this;
      }
   }

   @Override
   public boolean isAffected(BlockPos pos) {
      if (VeilHandler.isProtected(this.level(), pos)) {
         return false;
      } else {
         for (IDomainBarrier domain : VeilHandler.getDomainBarriers((ServerLevel)this.level(), pos)) {
            if (domain != this) {
               return false;
            }
         }

         return super.isAffected(pos);
      }
   }

   @Override
   public DomainExpansionEntity sureHitTarget(LivingEntity target) {
      DomainExpansionEntity surehit = this.checkSureHitEffect();
      return surehit != null && surehit.isAffected(target, false) ? surehit : null;
   }

   public void reexpand() {
   }

   @Override
   public int getRadius() {
      return (Integer)this.entityData.get(DATA_RADIUS);
   }

   public int getHeight() {
      return (Integer)this.entityData.get(DATA_HEIGHT);
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_RADIUS, 0);
      this.entityData.define(DATA_HEIGHT, 0);
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("radius", this.getRadius());
      pCompound.putInt("height", this.getHeight());
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.entityData.set(DATA_RADIUS, pCompound.getInt("radius"));
      this.entityData.set(DATA_HEIGHT, pCompound.getInt("height"));
   }

   public void onAddedToWorld() {
      super.onAddedToWorld();
      if (!this.level().isClientSide) {
         VeilHandler.barrier(this.level().dimension(), this.getUUID());
      }
   }

   @Override
   public AABB getBounds() {
      int radius = this.getRadius();
      int height = this.getHeight() / 2;
      return new AABB(
         this.getX() - radius,
         this.getY() - height,
         this.getZ() - radius,
         this.getX() + radius,
         this.getY() + height,
         this.getZ() + radius
      );
   }

   @Override
   public boolean isInsideBarrier(BlockPos pos) {
      int radius = this.getRadius();
      BlockPos center = this.blockPosition();
      BlockPos relative = pos.subtract(center);
      int dx = relative.getX();
      int dz = relative.getZ();
      return dx * dx + dz * dz >= radius * radius ? false : Math.abs(relative.getY()) < radius;
   }

   @Override
   public boolean shouldCollapse(float strength) {
      return strength / this.getStrength() > 2.5F;
   }

   @Override
   public void remove(@NotNull RemovalReason pReason) {
      super.remove(pReason);

      for (IDomainBarrier domain : VeilHandler.getDomainBarriers((ServerLevel)this.level(), this.blockPosition())) {
         if (domain instanceof DomainBarrierEntity barrier) {
            barrier.unregisterClasher(this, true);
         }
      }
   }

   @Override
   public void tick() {
      super.tick();
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         if (!this.level().isClientSide && this.checkSureHitEffect() != null) {
            this.doSureHitEffect();
         }
      }
   }
}
