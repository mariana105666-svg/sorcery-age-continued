package radon.jujutsu_kaisen.entity.curse;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.base.PackCursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class FishCurseEntity extends PackCursedSpirit {
   private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");

   public FishCurseEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
      this.moveControl = new FlyingMoveControl(this, 20, true);
   }

   public FishCurseEntity(FishCurseEntity leader) {
      this((EntityType<? extends TamableAnimal>)JJKEntities.FISH_CURSE.get(), leader.level());
      this.setLeader(leader);
   }

   protected float getFlyingSpeed() {
      return 0.1F;
   }

   @Override
   public int getMinCount() {
      return 2;
   }

   @Override
   public int getMaxCount() {
      return 8;
   }

   @Override
   protected PackCursedSpirit spawn() {
      return new FishCurseEntity(this);
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

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes().add(Attributes.FLYING_SPEED).add(Attributes.MAX_HEALTH, 5.0);
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
      return SorcererGrade.GRADE_4.getRequiredExperience();
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return null;
   }

   private PlayState swimPredicate(AnimationState<FishCurseEntity> animationState) {
      return animationState.isMoving() ? animationState.setAndContinue(SWIM) : PlayState.STOP;
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Swim", this::swimPredicate)});
   }
}
