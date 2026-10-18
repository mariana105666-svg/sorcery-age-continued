package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
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

public class CyclopsCurseEntity extends CursedSpirit {
   public static EntityDataAccessor<Integer> DATA_SMASH = SynchedEntityData.defineId(CyclopsCurseEntity.class, EntityDataSerializers.INT);
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
   private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
   private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

   public CyclopsCurseEntity(EntityType<? extends CursedSpirit> pType, Level pLevel) {
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
      return true;
   }

   @Override
   public boolean canJump() {
      return true;
   }

   @Override
   public boolean canChant() {
      return false;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_SMASH, 0);
   }

   public static Builder createAttributes() {
      return SorcererEntity.createMobAttributes()
         .add(Attributes.FOLLOW_RANGE, 100.0)
         .add(Attributes.ARMOR_TOUGHNESS, 10.0)
         .add(Attributes.ATTACK_DAMAGE, 15.0)
         .add(Attributes.ARMOR, 14.0);
   }

   private PlayState walkPredicate(AnimationState<CyclopsCurseEntity> animationState) {
      return animationState.isMoving() ? animationState.setAndContinue(this.isSprinting() ? RUN : WALK) : PlayState.STOP;
   }

   private PlayState swingPredicate(AnimationState<CyclopsCurseEntity> animationState) {
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
   public float getExperience() {
      return SorcererGrade.GRADE_2.getRequiredExperience();
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return CursedTechnique.SHOCKWAVE;
   }

   public float getStepHeight() {
      return 2.0F;
   }
}
