package radon.jujutsu_kaisen.entity.ten_shadows;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.sound.JJKSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WheelEntity extends Entity implements GeoEntity {
   private static final EntityDataAccessor<Integer> DATA_SPIN = SynchedEntityData.defineId(WheelEntity.class, EntityDataSerializers.INT);
   private static final RawAnimation SPIN = RawAnimation.begin().thenPlay("misc.spin");
   private static final int SPIN_DURATION = 20;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int ownerPID;
   @Nullable
   private UUID ownerUUID;
   @Nullable
   private LivingEntity cachedOwner;

   public WheelEntity(EntityType<?> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public WheelEntity(LivingEntity owner) {
      this((EntityType<?>)JJKEntities.WHEEL.get(), owner.level());
      this.setOwner(owner);
      this.setPos(owner.position());
      this.startRiding(owner);
   }

   public boolean shouldRender(double x, double y, double z) {
      return true;
   }

   public double getMyRidingOffset() {
      return 0.5;
   }

   public void spin() {
      this.entityData.set(DATA_SPIN, 20);
      this.playSound((SoundEvent)JJKSounds.WHEEL.get(), 6.0F, 1.0F);
      if (this.level().isClientSide) {
         this.triggerAnim("Spin", "misc.spin");
      }
   }

   public void setOwner(@Nullable LivingEntity pOwner) {
      if (pOwner != null) {
         this.ownerUUID = pOwner.getUUID();
         this.cachedOwner = pOwner;
         this.ownerPID = pOwner.getId();
      }
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
         return this.cachedOwner;
      } else if (this.ownerUUID != null && this.level().getEntity(this.ownerPID) instanceof LivingEntity living) {
         this.cachedOwner = living;
         return living;
      } else {
         return null;
      }
   }

   public void tick() {
      LivingEntity owner = this.getOwner();
      if (!this.level().isClientSide && owner != null) {
         ITenShadowsData cap = (ITenShadowsData)owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
         if (!JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.WHEEL.get())) {
            this.discard();
            return;
         }
      }

      if (this.level().isClientSide || owner != null && !owner.isRemoved() && owner.isAlive() && this.isPassenger()) {
         super.tick();
         if (!this.level().isClientSide) {
            int spin = (Integer)this.entityData.get(DATA_SPIN);
            if (spin > 0) {
               this.entityData.set(DATA_SPIN, --spin);
            }
         }
      } else {
         this.discard();
      }
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_SPIN, 0);
   }

   protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (pCompound.hasUUID("owner")) {
         this.ownerUUID = pCompound.getUUID("owner");
      }

      this.entityData.set(DATA_SPIN, pCompound.getInt("spin"));
   }

   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (this.ownerUUID != null) {
         pCompound.putUUID("owner", this.ownerUUID);
      }

      pCompound.putInt("spin", (Integer)this.entityData.get(DATA_SPIN));
   }

   public void remove(@NotNull RemovalReason pReason) {
      super.remove(pReason);
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         if (owner != null && JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.WHEEL.get())) {
            ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            cap.toggle((Ability)JJKAbilities.WHEEL.get());
         }
      }
   }

   private PlayState spinPredicate(AnimationState<WheelEntity> animationState) {
      int spin = (Integer)this.entityData.get(DATA_SPIN);
      if (spin > 0) {
         return animationState.setAndContinue(SPIN);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Spin", this::spinPredicate)});
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
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
}
