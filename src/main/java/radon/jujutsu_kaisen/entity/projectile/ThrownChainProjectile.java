package radon.jujutsu_kaisen.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ThrownChainProjectile extends AbstractArrow {
   private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(ThrownChainProjectile.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(ThrownChainProjectile.class, EntityDataSerializers.ITEM_STACK);
   private static final EntityDataAccessor<Boolean> DATA_BYPASS = SynchedEntityData.defineId(ThrownChainProjectile.class, EntityDataSerializers.BOOLEAN);
   private static final int DURATION = 10;
   private int flightTime = 0;
   private boolean released;
   private boolean dealtDamage;
   private Entity pulled;

   public ThrownChainProjectile(EntityType<? extends AbstractArrow> pType, Level pLevel) {
      super(pType, pLevel);
      this.noCulling = true;
   }

   public ThrownChainProjectile(LivingEntity pShooter, ItemStack stack) {
      super((EntityType)JJKEntities.THROWN_CHAIN.get(), pShooter, pShooter.level());
      Vec3 spawn = new Vec3(pShooter.getX(), pShooter.getEyeY() - this.getBbHeight() / 2.0F, pShooter.getZ())
         .add(RotationUtil.getTargetAdjustedLookAngle(pShooter));
      this.setPos(spawn.x, spawn.y, spawn.z);
      this.entityData.set(DATA_ITEM, stack);
      if (stack.is((Item)JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())) {
         this.setBypassInfinity(true);
      }
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TIME, 0);
      this.entityData.define(DATA_ITEM, ItemStack.EMPTY);
      this.entityData.define(DATA_BYPASS, false);
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
      pCompound.putBoolean("released", this.released);
      pCompound.putBoolean("dealt_damage", this.dealtDamage);
      pCompound.putBoolean("bypass_infinity", this.getBypassInfinity());
   }

   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setTime(pCompound.getInt("time"));
      this.released = pCompound.getBoolean("released");
      this.dealtDamage = pCompound.getBoolean("dealt_damage");
      this.setBypassInfinity(pCompound.getBoolean("bypass_infinity"));
   }

   public boolean shouldRender(double pX, double pY, double pZ) {
      return true;
   }

   @Nullable
   protected EntityHitResult findHitEntity(@NotNull Vec3 pStartVec, @NotNull Vec3 pEndVec) {
      return this.dealtDamage ? null : super.findHitEntity(pStartVec, pEndVec);
   }

   protected boolean tryPickup(Player pPlayer) {
      return false;
   }

   protected void onHitBlock(@NotNull BlockHitResult pResult) {
      super.onHitBlock(pResult);
      if (!this.dealtDamage) {
         Entity owner = this.getOwner();
         if (owner != null) {
            if (this.getStack().isEmpty()) {
               this.pulled = owner;
               this.dealtDamage = true;
            }
         }
      }
   }

   protected void onHitEntity(@NotNull EntityHitResult pResult) {
      Entity target = pResult.getEntity();
      Entity owner = this.getOwner();
      if (this.getStack().isEmpty()) {
         if (owner != null && target.isPushable()) {
            this.pulled = target;
            this.dealtDamage = true;
            this.setDeltaMovement(Vec3.ZERO);
         }
      } else {
         assert owner != null;

         DamageSource source = JJKDamageSources.indirectJujutsuAttack(this, (LivingEntity)owner, (Ability)JJKAbilities.PUNCH.get());
         this.dealtDamage = true;
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         float DAMAGE = 8.5F * cap.getRealPower();
         if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            DAMAGE = (float)(DAMAGE * 1.3);
         }

         SwordItem sword = (SwordItem)this.getStack().getItem();
         target.hurt(source, sword.getDamage() + DAMAGE);
         this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
      }
   }

   public void onRemovedFromWorld() {
      super.onRemovedFromWorld();
      if (this.getOwner() instanceof LivingEntity owner && !owner.isRemoved() && !owner.isDeadOrDying() && owner.getOffhandItem().isEmpty()) {
         owner.setItemInHand(InteractionHand.OFF_HAND, this.getStack());
      } else {
         this.spawnAtLocation(this.getStack(), 0.1F);
      }
   }

   public void tick() {
      this.setTime(this.getTime() + 1);
      Entity owner = this.getOwner();
      float speedMult = 5.0F;
      if (owner == null) {
         this.discard();
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            speedMult = 10.0F;
         }

         if (this.level().isClientSide || owner != null && !owner.isRemoved() && owner.isAlive() && this.inGroundTime <= 10) {
            if (!(owner instanceof LivingEntity living)) {
               return;
            }

            if (this.released) {
               super.tick();
               this.flightTime++;
               if (this.flightTime > 40) {
                  this.discard();
                  return;
               }

               if (this.inGroundTime > 4) {
                  this.dealtDamage = true;
               }

               if (this.dealtDamage && this.pulled != null) {
                  if (this.pulled == living) {
                     speedMult *= 0.6F;
                     living.setDeltaMovement(this.position().subtract(living.position()).normalize().multiply(speedMult, speedMult, speedMult));
                     if (living.distanceTo(this) <= 1.0) {
                        this.discard();
                        return;
                     }
                  } else {
                     this.setPos(this.pulled.position().add(0.0, this.pulled.getBbHeight() / 2.0F, 0.0));
                     speedMult *= 0.6F;
                     this.pulled.setDeltaMovement(living.position().subtract(this.pulled.position()).normalize().multiply(speedMult, speedMult, speedMult));
                     if (this.pulled.distanceTo(living) <= 1.0) {
                        this.discard();
                        return;
                     }
                  }

                  this.pulled.hurtMarked = true;
               }
            } else {
               double angle = Math.toRadians(this.getTime() * this.getTime());
               double radius = 2.0;
               float yaw = (float)Math.toRadians(living.getYRot());
               float pitch = (float)Math.toRadians(90.0);
               Vec3 offset = new Vec3(Math.cos(angle) * radius, 0.0, Math.sin(angle) * radius).xRot(pitch).yRot(-yaw);
               Vec3 position = living.position().add(RotationUtil.getTargetAdjustedLookAngle(living)).add(offset);
               if (living.isUsingItem()) {
                  this.setPos(position.x, position.y, position.z);
                  this.setRot(
                     (float)Math.toDegrees(Math.atan2(offset.x, offset.z)),
                     -((float)Math.toDegrees(Math.asin(offset.y / offset.length())))
                  );
                  if (this.random.nextInt(5) == 0) {
                     this.playSound(SoundEvents.CHAIN_PLACE);
                  }
               } else {
                  Vec3 spawn = new Vec3(living.getX(), living.getEyeY() - this.getBbHeight() / 2.0F, living.getZ())
                     .add(RotationUtil.getTargetAdjustedLookAngle(living));
                  this.setPos(spawn.x, spawn.y, spawn.z);
                  this.setRot(-RotationUtil.getTargetAdjustedYRot(living), RotationUtil.getTargetAdjustedXRot(living));
                  this.setDeltaMovement(
                     RotationUtil.getTargetAdjustedLookAngle(living)
                        .scale(new Vec3(this.xOld, this.yOld, this.zOld).subtract(position).length() * speedMult)
                  );
                  this.released = true;
                  this.flightTime = 0;
               }
            }
         } else {
            this.discard();
         }
      }
   }

   @NotNull
   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.TRIDENT_HIT_GROUND;
   }

   protected float getWaterInertia() {
      return 0.99F;
   }

   @NotNull
   protected ItemStack getPickupItem() {
      return ItemStack.EMPTY;
   }

   public ItemStack getStack() {
      return (ItemStack)this.entityData.get(DATA_ITEM);
   }

   public boolean getBypassInfinity() {
      return (Boolean)this.entityData.get(DATA_BYPASS);
   }

   public void setBypassInfinity(boolean value) {
      this.entityData.set(DATA_BYPASS, value);
   }
}
