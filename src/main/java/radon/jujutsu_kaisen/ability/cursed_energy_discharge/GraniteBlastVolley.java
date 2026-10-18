package radon.jujutsu_kaisen.ability.cursed_energy_discharge;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.entity.effect.GraniteBlastVolleyEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class GraniteBlastVolley extends Ability {
   private static final int MIN_BEAMS = 8;
   private static final int MAX_BEAMS = 15;
   private static final float MIN_SPREAD = 2.5F;
   private static final float MAX_SPREAD = 18.0F;
   private static final float FORK_CHANCE = 0.35F;
   private static final double SCATTER = 0.7;
   private static final double FORK_MIN_DISTANCE = 4.0;
   private static final double FORK_MAX_DISTANCE = 10.0;
   private static final int FORK_STEPS = 7;
   private static final double FORK_STEP_LENGTH = 1.5;
   private static final double FORK_OVERLAP = 1.6;
   private static final double FORK_LEAD = 0.45;
   private static final double FORK_ROLL = Math.toRadians(45.0);

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target == null ? false : HelperMethods.RANDOM.nextInt(6) == 0 && owner.hasLineOfSight(target) && owner.distanceTo(target) <= 30.0;
   }

   @Override
   public boolean isTechnique() {
      return true;
   }

   @Override
   public boolean worksThroughBurnout() {
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

   private float getSpread(LivingEntity owner) {
      Vec3 start = owner.getEyePosition();
      Vec3 end = start.add(RotationUtil.getTargetAdjustedLookAngle(owner).scale(60.0));
      HitResult result = RotationUtil.getHitResult(owner, start, end);
      double distance = result.getType() == Type.MISS ? 60.0 : result.getLocation().distanceTo(start);
      float ratio = (float)Math.min(1.0, distance / 60.0);
      return 2.5F + 15.5F * ratio;
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      if (!owner.level().isClientSide) {
         float spread = this.getSpread(owner);
         float power = this.getPower(owner);
         int count = HelperMethods.RANDOM.nextInt(8, 16);
         int spawned = 0;

         while (spawned < count) {
            float yaw = this.jitter(spread);
            float pitch = this.jitter(spread);
            Vec3 origin = this.scatter(0.7);
            Vec3 forward = direction(baseYaw(owner) + (float)Math.toRadians(yaw), basePitch(owner) + (float)Math.toRadians(pitch));
            this.segment(owner, power, forward, origin, 60.0, false, false);
            if (++spawned < count && !(HelperMethods.RANDOM.nextFloat() >= 0.35F)) {
               this.fork(owner, power, forward, origin);
               spawned++;
            }
         }

         int particles = 24 + (int)(owner.getBbWidth() * owner.getBbHeight()) * 16;

         for (int i = 0; i < particles; i++) {
            double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 2.0F);
            double y = owner.getY() + owner.getBbHeight() * (0.75 + HelperMethods.RANDOM.nextDouble() * 0.5);
            double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 2.0F);
            ((ServerLevel)owner.level())
               .sendParticles(
                  new CursedEnergyParticle.CursedEnergyParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), 0.2F, 1.0F, 1),
                  x,
                  y,
                  z,
                  0,
                  0.0,
                  0.0,
                  0.0,
                  0.0
               );
         }

         owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.SPARK.get(), SoundSource.MASTER, 1.0F, 0.8F);
      }
   }

   private void fork(LivingEntity owner, float power, Vec3 forward, Vec3 parentOrigin) {
      Vec3 sideways = forward.cross(new Vec3(0.0, 1.0, 0.0));
      if (sideways.lengthSqr() < 1.0E-4) {
         sideways = forward.cross(new Vec3(1.0, 0.0, 0.0));
      }

      sideways = sideways.normalize().scale(HelperMethods.RANDOM.nextBoolean() ? 1.0 : -1.0);
      double roll = (HelperMethods.RANDOM.nextDouble() - 0.5) * 2.0 * FORK_ROLL;
      sideways = sideways.scale(Math.cos(roll)).add(forward.cross(sideways).scale(Math.sin(roll))).normalize();
      double along = 4.0 + HelperMethods.RANDOM.nextDouble() * 6.0;
      Vec3 cursor = parentOrigin.add(forward.scale(along));

      for (int i = 0; i < 7; i++) {
         double t = (i + 0.5) / 7.0;
         Vec3 direction = sideways.scale(1.0 - t).add(forward.scale(t + 0.45)).normalize();
         this.segment(owner, power, direction, cursor, 2.4000000000000004, true, true);
         cursor = cursor.add(direction.scale(1.5));
      }

      this.segment(owner, power, forward, cursor, 60.0, false, true);
   }

   private void segment(LivingEntity owner, float power, Vec3 direction, Vec3 origin, double range, boolean cosmetic, boolean hideStart) {
      float yaw = (float)(Math.atan2(direction.z, direction.x) - baseYaw(owner));
      float pitch = (float)(Math.asin(direction.normalize().y) - basePitch(owner));
      owner.level().addFreshEntity(new GraniteBlastVolleyEntity(owner, power, yaw, pitch, 0.0F, 0.0F, origin, range, cosmetic, hideStart));
   }

   private static float baseYaw(LivingEntity owner) {
      return (float)((RotationUtil.getTargetAdjustedYRot(owner) + 90.0F) * Math.PI / 180.0);
   }

   private static float basePitch(LivingEntity owner) {
      return (float)(-RotationUtil.getTargetAdjustedXRot(owner) * Math.PI / 180.0);
   }

   private static Vec3 direction(float yaw, float pitch) {
      return new Vec3(Math.cos(yaw) * Math.cos(pitch), Math.sin(pitch), Math.sin(yaw) * Math.cos(pitch));
   }

   private float jitter(float spread) {
      return (HelperMethods.RANDOM.nextFloat() - 0.5F) * 2.0F * spread;
   }

   private Vec3 scatter(double amount) {
      return new Vec3(
         (HelperMethods.RANDOM.nextDouble() - 0.5) * 2.0 * amount,
         (HelperMethods.RANDOM.nextDouble() - 0.5) * 2.0 * amount,
         (HelperMethods.RANDOM.nextDouble() - 0.5) * 2.0 * amount
      );
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 500.0F;
   }

   @Override
   public int getCooldown() {
      return 300;
   }
}
