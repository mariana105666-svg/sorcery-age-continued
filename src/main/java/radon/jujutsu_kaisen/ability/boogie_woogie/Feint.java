package radon.jujutsu_kaisen.ability.boogie_woogie;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Feint extends Ability {
   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target == null ? false : HelperMethods.RANDOM.nextInt(3) == 0;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.CLAP.get(), SoundSource.MASTER, 2.0F, 1.0F);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.0F;
   }
}
