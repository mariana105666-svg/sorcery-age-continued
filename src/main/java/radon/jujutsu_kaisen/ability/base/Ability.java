package radon.jujutsu_kaisen.ability.base;

import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.AbilityDisplayInfo;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.BindingVow;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.chant.ChantHandler;
import radon.jujutsu_kaisen.client.JJKPose;
import radon.jujutsu_kaisen.compat.CuffedCompat;
import radon.jujutsu_kaisen.compat.PlayerReviveCompat;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.effect.GraniteRingEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public abstract class Ability {
   public static float getPower(Ability ability, LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return cap.getAbilityPower() * (1.0F + 0.75F * ChantHandler.getChant(owner, ability));
   }

   public float getPower(LivingEntity owner) {
      return getPower(this, owner);
   }

   public void cooldown(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (this.getRealCooldown(owner) > 0) {
         cap.addCooldown(this);
      }
   }

   public boolean isScalable(LivingEntity owner) {
      return this.getActivationType(owner) != Ability.ActivationType.TOGGLED;
   }

   public boolean isChantable() {
      return false;
   }

   public boolean worksThroughBurnout() {
      return false;
   }

   protected boolean isNotDisabledFromDA() {
      return false;
   }

   public boolean canDisable() {
      return true;
   }

   public boolean specialObtainment() {
      return false;
   }

   public boolean isCursedEnergyColor() {
      return false;
   }

   public boolean isDisplayed(LivingEntity owner) {
      return this.getPointsCost() > 0 && !this.isDeniedByHeavenlyRestriction(owner);
   }

   protected boolean isDeniedByHeavenlyRestriction(LivingEntity owner) {
      if (owner != null && owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.hasTrait(Trait.HEAVENLY_RESTRICTION) && this.getCost(owner) != 0.0F;
      } else {
         return false;
      }
   }

   protected int getPointsCost() {
      return 0;
   }

   public boolean isDomain() {
      return false;
   }

   public int getRealPointsCost(LivingEntity owner) {
      return this.getPointsCost();
   }

   public boolean isUnlocked(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return cap.isUnlocked(this);
   }

   public boolean canUnlock(LivingEntity owner) {
      if (this.isBlocked(owner)) {
         return false;
      } else if (this.isDeniedByHeavenlyRestriction(owner)) {
         return false;
      } else if (owner instanceof Player player && player.getAbilities().instabuild) {
         return true;
      } else if (this.isUnlockable() && this.getPointsCost() != 0) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getPoints() >= this.getRealPointsCost(owner);
      } else {
         return false;
      }
   }

   public boolean isUnlockable() {
      return this.getPointsCost() > 0;
   }

   public boolean isBlocked(LivingEntity owner) {
      Ability parent = this.getParent(owner);
      return parent != null && !parent.isUnlocked(owner);
   }

   public Vec2 getDisplayCoordinates() {
      return Vec2.ZERO;
   }

   public AbilityDisplayInfo getDisplay(LivingEntity owner) {
      Vec2 coordinates = this.getDisplayCoordinates();
      return new AbilityDisplayInfo(JJKAbilities.getKey(this).getPath(), coordinates.x, coordinates.y);
   }

   @Nullable
   public Ability getParent(LivingEntity owner) {
      return null;
   }

   public Ability.Classification getClassification() {
      return Ability.Classification.NONE;
   }

   public boolean isMelee() {
      return false;
   }

   public boolean usesHands() {
      return true;
   }

   public abstract boolean shouldTrigger(PathfinderMob var1, @Nullable LivingEntity var2);

   public abstract Ability.ActivationType getActivationType(LivingEntity var1);

   public abstract void run(LivingEntity var1);

   public List<Ability> getRequirements() {
      return List.of();
   }

   protected int getCooldown() {
      return 0;
   }

   public boolean isTechnique() {
      return true;
   }

   public int getRealCooldown(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (this.isMelee() && cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
         return this.getCooldown() / 2;
      } else {
         return this.isMelee() && cap.getNature() == CursedEnergyNature.DIVERGENT ? this.getCooldown() * 3 / 4 : this.getCooldown();
      }
   }

   public MenuType getMenuType() {
      return MenuType.RADIAL;
   }

   public boolean isValid(LivingEntity owner) {
      if (owner == null || owner instanceof Player player && player.isSpectator()) {
         return false;
      } else if (this.isUnlockable() && !this.isUnlocked(owner)) {
         return false;
      } else if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (this.isDomain() && !(owner instanceof Player player)) {
            return true;
         } else if (this.isTechnique()
            && cap.hasToggled((Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get())
            && (!this.isNotDisabledFromDA() || !cap.hasToggled(this))) {
            return false;
         } else if (this.isTechnique() && this.canDisable() && cap.hasDisable()) {
            return false;
         } else if (this.usesHands()
            && !this.isDomain()
            && (cap.hasToggled((Ability)JJKAbilities.HOLLOW_WICKER_BASKET.get()) || cap.hasDisarmed())
            && !cap.hasTrait(Trait.PERFECT_BODY)) {
            return false;
         } else {
            for (Ability ability : this.getRequirements()) {
               if (!ability.isUnlocked(owner)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public void addDuration(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (this instanceof Ability.IDurationable durationable && durationable.getRealDuration(owner) > 0) {
         cap.addDuration(this);
      }
   }

   public void charge(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
         cap.useEnergy(this.getRealCost(owner));
         if (this.getRealCooldown(owner) > 0) {
            cap.addCooldown(this);
         }
      }
   }

   public Ability.Status getStatus(LivingEntity owner) {
      if (this != JJKAbilities.WHEEL.get() && this != JJKAbilities.CURSED_ENERGY_FLOW.get() && owner.hasEffect((MobEffect)JJKEffects.UNLIMITED_VOID.get())) {
         return Ability.Status.FAILURE;
      } else if (VeilHandler.isAbilityBlocked(owner, this)) {
         return Ability.Status.FAILURE;
      } else if (GraniteRingEntity.suppresses(owner, this)) {
         return Ability.Status.FAILURE;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (!(owner instanceof Player player && (player.getAbilities().instabuild || (Boolean)ConfigHolder.SERVER.turboMode.get()))) {
            if (this.isTechnique() && cap.hasBurnout() && !this.worksThroughBurnout()) {
               return Ability.Status.BURNOUT;
            }

            if (this.isTechnique()
               && (this.canDisable() && cap.hasDisable() || owner.getHealth() / owner.getMaxHealth() > 0.25F && cap.hasBindingVow(BindingVow.RISK))) {
               return Ability.Status.DISABLE;
            }

            if (this.usesHands()
               && !this.isDomain()
               && (cap.hasToggled((Ability)JJKAbilities.HOLLOW_WICKER_BASKET.get()) || cap.hasDisarmed())
               && !cap.hasTrait(Trait.PERFECT_BODY)) {
               return Ability.Status.DISARMED;
            }

            if (owner instanceof Player playerx) {
               if (JujutsuKaisen.PlayerReviveInstalled && PlayerReviveCompat.IsBleedingOut(playerx)) {
                  return Ability.Status.FAILURE;
               }

               if (JujutsuKaisen.CuffedInstalled) {
                  if (this.usesHands() && CuffedCompat.armsRestrained(playerx)) {
                     return Ability.Status.DISARMED;
                  }

                  if (CuffedCompat.legsRestrained(playerx) && !this.usesHands() && this.getClassification() != Ability.Classification.CURSED_SPEECH) {
                     return Ability.Status.FAILURE;
                  }

                  if (CuffedCompat.headRestrained(playerx) && this.getClassification() == Ability.Classification.CURSED_SPEECH) {
                     return Ability.Status.FAILURE;
                  }
               }
            }

            if (!cap.isCooldownDone(this)) {
               return Ability.Status.COOLDOWN;
            }

            if (!this.checkCost(owner)) {
               return Ability.Status.ENERGY;
            }
         }

         return Ability.Status.SUCCESS;
      }
   }

   public Ability.Status isTriggerable(LivingEntity owner) {
      if (!JJKAbilities.getAbilities(owner).contains(this)) {
         return Ability.Status.UNUSABLE;
      } else {
         MobEffectInstance instance = owner.getEffect((MobEffect)JJKEffects.STUN.get());
         if (instance != null
            && instance.getAmplifier() > 0
            && this != JJKAbilities.HEAL.get()
            && this != JJKAbilities.RCT1.get()
            && this != JJKAbilities.RCT2.get()
            && this != JJKAbilities.RCT3.get()
            && this != JJKAbilities.CURSED_ENERGY_SHIELD.get()
            && this != JJKAbilities.HOLLOW_WICKER_BASKET.get()
            && this != JJKAbilities.SIMPLE_DOMAIN.get()
            && this != JJKAbilities.SIMPLE_DOMAIN_ENLARGEMENT.get()) {
            if (!this.isDomain()) {
               return Ability.Status.FAILURE;
            }

            ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if (cap.getType() != JujutsuType.CURSE || cap.hasTrait(Trait.DEATH_PAINTING)) {
               return Ability.Status.FAILURE;
            }
         }

         return this.getStatus(owner);
      }
   }

   public Ability.Status isStillUsable(LivingEntity owner) {
      if (!JJKAbilities.getAbilities(owner).contains(this)) {
         return Ability.Status.UNUSABLE;
      } else if (!(this instanceof Ability.IAttack) && !(this instanceof Ability.ICharged)) {
         Ability.Status status = this.getStatus(owner);
         if (status == Ability.Status.SUCCESS || status == Ability.Status.COOLDOWN) {
            if (this.checkCost(owner)) {
               this.charge(owner);
            } else {
               status = Ability.Status.ENERGY;
            }
         }

         return status;
      } else {
         return this.getStatus(owner);
      }
   }

   public boolean checkCost(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (owner instanceof Player player && player.getAbilities().instabuild) {
         return true;
      } else {
         float cost = this.getRealCost(owner);
         return cap.getEnergy() >= cost;
      }
   }

   public Component getName() {
      ResourceLocation key = JJKAbilities.getKey(this);
      return Component.translatable(String.format("ability.%s.%s", key.getNamespace(), key.getPath()));
   }

   public abstract float getCost(LivingEntity var1);

   public boolean shouldLog(LivingEntity owner) {
      return this.getActivationType(owner) == Ability.ActivationType.TOGGLED;
   }

   public Component getEnableMessage() {
      ResourceLocation key = JJKAbilities.getKey(this);
      return key == null ? Component.empty() : Component.translatable(String.format("ability.%s.%s.enable", key.getNamespace(), key.getPath()));
   }

   public Component getDisableMessage() {
      ResourceLocation key = JJKAbilities.getKey(this);
      return key == null ? Component.empty() : Component.translatable(String.format("ability.%s.%s.disable", key.getNamespace(), key.getPath()));
   }

   @Nullable
   public BlockHitResult getBlockHit(LivingEntity owner, double range) {
      Vec3 start = owner.getEyePosition();
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      Vec3 end = start.add(look.scale(range));
      HitResult result = RotationUtil.getHitResult(owner, start, end);
      if (result.getType() == Type.BLOCK) {
         return (BlockHitResult)result;
      } else if (result.getType() == Type.ENTITY) {
         Entity entity = ((EntityHitResult)result).getEntity();
         Vec3 offset = entity.position().subtract(0.0, 5.0, 0.0);
         return owner.level().clip(new ClipContext(entity.position(), offset, Block.COLLIDER, Fluid.NONE, null));
      } else {
         return null;
      }
   }

   public float getRealCost(LivingEntity owner, float cost) {
      float output = ChantHandler.getOutput(owner, this);
      if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return cost;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         CursedTechnique copied = cap.getCurrentCopied();
         if (copied != null && List.of(copied.getAbilities()).contains(this)) {
            cost *= 1.5F;
         }

         if (!cap.hasTrait(Trait.SIX_EYES) || !this.isTechnique() && !this.isDomain()) {
            if (!cap.hasTrait(Trait.SIX_EYES) && (this.isTechnique() || this.isDomain()) && JJKAbilities.getTechnique(this) == CursedTechnique.LIMITLESS) {
               cost *= ((Double)ConfigHolder.SERVER.limitlessNoSixEyesMult.get()).floatValue();
            }
         } else {
            cost *= ((Double)ConfigHolder.SERVER.sixEyesMult.get()).floatValue();
         }

         if (output > 0.0F) {
            if (output >= 1.0F) {
               output = Mth.clamp(output * 0.8F, 1.0F, 100.0F);
               cost *= !this.isScalable(owner) && !this.isChantable() ? 1.0F : output;
            } else {
               float scaledOutput = Mth.clamp(output, 0.1F, 1.0F);
               float costMultiplier = (float)Math.pow(scaledOutput, 0.5);
               cost *= !this.isScalable(owner) && !this.isChantable() ? 1.0F : costMultiplier;
            }

            cost *= ((Double)ConfigHolder.SERVER.cursedEnergyCostAmount.get()).floatValue();
         }

         return Float.parseFloat(String.format(Locale.ROOT, "%.2f", cost));
      }
   }

   public float getRealCost(LivingEntity owner) {
      return this.getRealCost(owner, this.getCost(owner));
   }

   public static enum ActivationType {
      INSTANT,
      TOGGLED,
      CHANNELED,
      DOMAIN;
   }

   public static enum Classification {
      NONE,
      CURSED_SPEECH,
      SLASHING,
      FIRE,
      WATER,
      PLANTS,
      BLUE,
      LIGHTNING,
      PROJECTION;
   }

   public interface IAttack {
      boolean attack(DamageSource var1, LivingEntity var2, LivingEntity var3);
   }

   public interface IChannelened {
      default void onStart(LivingEntity owner) {
      }

      default void onStop(LivingEntity owner) {
      }

      default int getCharge(LivingEntity owner) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getCharge((Ability)this);
      }
   }

   public interface ICharged extends Ability.IChannelened {
      default boolean onRelease(LivingEntity owner) {
         return true;
      }
   }

   public interface IDomainAttack {
      default void performEntity(LivingEntity owner, LivingEntity target, DomainExpansionEntity domain) {
      }

      default void performBlock(LivingEntity owner, DomainExpansionEntity domain, BlockPos pos) {
      }
   }

   public interface IDurationable {
      default int getDuration() {
         return 0;
      }

      default int getRealDuration(LivingEntity owner) {
         int duration = this.getDuration();
         if (duration > 0) {
            duration *= 12;
         }

         return duration;
      }
   }

   public interface IPosedMove {
      @NotNull
      default JJKPose getArmPose(LivingEntity entity) {
         return null;
      }

      default int poseTimer(LivingEntity owner) {
         return this.getArmPose(owner).defaultDuration();
      }

      default InteractionHand getHand(LivingEntity owner) {
         return InteractionHand.MAIN_HAND;
      }
   }

   public interface ITenShadowsAttack {
      void perform(LivingEntity var1, @Nullable LivingEntity var2);
   }

   public interface IToggled {
      void onEnabled(LivingEntity var1);

      void onDisabled(LivingEntity var1);

      default void applyModifiers(LivingEntity owner) {
      }

      default void removeModifiers(LivingEntity owner) {
      }
   }

   public static enum Status {
      FAILURE,
      UNUSABLE,
      SUCCESS,
      ENERGY,
      COOLDOWN,
      BURNOUT,
      DISABLE,
      DISARMED,
      SILENCED,
      CHANT,
      THROAT,
      EMPTYINV;
   }
}
