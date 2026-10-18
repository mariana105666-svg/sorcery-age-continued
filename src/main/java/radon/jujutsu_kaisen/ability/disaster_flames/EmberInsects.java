package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.projectile.EmberInsectProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class EmberInsects extends Ability {
   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return HelperMethods.RANDOM.nextInt(3) == 0 && target != null;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

      for (int i = 0; i < 12; i++) {
         cap.delayTickEvent(
            () -> {
               owner.swing(InteractionHand.MAIN_HAND);
               EmberInsectProjectile insect = new EmberInsectProjectile(
                  owner,
                  this.getPower(owner),
                  HelperMethods.RANDOM.nextFloat() * (HelperMethods.RANDOM.nextBoolean() ? 1 : -1),
                  HelperMethods.RANDOM.nextFloat() * (HelperMethods.RANDOM.nextBoolean() ? 1 : -1)
               );
               owner.level().addFreshEntity(insect);
            },
            i
         );
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 75.0F;
   }

   @Override
   public int getCooldown() {
      return 240;
   }
}
