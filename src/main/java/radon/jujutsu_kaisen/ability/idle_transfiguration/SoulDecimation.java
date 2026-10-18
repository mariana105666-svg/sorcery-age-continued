package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.IRMBAttack;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.idle_transfiguration.base.TransfiguredSoulEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class SoulDecimation extends Ability implements Ability.IToggled, Ability.IAttack, IRMBAttack {
   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.TOGGLED;
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public void run(LivingEntity owner) {
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (cap.hasToggled((Ability)JJKAbilities.IDLE_TRANSFIGURATION.get())) {
         cap.toggle((Ability)JJKAbilities.IDLE_TRANSFIGURATION.get());
      }

      return super.isTriggerable(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.0F;
   }

   @Override
   public void onEnabled(LivingEntity owner) {
   }

   @Override
   public void onDisabled(LivingEntity owner) {
   }

   @Override
   public LivingEntity hitEntity(LivingEntity owner) {
      LivingEntity domainTarget = RotationUtil.getExpandedLookAt(owner, this.getRange() * 20.0);
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      DomainExpansionEntity domain = cap.getSummonByClass(DomainExpansionEntity.class);
      if (domain != null && domain.sureHitTarget(domainTarget) == domain && domain.ability == JJKAbilities.SELF_EMBODIMENT_OF_PERFECTION.get()) {
         return domainTarget;
      } else {
         return RotationUtil.getLookAtHit(owner, this.getRange()) instanceof EntityHitResult ehr && ehr.getEntity() instanceof LivingEntity target
            ? target
            : null;
      }
   }

   @Override
   public void perform(LivingEntity owner, LivingEntity target) {
      MobEffectInstance existing = target.getEffect((MobEffect)JJKEffects.TRANSFIGURED_SOUL.get());
      int amplifier = 0;
      if (existing != null) {
         amplifier = existing.getAmplifier();
      }

      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      int required = 1;
      float cost = Math.min(4, amplifier) * 50;
      float ownerXP = cap.getExperience();
      ISorcererData targetCap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
      float targetXP = targetCap != null ? targetCap.getExperience() : target.getMaxHealth();
      float ratio = Math.max(20.0F, ownerXP) / Math.max(20.0F, targetXP);
      if ((target instanceof TransfiguredSoulEntity || amplifier >= required)
         && cap.getEnergy() >= cost
         && target.hurt(JJKDamageSources.soulAttack(owner), target.getMaxHealth() * Math.min(6.0F, amplifier * 1.5F) / 10.0F * ratio)) {
         target.removeEffect((MobEffect)JJKEffects.TRANSFIGURED_SOUL.get());
         cap.useEnergy(cost);
      }
   }

   @Override
   public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
      if (owner.level().isClientSide) {
         return false;
      } else if (!HelperMethods.isMelee(source)) {
         return false;
      } else {
         this.perform(owner, target);
         return true;
      }
   }
}
