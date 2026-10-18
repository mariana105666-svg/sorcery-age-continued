package radon.jujutsu_kaisen.entity.curse;

import java.util.List;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class JogoatEntity extends CursedSpirit {
   public static EntityDataAccessor<Integer> DATA_SMASH = SynchedEntityData.defineId(JogoatEntity.class, EntityDataSerializers.INT);
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
   private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
   private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
   private static final RawAnimation SMASH = RawAnimation.begin().thenPlay("attack.smash");
   private static final double SMASH_LAUNCH = 5.0;
   private static final float SMASH_EXPLOSION = 2.5F;
   private static final int SMASH_DURATION = 20;

   public JogoatEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_SMASH, 0);
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
      return true;
   }

   @Override
   public boolean canJump() {
      return true;
   }

   @Override
   public boolean canChant() {
      return true;
   }

   @Override
   public float getExperience() {
      return 1000000.0F;
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return null;
   }

   @NotNull
   @Override
   public List<Trait> getTraits() {
      return List.of(Trait.SIX_EYES);
   }

   @Override
   public float getMaxEnergy() {
      return 999999.0F;
   }

   @Nullable
   @Override
   public CursedEnergyNature getNature() {
      return CursedEnergyNature.LIGHTNING;
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      LivingEntity target = this.getTarget();
      int slash = (Integer)this.entityData.get(DATA_SMASH);
      if (slash > 0) {
         this.entityData.set(DATA_SMASH, --slash);
      } else if (target != null && this.onGround() && this.distanceTo(target) < 3.0) {
         this.entityData.set(DATA_SMASH, 20);
         target.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(this).scale(5.0));
         target.hurtMarked = true;
         Vec3 explosionPos = new Vec3(this.getX(), this.getEyeY() - 0.2, this.getZ()).add(RotationUtil.getTargetAdjustedLookAngle(this));
         this.level().explode(this, explosionPos.x, explosionPos.y, explosionPos.z, 2.5F, false, ExplosionInteraction.NONE);
      }
   }

   private PlayState walkRunPredicate(AnimationState<JogoatEntity> animationState) {
      return animationState.isMoving() ? animationState.setAndContinue(this.isSprinting() ? RUN : WALK) : PlayState.STOP;
   }

   private PlayState swingPredicate(AnimationState<JogoatEntity> animationState) {
      if (this.swinging) {
         return animationState.setAndContinue(SWING);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   private PlayState smashPredicate(AnimationState<JogoatEntity> animationState) {
      int smash = (Integer)this.entityData.get(DATA_SMASH);
      if (smash > 0) {
         return animationState.setAndContinue(SMASH);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Walk/Run", this::walkRunPredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Swing", this::swingPredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Smash", this::smashPredicate)});
   }
}
