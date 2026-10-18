package radon.jujutsu_kaisen.entity.curse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class KuchisakeOnnaEntity extends CursedSpirit {
   public static final double RANGE = 16.0;
   private static final int SNIP_DURATION = 5;
   private static final float DAMAGE = 5.0F;
   private static final int INTERVAL = 60;
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
   private static final RawAnimation SNIP = RawAnimation.begin().thenPlay("attack.snip");
   public static EntityDataAccessor<Optional<UUID>> DATA_TARGET = SynchedEntityData.defineId(KuchisakeOnnaEntity.class, EntityDataSerializers.OPTIONAL_UUID);
   public static EntityDataAccessor<Boolean> DATA_OPEN = SynchedEntityData.defineId(KuchisakeOnnaEntity.class, EntityDataSerializers.BOOLEAN);
   public static EntityDataAccessor<Integer> DATA_SNIP = SynchedEntityData.defineId(KuchisakeOnnaEntity.class, EntityDataSerializers.INT);
   private Vec3 start;
   private int cooldown;

   public KuchisakeOnnaEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   @Override
   protected boolean isCustom() {
      return false;
   }

   @Override
   public boolean canChant() {
      return false;
   }

   @Override
   public boolean canChangeTarget() {
      return true;
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

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.165).add(Attributes.ARMOR, 14.0).add(Attributes.ARMOR_TOUGHNESS, 14.0);
   }

   private PlayState walkPredicate(AnimationState<KuchisakeOnnaEntity> animationState) {
      return animationState.isMoving() ? animationState.setAndContinue(WALK) : PlayState.STOP;
   }

   private PlayState cutPredicate(AnimationState<KuchisakeOnnaEntity> animationState) {
      return this.entityData.get(DATA_SNIP) > 0 ? animationState.setAndContinue(SNIP) : PlayState.STOP;
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Walk", this::walkPredicate)});
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Snip", this::cutPredicate)});
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TARGET, Optional.empty());
      this.entityData.define(DATA_OPEN, false);
      this.entityData.define(DATA_SNIP, 0);
   }

   public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
      Entity source = pSource.getEntity();
      if (source != null) {
         Optional<UUID> identifier = (Optional<UUID>)this.entityData.get(DATA_TARGET);
         if (identifier.isPresent() && source.getUUID().equals(identifier.get())) {
            return false;
         }
      }

      return super.hurt(pSource, pAmount);
   }

   public boolean isOpen() {
      return (Boolean)this.entityData.get(DATA_OPEN);
   }

   public Optional<UUID> getCurrent() {
      return (Optional<UUID>)this.entityData.get(DATA_TARGET);
   }

   public void reset() {
      this.entityData.set(DATA_OPEN, false);
      this.entityData.set(DATA_TARGET, Optional.empty());
      if (JJKAbilities.hasToggled(this, (Ability)JJKAbilities.SCISSORS.get())) {
         AbilityHandler.trigger(this, (Ability)JJKAbilities.SCISSORS.get());
      }

      this.start = null;
      this.cooldown = 60;
   }

   @Override
   public float getExperience() {
      return SorcererGrade.SPECIAL_GRADE.getRequiredExperience();
   }

   @NotNull
   @Override
   public List<Ability> getCustom() {
      return List.of((Ability)JJKAbilities.SCISSORS.get());
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return null;
   }

   @Override
   public List<Ability> getUnlocked() {
      return List.of((Ability)JJKAbilities.SIMPLE_DOMAIN.get());
   }

   public void attack() {
      this.entityData.set(DATA_SNIP, 5);
      AbilityHandler.trigger(this, (Ability)JJKAbilities.SCISSORS.get());
      this.getCurrent()
         .ifPresent(
            identifier -> {
               Entity target = ((ServerLevel)this.level()).getEntity(identifier);
               if (target != null) {
                  this.getCapability(SorcererDataHandler.INSTANCE)
                     .ifPresent(cap -> target.hurt(JJKDamageSources.jujutsuAttack(this, null), 5.0F * cap.getAbilityPower()));
               }
            }
         );
      this.reset();
   }

   @Override
   public void changeTarget(LivingEntity target) {
      if (target != null && !target.isRemoved() && target.isAlive()) {
         this.entityData.set(DATA_TARGET, Optional.of(target.getUUID()));
         this.start = target.position();
         this.setTarget(target);
      }
   }

   @Override
   protected void customServerAiStep() {
      if (!this.level().isClientSide) {
         int snip = (Integer)this.entityData.get(DATA_SNIP);
         if (snip > 0) {
            this.entityData.set(DATA_SNIP, --snip);
         }

         if (this.cooldown > 0) {
            this.cooldown--;
         }

         this.getCurrent().ifPresent(identifier -> {
            if (((ServerLevel)this.level()).getEntity(identifier) instanceof LivingEntity targetx) {
               this.moveControl.setWantedPosition(this.getX(), this.getY(), this.getZ(), this.getSpeed());
               if (this.distanceTo(targetx) > 16.0) {
                  this.reset();
               } else if (Math.sqrt(targetx.distanceToSqr(this.start)) >= 3.0) {
                  this.attack();
               }
            }
         });
         LivingEntity target = this.getTarget();
         if (target != null && !target.isRemoved() && target.isAlive()) {
            if (!this.isOpen()) {
               if (this.cooldown > 0) {
                  return;
               }

               if (this.distanceTo(target) <= 16.0) {
                  this.entityData.set(DATA_TARGET, Optional.of(target.getUUID()));
                  this.start = target.position();
                  if (((Ability)JJKAbilities.SCISSORS.get()).getStatus(this) == Ability.Status.SUCCESS) {
                     target.sendSystemMessage(Component.translatable(String.format("chat.%s.kuchisake_onna", "jujutsu_kaisen"), new Object[]{this.getName().getString()}));
                     this.entityData.set(DATA_OPEN, true);
                  } else {
                     this.entityData.set(DATA_TARGET, Optional.empty());
                  }
               } else {
                  this.moveControl.setWantedPosition(target.getX(), target.getY(), target.getZ(), this.getSpeed());
               }
            }
         }
      }
   }
}
