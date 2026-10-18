package radon.jujutsu_kaisen.entity.curse;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.base.CursedObjectItem;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class FingerBearerEntity extends CursedSpirit {
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
   private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
   private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

   public FingerBearerEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
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

   @NotNull
   @Override
   public List<Trait> getTraits() {
      return List.of(Trait.CURSED_WOMB);
   }

   public static Builder createAttributes() {
      return SorcererEntity.createMobAttributes()
         .add(Attributes.FOLLOW_RANGE, 100.0)
         .add(Attributes.ARMOR_TOUGHNESS, 10.0)
         .add(Attributes.ATTACK_DAMAGE, 12.0)
         .add(Attributes.ARMOR, 14.0);
   }

   @Override
   public float getExperience() {
      return SorcererGrade.SPECIAL_GRADE.getRequiredExperience();
   }

   @Override
   public int getCursedEnergyColor() {
      return 16775936;
   }

   @Override
   public List<Ability> getUnlocked() {
      return List.of((Ability)JJKAbilities.CURSED_ENERGY_BLAST.get(), (Ability)JJKAbilities.CURSED_ENERGY_BOMB.get());
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return null;
   }

   @Override
   public void init(ISorcererData data) {
      super.init(data);
      data.addExtraEnergy(((CursedObjectItem)((Item)JJKItems.SUKUNA_FINGER.get()).getDefaultInstance().getItem()).getEnergy());
      data.setWombAwakened(true);
   }

   private PlayState walkRunPredicate(AnimationState<FingerBearerEntity> animationState) {
      return animationState.isMoving() ? animationState.setAndContinue(this.isSprinting() ? RUN : WALK) : PlayState.STOP;
   }

   private PlayState swingPredicate(AnimationState<FingerBearerEntity> animationState) {
      if (this.swinging) {
         return animationState.setAndContinue(SWING);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Walk/Run", this::walkRunPredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Swing", this::swingPredicate)});
   }
}
