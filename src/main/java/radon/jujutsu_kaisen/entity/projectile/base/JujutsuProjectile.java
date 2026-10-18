package radon.jujutsu_kaisen.entity.projectile.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class JujutsuProjectile extends Projectile {
   private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(JujutsuProjectile.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_POWER = SynchedEntityData.defineId(JujutsuProjectile.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> DATA_DOMAIN = SynchedEntityData.defineId(JujutsuProjectile.class, EntityDataSerializers.BOOLEAN);

   public JujutsuProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public JujutsuProjectile(EntityType<? extends Projectile> pType, Level pLevel, Entity pShooter) {
      super(pType, pLevel);
      this.setOwner(pShooter);
   }

   public JujutsuProjectile(EntityType<? extends Projectile> pType, Level pLevel, Entity pShooter, float power) {
      this(pType, pLevel, pShooter);
      this.setPower(power);
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_TIME, 0);
      this.entityData.define(DATA_POWER, 0.0F);
      this.entityData.define(DATA_DOMAIN, false);
   }

   public int getTime() {
      return (Integer)this.entityData.get(DATA_TIME);
   }

   public void setTime(int time) {
      this.entityData.set(DATA_TIME, time);
   }

   protected void setPower(float power) {
      this.entityData.set(DATA_POWER, power);
   }

   public float getPower() {
      return (Float)this.entityData.get(DATA_POWER);
   }

   public boolean isDomain() {
      return (Boolean)this.entityData.get(DATA_DOMAIN);
   }

   public void setDomain(boolean domain) {
      this.entityData.set(DATA_DOMAIN, domain);
   }

   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("time", this.getTime());
      pCompound.putFloat("power", this.getPower());
      pCompound.putBoolean("domain", this.isDomain());
   }

   protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setTime(pCompound.getInt("time"));
      this.setPower(pCompound.getFloat("power"));
      this.setDomain(pCompound.getBoolean("domain"));
   }

   public boolean ignoreExplosion() {
      return true;
   }

   public boolean isPushedByFluid(FluidType type) {
      return false;
   }

   protected boolean isProjectile() {
      return true;
   }

   public boolean canDeflect() {
      return true;
   }

   public void tick() {
      this.setTime(this.getTime() + 1);
      Entity owner = this.getOwner();
      if (this.level().isClientSide || owner != null && !owner.isRemoved() && owner.isAlive()) {
         super.tick();
         if (this.isProjectile()) {
            HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, x$0 -> this.canHitEntity(x$0));
            if (hit.getType() != Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hit)) {
               this.onHit(hit);
            }

            this.checkInsideBlocks();
            Vec3 movement = this.getDeltaMovement();
            double d0 = this.getX() + movement.x;
            double d1 = this.getY() + movement.y;
            double d2 = this.getZ() + movement.z;
            this.setPos(d0, d1, d2);
         }
      } else {
         this.discard();
      }
   }

   public boolean fireImmune() {
      return true;
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      double d0 = this.getBoundingBox().getSize() * 10.0;
      if (Double.isNaN(d0)) {
         d0 = 1.0;
      }

      d0 *= 302.0 * getViewScale();
      return pDistance < d0 * d0;
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
