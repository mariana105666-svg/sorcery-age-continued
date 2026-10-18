package radon.jujutsu_kaisen.entity.curse;

import java.util.List;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.base.IRightClickInputListener;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class DinoCurseEntity extends CursedSpirit implements PlayerRideable, IRightClickInputListener {
   private static final double EXPLOSION_FALL_DISTANCE = 10.0;
   private static final int EXPLOSION_DURATION = 20;
   private static final float EXPLOSION_POWER = 10.0F;
   private static final EntityDataAccessor<Boolean> DATA_SHOOTING = SynchedEntityData.defineId(DinoCurseEntity.class, EntityDataSerializers.BOOLEAN);
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
   private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
   private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
   private static final RawAnimation SHOOT = RawAnimation.begin().thenLoop("attack.shoot");
   private int riding;

   public DinoCurseEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   @Override
   protected boolean isCustom() {
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

   @Override
   public float getExperience() {
      return SorcererGrade.SPECIAL_GRADE.getRequiredExperience();
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return null;
   }

   public double getPassengersRidingOffset() {
      return this.getBbHeight();
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_SHOOTING, false);
   }

   private boolean isShooting() {
      return (Boolean)this.entityData.get(DATA_SHOOTING);
   }

   private void setShooting(boolean shooting) {
      this.entityData.set(DATA_SHOOTING, shooting);
   }

   public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
      boolean result = super.causeFallDamage(pFallDistance, pMultiplier, pSource);
      if (result && pFallDistance >= 10.0) {
         ExplosionHandler.spawn(this.level().dimension(), this.position(), 10.0F, 20, this, this.damageSources().mobAttack(this), false);
      }

      return result;
   }

   @NotNull
   public InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
      if (pPlayer == this.getOwner() && this.isTame() && !this.isVehicle()) {
         this.riding = this.tickCount;
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

   public static Builder createAttributes() {
      return SorcererEntity.createMobAttributes()
         .add(Attributes.FOLLOW_RANGE, 100.0)
         .add(Attributes.ARMOR_TOUGHNESS, 6.0)
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.ARMOR, 8.0);
   }

   private Vec2 getRiddenRotation(LivingEntity pEntity) {
      return new Vec2(pEntity.getXRot() * 0.5F, pEntity.getYRot());
   }

   protected float getRiddenSpeed(@NotNull Player pPlayer) {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.5F;
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
      return 2.0F;
   }

   private PlayState walkRunIdlePredicate(AnimationState<DinoCurseEntity> animationState) {
      return animationState.isMoving() ? animationState.setAndContinue(this.isSprinting() ? RUN : WALK) : animationState.setAndContinue(IDLE);
   }

   private PlayState swingPredicate(AnimationState<DinoCurseEntity> animationState) {
      if (this.swinging) {
         return animationState.setAndContinue(SWING);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   private PlayState shootPredicate(AnimationState<DinoCurseEntity> animationState) {
      return this.isShooting() ? animationState.setAndContinue(SHOOT) : PlayState.STOP;
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Walk/Run/Idle", this::walkRunIdlePredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Swing", this::swingPredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Shoot", this::shootPredicate)});
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      this.setShooting(JJKAbilities.isChanneling(this, (Ability)JJKAbilities.BLUE_FIRE.get()));
   }

   @NotNull
   @Override
   public List<Ability> getCustom() {
      return List.of((Ability)JJKAbilities.BLUE_FIRE.get());
   }

   @Override
   public void setDown(boolean down) {
      if (!this.level().isClientSide) {
         if (this.tickCount - this.riding >= 20) {
            boolean channelling = JJKAbilities.isChanneling(this, (Ability)JJKAbilities.BLUE_FIRE.get());
            if (down) {
               if (!channelling) {
                  AbilityHandler.trigger(this, (Ability)JJKAbilities.BLUE_FIRE.get());
               }
            } else if (channelling) {
               AbilityHandler.untrigger(this, (Ability)JJKAbilities.BLUE_FIRE.get());
            }
         }
      }
   }
}
