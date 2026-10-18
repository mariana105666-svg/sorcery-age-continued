package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Flamethrower extends Ability implements Ability.IChannelened, Ability.IDurationable {
   private static final float DAMAGE = 13.0F;
   private static final double RANGE = 9.0;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target == null || !owner.hasLineOfSight(target)) {
         return false;
      } else {
         return JJKAbilities.isChanneling(owner, this)
            ? HelperMethods.RANDOM.nextInt(2) != 0
            : HelperMethods.RANDOM.nextInt(3) == 0 && owner.distanceTo(target) <= 9.0;
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      if (owner.level() instanceof ServerLevel level) {
         for (int i = 0; i < 96; i++) {
            double theta = HelperMethods.RANDOM.nextDouble() * 2.0 * Math.PI;
            double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
            double r = HelperMethods.RANDOM.nextDouble() * 4.0;
            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.sin(phi) * Math.sin(theta);
            double z = r * Math.cos(phi);
            Vec3 start = owner.getEyePosition();
            Vec3 end = start.add(look.scale(18.0)).add(x, y, z);
            Vec3 speed = start.subtract(end).scale(0.08333333333333333).reverse();
            level.sendParticles(ParticleTypes.FLAME, start.x, start.y, start.z, 0, speed.x, speed.y, speed.z, 1.0);
         }

         AABB bounds = AABB.ofSize(owner.getEyePosition(), 1.0, 1.0, 1.0).expandTowards(look.scale(9.0)).inflate(1.0);

         for (Entity entity : owner.level().getEntitiesOfClass(LivingEntity.class, bounds, entityx -> entityx != owner && entityx.hasLineOfSight(owner))) {
            if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), 13.0F * this.getPower(owner))) {
               entity.setSecondsOnFire(5);
            }
         }
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 5.0F;
   }

   @Override
   public int getCooldown() {
      return 200;
   }

   @Override
   public int getDuration() {
      return 10;
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
