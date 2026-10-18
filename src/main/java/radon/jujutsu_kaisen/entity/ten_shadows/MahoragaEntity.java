package radon.jujutsu_kaisen.entity.ten_shadows;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class MahoragaEntity extends TenShadowsSummon {
   public static EntityDataAccessor<Integer> DATA_SLASH = SynchedEntityData.defineId(MahoragaEntity.class, EntityDataSerializers.INT);
   public static EntityDataAccessor<Boolean> DATA_BATTLE = SynchedEntityData.defineId(MahoragaEntity.class, EntityDataSerializers.BOOLEAN);
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
   private static final RawAnimation IDLE_BATTLE = RawAnimation.begin().thenLoop("misc.idle_battle");
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
   private static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
   private static final RawAnimation SWING = RawAnimation.begin().thenPlay("attack.swing");
   private static final RawAnimation SLASH = RawAnimation.begin().thenPlay("attack.slash");
   private static final double SLASH_LAUNCH = 30.0;
   private static final float SLASH_EXPLOSION = 5.0F;
   private static final float SLASH_DAMAGE = 15.0F;
   private static final int SLASH_DURATION = 20;
   private static final int RITUAL_DURATION = 60;
   private boolean healing;

   public MahoragaEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public MahoragaEntity(LivingEntity owner, boolean tame) {
      this((EntityType<? extends TamableAnimal>)JJKEntities.MAHORAGA.get(), owner.level());
      this.setTame(tame);
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
   public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
      return !this.isTame() && this.getTime() <= 60 ? false : super.hurt(pSource, pAmount);
   }

   public boolean isNoAi() {
      return !this.isTame() && this.getTime() <= 60 || super.isNoAi();
   }

   public double getPassengersRidingOffset() {
      return this.getBbHeight() - 0.35;
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

   public float getStepHeight() {
      return 4.0F;
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes().add(Attributes.MAX_HEALTH, 30.0).add(Attributes.ARMOR, 20.0).add(Attributes.ATTACK_DAMAGE, 40.0);
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
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_SLASH, 0);
      this.entityData.define(DATA_BATTLE, false);
   }

   private PlayState walkRunIdlePredicate(AnimationState<MahoragaEntity> animationState) {
      return animationState.isMoving()
         ? animationState.setAndContinue(this.isSprinting() ? RUN : WALK)
         : animationState.setAndContinue(this.entityData.get(DATA_BATTLE) ? IDLE_BATTLE : IDLE);
   }

   private PlayState swingPredicate(AnimationState<MahoragaEntity> animationState) {
      if (this.swinging) {
         return animationState.setAndContinue(SWING);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   private PlayState slashPredicate(AnimationState<MahoragaEntity> animationState) {
      int slash = (Integer)this.entityData.get(DATA_SLASH);
      if (slash > 0) {
         return animationState.setAndContinue(SLASH);
      } else {
         animationState.getController().forceAnimationReset();
         return PlayState.STOP;
      }
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Walk/Run/Idle", this::walkRunIdlePredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Swing", this::swingPredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Slash", this::slashPredicate)});
   }

   public boolean doHurtTarget(@NotNull Entity pEntity) {
      boolean result = super.doHurtTarget(pEntity);
      if (result) {
         if (!(pEntity instanceof LivingEntity living)) {
            return true;
         }

         if (!pEntity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            return true;
         }

         ISorcererData cap = (ISorcererData)pEntity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (cap.getType() == JujutsuType.CURSE) {
            pEntity.hurt(this.damageSources().mobAttack(this), living.getMaxHealth() * 0.5F);
         }
      }

      return result;
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      LivingEntity target = this.getTarget();
      this.entityData.set(DATA_BATTLE, target != null);
      int slash = (Integer)this.entityData.get(DATA_SLASH);
      if (slash > 0) {
         this.entityData.set(DATA_SLASH, --slash);
      } else if (target != null) {
         this.moveControl.setWantedPosition(target.getX(), target.getY(), target.getZ(), 0.8F);
         if (this.onGround() && this.distanceTo(target) < 3.0 && this.doHurtTarget(target)) {
            this.entityData.set(DATA_SLASH, 20);
            target.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(this).scale(30.0));
            target.hurtMarked = true;
            Vec3 center = target.position().add(0.0, target.getBbHeight() / 2.0F, 0.0);
            ((ServerLevel)this.level()).sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, 1.0, 0.0, 0.0, 1.0);
            this.level().playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);
         }
      }
   }

   private void setRitualOffset(Mob entity, int index, double padding) {
      entity.setNoAi(true);
      double x = entity.getX();
      double y = entity.getY();
      double z = entity.getZ();
      double distance = entity.getBbWidth() * 2.0F;
      Vec3 look = RotationUtil.calculateViewVector(0.0F, entity.getYRot());
      Vec3 up = new Vec3(0.0, 1.0, 0.0);
      Vec3 side = look.cross(up);
      Vec3 offset = side.scale(distance * (index < 3 ? 1 : -1)).add(look.scale(padding + index % 3 * 3.0));
      entity.setPos(x + offset.x, y, z + offset.z);
      float yRot = entity.getYRot();
      if (index < 3) {
         yRot -= 90.0F;
      } else {
         yRot += 90.0F;
      }

      entity.setYRot(yRot);
      entity.yHeadRot = entity.getYRot();
      entity.yHeadRotO = entity.yHeadRot;
   }

   @Override
   public void onAddedToWorld() {
      super.onAddedToWorld();
      if (this.isTame()) {
         LivingEntity owner = this.getOwner();
         if (owner != null) {
            if (!owner.getCapability(TenShadowsDataHandler.INSTANCE).isPresent()) {
               return;
            }

            ITenShadowsData src = (ITenShadowsData)owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
            ITenShadowsData dst = (ITenShadowsData)this.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
            dst.addAdapted(src.getAdapted());
            dst.addAdapting(src.getAdapting());
         }
      } else {
         this.playSound((SoundEvent)JJKSounds.WOLF_HOWLING.get(), 3.0F, 1.0F);

         for (int i = 0; i < 6; i++) {
            DivineDogBlackEntity dog = new DivineDogBlackEntity(this, true);
            dog.setRitual(60);
            this.setRitualOffset(dog, i, 1.5);
            this.level().addFreshEntity(dog);
         }

         for (int i = 0; i < 6; i++) {
            ToadEntity toad = new ToadEntity((EntityType<? extends TamableAnimal>)JJKEntities.TOAD.get(), this, false, true);
            toad.setRitual(60);
            this.setRitualOffset(toad, i, 0.0);
            this.level().addFreshEntity(toad);
         }
      }
   }

   @Override
   public void onRemovedFromWorld() {
      super.onRemovedFromWorld();
      if (this.isTame()) {
         LivingEntity owner = this.getOwner();
         if (owner != null) {
            if (owner.getCapability(TenShadowsDataHandler.INSTANCE).isPresent()) {
               ITenShadowsData src = (ITenShadowsData)this.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
               ITenShadowsData dst = (ITenShadowsData)owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
               dst.addAdapted(src.getAdapted());
               dst.addAdapting(src.getAdapting());
            }
         }
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getHealth() != this.getMaxHealth()) {
         this.heal(0.05F);
      }
   }

   @Override
   public float getExperience() {
      return Math.max(SorcererGrade.SPECIAL_GRADE.getRequiredExperience(), super.getExperience());
   }

   @Override
   public float getMaxEnergy() {
      return 999999.0F;
   }

   @NotNull
   @Override
   public List<Ability> getCustom() {
      return List.of((Ability)JJKAbilities.WHEEL.get());
   }

   @Override
   public Summon<?> getAbility() {
      return (Summon<?>)JJKAbilities.MAHORAGA.get();
   }
}
