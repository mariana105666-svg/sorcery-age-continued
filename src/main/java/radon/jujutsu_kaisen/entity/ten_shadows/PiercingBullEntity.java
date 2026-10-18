package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class PiercingBullEntity extends TenShadowsSummon {
   private static final float DAMAGE = 17.0F;
   private static final int INTERVAL = 200;
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
   private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
   private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");

   public PiercingBullEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public PiercingBullEntity(LivingEntity owner, boolean tame) {
      this((EntityType<? extends TamableAnimal>)JJKEntities.PIERCING_BULL.get(), owner.level());
      this.setTame(tame);
      this.setOwner(owner);
      Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
      Vec3 pos = owner.position().subtract(direction.multiply(this.getBbWidth(), 0.0, this.getBbWidth()));
      this.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());
      this.yHeadRot = this.getYRot();
      this.yHeadRotO = this.yHeadRot;
      this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
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
      return false;
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
   protected void customServerAiStep() {
      LivingEntity target = this.getTarget();
      if (target != null) {
         this.lookAt(Anchor.EYES, target.position());
         if ((this.onGround() || this.isInFluidType()) && (this.isSprinting() || this.getTime() % 200 == 0)) {
            this.setSprinting(true);
            this.move(MoverType.SELF, target.position().subtract(this.position()).normalize().scale(this.distanceTo(target)));
            float distance = this.distanceTo(target);
            ISorcererData cap = (ISorcererData)this.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            for (Entity entity : this.level().getEntities(this.getOwner(), this.getBoundingBox().inflate(1.0))) {
               if (entity != this && entity.hurt(this.damageSources().mobAttack(this), 17.0F * distance * cap.getAbilityPower())) {
                  entity.setDeltaMovement(this.position().subtract(entity.position()).normalize().reverse().scale(cap.getAbilityPower()));
                  entity.hurtMarked = true;
                  this.level()
                     .explode(
                        this,
                        entity.getX(),
                        entity.getY() + entity.getBbHeight() / 2.0F,
                        entity.getZ(),
                        cap.getAbilityPower(),
                        false,
                        ExplosionInteraction.NONE
                     );
                  if (entity == target) {
                     this.setSprinting(false);
                  }
               }
            }
         }
      }
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes()
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.MOVEMENT_SPEED, 0.33)
         .add(Attributes.ARMOR, 10.0)
         .add(Attributes.ATTACK_DAMAGE, 17.5);
   }

   private PlayState walkRunIdlePredicate(AnimationState<PiercingBullEntity> animationState) {
      return animationState.isMoving() ? animationState.setAndContinue(this.isSprinting() ? RUN : WALK) : animationState.setAndContinue(IDLE);
   }

   private PlayState swingPredicate(AnimationState<PiercingBullEntity> animationState) {
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

   @Override
   public Summon<?> getAbility() {
      return (Summon<?>)JJKAbilities.PIERCING_BULL.get();
   }

   public float getStepHeight() {
      return 2.0F;
   }
}
