package radon.jujutsu_kaisen.entity.effect;

import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.entity.JJKEntities;

public class BlackFlashEntity extends Entity {
   private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(BlackFlashEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Vector3f> DATA_START = SynchedEntityData.defineId(BlackFlashEntity.class, EntityDataSerializers.VECTOR3);
   private static final int DURATION = 10;
   @Nullable
   private UUID victimUUID;
   @Nullable
   private LivingEntity cachedVictim;
   @Nullable
   private UUID ownerUUID;
   @Nullable
   private LivingEntity cachedOwner;

   public BlackFlashEntity(EntityType<?> pType, Level pLevel) {
      super(pType, pLevel);
      this.noCulling = true;
   }

   public BlackFlashEntity(LivingEntity owner, LivingEntity target) {
      this((EntityType<?>)JJKEntities.BLACk_FLASH.get(), target.level());
      this.setOwner(owner);
      this.setVictim(target);
      this.entityData.set(DATA_START, owner.position().add(0.0, owner.getEyeHeight() - this.getBbHeight() / 2.0F, 0.0).toVector3f());
      this.setPos(target.position().add(0.0, target.getBbHeight() / 2.0F, 0.0).add(0.0, this.getBbHeight() / 2.0F, 0.0));
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_TIME, 0);
      this.entityData.define(DATA_START, Vec3.ZERO.toVector3f());
   }

   public int getTime() {
      return (Integer)this.entityData.get(DATA_TIME);
   }

   public void setTime(int time) {
      this.entityData.set(DATA_TIME, time);
   }

   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (this.victimUUID != null) {
         pCompound.putUUID("victim", this.victimUUID);
      }

      if (this.ownerUUID != null) {
         pCompound.putUUID("owner", this.ownerUUID);
      }

      pCompound.putInt("time", this.getTime());
      Vector3f start = (Vector3f)this.entityData.get(DATA_START);
      pCompound.putFloat("start_x", start.x);
      pCompound.putFloat("start_y", start.y);
      pCompound.putFloat("start_z", start.z);
   }

   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (pCompound.hasUUID("victim")) {
         this.victimUUID = pCompound.getUUID("victim");
      }

      if (pCompound.hasUUID("owner")) {
         this.ownerUUID = pCompound.getUUID("owner");
      }

      this.setTime(pCompound.getInt("time"));
      this.entityData.set(DATA_START, new Vector3f(pCompound.getFloat("start_x"), pCompound.getFloat("start_y"), pCompound.getFloat("start_z")));
   }

   public Vec3 getStart() {
      return new Vec3((Vector3f)this.entityData.get(DATA_START));
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      double d0 = this.getBoundingBox().getSize() * 10.0;
      if (Double.isNaN(d0)) {
         d0 = 1.0;
      }

      d0 *= 64.0 * getViewScale();
      return pDistance < d0 * d0;
   }

   public void tick() {
      super.tick();
      this.setTime(this.getTime() + 1);
      LivingEntity victim = this.getVictim();

      for (int i = 0; i < 32; i++) {
         double offsetX = this.random.nextGaussian() * 3.0;
         double offsetY = this.random.nextGaussian() * 3.0;
         double offsetZ = this.random.nextGaussian() * 3.0;
         this.level()
            .addParticle(
               (ParticleOptions)JJKParticles.BLACK_FLASH.get(), this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ, 0.0, 0.0, 0.0
            );
      }

      if (this.getTime() >= 10) {
         this.discard();
      } else if (victim != null) {
         this.setPos(victim.position().add(0.0, victim.getBbHeight() / 2.0F, 0.0).add(0.0, this.getBbHeight() / 2.0F, 0.0));
      }
   }

   public void setVictim(@Nullable LivingEntity victim) {
      if (victim != null) {
         this.victimUUID = victim.getUUID();
         this.cachedVictim = victim;
      }
   }

   @Nullable
   public LivingEntity getVictim() {
      if (this.cachedVictim != null && !this.cachedVictim.isRemoved()) {
         return this.cachedVictim;
      } else if (this.victimUUID != null && this.level() instanceof ServerLevel) {
         this.cachedVictim = (LivingEntity)((ServerLevel)this.level()).getEntity(this.victimUUID);
         return this.cachedVictim;
      } else {
         return null;
      }
   }

   public void setOwner(@Nullable LivingEntity pOwner) {
      if (pOwner != null) {
         this.ownerUUID = pOwner.getUUID();
         this.cachedOwner = pOwner;
      }
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
         return this.cachedOwner;
      } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
         this.cachedOwner = (LivingEntity)((ServerLevel)this.level()).getEntity(this.ownerUUID);
         return this.cachedOwner;
      } else {
         return null;
      }
   }
}
