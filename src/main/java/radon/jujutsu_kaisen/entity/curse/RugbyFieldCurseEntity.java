package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class RugbyFieldCurseEntity extends CursedSpirit implements PlayerRideable {
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
   private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
   private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

   public RugbyFieldCurseEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
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
      return SorcererGrade.SEMI_GRADE_2.getRequiredExperience();
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return null;
   }

   private PlayState walkRunPredicate(AnimationState<RugbyFieldCurseEntity> animationState) {
      return animationState.isMoving() ? animationState.setAndContinue(this.isSprinting() ? RUN : WALK) : PlayState.STOP;
   }

   private PlayState swingPredicate(AnimationState<RugbyFieldCurseEntity> animationState) {
      if (this.swinging) {
         return animationState.setAndContinue(SWING);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes()
         .add(Attributes.ARMOR, 12.0)
         .add(Attributes.FOLLOW_RANGE, 100.0)
         .add(Attributes.ATTACK_DAMAGE, 25.0)
         .add(Attributes.ARMOR_TOUGHNESS, 8.0);
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Walk/Run", this::walkRunPredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Swing", this::swingPredicate)});
   }
}
