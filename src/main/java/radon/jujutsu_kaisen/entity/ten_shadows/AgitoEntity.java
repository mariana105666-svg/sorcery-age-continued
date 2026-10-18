package radon.jujutsu_kaisen.entity.ten_shadows;

import java.util.List;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class AgitoEntity extends TenShadowsSummon {
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
   private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
   private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

   public AgitoEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public AgitoEntity(LivingEntity owner) {
      this((EntityType<? extends TamableAnimal>)JJKEntities.AGITO.get(), owner.level());
      this.setTame(true);
      this.setOwner(owner);
      Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
      Vec3 pos = owner.position().subtract(direction.multiply(this.getBbWidth(), 0.0, this.getBbWidth()));
      this.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());
      this.yHeadRot = this.getYRot();
      this.yHeadRotO = this.yHeadRot;
      this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
      this.moveControl = new MoveControl(this);
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
      return true;
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

   @NotNull
   public InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
      if (pPlayer == this.getOwner() && this.isTame()) {
         this.lookAt(Anchor.EYES, pPlayer.position().add(0.0, pPlayer.getBbHeight() / 2.0F, 0.0));
         this.setTarget(null);
         return AbilityHandler.trigger(this, (Ability)JJKAbilities.OUTPUT_RCT.get()) == Ability.Status.SUCCESS
            ? InteractionResult.sidedSuccess(this.level().isClientSide)
            : InteractionResult.FAIL;
      } else {
         return super.mobInteract(pPlayer, pHand);
      }
   }

   public float getStepHeight() {
      return 2.0F;
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes().add(Attributes.MAX_HEALTH, 44.0).add(Attributes.ARMOR, 16.0).add(Attributes.ATTACK_DAMAGE, 24.0);
   }

   private PlayState walkRunIdlePredicate(AnimationState<AgitoEntity> animationState) {
      return animationState.isMoving() ? animationState.setAndContinue(this.isSprinting() ? RUN : WALK) : animationState.setAndContinue(IDLE);
   }

   private PlayState swingPredicate(AnimationState<AgitoEntity> animationState) {
      if (this.swinging) {
         return animationState.setAndContinue(SWING);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Walk/Run/Idle", this::walkRunIdlePredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Swing", this::swingPredicate)});
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
      LivingEntity target = this.getTarget();
      if (target != null) {
         this.moveControl.setWantedPosition(target.getX(), target.getY(), target.getZ(), 0.8F);
      }
   }

   @Override
   public void onRemovedFromWorld() {
      super.onRemovedFromWorld();
      if (this.isTame()) {
         LivingEntity owner = this.getOwner();
         if (owner != null) {
            this.getCapability(TenShadowsDataHandler.INSTANCE)
               .ifPresent(srcCap -> owner.getCapability(TenShadowsDataHandler.INSTANCE).ifPresent(dstCap -> dstCap.addAdapted(srcCap.getAdapted())));
         }
      }
   }

   @Override
   public Summon<?> getAbility() {
      return (Summon<?>)JJKAbilities.AGITO.get();
   }

   @NotNull
   @Override
   public List<Ability> getCustom() {
      return List.of((Ability)JJKAbilities.NUE_LIGHTNING.get());
   }

   @Override
   public List<Ability> getUnlocked() {
      return List.of(
         (Ability)JJKAbilities.RCT1.get(),
         (Ability)JJKAbilities.RCT2.get(),
         (Ability)JJKAbilities.RCT3.get(),
         (Ability)JJKAbilities.OUTPUT_RCT.get(),
         (Ability)JJKAbilities.QUICKDASH.get()
      );
   }
}
