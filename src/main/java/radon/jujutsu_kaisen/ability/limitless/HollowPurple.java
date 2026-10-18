package radon.jujutsu_kaisen.ability.limitless;

import java.util.List;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.projectile.HollowPurpleProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.SorcererUtil;

public class HollowPurple extends Ability {
   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target == null) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         if (cap == null) {
            return false;
         } else {
            return SorcererUtil.getGrade(cap.getExperience()).ordinal() <= SorcererGrade.GRADE_1.ordinal()
               ? false
               : owner.distanceTo(target) >= 20.0 && HelperMethods.RANDOM.nextInt(3) == 0;
         }
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      HollowPurpleProjectile purple = new HollowPurpleProjectile(owner, this.getPower(owner));
      owner.level().addFreshEntity(purple);
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      cap.setDisarmed(40);
   }

   @Override
   public List<Ability> getRequirements() {
      return List.of((Ability)JJKAbilities.RCT1.get());
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 800.0F;
   }

   @Override
   public int getCooldown() {
      return 600;
   }
}
