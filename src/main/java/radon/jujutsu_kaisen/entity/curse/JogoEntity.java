package radon.jujutsu_kaisen.entity.curse;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.entity.curse.base.DisasterCurse;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class JogoEntity extends DisasterCurse {
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
   private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
   private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

   public JogoEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   @Override
   protected boolean isCustom() {
      return false;
   }

   @Override
   public boolean canChant() {
      return true;
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return CursedTechnique.DISASTER_FLAMES;
   }

   @Override
   public List<Ability> getUnlocked() {
      return List.of(
         (Ability)JJKAbilities.SIMPLE_DOMAIN.get(),
         (Ability)JJKAbilities.MAXIMUM_METEOR.get(),
         (Ability)JJKAbilities.COFFIN_OF_THE_IRON_MOUNTAIN.get(),
         (Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get()
      );
   }

   private PlayState walkRunPredicate(AnimationState<JogoEntity> animationState) {
      return animationState.isMoving() ? animationState.setAndContinue(this.isSprinting() ? RUN : WALK) : PlayState.STOP;
   }

   private PlayState swingPredicate(AnimationState<JogoEntity> animationState) {
      if (this.swinging) {
         return animationState.setAndContinue(SWING);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes().add(Attributes.ARMOR, 18.0).add(Attributes.ARMOR_TOUGHNESS, 12.0);
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Walk/Run", this::walkRunPredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Swing", this::swingPredicate)});
   }
}
