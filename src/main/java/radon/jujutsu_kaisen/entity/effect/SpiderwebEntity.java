package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.shrine.Spiderweb;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class SpiderwebEntity extends JujutsuProjectile {
   private static final float RADIUS = 3.0F;
   private static final float MAX_RADIUS = 16.0F;
   private static final EntityDataAccessor<BlockPos> DATA_CENTER = SynchedEntityData.defineId(SpiderwebEntity.class, EntityDataSerializers.BLOCK_POS);
   private static final EntityDataAccessor<Integer> DATA_FACE = SynchedEntityData.defineId(SpiderwebEntity.class, EntityDataSerializers.INT);
   private int charge;

   public SpiderwebEntity(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public SpiderwebEntity(LivingEntity owner, float power, BlockPos pos, Direction face) {
      super((EntityType<? extends Projectile>)JJKEntities.SPIDERWEB.get(), owner.level(), owner, power);
      this.noPhysics = true;
      this.entityData.set(DATA_CENTER, pos);
      this.entityData.set(DATA_FACE, face.ordinal());
      this.update();
   }

   private BlockPos getCenter() {
      return (BlockPos)this.entityData.get(DATA_CENTER);
   }

   private Direction getFace() {
      return Direction.values()[this.entityData.get(DATA_FACE)];
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_CENTER, BlockPos.ZERO);
      this.entityData.define(DATA_FACE, -1);
   }

   @Override
   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.put("center", (Tag)BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.getCenter()).result().orElseThrow());
      pCompound.putInt("face", (Integer)this.entityData.get(DATA_FACE));
   }

   @Override
   protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.entityData.set(DATA_CENTER, (BlockPos)BlockPos.CODEC.parse(NbtOps.INSTANCE, pCompound.get("center")).result().orElseThrow());
      this.entityData.set(DATA_FACE, pCompound.getInt("face"));
   }

   private void update() {
      BlockPos center = this.getCenter();
      Direction face = this.getFace();
      Vec3 pos = center.relative(face).getCenter();
      pos = pos.subtract(face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
      float xRot = (float)(Mth.atan2(face.getStepY(), face.getStepX()) * 180.0 / (float) Math.PI);
      switch (face) {
         case UP:
         case DOWN:
            xRot = -xRot;
            break;
         case WEST:
            xRot -= 180.0F;
      }

      float radius = this.getScaledRadius();
      Direction opposite = face.getOpposite();
      pos = pos.add(opposite.getStepX() * radius, opposite.getStepY() * radius, opposite.getStepZ() * radius);
      this.moveTo(pos.x, pos.y - radius, pos.z, face.toYRot(), xRot);
   }

   public float getMaximumRadius() {
      return Math.max(3.0F, Math.min(16.0F, 3.0F * this.getPower() * 0.133F));
   }

   public float getScaledRadius() {
      float radius = this.getMaximumRadius();
      float scale = (float)Math.pow(Math.min(15, this.charge) / 15.0, 0.5);
      return radius * scale;
   }

   @NotNull
   public Vec3 getDeltaMovement() {
      return Vec3.ZERO;
   }

   @NotNull
   public EntityDimensions getDimensions(@NotNull Pose pPose) {
      float radius = this.getScaledRadius() * 2.0F;
      return EntityDimensions.fixed(radius, radius);
   }

   public void onAddedToWorld() {
      super.onAddedToWorld();
      this.refreshDimensions();
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getOwner() instanceof LivingEntity owner) {
         ISorcererData var28 = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         Spiderweb ability = (Spiderweb)JJKAbilities.SPIDERWEB.get();
         if (!var28.isChanneling(ability)) {
            this.explode();
            this.discard();
         } else {
            this.charge = ability.getCharge(owner);
            int time = this.getTime();
            if (time < 15 && time % 2 == 0) {
               owner.level()
                  .playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
            }

            if (owner.level() instanceof ServerLevel level) {
               float radius = this.getScaledRadius();
               float real = radius % 2.0F == 0.0F ? radius + 1.0F : radius;
               Vec3 webPos = this.position();
               double dropRadius = real;
               int minX = Mth.floor(webPos.x - dropRadius);
               int maxX = Mth.floor(webPos.x + dropRadius);
               int minZ = Mth.floor(webPos.z - dropRadius);
               int maxZ = Mth.floor(webPos.z + dropRadius);
               Direction face = this.getFace();
               int topY;
               int bottomY;
               switch (face) {
                  case UP:
                     topY = Mth.floor(webPos.y + dropRadius * 2.0);
                     bottomY = Mth.floor(webPos.y - dropRadius);
                     break;
                  case DOWN:
                     topY = Mth.floor(webPos.y + dropRadius);
                     bottomY = Mth.floor(webPos.y);
                     break;
                  default:
                     topY = Mth.floor(webPos.y + dropRadius);
                     bottomY = Mth.floor(webPos.y - dropRadius);
               }

               for (int x = minX; x <= maxX; x++) {
                  for (int z = minZ; z <= maxZ; z++) {
                     double dx = x + 0.5 - webPos.x;
                     double dz = z + 0.5 - webPos.z;
                     if (!(dx * dx + dz * dz > dropRadius * dropRadius)) {
                        for (int y = topY; y >= bottomY; y--) {
                           BlockPos pos = new BlockPos(x, y, z);
                           BlockState state = owner.level().getBlockState(pos);
                           int bound = Math.max(1, Math.round(radius) * 6);
                           if (HelperMethods.RANDOM.nextInt(bound) == 0 && !state.isAir() && time % 2 == 0) {
                              level.sendParticles(
                                 (SimpleParticleType)JJKParticles.SPIDERWEB.get(), pos.getX(), pos.getY(), pos.getZ(), 1, 0.0, 0.0, 0.0, 0.0
                              );
                           }
                        }
                     }
                  }
               }
            }

            this.refreshDimensions();
            this.update();
         }
      }
   }

   private void explode() {
      if (this.level() instanceof ServerLevel level) {
         if (this.getOwner() instanceof LivingEntity owner) {
            float radius = this.getScaledRadius();
            float diameter = radius * 2.0F;
            Vec3 look = this.getLookAngle();
            Vec3 direction = look.scale(radius);
            Vec3 center = this.position().add(0.0, radius, 0.0).add(direction);
            int size = (int)Math.ceil(radius);
            int cx = Mth.floor(center.x);
            int cy = Mth.floor(center.y);
            int cz = Mth.floor(center.z);
            AABB box = new AABB(
               center.x - radius,
               center.y - radius,
               center.z - radius,
               center.x + radius,
               center.y + radius,
               center.z + radius
            );

            for (Entity entity : owner.level().getEntities(null, box)) {
               if (entity instanceof LivingEntity living && living != owner && owner.canAttack(living) && entity.onGround()) {
                  Vec3 entityCenter = entity.position().add(0.0, entity.getBbHeight() * 0.5, 0.0);
                  Vec3 offset = entityCenter.subtract(center);
                  if (!(offset.length() > radius)) {
                     JJKEffectUtil.addEffect(living, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), 20, 0, false, false, false));
                     Vec3 knock = offset.normalize();
                     living.push(knock.x * 0.1, knock.y * 0.1, knock.z * 0.1);
                  }
               }
            }

            for (int x = cx - size; x <= cx + size; x++) {
               for (int y = cy - size; y <= cy + size; y++) {
                  for (int z = cz - size; z <= cz + size; z++) {
                     Vec3 blockCenter = new Vec3(x + 0.5, y + 0.5, z + 0.5);
                     Vec3 offset = blockCenter.subtract(center);
                     if (!(offset.dot(look) >= 0.0) && !(offset.length() > radius)) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = level.getBlockState(pos);
                        if (HelperMethods.isDestroyable(level, owner, pos)) {
                           if (state.getFluidState().isEmpty()) {
                              if (this.random.nextFloat() < 0.1F) {
                                 if (level.destroyBlock(pos, false)) {
                                    FallingBlockEntity entityx = FallingBlockEntity.fall(owner.level(), pos, state);
                                    entityx.noPhysics = true;
                                 }
                              } else {
                                 level.destroyBlock(pos, false);
                              }
                           } else {
                              level.setBlock(pos, Blocks.AIR.defaultBlockState(), 50);
                           }
                        }
                     }
                  }
               }
            }

            level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, diameter, 1.0F);
         }
      }
   }

   @Override
   protected boolean isProjectile() {
      return false;
   }
}
