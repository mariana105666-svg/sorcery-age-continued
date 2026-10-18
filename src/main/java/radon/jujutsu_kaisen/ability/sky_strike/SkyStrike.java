package radon.jujutsu_kaisen.ability.sky_strike;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.effect.SkyStrikeEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public class SkyStrike extends Ability {
   public static final double RANGE = 50.0;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target != null && this.getTarget(owner) == target;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Nullable
   private LivingEntity getTarget(LivingEntity owner) {
      if (!(RotationUtil.getLookAtHit(owner, 50.0) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target)) {
         return null;
      } else {
         return !owner.canAttack(target) ? null : target;
      }
   }

   @Override
   public void run(LivingEntity owner) {
      if (owner instanceof Player) {
         owner.swing(InteractionHand.MAIN_HAND);
      }

      LivingEntity target = this.getTarget(owner);
      if (target != null) {
         SkyStrikeEntity strike = new SkyStrikeEntity(owner, this.getPower(owner), target.position());
         owner.level().addFreshEntity(strike);
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      LivingEntity target = this.getTarget(owner);
      return target == null ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public int getCooldown() {
      return 40;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 100.0F;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
