package radon.jujutsu_kaisen.entity.base;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class SummonEntity extends TamableAnimal implements GeoEntity {
   private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(SummonEntity.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   @Nullable
   private UUID ownerUUID;
   @Nullable
   private LivingEntity cachedOwner;

   protected SummonEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public boolean startRiding(Entity vehicle, boolean force) {
      return !(vehicle instanceof Boat) && !(vehicle instanceof AbstractMinecart) ? super.startRiding(vehicle, force) : false;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TIME, 0);
   }

   public boolean canBeLeashed(@Nullable Player player) {
      return false;
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      LivingEntity passenger = this.getControllingPassenger();
      if (passenger != null) {
         this.setSprinting(new Vec3(passenger.xxa, passenger.yya, passenger.zza).lengthSqr() > 0.01);
      } else {
         this.setSprinting(this.getDeltaMovement().lengthSqr() > 0.01 && this.moveControl.getSpeedModifier() > 1.0);
      }
   }

   public int getTime() {
      return (Integer)this.entityData.get(DATA_TIME);
   }

   public void setTime(int time) {
      this.entityData.set(DATA_TIME, time);
   }

   public boolean canAttack(@NotNull LivingEntity pTarget) {
      return (!this.isTame() || pTarget != this.getOwner()) && super.canAttack(pTarget);
   }

   protected void actuallyHurt(@NotNull DamageSource pDamageSource, float pDamageAmount) {
      super.actuallyHurt(pDamageSource, pDamageAmount);
      if (pDamageSource.getEntity() instanceof LivingEntity attacker && this.canAttack(attacker)) {
         this.setTarget(attacker);
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

   public void aiStep() {
      super.aiStep();
      this.updateSwingTime();
   }

   public void onAddedToWorld() {
      super.onAddedToWorld();
      if (this instanceof ISorcerer sorcerer) {
         this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(sorcerer::init);
      }

      if (this instanceof ICommandable commandable && commandable.canChangeTarget() && this.getOwner() instanceof ServerPlayer player) {
         PacketHandler.sendToClient(
            new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.set_target_info", "jujutsu_kaisen")), false), player
         );
      }
   }

   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putBoolean("tame", this.isTame());
      if (this.ownerUUID != null) {
         pCompound.putUUID("owner", this.ownerUUID);
      }

      pCompound.putInt("time", this.getTime());
   }

   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setTame(pCompound.getBoolean("tame"));
      if (pCompound.hasUUID("owner")) {
         this.ownerUUID = pCompound.getUUID("owner");
      }

      this.setTime(pCompound.getInt("time"));
   }

   public void tick() {
      this.setTime(this.getTime() + 1);
      LivingEntity owner = this.getOwner();
      if (this.level().isClientSide || !this.isTame() || owner != null && !owner.isRemoved() && owner.isAlive()) {
         super.tick();
      } else {
         this.discard();
      }
   }

   @Nullable
   public AgeableMob getBreedOffspring(@NotNull ServerLevel pLevel, @NotNull AgeableMob pOtherParent) {
      return null;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public float getCost(LivingEntity owner) {
      return this.getAbility().getCost(owner);
   }

   public abstract Summon<?> getAbility();

   protected boolean shouldToggleOnDeath() {
      return true;
   }

   public void remove(@NotNull RemovalReason pReason) {
      super.remove(pReason);
      if (this.shouldToggleOnDeath()) {
         LivingEntity owner = this.getOwner();
         if (owner != null) {
            Ability ability = this.getAbility();
            if (ability != null && JJKAbilities.hasToggled(owner, ability)) {
               ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               cap.toggle(ability);
            }
         }
      }
   }
}
