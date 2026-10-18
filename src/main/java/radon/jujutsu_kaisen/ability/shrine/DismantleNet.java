package radon.jujutsu_kaisen.ability.shrine;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;
import radon.jujutsu_kaisen.util.SorcererUtil;

public class DismantleNet extends Ability {
   private static final int MIN_SIZE = 5;
   private static final int MAX_SIZE = 20;
   private static final int SIZE = 10;
   private static final int STARTUP = 10;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target == null) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         return cap != null && SorcererUtil.getGrade(cap.getExperience()).ordinal() > SorcererGrade.GRADE_1.ordinal()
            ? HelperMethods.RANDOM.nextInt(5) == 0 && target != null
            : false;
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      int size = Math.max(5, Math.min(20, (int)(10.0F * this.getPower(owner))));
      int count = size / 2;
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      Vec3 center = new Vec3(owner.getX(), owner.getEyeY(), owner.getZ()).add(look);
      Vec3 xAxis = owner.getUpVector(1.0F);
      Vec3 yAxis = look.cross(xAxis).normalize();
      float power = this.getPower(owner) * 1.9F;
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

      for (int i = 0; i < count; i++) {
         for (int j = 0; j < count; j++) {
            double xOffset = (i - (count - 1) / 2.0) * size / count;
            double yOffset = (j - (count - 1) / 2.0) * size / count;
            Vec3 position = center.add(xAxis.scale(xOffset)).add(yAxis.scale(yOffset));
            DismantleProjectile horizontal = new DismantleProjectile(owner, power, 0.0F, position, size);
            DismantleProjectile vertical = new DismantleProjectile(owner, power, 90.0F, position, size);
            horizontal.setDuration(20);
            vertical.setDuration(20);
            horizontal.setCanHurt(false);
            vertical.setCanHurt(false);
            cap.delayTickEvent(() -> {
               horizontal.setDeltaMovement(look.scale(5.0));
               vertical.setDeltaMovement(look.scale(5.0));
               horizontal.setCanHurt(true);
               vertical.setCanHurt(true);
            }, 10);
            horizontal.setDeltaMovement(look.scale(0.0));
            vertical.setDeltaMovement(look.scale(0.0));
            owner.level().addFreshEntity(horizontal);
            owner.level().addFreshEntity(vertical);
         }
      }

      if (!owner.level().isClientSide) {
         owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 450.0F;
   }

   @Override
   public int getCooldown() {
      return 600;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.SLASHING;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
