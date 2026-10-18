package radon.jujutsu_kaisen.ability.shrine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Dismantle extends Ability implements Ability.IChannelened, Ability.IDurationable, Ability.IDomainAttack {
   public static final float SPEED = 5.0F;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target == null) {
         return false;
      } else {
         return JJKAbilities.isChanneling(owner, this) ? HelperMethods.RANDOM.nextInt(1) != 0 : HelperMethods.RANDOM.nextInt(2) == 0;
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   @Override
   public void run(LivingEntity owner) {
      if (this.getCharge(owner) % 2 == 0) {
         owner.swing(InteractionHand.MAIN_HAND);
         if (owner.level().isClientSide) {
            return;
         }

         DismantleProjectile dismantle = new DismantleProjectile(
            owner, this.getPower(owner), (owner.isShiftKeyDown() ? 90.0F : 0.0F) + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 60.0F
         );
         dismantle.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(5.0));
         owner.level().addFreshEntity(dismantle);
         owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
      }
   }

   @Override
   public void performEntity(LivingEntity owner, LivingEntity target, DomainExpansionEntity domain) {
      float power = this.getPower(owner) * Math.min(0.6F, DomainExpansion.getStrength(owner, false));
      DismantleProjectile dismantle = new DismantleProjectile(
         owner, power, (HelperMethods.RANDOM.nextFloat() - 0.5F) * 360.0F, target.position(), HelperMethods.RANDOM.nextInt(3, 13), true, false
      );
      dismantle.setDomain(true);
      owner.level().addFreshEntity(dismantle);
      if (!(owner.level() instanceof ServerLevel level)) {
         ;
      }
   }

   @Override
   public void performBlock(LivingEntity owner, DomainExpansionEntity domain, BlockPos pos) {
      if (owner.level() instanceof ServerLevel level) {
         level.sendParticles((SimpleParticleType)JJKParticles.DISMANTLE.get(), pos.getX(), pos.getY(), pos.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
         level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), (SoundEvent)JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 18.0F;
   }

   @Override
   public int getCooldown() {
      return 100;
   }

   @Override
   public int getDuration() {
      return 5;
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
