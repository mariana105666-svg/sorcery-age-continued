package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.ai.goal.BetterFollowOwnerGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class DivineDogEntity extends TenShadowsSummon implements PlayerRideable {
   private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(DivineDogEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_LEAP = SynchedEntityData.defineId(DivineDogEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_RITUAL = SynchedEntityData.defineId(DivineDogEntity.class, EntityDataSerializers.INT);
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
   private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
   private static final RawAnimation LEAP = RawAnimation.begin().thenPlay("attack.leap");
   private static final RawAnimation HOWL = RawAnimation.begin().thenPlayAndHold("misc.howl");
   private static final int LEAP_DURATION = 10;

   public DivineDogEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public DivineDogEntity(EntityType<? extends TamableAnimal> type, LivingEntity owner, boolean ritual, DivineDogEntity.Variant variant) {
      super(type, owner.level());
      this.setTame(true);
      this.setOwner(owner);
      Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
      Vec3 pos = ritual
         ? owner.position()
         : owner.position()
            .subtract(direction.multiply(this.getBbWidth(), 0.0, this.getBbWidth()))
            .add(direction.yRot(90.0F).scale(this.getVariant() == DivineDogEntity.Variant.WHITE ? -0.45 : 0.45));
      this.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());
      this.yHeadRot = this.getYRot();
      this.yHeadRotO = this.yHeadRot;
      this.setVariant(variant);
      this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
      this.moveControl = new MoveControl(this);
   }

   public boolean isInvulnerable() {
      return (Integer)this.entityData.get(DATA_RITUAL) > 0 || super.isInvulnerable();
   }

   @Override
   protected boolean isCustom() {
      return true;
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
      return false;
   }

   @Override
   public boolean canChant() {
      return false;
   }

   protected void setVariant(DivineDogEntity.Variant variant) {
      this.entityData.set(DATA_VARIANT, variant.ordinal());
   }

   public void setRitual(int duration) {
      this.entityData.set(DATA_RITUAL, duration);
   }

   @NotNull
   public InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
      if (pPlayer == this.getOwner() && !this.isVehicle()) {
         if (pPlayer.startRiding(this)) {
            pPlayer.setYRot(this.getYRot());
            pPlayer.setXRot(this.getXRot());
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         return super.mobInteract(pPlayer, pHand);
      }
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      return this.getFirstPassenger() instanceof LivingEntity living ? living : null;
   }

   private Vec2 getRiddenRotation(LivingEntity pEntity) {
      return new Vec2(pEntity.getXRot() * 0.5F, pEntity.getYRot());
   }

   protected float getRiddenSpeed(@NotNull Player pPlayer) {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.0F;
   }

   protected void tickRidden(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
      super.tickRidden(pPlayer, pTravelVector);
      Vec2 vec2 = this.getRiddenRotation(pPlayer);
      this.setRot(vec2.y, vec2.x);
      this.yRotO = this.yBodyRot = this.yHeadRot = this.yHeadRotO = this.getYRot();
   }

   @NotNull
   protected Vec3 getRiddenInput(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
      float f = pPlayer.xxa * 0.5F;
      float f1 = pPlayer.zza;
      if (f1 <= 0.0F) {
         f1 *= 0.25F;
      }

      return new Vec3(f, 0.0, f1);
   }

   public float getStepHeight() {
      return 1.0F;
   }

   @Override
   protected boolean shouldToggleOnDeath() {
      return false;
   }

   protected void registerGoals() {
      int goal = 1;
      int target = 1;
      this.goalSelector.addGoal(goal++, new FloatGoal(this));
      if (this.hasMeleeAttack()) {
         this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 1.0, true));
      }

      this.goalSelector.addGoal(goal++, new DivineDogEntity.CustomLeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(goal++, new SorcererGoal(this));
      this.targetSelector.addGoal(target++, new HurtByTargetGoal(this, new Class[0]));
      this.goalSelector.addGoal(goal++, new BetterFollowOwnerGoal(this, 1.0, 25.0F, 10.0F, this.canFly()));
      this.targetSelector.addGoal(target++, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(target, new OwnerHurtTargetGoal(this));
      this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));
   }

   @NotNull
   protected PathNavigation createNavigation(@NotNull Level pLevel) {
      LivingEntity target = this.getTarget();
      GroundPathNavigation navigation = new GroundPathNavigation(this, pLevel);
      navigation.setCanOpenDoors(false);
      navigation.setCanFloat(false);
      navigation.setCanPassDoors(true);
      return navigation;
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes()
         .add(Attributes.MOVEMENT_SPEED, 0.42900000000000005)
         .add(Attributes.MAX_HEALTH, 36.0)
         .add(Attributes.ARMOR, 14.0)
         .add(Attributes.ATTACK_DAMAGE, 21.0);
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_VARIANT, DivineDogEntity.Variant.WHITE.ordinal());
      this.entityData.define(DATA_LEAP, 0);
      this.entityData.define(DATA_RITUAL, 0);
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("variant", (Integer)this.entityData.get(DATA_VARIANT));
      pCompound.putInt("ritual", (Integer)this.entityData.get(DATA_RITUAL));
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.entityData.set(DATA_VARIANT, pCompound.getInt("variant"));
      this.entityData.set(DATA_RITUAL, pCompound.getInt("ritual"));
   }

   private PlayState walkRunIdlePredicate(AnimationState<DivineDogEntity> animationState) {
      return animationState.isMoving() ? animationState.setAndContinue(this.isSprinting() ? RUN : WALK) : animationState.setAndContinue(IDLE);
   }

   private PlayState leapPredicate(AnimationState<DivineDogEntity> animationState) {
      if ((Integer)this.entityData.get(DATA_LEAP) > 0) {
         return animationState.setAndContinue(LEAP);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   private PlayState howlPredicate(AnimationState<DivineDogEntity> animationState) {
      if ((Integer)this.entityData.get(DATA_RITUAL) > 0) {
         return animationState.setAndContinue(HOWL);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Walk/Run/Idle", this::walkRunIdlePredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Leap", this::leapPredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Howl", this::howlPredicate)});
   }

   public DivineDogEntity.Variant getVariant() {
      return DivineDogEntity.Variant.values()[this.entityData.get(DATA_VARIANT)];
   }

   @Override
   public Summon<?> getAbility() {
      return (Summon<?>)JJKAbilities.DIVINE_DOGS.get();
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      LivingEntity target = this.getTarget();
      if (target != null) {
         this.moveControl.setWantedPosition(target.getX(), target.getY(), target.getZ(), 0.9F);
      }

      int leap = (Integer)this.entityData.get(DATA_LEAP);
      if (leap > 0) {
         this.entityData.set(DATA_LEAP, --leap);
      }
   }

   @Override
   public void tick() {
      int ritual = (Integer)this.entityData.get(DATA_RITUAL);
      if (ritual > 0) {
         this.entityData.set(DATA_RITUAL, --ritual);
         if (ritual == 0) {
            this.discard();
         }
      } else {
         super.tick();
      }
   }

   private class CustomLeapAtTargetGoal extends LeapAtTargetGoal {
      public CustomLeapAtTargetGoal(Mob pMob, float pYd) {
         super(pMob, pYd);
      }

      public void start() {
         super.start();
         DivineDogEntity.this.entityData.set(DivineDogEntity.DATA_LEAP, 10);
      }
   }

   public static enum Variant {
      WHITE,
      BLACK;
   }
}
