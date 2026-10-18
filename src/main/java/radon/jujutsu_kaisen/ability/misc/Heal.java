package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Heal extends Ability implements Ability.IChannelened {
   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return owner.getHealth() < owner.getMaxHealth() * 0.65F;
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   @Override
   public void run(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (!(cap.getEnergy() < cap.getMaxEnergy() * 0.02F)) {
         if (cap.hasTrait(Trait.DEATH_PAINTING)) {
            if (owner instanceof Player player) {
               owner.heal(((Double)ConfigHolder.SERVER.curseHealingAmount.get()).floatValue() * this.getPower(owner) * 0.225F + 0.55F);
            } else {
               owner.heal(((Double)ConfigHolder.SERVER.curseHealingAmount.get()).floatValue() * this.getPower(owner) * 0.19F + 0.075F);
            }
         } else if (owner instanceof Player player) {
            owner.heal(((Double)ConfigHolder.SERVER.curseHealingAmount.get()).floatValue() * this.getPower(owner) * 0.2F + 0.5F);
         } else {
            owner.heal(((Double)ConfigHolder.SERVER.curseHealingAmount.get()).floatValue() * this.getPower(owner) * 0.175F + 0.05F);
         }

         if (owner.level() instanceof ServerLevel level) {
            for (int var7 = 0; var7 < 2; var7++) {
               cap.delayTickEvent(
                  () -> {
                     for (int j = 0; j < 2; j++) {
                        double x = owner.getX()
                           + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.25F)
                           - owner.getLookAngle().scale(0.35).x;
                        double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * owner.getBbHeight();
                        double z = owner.getZ()
                           + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.25F)
                           - owner.getLookAngle().scale(0.35).z;
                        double speed = owner.getBbHeight() * 0.1F * HelperMethods.RANDOM.nextDouble();
                        level.sendParticles(
                           new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.RCT, owner.getBbWidth() * 0.5F, 0.2F, 16),
                           x,
                           y,
                           z,
                           0,
                           0.0,
                           speed,
                           0.0,
                           1.0
                        );
                     }
                  },
                  var7 * 2
               );
            }
         }
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (cap.getEnergy() < 100.0F) {
         return 0.0F;
      } else if (owner.getHealth() < owner.getMaxHealth()) {
         float base = ((Double)ConfigHolder.SERVER.curseHealingAmount.get()).floatValue() * this.getPower(owner) * this.getMultiplier();
         if (cap.hasTrait(Trait.DEATH_PAINTING)) {
            return owner instanceof Player ? base + 6.0F : base + 2.0F;
         } else {
            return owner instanceof Player ? base + 5.0F : base + 1.75F;
         }
      } else {
         return 0.0F;
      }
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.NONE;
   }

   protected float getMultiplier() {
      return 2.5F;
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return cap.getType() == JujutsuType.CURSE && super.isValid(owner);
   }
}
