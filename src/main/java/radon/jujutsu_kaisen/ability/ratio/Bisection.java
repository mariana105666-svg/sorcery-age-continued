package radon.jujutsu_kaisen.ability.ratio;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Bisection extends Ability implements Ability.IChannelened, Ability.IDurationable {
   private static final double RANGE = 50.0;
   private static final int DELAY = 20;
   private static final float DAMAGE = 20.0F;
   private static final int DURATION = 26;
   private static final int STUN = 21;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target == null || !owner.hasLineOfSight(target)) {
         return false;
      } else {
         return JJKAbilities.isChanneling(owner, this) ? HelperMethods.RANDOM.nextInt(5) != 0 : HelperMethods.RANDOM.nextInt(3) == 0;
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   @Override
   public void run(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         ISorcererData var16 = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         Vec3 pos = owner.position();
         int index = this.getCharge(owner);
         JJKEffectUtil.addEffect(owner, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), 2, 1, false, false, false));
         float scale = 1.5F;

         for (int player = 0; player < 12.0F * scale; player++) {
            double x = owner.getX()
               + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.5F * scale)
               - owner.getLookAngle().scale(0.35).x;
            double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * owner.getBbHeight();
            double z = owner.getZ()
               + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.5F * scale)
               - owner.getLookAngle().scale(0.35).z;
            double speed = owner.getBbHeight() * 0.8F * HelperMethods.RANDOM.nextDouble();
            level.sendParticles(
               new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.DARK_BLUE, owner.getBbWidth() * 1.0F, 0.2F, 16),
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

         if (index == 15 && owner instanceof ServerPlayer player) {
            owner.level()
               .playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.COLLAPSE.get(), SoundSource.MASTER, 1.0F, 1.0F);
         }
      }
   }

   @Override
   public void onStop(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         owner.swing(InteractionHand.MAIN_HAND);
         int var13 = this.getCharge(owner);
         LivingEntity target = RotationUtil.getExpandedLookAt(owner, 50.0);
         if (target != null && var13 >= 20 && var13 < 26) {
            Vec3 targeter = target.position();
            owner.teleportTo(targeter.x, targeter.y, targeter.z);
            target.hurt(JJKDamageSources.jujutsuAttack(owner, (Ability)JJKAbilities.BISECTION.get()), 20.0F * (this.getPower(owner) * 0.75F));
            if (target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
               ISorcererData capHit = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               if (!capHit.isChanneling((Ability)JJKAbilities.CURSED_ENERGY_SHIELD.get())) {
                  JJKEffectUtil.addEffect(target, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), 21, 1, false, false, false));
                  JJKEffectUtil.addEffect(target, new MobEffectInstance((MobEffect)JJKEffects.STAGGER.get(), 21, 1, false, false, false));
               }
            } else {
               JJKEffectUtil.addEffect(target, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), 21, 1, false, false, false));
               JJKEffectUtil.addEffect(target, new MobEffectInstance((MobEffect)JJKEffects.STAGGER.get(), 21, 1, false, false, false));
            }

            float scale = 1.5F;
            double x = owner.getX()
               + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.5F * scale)
               - owner.getLookAngle().scale(0.35).x;
            double y = owner.getY() + HelperMethods.RANDOM.nextDouble() + 0.5 * owner.getBbHeight();
            double z = owner.getZ()
               + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.5F * scale)
               - owner.getLookAngle().scale(0.35).z;
            level.sendParticles(ParticleTypes.SONIC_BOOM, x, y, z, 0, 0.0, 0.0, 0.0, 1.0);
            owner.level()
               .playSound(null, target.getX(), target.getY(), target.getZ(), (SoundEvent)JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
         }
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      return super.isTriggerable(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 10.0F;
   }

   @Override
   public int getCooldown() {
      return 240;
   }

   @Override
   public int getDuration() {
      return 5;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.SLASHING;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
