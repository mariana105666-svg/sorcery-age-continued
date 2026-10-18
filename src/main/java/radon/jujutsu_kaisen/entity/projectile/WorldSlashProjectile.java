package radon.jujutsu_kaisen.entity.projectile;

import java.util.HashSet;
import java.util.Set;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.SlicedEntityParticle;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.ParticleUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

public class WorldSlashProjectile extends JujutsuProjectile {
   public static final int MIN_LENGTH = 3;
   public static final int MAX_LENGTH = 30;
   private static final EntityDataAccessor<Float> DATE_ROLL = SynchedEntityData.defineId(WorldSlashProjectile.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_LENGTH = SynchedEntityData.defineId(WorldSlashProjectile.class, EntityDataSerializers.INT);
   private static final int DURATION = 10;
   private static final float SCALAR = 6.0F;

   public WorldSlashProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public WorldSlashProjectile(LivingEntity owner, float power, float roll) {
      super((EntityType<? extends Projectile>)JJKEntities.WORLD_SLASH.get(), owner.level(), owner, power);
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - this.getBbHeight() / 2.0F, owner.getZ()).add(look));
      this.setRoll(roll);
   }

   public WorldSlashProjectile(LivingEntity owner, float power, float roll, Vec3 pos, int length) {
      super((EntityType<? extends Projectile>)JJKEntities.WORLD_SLASH.get(), owner.level(), owner, power);
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      EntityUtil.offset(this, look, pos.subtract(0.0, this.getBbHeight() / 2.0F, 0.0));
      this.setRoll(roll);
      this.setLength(length);
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATE_ROLL, 0.0F);
      this.entityData.define(DATA_LENGTH, 0);
   }

   public int getLength() {
      int length = (Integer)this.entityData.get(DATA_LENGTH);
      return length > 0 ? length : Math.max(3, Math.min(30, Mth.floor(6.0 * Math.pow(this.getPower(), 2.0))));
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
   public boolean canDeflect() {
      return false;
   }

   @Override
   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putFloat("roll", this.getRoll());
      pCompound.putInt("length", this.getLength());
   }

   @Override
   protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.entityData.set(DATE_ROLL, pCompound.getFloat("roll"));
      this.entityData.set(DATA_LENGTH, pCompound.getInt("length"));
   }

   protected void onInsideBlock(@NotNull BlockState pState) {
      if (pState.getBlock().defaultDestroyTime() <= -1.0F) {
         this.discard();
      }
   }

   private Vec3[] getWideSlicePositions(Vec3 center, Vec3 forward, Vec3 up, double radius, int samples) {
      Vec3 side = forward.cross(up).normalize();
      Vec3 trueUp = side.cross(forward).normalize();
      Vec3[] positions = new Vec3[samples * samples];
      int index = 0;

      for (int i = 0; i < samples; i++) {
         double offsetSide = ((double)i / (samples - 1) - 0.5) * 2.0 * radius;

         for (int j = 0; j < samples; j++) {
            double offsetUp = ((double)j / (samples - 1) - 0.5) * 2.0 * radius;
            positions[index++] = center.add(side.scale(offsetSide)).add(trueUp.scale(offsetUp));
         }
      }

      return positions;
   }

   public Set<Entity> getHits() {
      if (!(this.getOwner() instanceof LivingEntity)) {
         return Set.of();
      } else {
         LivingEntity owner = (LivingEntity)this.getOwner();
         Vec3 center = this.position().add(0.0, this.getBbHeight() / 2.0F, 0.0);
         float yaw = this.getYRot();
         float pitch = this.getXRot();
         float roll = this.getRoll();
         Vec3 forward = this.calculateViewVector(pitch, 180.0F - yaw);
         Vec3 up = this.calculateViewVector(pitch - 90.0F, 180.0F - yaw);
         Quaternionf quaternion = new Quaternionf()
            .rotateAxis((float)Math.toRadians(-roll), (float)forward.x, (float)forward.y, (float)forward.z);
         Vec3 side = new Vec3(quaternion.transform(forward.cross(up).toVector3f()));
         int length = this.getLength();
         Vec3 start = center.add(side.scale(length / 2.0));
         Vec3 end = center.add(forward.subtract(side.scale(length / 2.0)));
         Set<Entity> hits = new HashSet<>();
         double depth = Math.max(1L, Math.round(this.getDeltaMovement().length()));

         for (int z = 0; z < depth; z++) {
            for (int x = 0; x < length; x++) {
               double radius = 0.25 * Math.pow(this.getPower(), 2.0);
               int samples = 3;
               Vec3[] slicePositions = this.getWideSlicePositions(
                  start.add(forward.scale(z)).add(end.subtract(start).scale((double)x / length)), forward, up, radius, samples
               );

               for (Vec3 pos : slicePositions) {
                  BlockPos current = BlockPos.containing(pos);
                  AABB bounds = AABB.ofSize(current.getCenter(), 1.0, 1.0, 1.0);
                  hits.addAll(this.level().getEntities(this, bounds));
                  BlockState state = this.level().getBlockState(current);
                  if ((Boolean)ConfigHolder.SERVER.wcsCutAnything.get() || HelperMethods.isDestroyable(owner.level(), owner, current)) {
                     this.level().setBlockAndUpdate(current, Blocks.AIR.defaultBlockState());
                  }

                  if (!state.isAir()) {
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
         for (Entity entity : this.getHits()) {
            if (entity instanceof LivingEntity living) {
               if (this.getOwner() instanceof LivingEntity owner && living != owner) {
                  float distance = (float)Math.abs(living.getY() + living.getBbHeight() / 2.0F - (this.getY() + this.getBbHeight() / 2.0F));
                  if (distance < this.getBbHeight()) {
                     distance = 0.0F;
                  }

                  float strength = 1.0F - Math.min(living.getBbHeight(), distance) / living.getBbHeight();
                  if (living.hurt(JJKDamageSources.worldSlash(this, owner), living.getMaxHealth() * strength * this.getPower())) {
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
                     float dist = (float)plane.dot(center.subtract(living.position()));
                     ParticleUtil.sendParticles(
                        (ServerLevel)this.level(),
                        new SlicedEntityParticle.SliceParticleOptions(living.getId(), plane.toVector3f(), dist),
                        true,
                        living.getX(),
                        living.getY(),
                        living.getZ(),
                        0.0,
                        0.0,
                        0.0
                     );
                     living.setInvisible(true);
                     JJKEffectUtil.addEffect(living, new MobEffectInstance((MobEffect)JJKEffects.INVISIBILITY.get(), 60, 0, false, false, false));
                     JJKEffectUtil.addEffect(living, new MobEffectInstance(MobEffects.INVISIBILITY, 60, 0, false, false, false));
                  }
               }
            } else {
               entity.discard();
            }
         }
      }

      if (this.getTime() >= 10) {
         this.discard();
      }
   }
}
