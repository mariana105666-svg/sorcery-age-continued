package radon.jujutsu_kaisen.entity.effect;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BodyRepelEntity extends Projectile implements GeoEntity {
   public static EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(BodyRepelEntity.class, EntityDataSerializers.INT);
   private static final double HITBOX_RADIUS = 1.5;
   private static final double HITBOX_HEIGHT = 1.5;
   private AABB hitbox;
   private static final double SPEED = 1.0;
   private static final float DAMAGE = 10.0F;
   private static final float EXPLOSIVE_POWER = 2.0F;
   private static final float MAX_EXPLOSION = 15.0F;
   private static final int DURATION = 60;
   private static final int MAX_SEGMENTS = 24;
   private final BodyRepelSegmentEntity[] segments;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int souls;

   protected void defineSynchedData() {
      this.entityData.define(DATA_TIME, 1);
   }

   private void initializeSegments() {
      Vec3 prevPos = this.position();

      for (int i = 0; i < this.segments.length; i++) {
         BodyRepelSegmentEntity seg = this.segments[i];
         double offsetY = prevPos.y - (i + 1) * seg.getBbHeight();
         seg.setPos(prevPos.x, offsetY, prevPos.z);
         prevPos = seg.position();
      }
   }

   public BodyRepelEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.segments = new BodyRepelSegmentEntity[24];

      for (int i = 0; i < this.segments.length; i++) {
         this.segments[i] = new BodyRepelSegmentEntity(this, i);
      }

      this.setId(ENTITY_COUNTER.getAndAdd(this.segments.length + 1) + 1);
      this.initializeSegments();
   }

   public BodyRepelEntity(LivingEntity pShooter, int souls) {
      this((EntityType<? extends Projectile>)JJKEntities.BODY_REPEL.get(), pShooter.level());
      this.setOwner(pShooter);
      this.souls = souls;
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(pShooter);
      this.moveTo(pShooter.getEyePosition());
      Vec3 dest = new Vec3(pShooter.getX(), pShooter.getEyeY() - this.getBbHeight() / 2.0F, pShooter.getZ()).add(look);
      EntityUtil.offset(this, pShooter.getLookAngle(), dest);
      this.setYRot(pShooter.getYHeadRot());
      double speed = 1.0 * (1.0 + 3.0 * this.souls / 10.0);
      this.setDeltaMovement(look.scale(speed));
      if (pShooter.level() instanceof ServerLevel level) {
         level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 0, 0.0, 0.0, 0.0, 1.0);
      }
   }

   public int getTime() {
      return (Integer)this.entityData.get(DATA_TIME);
   }

   public void setTime(int time) {
      this.entityData.set(DATA_TIME, time);
   }

   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("time", this.getTime());
      pCompound.putInt("souls", this.souls);
   }

   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setTime(pCompound.getInt("time"));
      this.souls = pCompound.getInt("souls");
   }

   public void setId(int id) {
      super.setId(id);

      for (int i = 0; i < this.segments.length; i++) {
         this.segments[i].setId(id + i + 1);
      }
   }

   @NotNull
   public PartEntity<?>[] getParts() {
      return this.segments;
   }

   public void remove(@NotNull RemovalReason reason) {
      super.remove(reason);
      if (!this.level().isClientSide) {
         for (BodyRepelSegmentEntity seg : this.segments) {
            seg.kill();
         }
      }
   }

   private void moveSegments() {
      for (int i = 0; i < this.segments.length; i++) {
         this.segments[i].tick();
         Entity leader = (Entity)(i == 0 ? this : this.segments[i - 1]);
         Vec3 follow = i == 0
            ? leader.position().add(0.0, this.getBbHeight() - this.segments[i].getBbHeight(), 0.0).add(this.getDeltaMovement())
            : leader.position();
         Vec3 diff = new Vec3(
               this.segments[i].getX() - follow.x, this.segments[i].getY() - follow.y, this.segments[i].getZ() - follow.z
            )
            .normalize();
         double f = leader.getBbWidth() / 2.0F + this.segments[i].getBbWidth() / 2.0F;
         double destX = follow.x + f * diff.x;
         double destY = follow.y + f * diff.y;
         double destZ = follow.z + f * diff.z;
         this.segments[i].setPos(destX, destY, destZ);
         double d0 = diff.horizontalDistance();
         this.segments[i]
            .setRot((float)(Math.atan2(diff.z, diff.x) * 180.0 / Math.PI) + 90.0F, (float)(Math.atan2(diff.y, d0) * 180.0 / Math.PI));
      }
   }

   public boolean isMultipartEntity() {
      return this.segments != null;
   }

   public boolean isPushable() {
      return false;
   }

   protected boolean canRide(Entity entity) {
      return false;
   }

   public boolean canCollideWith(Entity entity) {
      return true;
   }

   private boolean isSelfOrSegment(Entity entity) {
      if (entity != this && entity != this.getOwner()) {
         for (BodyRepelSegmentEntity seg : this.segments) {
            if (entity == seg) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public void tick() {
      this.setTime(this.getTime() + 1);
      Entity owner = this.getOwner();
      if (this.level().isClientSide || owner != null && !owner.isRemoved() && owner.isAlive()) {
         super.tick();
         if (this.getTime() == 2 && owner != null) {
            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.ALLAY_DEATH, SoundSource.MASTER, 1.0F, 0.5F);
            owner.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.MASTER, 2.0F, 1.0F);
         }

         if (this.getTime() >= 60) {
            this.discard();
            return;
         }

         this.moveSegments();
         HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, target -> false);
         if (hit.getType() == Type.BLOCK && !ForgeEventFactory.onProjectileImpact(this, hit)) {
            this.onHitBlock((BlockHitResult)hit);
         }

         Vec3 pos = this.position();
         this.hitbox = new AABB(pos.x - 1.5, pos.y, pos.z - 1.5, pos.x + 1.5, pos.y + 1.5, pos.z + 1.5);
         List<Entity> targets = this.level().getEntities(this, this.hitbox, x$0 -> this.canHitEntity(x$0));
         if (owner != null) {
            for (Entity target : targets) {
               if (!this.isSelfOrSegment(target)) {
                  ISorcererData ownerCap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                  if (target == owner && !ownerCap.hasSelfHit()) {
                     return;
                  }

                  if (owner instanceof LivingEntity livingOwner) {
                     ExplosionHandler.spawn(
                        this.level().dimension(),
                        target.position().add(0.0, target.getBbHeight() / 2.0F, 0.0),
                        Math.min(15.0F, 2.0F * Ability.getPower((Ability)JJKAbilities.BODY_REPEL.get(), livingOwner) * (0.05F + 0.05F * this.souls)),
                        20,
                        this.getRealDamage(),
                        livingOwner,
                        JJKDamageSources.indirectJujutsuAttack(this, livingOwner, (Ability)JJKAbilities.BODY_REPEL.get()),
                        false
                     );
                     this.discard();
                     return;
                  }
               }
            }
         }

         this.checkInsideBlocks();
         Vec3 movement = this.getDeltaMovement();
         double d0 = this.getX() + movement.x;
         double d1 = this.getY() + movement.y;
         double d2 = this.getZ() + movement.z;
         this.setPos(d0, d1, d2);
         if (owner == null || !(owner.level() instanceof ServerLevel level)) {
            return;
         }

         level.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), 0, 0.0, 0.0, 0.0, 1.0);
      } else {
         this.discard();
      }
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public boolean fireImmune() {
      return true;
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      double d0 = this.getBoundingBox().getSize() * 10.0;
      if (Double.isNaN(d0)) {
         d0 = 1.0;
      }

      d0 *= 64.0 * getViewScale();
      return pDistance < d0 * d0;
   }

   public float getRealDamage() {
      return this.getOwner() instanceof LivingEntity owner
         ? Ability.getPower((Ability)JJKAbilities.BODY_REPEL.get(), owner) * (0.2F + 0.09F * this.souls / 5.0F)
         : 0.0F;
   }

   protected void onHitBlock(@NotNull BlockHitResult pResult) {
      super.onHitBlock(pResult);
      if (this.getOwner() instanceof LivingEntity livingOwner) {
         Vec3 var4 = pResult.getLocation();
         ExplosionHandler.spawn(
            this.level().dimension(),
            var4,
            Math.min(15.0F, 2.0F * Ability.getPower((Ability)JJKAbilities.BODY_REPEL.get(), livingOwner) * (0.05F + 0.05F * this.souls)),
            20,
            this.getRealDamage(),
            livingOwner,
            JJKDamageSources.indirectJujutsuAttack(this, livingOwner, (Ability)JJKAbilities.BODY_REPEL.get()),
            false
         );
         this.discard();
      }
   }

   @NotNull
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      Entity entity = this.getOwner();
      int i = entity == null ? 0 : entity.getId();
      return new ClientboundAddEntityPacket(
         this.getId(),
         this.getUUID(),
         this.getX(),
         this.getY(),
         this.getZ(),
         this.getXRot(),
         this.getYRot(),
         this.getType(),
         i,
         this.getDeltaMovement(),
         0.0
      );
   }

   public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
      super.recreateFromPacket(pPacket);
      this.moveTo(pPacket.getX(), pPacket.getY(), pPacket.getZ(), pPacket.getYRot(), pPacket.getXRot());
      this.setDeltaMovement(pPacket.getXa(), pPacket.getYa(), pPacket.getZa());
   }
}
