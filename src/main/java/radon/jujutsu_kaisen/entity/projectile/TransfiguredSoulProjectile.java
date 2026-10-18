package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;
import radon.jujutsu_kaisen.util.SorcererUtil;

public class TransfiguredSoulProjectile extends Projectile {
   private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(TransfiguredSoulProjectile.class, EntityDataSerializers.INT);
   private static final double SPEED = 10.0;
   private static final float DAMAGE = 17.0F;
   private static final int DURATION = 100;

   public TransfiguredSoulProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public TransfiguredSoulProjectile(LivingEntity pShooter) {
      super((EntityType)JJKEntities.TRANSFIGURED_SOUL.get(), pShooter.level());
      this.setOwner(pShooter);
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(pShooter);
      EntityUtil.offset(this, look, new Vec3(pShooter.getX(), pShooter.getEyeY() - this.getBbHeight() / 2.0F, pShooter.getZ()).add(look));
      this.setDeltaMovement(look.scale(10.0));
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_TIME, 0);
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
   }

   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setTime(pCompound.getInt("time"));
   }

   public boolean ignoreExplosion() {
      return true;
   }

   public boolean isPushedByFluid(FluidType type) {
      return false;
   }

   public void tick() {
      this.setTime(this.getTime() + 1);
      Entity owner = this.getOwner();
      if (this.level().isClientSide || owner != null && !owner.isRemoved() && owner.isAlive()) {
         super.tick();
         if (this.getTime() >= 100) {
            this.discard();
            return;
         }

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

      d0 *= 64.0 * getViewScale();
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

   protected void onHitEntity(@NotNull EntityHitResult pResult) {
      super.onHitEntity(pResult);
      Entity entity = pResult.getEntity();
      if (this.getOwner() instanceof LivingEntity owner) {
         if (entity != owner) {
            ISorcererData ownerCap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            entity.hurt(this.damageSources().thrown(this, owner), 17.0F * SorcererUtil.getPower(ownerCap.getExperience()));
         }
      }
   }
}
