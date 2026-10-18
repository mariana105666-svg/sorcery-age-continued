package radon.jujutsu_kaisen.entity.effect;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class MeteorEntity extends JujutsuProjectile {
   private static final EntityDataAccessor<Integer> DATA_EXPLOSION_TIME = SynchedEntityData.defineId(MeteorEntity.class, EntityDataSerializers.INT);
   public static final int SIZE = 5;
   public static final int HEIGHT = 20;
   private static final int MAX_SIZE = 25;
   public static final int DELAY = 80;
   private static final double SPEED = 2.0;
   private static final int DURATION = 100;
   private static final float DAMAGE = 18.0F;
   private int lerpSteps;
   private double lerpX;
   private double lerpY;
   private double lerpZ;
   private double lerpYRot;
   private double lerpXRot;

   public MeteorEntity(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
      this.noCulling = true;
   }

   public MeteorEntity(LivingEntity owner, float power) {
      super((EntityType<? extends Projectile>)JJKEntities.METEOR.get(), owner.level(), owner, power);
      owner.setPos(owner.position().add(0.0, 20 + getSize(power), 0.0));
      this.applyOffset();
   }

   public void push(@NotNull Entity pEntity) {
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_EXPLOSION_TIME, 0);
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return true;
   }

   public boolean shouldRiderSit() {
      return false;
   }

   public double getPassengersRidingOffset() {
      return this.getBbHeight() * 1.5F;
   }

   @Override
   public boolean canDeflect() {
      return false;
   }

   @Override
   protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setExplosionTime(pCompound.getInt("explosion_time"));
   }

   @Override
   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("explosion_time", this.getExplosionTime());
   }

   private float getFrictionInfluencedSpeed(float pFriction) {
      return this.onGround() ? 0.7F * (0.21600002F / (pFriction * pFriction * pFriction)) : 0.02F;
   }

   public Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 pDeltaMovement, float pFriction) {
      this.moveRelative(this.getFrictionInfluencedSpeed(pFriction), pDeltaMovement);
      this.move(MoverType.SELF, this.getDeltaMovement());
      return this.getDeltaMovement();
   }

   private void doPush(Entity entity) {
      entity.push(this);
   }

   private void pushEntities() {
      AABB bounds = AABB.ofSize(this.position(), this.getSize() * 2, this.getSize() * 2, this.getSize() * 2);
      if (this.level().isClientSide) {
         this.level().getEntities(EntityTypeTest.forClass(Player.class), bounds, EntitySelector.pushableBy(this)).forEach(this::doPush);
      } else {
         List<Entity> entities = this.level().getEntities(this, bounds, EntitySelector.pushableBy(this));
         if (!entities.isEmpty()) {
            int i = this.level().getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
            if (i > 0 && entities.size() > i - 1 && this.random.nextInt(4) == 0) {
               int j = 0;

               for (Entity entity : entities) {
                  if (!entity.isPassenger()) {
                     j++;
                  }
               }

               if (j > i - 1) {
                  this.hurt(this.damageSources().cramming(), 4.0F);
               }
            }

            for (Entity entityx : entities) {
               this.doPush(entityx);
            }
         }
      }
   }

   public void lerpTo(double pX, double pY, double pZ, float pYaw, float pPitch, int pPosRotationIncrements, boolean pTeleport) {
      this.lerpX = pX;
      this.lerpY = pY;
      this.lerpZ = pZ;
      this.lerpYRot = pYaw;
      this.lerpXRot = pPitch;
      this.lerpSteps = pPosRotationIncrements;
   }

   public void travel() {
      if (this.isControlledByLocalInstance()) {
         double d0 = 0.08;
         BlockPos pos = this.getBlockPosBelowThatAffectsMyMovement();
         float f2 = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getFriction(this.level(), this.getBlockPosBelowThatAffectsMyMovement(), this);
         float f3 = this.onGround() ? f2 * 0.91F : 0.91F;
         Vec3 vec35 = this.handleRelativeFrictionAndCalculateMovement(Vec3.ZERO, f2);
         double d2 = vec35.y;
         if (this.level().isClientSide && !this.level().hasChunkAt(pos)) {
            if (this.getY() > this.level().getMinBuildHeight()) {
               d2 = -0.1;
            } else {
               d2 = 0.0;
            }
         } else if (!this.isNoGravity()) {
            d2 -= d0;
         }

         this.setDeltaMovement(vec35.x * f3, d2 * 0.98F, vec35.z * f3);
      }
   }

   public void aiStep() {
      if (this.isControlledByLocalInstance()) {
         this.lerpSteps = 0;
         this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
      }

      if (this.lerpSteps > 0) {
         double d0 = this.getX() + (this.lerpX - this.getX()) / this.lerpSteps;
         double d2 = this.getY() + (this.lerpY - this.getY()) / this.lerpSteps;
         double d4 = this.getZ() + (this.lerpZ - this.getZ()) / this.lerpSteps;
         double d6 = Mth.wrapDegrees(this.lerpYRot - this.getYRot());
         this.setYRot(this.getYRot() + (float)d6 / this.lerpSteps);
         this.setXRot(this.getXRot() + (float)(this.lerpXRot - this.getXRot()) / this.lerpSteps);
         this.lerpSteps--;
         this.setPos(d0, d2, d4);
         this.setRot(this.getYRot(), this.getXRot());
      } else if (!this.isEffectiveAi()) {
         this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
      }

      Vec3 vec31 = this.getDeltaMovement();
      double d1 = vec31.x;
      double d3 = vec31.y;
      double d5 = vec31.z;
      if (Math.abs(vec31.x) < 0.003) {
         d1 = 0.0;
      }

      if (Math.abs(vec31.y) < 0.003) {
         d3 = 0.0;
      }

      if (Math.abs(vec31.z) < 0.003) {
         d5 = 0.0;
      }

      this.setDeltaMovement(d1, d3, d5);
      this.travel();
      this.pushEntities();
   }

   public static int getSize(float power) {
      return Math.min(25, Math.round(5.0F * power));
   }

   public int getSize() {
      return Math.round(getSize(this.getPower()) * (Math.min(80, this.getTime()) / 80.0F));
   }

   @NotNull
   public EntityDimensions getDimensions(@NotNull Pose pPose) {
      int size = this.getSize();
      return EntityDimensions.fixed(size, size);
   }

   private void hurtEntities() {
      double radius = this.getSize();
      AABB bounds = this.getBoundingBox().inflate(radius * 1.5);
      if (this.getOwner() instanceof LivingEntity owner) {
         for (Entity entity : this.level().getEntities(owner, bounds)) {
            if (!(Math.sqrt(entity.distanceToSqr(this.getX(), this.getY() + this.getBbHeight() / 2.0F, this.getZ())) >= this.getSize())
               && entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.MAXIMUM_METEOR.get()), 18.0F * this.getPower())) {
               entity.setSecondsOnFire(10);
            }
         }
      }
   }

   @Override
   protected boolean isProjectile() {
      return false;
   }

   private void spawnParticles() {
      Vec3 center = new Vec3(this.getX(), this.getY() + this.getBbHeight() / 2.0F, this.getZ());
      float radius = this.getSize() * 1.1F;
      int count = (int)(radius * Math.PI * 2.0) / 2;

      for (int i = 0; i < count; i++) {
         double theta = this.random.nextDouble() * Math.PI * 2.0;
         double phi = this.random.nextDouble() * Math.PI;
         double xOffset = radius * Math.sin(phi) * Math.cos(theta);
         double yOffset = radius * Math.sin(phi) * Math.sin(theta);
         double zOffset = radius * Math.cos(phi);
         double x = center.x + xOffset;
         double y = center.y + yOffset;
         double z = center.z + zOffset;
         this.level()
            .addParticle(
               new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.FIRE_ORANGE, radius * 0.4F, 0.25F, true, true, 20),
               true,
               x,
               y,
               z,
               0.0,
               0.0,
               0.0
            );
      }
   }

   private void evaporateWater() {
      if (this.getOwner() instanceof LivingEntity owner) {
         float var17 = this.getSize();
         AABB bounds = AABB.ofSize(this.position(), var17 * 2.0F, var17 * 2.0F, var17 * 2.0F);
         double centerX = bounds.getCenter().x;
         double centerY = bounds.getCenter().y;
         double centerZ = bounds.getCenter().z;

         for (int x = (int)bounds.minX; x <= bounds.maxX; x++) {
            for (int y = (int)bounds.minY; y <= bounds.maxY; y++) {
               for (int z = (int)bounds.minZ; z <= bounds.maxZ; z++) {
                  BlockPos pos = new BlockPos(x, y, z);
                  BlockState state = this.level().getBlockState(pos);
                  double distance = Math.sqrt(Math.pow(x - centerX, 2.0) + Math.pow(y - centerY, 2.0) + Math.pow(z - centerZ, 2.0));
                  if (!(distance > var17) && HelperMethods.isDestroyable(this.level(), owner, pos) && !state.getFluidState().isEmpty()) {
                     owner.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 18);
                     this.level().levelEvent(1501, pos, 0);
                  }
               }
            }
         }
      }
   }

   private void breakBlocks() {
      if (this.getOwner() instanceof LivingEntity owner) {
         float var17 = this.getSize();
         AABB bounds = AABB.ofSize(this.position(), var17 * 2.0F, var17 * 2.0F, var17 * 2.0F);
         double centerX = bounds.getCenter().x;
         double centerY = bounds.getCenter().y;
         double centerZ = bounds.getCenter().z;

         for (int x = (int)bounds.minX; x <= bounds.maxX; x++) {
            for (int y = (int)bounds.minY; y <= bounds.maxY; y++) {
               for (int z = (int)bounds.minZ; z <= bounds.maxZ; z++) {
                  BlockPos pos = new BlockPos(x, y, z);
                  BlockState state = this.level().getBlockState(pos);
                  double distance = Math.sqrt(Math.pow(x - centerX, 2.0) + Math.pow(y - centerY, 2.0) + Math.pow(z - centerZ, 2.0));
                  if (!(distance > var17)
                     && HelperMethods.isDestroyable(
                        this.level(),
                        owner,
                        pos,
                        JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.MAXIMUM_METEOR.get()),
                        18.0F * this.getPower()
                     )
                     && state.getFluidState().isEmpty()) {
                     this.level().destroyBlock(pos, false);
                  }
               }
            }
         }
      }
   }

   public int getExplosionTime() {
      return (Integer)this.entityData.get(DATA_EXPLOSION_TIME);
   }

   private void setExplosionTime(int time) {
      this.entityData.set(DATA_EXPLOSION_TIME, time);
   }

   private void applyOffset() {
      Entity owner = this.getOwner();
      if (owner != null) {
         Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
         this.setPos(owner.position().subtract(look.multiply(this.getBbWidth() * 2.0F, this.getSize() * 2, this.getBbWidth() * 2.0F)));
      }
   }

   @Override
   public void tick() {
      super.tick();
      this.refreshDimensions();
      if (this.getExplosionTime() == 0 && this.getTime() - 80 >= 100) {
         this.discard();
      } else {
         this.spawnParticles();
         if (this.getOwner() instanceof LivingEntity owner) {
            if (this.getTime() >= 80 && this.getExplosionTime() == 0) {
               this.move(MoverType.SELF, this.getDeltaMovement());
            } else {
               this.aiStep();
            }

            if (this.getTime() < 80) {
               Vec3 movement = owner.getDeltaMovement();
               if (movement.y < 0.0) {
                  owner.setDeltaMovement(movement.x, 0.0, movement.z);
                  owner.moveRelative(0.04F, new Vec3(owner.xxa, 0.0, owner.zza));
               }

               if (!this.level().isClientSide) {
                  this.applyOffset();
               }
            } else if (!this.level().isClientSide) {
               this.hurtEntities();
               if (this.getTime() == 80) {
                  this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(2.0));
               }

               int duration = this.getSize() * 5;
               int time = this.getExplosionTime();
               if (time > 0) {
                  if (time >= duration) {
                     this.discard();
                  } else {
                     this.setExplosionTime(++time);
                  }
               } else {
                  Vec3 start = this.position();
                  Vec3 end = start.add(this.getDeltaMovement().scale(this.getSize() / 2.0));
                  BlockHitResult clip = this.level().clip(new ClipContext(start, end, Block.COLLIDER, Fluid.ANY, this));
                  if (!this.level().getBlockState(clip.getBlockPos()).isAir()) {
                     this.setExplosionTime(1);
                     ExplosionHandler.spawn(
                        this.level().dimension(),
                        clip.getLocation(),
                        this.getSize() * 1.5F,
                        duration,
                        this.getPower() * 0.5F,
                        owner,
                        JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.MAXIMUM_METEOR.get()),
                        true
                     );
                  }

                  this.breakBlocks();
               }

               this.evaporateWater();
            }
         }
      }
   }
}
