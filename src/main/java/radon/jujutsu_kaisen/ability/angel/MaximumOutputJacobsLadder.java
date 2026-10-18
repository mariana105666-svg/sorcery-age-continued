package radon.jujutsu_kaisen.ability.angel;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.chant.ChantHandler;
import radon.jujutsu_kaisen.entity.effect.MaximumOutputJacobsLadderEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class MaximumOutputJacobsLadder extends Ability {
   public static final double RANGE = 100.0;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return true;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return HelperMethods.RANDOM.nextInt(3) == 0 && target != null && this.getTarget(owner) == target;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Nullable
   private LivingEntity getTarget(LivingEntity owner) {
      if (!(RotationUtil.getLookAtHit(owner, 100.0) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target)) {
         return null;
      } else {
         return !owner.canAttack(target) ? null : target;
      }
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      LivingEntity target = this.getTarget(owner);
      Vec3 pos;
      if (target == null) {
         BlockHitResult hit = this.getBlockHit(owner, 100.0);
         if (hit == null) {
            return;
         }

         Vec3 topCenter = Vec3.atCenterOf(hit.getBlockPos()).add(0.0, 0.5, 0.0);
         pos = topCenter;
      } else {
         pos = target.position();
      }

      float output = ChantHandler.getOutput(owner, this);
      MaximumOutputJacobsLadderEntity strike = new MaximumOutputJacobsLadderEntity(owner, this.getPower(owner), pos, output);
      owner.level().addFreshEntity(strike);
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      LivingEntity target = this.getTarget(owner);
      BlockHitResult hit = this.getBlockHit(owner, 100.0);
      return target == null && hit == null ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public int getCooldown() {
      return 600;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 450.0F;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
