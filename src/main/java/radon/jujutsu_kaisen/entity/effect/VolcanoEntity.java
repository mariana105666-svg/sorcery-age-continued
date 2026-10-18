package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VolcanoEntity extends JujutsuProjectile implements GeoEntity {
   public static final int DELAY = 15;
   private static final int DURATION = 70;
   private static final float DAMAGE = 12.0F;
   private static final double RANGE = 20.0;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public VolcanoEntity(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public VolcanoEntity(LivingEntity owner, float power, BlockPos pos, Direction dir) {
      super((EntityType<? extends Projectile>)JJKEntities.VOLCANO.get(), owner.level(), owner, power);
      Vec3 center = pos.relative(dir).getCenter();
      center = center.subtract(dir.getStepX() * 0.5, dir.getStepY() * 0.5, dir.getStepZ() * 0.5);
      float xRot = (float)(Mth.atan2(dir.getStepY(), dir.getStepX()) * 180.0 / (float) Math.PI);
      switch (dir) {
         case UP:
         case DOWN:
            xRot = -xRot;
            break;
         case WEST:
            xRot -= 180.0F;
      }

      this.moveTo(center.x, center.y - this.getBbHeight() / 2.0F, center.z, dir.toYRot(), xRot);
   }

   @NotNull
   public Vec3 getDeltaMovement() {
      return Vec3.ZERO;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getTime() >= 70) {
         this.discard();
      } else if (this.getTime() >= 15) {
         Vec3 look = RotationUtil.getTargetAdjustedLookAngle(this);

         for (int i = 0; i < 96; i++) {
            double theta = HelperMethods.RANDOM.nextDouble() * 2.0 * Math.PI;
            double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
            double r = HelperMethods.RANDOM.nextDouble() * 0.8;
            double x = r * Math.sin(phi) * Math.cos(theta);
            double y = r * Math.sin(phi) * Math.sin(theta);
            double z = r * Math.cos(phi);
            Vec3 start = this.position().add(0.0, this.getBbHeight() / 2.0F, 0.0);
            Vec3 end = start.add(look.scale(20.0)).add(x, y, z);
            Vec3 speed = start.subtract(end).scale(0.08333333333333333).reverse();
            this.level().addParticle(ParticleTypes.FLAME, start.x, start.y, start.z, speed.x, speed.y, speed.z);
         }

         if (this.getOwner() instanceof LivingEntity owner) {
            AABB bounds = this.getBoundingBox().expandTowards(look.scale(20.0)).inflate(3.0);

            for (Entity entity : this.level().getEntities(owner, bounds)) {
               if (entity instanceof LivingEntity living
                  && living.hasLineOfSight(this)
                  && entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.VOLCANO.get()), 12.0F * this.getPower())) {
                  entity.setSecondsOnFire(5);
               }
            }
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
