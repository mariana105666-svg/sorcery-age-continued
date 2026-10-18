package radon.jujutsu_kaisen.entity.projectile;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.VeilBlock;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.SlicedEntityParticle;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.ParticleUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

public class DismantleProjectile extends JujutsuProjectile {
   private static final EntityDataAccessor<Float> DATE_ROLL = SynchedEntityData.defineId(DismantleProjectile.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_LENGTH = SynchedEntityData.defineId(DismantleProjectile.class, EntityDataSerializers.INT);
   private int DURATION = 10;
   private static final int LINE_LENGTH = 2;
   public static final int MIN_LENGTH = 3;
   public static final int MAX_LENGTH = 12;
   private boolean canHurt = true;
   private boolean instant;
   private boolean destroy = true;
   private int destroyed;

   public DismantleProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public DismantleProjectile(LivingEntity owner, float power, float roll) {
      super((EntityType<? extends Projectile>)JJKEntities.DISMANTLE.get(), owner.level(), owner, power);
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - this.getBbHeight() / 2.0F, owner.getZ()).add(look));
      this.setRoll(roll);
   }

   public DismantleProjectile(LivingEntity owner, float power, float roll, Vec3 pos, int length) {
      super((EntityType<? extends Projectile>)JJKEntities.DISMANTLE.get(), owner.level(), owner, power);
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      EntityUtil.offset(this, look, pos.subtract(0.0, this.getBbHeight() / 2.0F, 0.0));
      this.setRoll(roll);
      this.setLength(length);
   }

   public DismantleProjectile(LivingEntity owner, float power, float roll, Vec3 pos, int length, boolean instant, boolean destroy) {
      this(owner, power, roll, pos, length);
      this.moveTo(pos.x, pos.y, pos.z, (this.random.nextFloat() - 0.5F) * 360.0F, 0.0F);
      this.instant = instant;
      this.destroy = destroy;
   }

   public void setDuration(int tim) {
      this.DURATION = tim;
   }

   public void setCanHurt(boolean bool) {
      this.canHurt = bool;
   }

   protected float getDamage() {
      return 15.0F;
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATE_ROLL, 0.0F);
      this.entityData.define(DATA_LENGTH, 0);
   }

   public int getLength() {
      int length = (Integer)this.entityData.get(DATA_LENGTH);
      return length > 0 ? length : Math.max(3, Math.min(12, Mth.floor(2.0F * this.getPower())));
   }

   private void setLength(int length) {
      this.entityData.set(DATA_LENGTH, length);
   }

   public float getRoll() {
      return (Float)this.entityData.get(DATE_ROLL);
   }

   private void setRoll(float roll) {
      this.entityData.set(DATE_ROLL, roll);
   }

   @Override
   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putFloat("roll", this.getRoll());
      pCompound.putInt("length", this.getLength());
      pCompound.putBoolean("instant", this.instant);
      pCompound.putBoolean("destroy", this.destroy);
      pCompound.putInt("destroyed", this.destroyed);
   }

   @Override
   protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.entityData.set(DATE_ROLL, pCompound.getFloat("roll"));
      this.entityData.set(DATA_LENGTH, pCompound.getInt("length"));
      this.instant = pCompound.getBoolean("instant");
      this.destroy = pCompound.getBoolean("destroy");
      this.destroyed = pCompound.getInt("destroyed");
   }

   protected void onInsideBlock(@NotNull BlockState pState) {
      if (pState.getBlock().defaultDestroyTime() <= -1.0F) {
         this.discard();
      }
   }

   protected void onHitEntity(@NotNull EntityHitResult pResult) {
      super.onHitEntity(pResult);
      if (!this.level().isClientSide) {
         Entity entity = pResult.getEntity();
         if (this.getOwner() instanceof LivingEntity owner && entity instanceof LivingEntity living) {
            ISorcererData var21 = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if (owner != entity || var21.hasSelfHit()) {
               if (this.canHurt) {
                  DomainExpansionEntity domain = var21.getSummonByClass(DomainExpansionEntity.class);
                  if (entity.hurt(
                     JJKDamageSources.indirectJujutsuAttack((Entity)(domain == null ? this : domain), owner, (Ability)JJKAbilities.DISMANTLE.get()),
                     this.getDamage() * this.getPower()
                  )) {
                     if (!(Boolean)ConfigHolder.SERVER.entitySlicing.get() || !living.isDeadOrDying()) {
                        return;
                     }

                     ISorcererData targetCap = (ISorcererData)living.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
                     if (targetCap != null) {
                        targetCap.setRevivable(false);
                     }

                     Vec3 center = this.position().add(0.0, this.getBbHeight() / 2.0F, 0.0);
                     float yaw = this.getYRot();
                     float pitch = this.getXRot();
                     float roll = this.getRoll();
                     Vec3 forward = this.calculateViewVector(pitch, yaw);
                     Vec3 up = this.calculateViewVector(pitch - 90.0F, yaw);
                     Quaternionf quaternion = new Quaternionf()
                        .rotateAxis((float)Math.toRadians(-roll), (float)forward.x, (float)forward.y, (float)forward.z);
                     Vec3 side = new Vec3(quaternion.transform(forward.cross(up).toVector3f()));
                     int length = this.getLength();
                     Vec3 start = side.scale(length / 2.0);
                     Vec3 end = forward.subtract(start);
                     Vec3 plane = end.cross(start).normalize();
                     float distance = (float)plane.dot(center.subtract(living.position()));
                     ParticleUtil.sendParticles(
                        (ServerLevel)this.level(),
                        new SlicedEntityParticle.SliceParticleOptions(living.getId(), plane.toVector3f(), distance),
                        true,
                        living.getX(),
                        living.getY(),
                        living.getZ(),
                        0.0,
                        0.0,
                        0.0
                     );
                     living.setInvisible(true);
                     living.addEffect(new MobEffectInstance((MobEffect)JJKEffects.INVISIBILITY.get(), 60, 0, false, false, false));
                     living.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60, 0, false, false, false));
                  }
               }
            }
         }
      }
   }

   private Vec3 rotate(Vec3 vector, Vec3 axis, double degrees) {
      double radians = degrees * Math.PI / 180.0;
      double cosine = Math.cos(radians);
      double sine = Math.sin(radians);
      double xx = (1.0 - cosine) * axis.x * axis.x;
      double xy = (1.0 - cosine) * axis.x * axis.y;
      double xz = (1.0 - cosine) * axis.x * axis.z;
      double yy = (1.0 - cosine) * axis.y * axis.y;
      double yz = (1.0 - cosine) * axis.y * axis.z;
      double zz = (1.0 - cosine) * axis.z * axis.z;
      return new Vec3(
         (cosine + xx) * vector.x + (xy - axis.z * sine) * vector.y + (xz + axis.y * sine) * vector.z,
         (xy + axis.z * sine) * vector.x + (cosine + yy) * vector.y + (yz - axis.x * sine) * vector.z,
         (xz - axis.y * sine) * vector.x + (yz + axis.x * sine) * vector.y + (cosine + zz) * vector.z
      );
   }

   public List<HitResult> getHitResults() {
      if (!(this.getOwner() instanceof LivingEntity owner)) {
         return List.of();
      } else {
         Vec3 var21 = this.position().add(0.0, this.getBbHeight() / 2.0F, 0.0);
         float yaw = this.getYRot();
         float pitch = this.getXRot();
         float roll = this.getRoll();
         Vec3 forward = this.calculateViewVector(pitch, 180.0F - yaw);
         Vec3 up = this.calculateViewVector(pitch - 90.0F, 180.0F - yaw);
         Vec3 side = this.rotate(forward.cross(up), forward, -roll);
         int length = this.getLength();
         Vec3 start = var21.add(side.scale(length / 2.0));
         Vec3 end = var21.add(forward.subtract(side.scale(length / 2.0)));
         ArrayList hits = new ArrayList();
         double depth = Math.max(1L, Math.round(this.getDeltaMovement().length()));

         for (int z = 0; z < depth; z++) {
            for (int x = 0; x < length; x++) {
               BlockPos current = BlockPos.containing(start.add(end.subtract(start).scale(1.0 / length * x).add(forward.scale(z))));
               AABB bounds = AABB.ofSize(current.getCenter(), 0.5, 0.5, 0.5);

               for (Entity entity : this.level().getEntities(this, bounds)) {
                  hits.add(new EntityHitResult(entity));
               }

               if (this.destroy && this.canHurt) {
                  BlockState state = this.level().getBlockState(current);
                  if (state.getBlock() instanceof VeilBlock) {
                     this.discard();
                     this.destroyed += 15;
                     return hits;
                  }

                  if (!this.isDomain()
                     && HelperMethods.isDestroyable(
                        this.level(),
                        owner,
                        current,
                        JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.DISMANTLE.get()),
                        this.getDamage() * this.getPower()
                     )) {
                     boolean destroyed;
                     if (state.getFluidState().isEmpty()) {
                        destroyed = owner.level().setBlock(current, Blocks.AIR.defaultBlockState(), 18);
                     } else {
                        destroyed = this.level().setBlockAndUpdate(current, Blocks.AIR.defaultBlockState());
                     }

                     if (destroyed) {
                        ((ServerLevel)this.level())
                           .sendParticles(
                              ParticleTypes.EXPLOSION,
                              current.getCenter().x,
                              current.getCenter().y,
                              current.getCenter().z,
                              0,
                              1.0,
                              0.0,
                              0.0,
                              1.0
                           );
                        this.destroyed++;
                     }
                  }
               }
            }
         }

         return hits;
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         for (HitResult result : this.getHitResults()) {
            if (result.getType() != Type.MISS) {
               this.onHit(result);
            }
         }

         if (this.instant || this.destroyed >= this.getLength() * 2 || this.getTime() >= this.DURATION) {
            this.discard();
         }
      }
   }
}
