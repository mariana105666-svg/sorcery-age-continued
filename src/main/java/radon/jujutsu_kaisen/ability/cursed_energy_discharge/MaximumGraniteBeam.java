package radon.jujutsu_kaisen.ability.cursed_energy_discharge;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.chant.ChantHandler;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.entity.effect.MaximumGraniteBeamEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class MaximumGraniteBeam extends Ability implements Ability.ICharged {
   public static final int MAX_CHARGE = 60;
   private static final float COST = 1000.0F;
   private static final int COOLDOWN = 1200;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return true;
   }

   private float chargeRatio(LivingEntity owner) {
      return Math.min(60, this.getCharge(owner)) / 60.0F;
   }

   private boolean isFullyCharged(LivingEntity owner) {
      return this.getCharge(owner) >= 60;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target == null ? false : HelperMethods.RANDOM.nextInt(20) == 0 && owner.hasLineOfSight(target) && owner.distanceTo(target) <= 40.0;
   }

   @Override
   public boolean isTechnique() {
      return true;
   }

   @Override
   public boolean isChantable() {
      return true;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }

   @Override
   public void run(LivingEntity owner) {
      if (owner instanceof Player && owner.level().isClientSide) {
         ClientWrapper.setOverlayMessage(
            Component.translatable(String.format("chat.%s.charge", "jujutsu_kaisen"), new Object[]{Math.round(this.chargeRatio(owner) * 100.0F)}), false
         );
      }
   }

   @Override
   public boolean onRelease(LivingEntity owner) {
      if (!this.isFullyCharged(owner)) {
         return false;
      } else {
         owner.swing(InteractionHand.MAIN_HAND);
         if (owner.level().isClientSide) {
            return true;
         } else {
            owner.level().addFreshEntity(new MaximumGraniteBeamEntity(owner, this.getPower(owner), ChantHandler.getOutput(owner, this)));
            int count = 48 + (int)(owner.getBbWidth() * owner.getBbHeight()) * 32;

            for (int i = 0; i < count; i++) {
               double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 3.0F);
               double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * owner.getBbHeight();
               double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 3.0F);
               ((ServerLevel)owner.level())
                  .sendParticles(
                     new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), 0.4F, 1.0F, 1),
                     x,
                     y,
                     z,
                     0,
                     0.0,
                     0.0,
                     0.0,
                     0.0
                  );
            }

            owner.level()
               .playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.SPARK.get(), SoundSource.MASTER, 3.0F, 0.5F);
            return true;
         }
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 1000.0F;
   }

   @Override
   public int getCooldown() {
      return 1200;
   }
}
