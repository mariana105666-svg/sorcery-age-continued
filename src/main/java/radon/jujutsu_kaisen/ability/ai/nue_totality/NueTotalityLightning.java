package radon.jujutsu_kaisen.ability.ai.nue_totality;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.JujutsuLightningEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.NueTotalityEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class NueTotalityLightning extends Ability {
   private static final float DAMAGE = 10.0F;

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
   public boolean isValid(LivingEntity owner) {
      return owner instanceof NueTotalityEntity;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      JujutsuLightningEntity lightning = new JujutsuLightningEntity(owner, 10.0F);
      BlockPos target = owner.blockPosition();

      for (int i = 0; i < owner.level().getMaxBuildHeight() - owner.getY(); i++) {
         BlockState state = owner.level().getBlockState(target.below(i));
         if (state.canOcclude()) {
            target = target.below(i);
            break;
         }
      }

      Vec3 pos = target.getCenter().add((HelperMethods.RANDOM.nextDouble() - 0.5) * 10.0, 0.0, (HelperMethods.RANDOM.nextDouble() - 0.5) * 10.0);
      lightning.setPos(pos);
      owner.level().addFreshEntity(lightning);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 10.0F;
   }
}
