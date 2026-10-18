package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;

public class Fly extends Ability implements Ability.IChannelened {
   private static final float SPEED = 0.05F;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return owner.fallDistance > 1.0F;
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
   public boolean usesHands() {
      return false;
   }

   @Override
   public void run(LivingEntity owner) {
      owner.resetFallDistance();
      Vec3 movement = owner.getDeltaMovement();
      owner.setDeltaMovement(movement.x, 0.1F, movement.z);
      float f = owner.xxa * 0.5F;
      float f1 = owner.zza;
      if (f1 <= 0.0F) {
         f1 *= 0.25F;
      }

      owner.moveRelative(0.05F, new Vec3(f, 0.0, f1));
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 1.0F;
   }
}
