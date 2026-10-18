package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.entity.IDomainBarrier;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.IncompleteDomainEntity;
import radon.jujutsu_kaisen.entity.OpenDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;

public class ZeroPointTwoSecondDomainExpansion extends Ability {
   private static final double COMPLETE_DOMAIN_CLEARANCE = 8.0;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.DOMAIN;
   }

   @Override
   public boolean isDisplayed(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      CursedTechnique technique = cap.getTechnique();
      return technique != null && technique.getDomain() != null && super.isDisplayed(owner);
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      CursedTechnique technique = cap.getTechnique();
      if (technique == null) {
         return false;
      } else {
         Ability domain = technique.getDomain();
         return domain != null && !cap.hasToggled(domain) ? domain.isValid(owner) && super.isValid(owner) : false;
      }
   }

   private boolean isNearCompleteDomain(LivingEntity owner) {
      if (!(owner.level() instanceof ServerLevel level)) {
         return false;
      } else {
         AABB var9 = owner.getBoundingBox().inflate(8.0);

         for (IDomainBarrier barrier : VeilHandler.getDomainBarriers(level, var9)) {
            for (DomainExpansionEntity domain : barrier.getClashers()) {
               if (domain != null
                  && !domain.isRemoved()
                  && domain.isAlive()
                  && !domain.isInstant()
                  && !(domain instanceof OpenDomainExpansionEntity)
                  && (!(domain instanceof IncompleteDomainEntity incomplete) || !incomplete.isIncomplete())) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      if (this.isNearCompleteDomain(owner)) {
         return Ability.Status.FAILURE;
      } else {
         if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
            ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            CursedTechnique technique = cap.getTechnique();
            if (technique == null || !(technique.getDomain() instanceof DomainExpansion ability)) {
               return Ability.Status.FAILURE;
            }

            if (ability.getStatus(owner) != Ability.Status.SUCCESS) {
               return Ability.Status.FAILURE;
            }
         }

         return super.isTriggerable(owner);
      }
   }

   @Override
   public void run(LivingEntity owner) {
      if (!owner.level().isClientSide) {
         owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.SPARK.get(), SoundSource.MASTER, 2.0F, 1.0F);
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         CursedTechnique technique = cap.getTechnique();
         if (technique == null || !(technique.getDomain() instanceof DomainExpansion ability)) {
            return;
         }

         AbilityHandler.trigger(owner, ability);
         DomainExpansionEntity domain = cap.getSummonByClass(DomainExpansionEntity.class);
         if (domain == null) {
            return;
         }

         domain.setInstant(true);
         cap.delayTickEvent(() -> {
            for (LivingEntity entity : domain.getAffected()) {
               if (JJKAbilities.hasTrait(entity, Trait.HEAVENLY_RESTRICTION)) {
                  ability.onHitBlock(domain, owner, entity.blockPosition());
               } else {
                  ability.onHitEntity(domain, owner, entity, true);
               }
            }

            cap.delayTickEvent(() -> domain.discard(), 10);
         }, 10);
         if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
            cap.addCooldown(ability);
         }
      }
   }

   @Override
   public int getPointsCost() {
      return (Integer)ConfigHolder.SERVER.zeroPointTwoSecondDomainExpansionCost.get();
   }

   @Nullable
   @Override
   public Ability getParent(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      CursedTechnique technique = cap.getTechnique();
      return technique != null && technique.getDomain() != null ? technique.getDomain() : (Ability)JJKAbilities.SIMPLE_DOMAIN.get();
   }

   @Override
   public Vec2 getDisplayCoordinates() {
      return new Vec2(4.0F, 0.0F);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 250.0F;
   }
}
