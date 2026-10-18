package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.base.IJumpInputListener;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class BirdCurseEntity extends CursedSpirit implements PlayerRideable, IJumpInputListener {
   private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");
   private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
   private boolean jump;

   public BirdCurseEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
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

   protected float getFlyingSpeed() {
      return this.getTarget() != null && !this.isVehicle() ? 0.05F : 0.15F;
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes()
         .add(Attributes.FOLLOW_RANGE, 100.0)
         .add(Attributes.ARMOR, 12.0)
         .add(Attributes.ARMOR_TOUGHNESS, 8.0)
         .add(Attributes.ATTACK_DAMAGE, 20.0)
         .add(Attributes.FLYING_SPEED);
   }

   @NotNull
   @Override
   protected PathNavigation createNavigation(@NotNull Level pLevel) {
      FlyingPathNavigation navigation = new FlyingPathNavigation(this, pLevel);
      navigation.setCanOpenDoors(false);
      navigation.setCanFloat(true);
      navigation.setCanPassDoors(true);
      return navigation;
   }

   @Override
   public float getExperience() {
      return SorcererGrade.GRADE_3.getRequiredExperience() * 0.5F;
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return null;
   }

   private PlayState flyPredicate(AnimationState<BirdCurseEntity> animationState) {
      return this.onGround() && !animationState.isMoving() ? PlayState.STOP : animationState.setAndContinue(FLY);
   }

   private PlayState swingPredicate(AnimationState<BirdCurseEntity> animationState) {
      if (this.swinging) {
         return animationState.setAndContinue(SWING);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Fly", 5, this::flyPredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Swing", this::swingPredicate)});
   }

   @NotNull
   public InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
      if (pPlayer == this.getOwner() && this.isTame() && !this.isVehicle()) {
         if (pPlayer.startRiding(this)) {
            pPlayer.setYRot(this.getYRot());
            pPlayer.setXRot(this.getXRot());
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         return super.mobInteract(pPlayer, pHand);
      }
   }

   public double getPassengersRidingOffset() {
      return this.getBbHeight();
   }

   public boolean shouldRiderSit() {
      return false;
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

   static enum Flight {
      ASCEND,
      SPRINT,
      NORMAL;
   }
}
