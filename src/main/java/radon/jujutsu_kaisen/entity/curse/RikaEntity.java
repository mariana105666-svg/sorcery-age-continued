package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ai.goal.BetterFollowOwnerGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.ai.goal.WaterWalkingFloatGoal;
import radon.jujutsu_kaisen.entity.base.ICommandable;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.base.SummonEntity;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class RikaEntity extends SummonEntity implements ICommandable, ISorcerer {
   private static final int DURATION = 6000;
   public static EntityDataAccessor<Integer> DATA_OPEN = SynchedEntityData.defineId(RikaEntity.class, EntityDataSerializers.INT);
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
   private static final RawAnimation OPEN = RawAnimation.begin().thenPlayAndHold("misc.open");
   private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

   public RikaEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public RikaEntity(LivingEntity owner) {
      this((EntityType<? extends TamableAnimal>)JJKEntities.RIKA.get(), owner.level());
      this.setTame(true);
      this.setOwner(owner);
      Vec3 pos = owner.position().subtract(RotationUtil.getTargetAdjustedLookAngle(owner).multiply(this.getBbWidth(), 0.0, this.getBbWidth()));
      this.moveTo(pos.x, pos.y, pos.z);
      this.moveControl = new FlyingMoveControl(this, 1, true);
   }

   protected float getFlyingSpeed() {
      float speed = super.getFlyingSpeed();
      if (this.getTarget() != null) {
         speed *= 25.0F;
      } else {
         speed *= 5.0F;
      }

      return speed;
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes()
         .add(Attributes.FLYING_SPEED, 0.33)
         .add(Attributes.MAX_HEALTH, 72.0)
         .add(Attributes.ARMOR, 30.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.25)
         .add(Attributes.ATTACK_DAMAGE, 21.0)
         .add(Attributes.FOLLOW_RANGE, 64.0);
   }

   @NotNull
   protected PathNavigation createNavigation(@NotNull Level pLevel) {
      FlyingPathNavigation navigation = new FlyingPathNavigation(this, pLevel);
      navigation.setCanOpenDoors(false);
      navigation.setCanFloat(true);
      navigation.setCanPassDoors(true);
      return navigation;
   }

   protected void registerGoals() {
      int goal = 1;
      int target = 1;
      this.goalSelector.addGoal(goal++, new WaterWalkingFloatGoal(this));
      this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 0.9, true));
      this.goalSelector.addGoal(goal++, new SorcererGoal(this));
      this.goalSelector.addGoal(goal++, new BetterFollowOwnerGoal(this, 1.0, 10.0F, 5.0F, true));
      this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(target, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(target++, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(target, new OwnerHurtTargetGoal(this));
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_OPEN, 0);
   }

   public void setOpen(int duration) {
      this.entityData.set(DATA_OPEN, duration);
   }

   public int getOpen() {
      return (Integer)this.entityData.get(DATA_OPEN);
   }

   public boolean isOpen() {
      return this.getOpen() > 0;
   }

   private PlayState openPredicate(AnimationState<RikaEntity> animationState) {
      return this.isOpen() ? animationState.setAndContinue(OPEN) : PlayState.STOP;
   }

   private PlayState swingPredicate(AnimationState<RikaEntity> animationState) {
      if (this.swinging) {
         return animationState.setAndContinue(SWING);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Idle", state -> state.setAndContinue(IDLE))});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Open", this::openPredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Swing", this::swingPredicate)});
   }

   @Override
   public Summon<?> getAbility() {
      return (Summon<?>)JJKAbilities.RIKA.get();
   }

   @Override
   public void tick() {
      LivingEntity owner = this.getOwner();
      if (this.level().isClientSide
         || owner != null && !owner.isRemoved() && owner.isAlive() && (this.isDeadOrDying() || JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.RIKA.get()))) {
         super.tick();
         LivingEntity target = this.getTarget();
         this.setOrderedToSit(target != null && !target.isRemoved() && target.isAlive());
         if (target != null) {
            this.moveControl.setWantedPosition(target.getX(), target.getY(), target.getZ(), 0.85F);
         }

         if (owner != null && this.isOpen()) {
            Vec3 pos = owner.position()
               .subtract(RotationUtil.getTargetAdjustedLookAngle(owner).multiply(this.getBbWidth(), 0.0, this.getBbWidth()))
               .add(RotationUtil.getTargetAdjustedLookAngle(owner).yRot(90.0F).scale(-0.45));
            this.moveTo(pos.x, pos.y, pos.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));
            this.yHeadRot = this.getYRot();
            this.yHeadRotO = this.yHeadRot;
         }

         if (!this.level().isClientSide) {
            if (this.getHealth() != this.getMaxHealth()) {
               this.heal(0.075F);
            }

            int remaining = this.getOpen();
            ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if (remaining > 0) {
               if (--remaining == 0) {
                  this.discard();
               }

               this.setOpen(remaining);
            }

            if (this.getTime() >= 6000) {
               this.discard();
            }
         }
      } else {
         this.discard();
      }
   }

   @Override
   public boolean hasMeleeAttack() {
      return true;
   }

   @Override
   public boolean hasArms() {
      return true;
   }

   @Override
   public boolean canJump() {
      return false;
   }

   @Override
   public boolean canChant() {
      return true;
   }

   @Override
   public float getExperience() {
      LivingEntity owner = this.getOwner();
      if (owner != null && owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getExperience();
      } else {
         return 0.0F;
      }
   }

   @Override
   public float getMaxEnergy() {
      return 999999.0F;
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return null;
   }

   @Override
   public JujutsuType getJujutsuType() {
      return JujutsuType.SHIKIGAMI;
   }

   @Override
   public boolean canChangeTarget() {
      return true;
   }

   @Override
   public void changeTarget(LivingEntity target) {
      this.setTarget(target);
   }
}
