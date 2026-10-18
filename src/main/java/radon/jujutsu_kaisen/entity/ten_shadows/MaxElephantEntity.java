package radon.jujutsu_kaisen.entity.ten_shadows;

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
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
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
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.IRightClickInputListener;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.RotationUtil;
import radon.jujutsu_kaisen.util.SorcererUtil;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class MaxElephantEntity extends TenShadowsSummon implements PlayerRideable, IRightClickInputListener {
   private static final double EXPLOSION_FALL_DISTANCE = 7.0;
   private static final int EXPLOSION_DURATION = 20;
   private static final float EXPLOSION_POWER = 15.0F;
   private static final float DAMAGE = 5.0F;
   private static final EntityDataAccessor<Boolean> DATA_SHOOTING = SynchedEntityData.defineId(MaxElephantEntity.class, EntityDataSerializers.BOOLEAN);
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
   private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
   private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
   private static final RawAnimation SHOOT = RawAnimation.begin().thenLoop("attack.shoot");
   private int riding;
   private float power;

   public MaxElephantEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
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
   public boolean canChant() {
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

   public MaxElephantEntity(LivingEntity owner, boolean tame) {
      this((EntityType<? extends TamableAnimal>)JJKEntities.MAX_ELEPHANT.get(), owner.level());
      this.setTame(tame);
      this.setOwner(owner);
      Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
      Vec3 pos = owner.position().subtract(direction.multiply(this.getBbWidth(), 0.0, this.getBbWidth()));
      this.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());
      this.yHeadRot = this.getYRot();
      this.yHeadRotO = this.yHeadRot;
      this.moveControl = new MoveControl(this);
   }

   public double getPassengersRidingOffset() {
      return this.getBbHeight();
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_SHOOTING, false);
   }

   public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
      boolean result = super.causeFallDamage(pFallDistance, pMultiplier, pSource);
      if (result && pFallDistance >= 7.0) {
         LivingEntity owner = this.getOwner();
         if (owner != null) {
            ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
            float power = SorcererUtil.getPower(cap.getExperience());
            ExplosionHandler.spawn(
               this.level().dimension(),
               this.position(),
               15.0F,
               20,
               power * 5.0F * 0.25F,
               this,
               JJKDamageSources.jujutsuAttack(this.getOwner(), (Ability)JJKAbilities.EXPLODE.get()),
               false
            );
         }
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

   private Vec2 getRiddenRotation(LivingEntity pEntity) {
      return new Vec2(pEntity.getXRot() * 0.5F, pEntity.getYRot());
   }

   protected float getRiddenSpeed(@NotNull Player pPlayer) {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.25F;
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

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes()
         .add(Attributes.MOVEMENT_SPEED, 0.33)
         .add(Attributes.MAX_HEALTH, 45.0)
         .add(Attributes.ARMOR, 14.0)
         .add(Attributes.ATTACK_DAMAGE, 21.0);
   }

   private PlayState walkRunIdlePredicate(AnimationState<MaxElephantEntity> animationState) {
      return animationState.isMoving() ? animationState.setAndContinue(this.isSprinting() ? RUN : WALK) : animationState.setAndContinue(IDLE);
   }

   private PlayState swingPredicate(AnimationState<MaxElephantEntity> animationState) {
      if (this.swinging) {
         return animationState.setAndContinue(SWING);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   private PlayState shootPredicate(AnimationState<MaxElephantEntity> animationState) {
      return this.entityData.get(DATA_SHOOTING) ? animationState.setAndContinue(SHOOT) : PlayState.STOP;
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Walk/Run/Idle", this::walkRunIdlePredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Swing", this::swingPredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Shoot", this::shootPredicate)});
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

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      this.entityData.set(DATA_SHOOTING, JJKAbilities.isChanneling(this, (Ability)JJKAbilities.WATER.get()));
      LivingEntity target = this.getTarget();
      if (target != null) {
         this.moveControl.setWantedPosition(target.getX(), target.getY(), target.getZ(), 0.75);
      }
   }

   @NotNull
   @Override
   public List<Ability> getCustom() {
      return List.of((Ability)JJKAbilities.WATER.get());
   }

   @Override
   public Summon<?> getAbility() {
      return (Summon<?>)JJKAbilities.MAX_ELEPHANT.get();
   }

   @Override
   public void setDown(boolean down) {
      if (!this.level().isClientSide) {
         if (this.tickCount - this.riding >= 20) {
            boolean channelling = JJKAbilities.isChanneling(this, (Ability)JJKAbilities.WATER.get());
            if (down) {
               if (!channelling) {
                  AbilityHandler.trigger(this, (Ability)JJKAbilities.WATER.get());
               }
            } else if (channelling) {
               AbilityHandler.untrigger(this, (Ability)JJKAbilities.WATER.get());
            }
         }
      }
   }
}
