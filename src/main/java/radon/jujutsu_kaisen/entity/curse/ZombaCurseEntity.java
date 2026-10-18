package radon.jujutsu_kaisen.entity.curse;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class ZombaCurseEntity extends CursedSpirit {
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
   private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

   public ZombaCurseEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
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
      return false;
   }

   @Override
   public boolean canChant() {
      return false;
   }

   @Override
   public float getExperience() {
      return SorcererGrade.GRADE_1.getRequiredExperience();
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return CursedTechnique.SKY_STRIKE;
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes()
         .add(Attributes.ARMOR, 8.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.MOVEMENT_SPEED, 0.0)
         .add(Attributes.ARMOR_TOUGHNESS, 5.0);
   }

   @NotNull
   @Override
   public List<Ability> getCustom() {
      return List.of((Ability)JJKAbilities.SKY_STRIKE.get());
   }

   private PlayState swingPredicate(AnimationState<ZombaCurseEntity> animationState) {
      if (this.swinging) {
         return animationState.setAndContinue(SWING);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Idle", animationState -> animationState.setAndContinue(IDLE))});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Swing", this::swingPredicate)});
   }
}
