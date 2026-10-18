package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.projectile.Projectile;
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
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class FallingBlossomEmotion extends Ability implements Ability.IToggled {
   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      for (IDomainBarrier domain : VeilHandler.getDomainBarriers((ServerLevel)owner.level(), owner.blockPosition())) {
         if (domain.checkSureHitEffect() != null) {
            return true;
         }
      }

      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.TOGGLED;
   }

   @Override
   public void run(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         JJKEffectUtil.addEffect(owner, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), 2, 0, false, false, false));

         for (int var12 = 0; var12 < 16; var12++) {
            double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.25F) - owner.getLookAngle().scale(0.35).x;
            double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * owner.getBbHeight();
            double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.25F) - owner.getLookAngle().scale(0.35).z;
            double speed = owner.getBbHeight() * 0.1F * HelperMethods.RANDOM.nextDouble();
            level.sendParticles(
               new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.FALLING_BLOSSOM_EMOTION, owner.getBbWidth() * 0.5F, 0.2F, 16),
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

         for (Projectile projectile : owner.level().getEntitiesOfClass(Projectile.class, owner.getBoundingBox().inflate(1.0))) {
            if (projectile.getOwner() instanceof LivingEntity living && living.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
               ISorcererData cap = (ISorcererData)living.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               DomainExpansionEntity domain = cap.getSummonByClass(DomainExpansionEntity.class);
               if (domain != null && domain.isAffected(owner) && projectile.getOwner() == living) {
                  projectile.discard();
               }
            }
         }
      }
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public int getCooldown() {
      return 200;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 1.0F;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.DOMAIN;
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Nullable
   @Override
   public Ability getParent(LivingEntity owner) {
      return (Ability)JJKAbilities.CURSED_ENERGY_FLOW.get();
   }

   @Override
   public Vec2 getDisplayCoordinates() {
      return new Vec2(3.0F, 5.0F);
   }

   @Override
   public int getPointsCost() {
      return (Integer)ConfigHolder.SERVER.fallingBlossomEmotionCost.get();
   }

   @Override
   public void onEnabled(LivingEntity owner) {
   }

   @Override
   public void onDisabled(LivingEntity owner) {
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class FallingBlossomEmotionForgeEvents {
      @SubscribeEvent
      public static void onLivingHurt(LivingHurtEvent event) {
         if (event.getSource() instanceof JJKDamageSources.JujutsuDamageSource source) {
            LivingEntity victim = event.getEntity();
            if (JJKAbilities.hasToggled(victim, (Ability)JJKAbilities.FALLING_BLOSSOM_EMOTION.get())) {
               if (source.getDirectEntity() instanceof DomainExpansionEntity) {
                  event.setAmount(event.getAmount() * 0.25F);
               }
            }
         }
      }
   }
}
