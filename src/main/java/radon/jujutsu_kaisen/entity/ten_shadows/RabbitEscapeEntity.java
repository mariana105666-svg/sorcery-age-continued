package radon.jujutsu_kaisen.entity.ten_shadows;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class RabbitEscapeEntity extends TenShadowsSummon {
   private static final int COUNT = 16;
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
   private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
   public static EntityDataAccessor<Boolean> DATA_LEADER = SynchedEntityData.defineId(RabbitEscapeEntity.class, EntityDataSerializers.BOOLEAN);
   @Nullable
   private UUID leaderUUID;
   @Nullable
   private RabbitEscapeEntity cachedLeader;
   private boolean original;

   public RabbitEscapeEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public RabbitEscapeEntity(LivingEntity owner, boolean tame) {
      this((EntityType<? extends TamableAnimal>)JJKEntities.RABBIT_ESCAPE.get(), owner.level());
      this.setOwner(owner);
      this.setTame(tame);
      Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
      Vec3 pos = owner.position().subtract(direction.multiply(this.getBbWidth(), 0.0, this.getBbWidth()));
      this.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      this.setDeltaMovement(
         look.x * (this.random.nextDouble() * 2.0 + 1.0) + this.random.nextDouble() * (this.random.nextBoolean() ? 1 : -1) * 0.5,
         look.y * (this.random.nextDouble() * 4.0 + 2.0) + this.random.nextDouble() * (this.random.nextBoolean() ? 1 : -1) * 0.5,
         look.z * (this.random.nextDouble() * 2.0 + 1.0) + this.random.nextDouble() * (this.random.nextBoolean() ? 1 : -1) * 0.5
      );
      this.yHeadRot = this.getYRot();
      this.yHeadRotO = this.yHeadRot;
   }

   public RabbitEscapeEntity(RabbitEscapeEntity leader) {
      this(leader, leader.isTame());
      this.setLeader(leader);
      this.setOwner(leader.getOwner());
   }

   @Override
   public boolean canAttack(@NotNull LivingEntity pTarget) {
      return this.getLeader() != pTarget && !(pTarget instanceof RabbitEscapeEntity rabbit && rabbit.getLeader() == pTarget) ? super.canAttack(pTarget) : false;
   }

   @Override
   protected void customServerAiStep() {
      if (this.getTarget() != null) {
         double d0 = this.getAttributeValue(Attributes.FOLLOW_RANGE);
         AABB bounds = AABB.unitCubeFromLowerCorner(this.position()).inflate(d0, 10.0, d0);
         this.level()
            .getEntitiesOfClass(RabbitEscapeEntity.class, bounds, EntitySelector.NO_SPECTATORS)
            .stream()
            .filter(entity -> entity != this)
            .filter(entity -> entity.getTarget() == null)
            .filter(entity -> entity == this.getOwner() || this.getOwner() == entity || entity.getOwner() == this)
            .filter(entity -> !entity.isAlliedTo(this.getTarget()))
            .filter(entity -> entity.getLeader() != null && entity.getLeader() == this.getLeader() || this.getLeader() == entity || entity.getLeader() == this)
            .forEach(entity -> entity.setTarget(this.getTarget()));
      } else {
         RabbitEscapeEntity leader = this.getLeader();
         if (leader != null && !leader.isRemoved() && leader.isAlive()) {
            if (this.distanceTo(leader) >= 1.0) {
               this.lookControl.setLookAt(leader, 10.0F, this.getMaxHeadXRot());
               this.navigation.moveTo(leader, 1.0);
            }
         } else if (!this.original) {
            this.discard();
         }
      }

      if (!this.original) {
         RabbitEscapeEntity leaderx = this.getLeader();
         if (leaderx == null || leaderx.isRemoved() || !leaderx.isAlive()) {
            this.discard();
            return;
         }
      }
   }

   public void setLeader(@Nullable RabbitEscapeEntity leader) {
      if (leader != null) {
         this.leaderUUID = leader.getUUID();
         this.cachedLeader = leader;
      }
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_LEADER, false);
   }

   public void setLeader(boolean leader) {
      this.entityData.set(DATA_LEADER, leader);
   }

   public boolean getOriginal() {
      return this.original;
   }

   public boolean directLeader() {
      return (Boolean)this.entityData.get(DATA_LEADER);
   }

   @Nullable
   public RabbitEscapeEntity getLeader() {
      if (this.cachedLeader != null && !this.cachedLeader.isRemoved()) {
         return this.cachedLeader;
      } else if (this.leaderUUID != null && this.level() instanceof ServerLevel) {
         this.cachedLeader = (RabbitEscapeEntity)((ServerLevel)this.level()).getEntity(this.leaderUUID);
         return this.cachedLeader;
      } else {
         return null;
      }
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      if (this.leaderUUID != null) {
         pCompound.putUUID("leader", this.leaderUUID);
      }

      pCompound.putBoolean("original", this.original);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      if (pCompound.hasUUID("leader")) {
         this.leaderUUID = pCompound.getUUID("leader");
      }

      this.original = pCompound.getBoolean("original");
   }

   @Override
   protected boolean shouldDespawn() {
      return this.original && super.shouldDespawn();
   }

   @Override
   public void onAddedToWorld() {
      super.onAddedToWorld();
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         if (this.getLeader() == null && !this.original) {
            this.setLeader(true);
            this.original = true;

            for (int i = 0; i < 16; i++) {
               RabbitEscapeEntity entity = new RabbitEscapeEntity(this);
               entity.setPos(this.position());
               entity.setTame(this.isTame());
               this.level().addFreshEntity(entity);
            }
         }
      }
   }

   private PlayState walkPredicate(AnimationState<RabbitEscapeEntity> animationState) {
      return animationState.isMoving() ? animationState.setAndContinue(WALK) : PlayState.STOP;
   }

   private PlayState swingPredicate(AnimationState<RabbitEscapeEntity> animationState) {
      if (this.swinging) {
         return animationState.setAndContinue(SWING);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Walk", this::walkPredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Swing", this::swingPredicate)});
   }

   @Override
   protected boolean isCustom() {
      return false;
   }

   @Override
   protected boolean canFly() {
      return false;
   }

   @Override
   public boolean hasMeleeAttack() {
      return true;
   }

   @Override
   public boolean hasArms() {
      return false;
   }

   @Override
   public boolean canJump() {
      return true;
   }

   @Override
   public boolean canChant() {
      return false;
   }

   protected void doPush(@NotNull Entity p_20971_) {
   }

   @Override
   public Summon<?> getAbility() {
      return (Summon<?>)JJKAbilities.RABBIT_ESCAPE.get();
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.66).add(Attributes.MAX_HEALTH, 0.01).add(Attributes.ATTACK_DAMAGE, 4.8);
   }
}
