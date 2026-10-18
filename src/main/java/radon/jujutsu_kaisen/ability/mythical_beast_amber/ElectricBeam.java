package radon.jujutsu_kaisen.ability.mythical_beast_amber;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.entity.effect.ElectricBeamEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class ElectricBeam extends Ability {
   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target == null) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getTechnique() == null && HelperMethods.RANDOM.nextInt(5) == 0 && owner.hasLineOfSight(target) && owner.distanceTo(target) <= 25.0;
      }
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      ElectricBeamEntity blast = new ElectricBeamEntity(owner, this.getPower(owner));
      owner.level().addFreshEntity(blast);
      if (!owner.level().isClientSide) {
         int count = 12 + (int)(owner.getBbWidth() * owner.getBbHeight()) * 16;

         for (int i = 0; i < count; i++) {
            double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 2.0F) - owner.getLookAngle().scale(0.5).x;
            double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * owner.getBbHeight();
            double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 2.0F) - owner.getLookAngle().scale(0.5).z;
            ((ServerLevel)owner.level())
               .sendParticles(
                  new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), 0.2F, 1), x, y, z, 0, 0.0, 0.0, 0.0, 0.0
               );
         }

         owner.level()
            .playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.ELECTRICITY.get(), SoundSource.MASTER, 1.0F, 1.0F);
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 100.0F;
   }

   @Override
   public int getCooldown() {
      return 200;
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      return JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.MYTHICAL_BEAST_AMBER.get()) && super.isValid(owner);
   }

   @Override
   public boolean isDisplayed(LivingEntity owner) {
      return JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.MYTHICAL_BEAST_AMBER.get()) && super.isDisplayed(owner);
   }
}
