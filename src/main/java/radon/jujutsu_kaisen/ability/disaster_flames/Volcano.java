package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.effect.VolcanoEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Volcano extends Ability implements Ability.IChannelened, Ability.IDurationable {
   private static final double RANGE = 30.0;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return JJKAbilities.isChanneling(owner, this)
         ? HelperMethods.RANDOM.nextInt(5) != 0
         : HelperMethods.RANDOM.nextInt(5) == 0 && target != null && owner.hasLineOfSight(target);
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      Vec3 start = owner.getEyePosition();
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      Vec3 end = start.add(look.scale(30.0));
      HitResult result = RotationUtil.getHitResult(owner, start, end);
      if (result.getType() == Type.BLOCK) {
         BlockHitResult hitter = this.getBlockHit(owner, 10.0);
         if (hitter != null) {
            Direction dir = hitter.getDirection();
            VolcanoEntity volcano = new VolcanoEntity(owner, this.getPower(owner), hitter.getBlockPos(), dir);
            owner.level().addFreshEntity(volcano);
         }
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      return super.isTriggerable(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 8.0F;
   }

   @Override
   public int getDuration() {
      return 2;
   }

   @Override
   public int getCooldown() {
      return 300;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.FIRE;
   }
}
