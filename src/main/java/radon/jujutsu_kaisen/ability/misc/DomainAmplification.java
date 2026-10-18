package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.entity.IDomainBarrier;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public class DomainAmplification extends Ability implements Ability.IToggled {
   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target != null && !target.isDeadOrDying() && !(owner.distanceTo(target) > 10.0) && !JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.WHEEL.get())) {
         if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            return false;
         } else {
            boolean hasDomainUp = false;
            boolean opponentDomainUp = false;

            for (IDomainBarrier domain : VeilHandler.getDomainBarriers((ServerLevel)owner.level(), owner.blockPosition())) {
               for (DomainExpansionEntity d : domain.getClashers()) {
                  if (d.getOwner() == owner) {
                     hasDomainUp = true;
                  } else if (d.getOwner() == target) {
                     opponentDomainUp = true;
                  }
               }
            }

            ISorcererData cap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            ISorcererData ownercap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            return hasDomainUp && !(cap.getExperience() >= ConfigHolder.SERVER.requiredExperienceForExperienced.get())
               ? false
               : (opponentDomainUp || !hasDomainUp) && cap.hasToggled((Ability)JJKAbilities.INFINITY.get());
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.TOGGLED;
   }

   @Override
   public boolean isCursedEnergyColor() {
      return true;
   }

   @Override
   public void run(LivingEntity owner) {
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.4F;
   }

   @Override
   public void onEnabled(LivingEntity owner) {
   }

   @Override
   public void onDisabled(LivingEntity owner) {
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.DOMAIN;
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @Nullable
   @Override
   public Ability getParent(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      CursedTechnique technique = cap.getTechnique();
      return technique != null && technique.getDomain() != null ? technique.getDomain() : (Ability)JJKAbilities.CURSED_ENERGY_FLOW.get();
   }

   @Override
   public Vec2 getDisplayCoordinates() {
      return new Vec2(4.0F, 1.0F);
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public int getPointsCost() {
      return (Integer)ConfigHolder.SERVER.domainAmplificationCost.get();
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class DomainAmplificationForgeEvents {
      @SubscribeEvent
      public static void onLivingHurt(LivingHurtEvent event) {
         if (event.getSource() instanceof JJKDamageSources.JujutsuDamageSource source) {
            LivingEntity victim = event.getEntity();
            if (JJKAbilities.hasToggled(victim, (Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
               Ability ability = source.getAbility();
               if (ability != null) {
                  if (ability.isTechnique()) {
                     event.setAmount(event.getAmount() * 0.75F);
                  }
               }
            }
         }
      }
   }
}
