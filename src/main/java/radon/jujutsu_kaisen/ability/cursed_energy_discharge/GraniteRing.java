package radon.jujutsu_kaisen.ability.cursed_energy_discharge;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.entity.IDomain;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.effect.GraniteRingEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class GraniteRing extends Ability {
   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target == null ? false : HelperMethods.RANDOM.nextInt(8) == 0 && owner.distanceTo(target) <= 8.0;
   }

   @Override
   public boolean isTechnique() {
      return true;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }

   private static boolean isInsideDomain(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         for (IDomain domain : VeilHandler.getDomains(level, owner.blockPosition())) {
            if (domain instanceof DomainExpansionEntity expansion && !expansion.isRemoved() && expansion.isAlive()) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      return isInsideDomain(owner) ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      if (!owner.level().isClientSide) {
         owner.level().addFreshEntity(new GraniteRingEntity(owner));
         owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.SPARK.get(), SoundSource.MASTER, 1.5F, 0.6F);
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 350.0F;
   }

   @Override
   public int getCooldown() {
      return 600;
   }
}
