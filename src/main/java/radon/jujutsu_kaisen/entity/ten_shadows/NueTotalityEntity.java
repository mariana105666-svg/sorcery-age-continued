package radon.jujutsu_kaisen.entity.ten_shadows;

import java.util.List;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.IJumpInputListener;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class NueTotalityEntity extends TenShadowsSummon implements PlayerRideable, IJumpInputListener {
   private static final EntityDataAccessor<Integer> DATA_FLIGHT = SynchedEntityData.defineId(NueTotalityEntity.class, EntityDataSerializers.INT);
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
   private static final RawAnimation FLY_1 = RawAnimation.begin().thenLoop("move.fly_1");
   private static final RawAnimation FLY_2 = RawAnimation.begin().thenLoop("move.fly_2");
   private static final RawAnimation FLY_3 = RawAnimation.begin().thenLoop("move.fly_3");
   private static final RawAnimation SWING = RawAnimation.begin().thenLoop("attack.swing");
   private static final RawAnimation FLIGHT_FEET = RawAnimation.begin().thenLoop("misc.flight_feet");
   private static final RawAnimation GRAB_FEET = RawAnimation.begin().thenLoop("misc.grab_feet");
   private boolean jump;

   public NueTotalityEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public NueTotalityEntity(LivingEntity owner) {
      this((EntityType<? extends TamableAnimal>)JJKEntities.NUE_TOTALITY.get(), owner.level());
      this.setTame(true);
      this.setOwner(owner);
      Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
      Vec3 pos = owner.position().subtract(direction.multiply(this.getBbWidth(), 0.0, this.getBbWidth()));
      this.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());
      this.yHeadRot = this.getYRot();
      this.yHeadRotO = this.yHeadRot;
      this.moveControl = new FlyingMoveControl(this, 20, true);
   }

   @Override
   protected boolean isCustom() {
      return false;
   }

   @Override
   protected boolean canFly() {
      return true;
   }

   @Override
   public boolean canChant() {
      return false;
   }

   @Override
   public boolean hasMeleeAttack() {
      return false;
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
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLIGHT, 0);
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      LivingEntity passenger = this.getControllingPassenger();
      Vec3 movement = passenger == null ? this.getDeltaMovement() : new Vec3(passenger.xxa, 0.0, passenger.zza);
      if (this.jump) {
         this.setFlight(NueEntity.Flight.ASCEND);
      } else if (movement.length() > 1.0) {
         this.setFlight(NueEntity.Flight.SPRINT);
      } else {
         this.setFlight(NueEntity.Flight.NORMAL);
      }

      LivingEntity target = this.getTarget();
      if (target != null && !target.isRemoved() && target.isAlive()) {
         if (this.getY() >= target.getY() + this.getBbHeight() * 1.4F
            && Math.sqrt(this.distanceToSqr(target.getX(), this.getY(), target.getZ())) <= 5.0) {
            if (this.random.nextInt(5) != 0) {
               return;
            }

            if (AbilityHandler.trigger(this, (Ability)JJKAbilities.NUE_TOTALITY_LIGHTNING.get()) == Ability.Status.SUCCESS) {
               this.swing(InteractionHand.MAIN_HAND);
            }
         } else if (!this.isVehicle()) {
            this.moveControl.setWantedPosition(target.getX(), target.getY() + this.getBbHeight() * 1.4F, target.getZ(), this.getFlyingSpeed());
         }
      }
   }

   public boolean doHurtTarget(@NotNull Entity pEntity) {
      if (super.doHurtTarget(pEntity)) {
         if (pEntity instanceof LivingEntity living) {
            Ability lightning = (Ability)JJKAbilities.NUE_LIGHTNING.get();
            ((Ability.ITenShadowsAttack)lightning).perform(this, living);
         }

         return true;
      } else {
         return false;
      }
   }

   protected float getFlyingSpeed() {
      return this.getTarget() != null && !this.isVehicle() ? 0.65F : 0.15F;
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes().add(Attributes.FLYING_SPEED).add(Attributes.MAX_HEALTH, 22.5);
   }

   @NotNull
   protected PathNavigation createNavigation(@NotNull Level pLevel) {
      FlyingPathNavigation navigation = new FlyingPathNavigation(this, pLevel);
      navigation.setCanOpenDoors(false);
      navigation.setCanFloat(true);
      navigation.setCanPassDoors(true);
      return navigation;
   }

   private NueEntity.Flight getFlight() {
      return NueEntity.Flight.values()[this.entityData.get(DATA_FLIGHT)];
   }

   private void setFlight(NueEntity.Flight flight) {
      this.entityData.set(DATA_FLIGHT, flight.ordinal());
   }

   private PlayState feetPredicate(AnimationState<NueTotalityEntity> animationState) {
      return this.isVehicle() ? animationState.setAndContinue(GRAB_FEET) : animationState.setAndContinue(FLIGHT_FEET);
   }

   private PlayState swingPredicate(AnimationState<NueTotalityEntity> animationState) {
      if (this.swinging) {
         return animationState.setAndContinue(SWING);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   private PlayState flyIdlePredicate(AnimationState<NueTotalityEntity> animationState) {
      if (animationState.isMoving()) {
         return switch (this.getFlight()) {
            case ASCEND -> animationState.setAndContinue(FLY_1);
            case SPRINT -> animationState.setAndContinue(FLY_2);
            default -> animationState.setAndContinue(FLY_3);
         };
      } else {
         return animationState.setAndContinue(IDLE);
      }
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Fly/Idle", this::flyIdlePredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Swing", this::swingPredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Feet", this::feetPredicate)});
   }

   @Override
   public Summon<?> getAbility() {
      return (Summon<?>)JJKAbilities.NUE_TOTALITY.get();
   }

   @NotNull
   @Override
   public List<Ability> getCustom() {
      return List.of((Ability)JJKAbilities.NUE_TOTALITY_LIGHTNING.get());
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

   public boolean shouldRiderSit() {
      return false;
   }

   public double getPassengersRidingOffset() {
      LivingEntity passenger = this.getControllingPassenger();
      return passenger == null ? super.getPassengersRidingOffset() : -passenger.getBbHeight() + 0.8F;
   }

   @NotNull
   @Override
   public EntityDimensions getDimensions(@NotNull Pose pPose) {
      EntityDimensions dimensions = super.getDimensions(pPose);
      LivingEntity passenger = this.getControllingPassenger();
      return passenger != null ? new EntityDimensions(dimensions.width, dimensions.height + passenger.getBbHeight(), dimensions.fixed) : dimensions;
   }

   @NotNull
   protected AABB makeBoundingBox() {
      AABB bounds = super.makeBoundingBox();
      LivingEntity passenger = this.getControllingPassenger();
      return passenger != null
         ? bounds.setMinY(bounds.minY - passenger.getBbHeight() / 2.0F - 0.4).setMaxY(bounds.maxY - passenger.getBbHeight() + 0.4)
         : bounds;
   }

   @NotNull
   protected Vec3 getRiddenInput(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
      if (this.onGround()) {
         return Vec3.ZERO;
      } else {
         float f = pPlayer.xxa * 0.5F;
         float f1 = pPlayer.zza;
         if (f1 <= 0.0F) {
            f1 *= 0.25F;
         }

         return new Vec3(f, 0.0, f1);
      }
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      return this.getFirstPassenger() instanceof LivingEntity living ? living : null;
   }

   private Vec2 getRiddenRotation(LivingEntity pEntity) {
      return new Vec2(pEntity.getXRot() * 0.5F, pEntity.getYRot());
   }

   public boolean isNoGravity() {
      return !this.isVehicle() && super.isNoGravity();
   }

   protected void tickRidden(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector) {
      super.tickRidden(pPlayer, pTravelVector);
      Vec2 vec2 = this.getRiddenRotation(pPlayer);
      this.setRot(vec2.y, vec2.x);
      this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
      Vec3 movement = this.getDeltaMovement();
      if (this.jump) {
         this.setDeltaMovement(movement.add(0.0, this.getFlyingSpeed(), 0.0));
      }
   }

   @Override
   public void setJump(boolean jump) {
      this.jump = jump;
   }

   @Override
   public void tick() {
      super.tick();
      this.refreshDimensions();
   }
}
