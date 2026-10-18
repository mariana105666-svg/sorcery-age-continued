package radon.jujutsu_kaisen.entity;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

public class HollowWickerBasketEntity extends Entity {
   private static final float STRENGTH = 100.0F;
   private static final double X_STEP = 0.025;
   public static final float RADIUS = 1.25F;
   private static final float MAX_RADIUS = 4.0F;
   private static final float DAMAGE = 3.0F;
   private boolean invuln = false;
   private boolean domainInvuln = false;
   @Nullable
   private UUID ownerUUID;
   @Nullable
   private LivingEntity cachedOwner;

   protected HollowWickerBasketEntity(EntityType<?> pType, Level pLevel) {
      super(pType, pLevel);
      this.noPhysics = true;
      this.setNoGravity(true);
      this.setDeltaMovement(Vec3.ZERO);
   }

   public HollowWickerBasketEntity(LivingEntity owner) {
      super((EntityType)JJKEntities.HOLLOW_WICKER_BASKET.get(), owner.level());
      this.setOwner(owner);
      this.setPos(owner.position());
   }

   public boolean fireImmune() {
      return true;
   }

   @NotNull
   public EntityDimensions getDimensions(@NotNull Pose pPose) {
      float radius = 1.25F;
      return EntityDimensions.fixed(radius, radius);
   }

   @NotNull
   public Vec3 getDeltaMovement() {
      return Vec3.ZERO;
   }

   public boolean hurt(@NotNull DamageSource pSource, float pAmount, boolean isDomainAttack) {
      if (!isDomainAttack && this.invuln) {
         return false;
      } else if (isDomainAttack && this.domainInvuln) {
         return false;
      } else if (pSource.getEntity() instanceof LivingEntity attacker && attacker == this.getOwner()) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)this.getOwner().getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (isDomainAttack) {
            this.domainInvuln = true;
            cap.delayTickEvent(() -> this.domainInvuln = false, 10);
         }

         if (!isDomainAttack) {
            this.invuln = true;
            cap.delayTickEvent(() -> this.invuln = false, 5);
         }

         return true;
      }
   }

   public void tick() {
      this.refreshDimensions();
      LivingEntity owner = this.getOwner();
      if (this.level().isClientSide
         || owner != null && !owner.isRemoved() && owner.isAlive() && JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.HOLLOW_WICKER_BASKET.get())) {
         if (owner != null) {
            super.tick();
            this.setPos(owner.position());
         }
      } else {
         this.discard();
      }
   }

   public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
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

   public boolean isInWall() {
      return false;
   }

   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (this.ownerUUID != null) {
         pCompound.putUUID("owner", this.ownerUUID);
      }
   }

   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (pCompound.hasUUID("owner")) {
         this.ownerUUID = pCompound.getUUID("owner");
      }
   }

   @NotNull
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      Entity entity = this.getOwner();
      return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
   }

   public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
      super.recreateFromPacket(pPacket);
      LivingEntity owner = (LivingEntity)this.level().getEntity(pPacket.getData());
      if (owner != null) {
         this.setOwner(owner);
      }
   }

   protected void defineSynchedData() {
   }
}
