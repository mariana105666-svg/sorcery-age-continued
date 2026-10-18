package radon.jujutsu_kaisen.entity.effect;

import java.util.UUID;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.CursedBudProjectile;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DisasterPlantEntity extends JujutsuProjectile implements GeoEntity {
   private static final EntityDataAccessor<Integer> DATA_BUD_COUNT = SynchedEntityData.defineId(DisasterPlantEntity.class, EntityDataSerializers.INT);
   public static final int DEFAULT_BUD_COUNT = 15;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   @Nullable
   private UUID targetUUID;
   @Nullable
   private LivingEntity cachedTarget;

   public DisasterPlantEntity(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public DisasterPlantEntity(LivingEntity owner, float power, LivingEntity target) {
      super((EntityType<? extends Projectile>)JJKEntities.DISASTER_PLANT.get(), owner.level(), owner, power * 0.4F);
      this.setTarget(target);
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      EntityUtil.offset(this, look, owner.position().subtract(look.multiply(this.getBbWidth(), 0.0, this.getBbWidth())));
   }

   @NotNull
   public Vec3 getDeltaMovement() {
      return Vec3.ZERO;
   }

   public int getBudCount() {
      return (Integer)this.entityData.get(DATA_BUD_COUNT);
   }

   public void setTarget(@Nullable LivingEntity target) {
      if (target != null) {
         this.targetUUID = target.getUUID();
         this.cachedTarget = target;
      }
   }

   @Nullable
   public LivingEntity getTarget() {
      if (this.cachedTarget != null && !this.cachedTarget.isRemoved()) {
         return this.cachedTarget;
      } else if (this.targetUUID != null && this.level() instanceof ServerLevel) {
         this.cachedTarget = (LivingEntity)((ServerLevel)this.level()).getEntity(this.targetUUID);
         return this.cachedTarget;
      } else {
         return null;
      }
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      if (this.targetUUID != null) {
         pCompound.putUUID("target", this.targetUUID);
      }
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      if (pCompound.hasUUID("target")) {
         this.targetUUID = pCompound.getUUID("target");
      }
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_BUD_COUNT, 15);
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         LivingEntity target = this.getTarget();
         if (target != null && target.isAlive() && !target.isRemoved()) {
            this.lookAt(Anchor.EYES, target.position().add(0.0, target.getBbHeight() / 2.0F, 0.0));
            if (this.getOwner() instanceof LivingEntity owner) {
               int buds = (Integer)this.entityData.get(DATA_BUD_COUNT);
               if (buds == 0) {
                  this.discard();
               } else if (this.getTime() % 15 == 0) {
                  this.level().addFreshEntity(new CursedBudProjectile(owner, this.getPower(), this));
                  this.entityData.set(DATA_BUD_COUNT, --buds);
               }
            }
         } else {
            this.discard();
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
