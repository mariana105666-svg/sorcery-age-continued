package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.BetterSmokeParticle;
import radon.jujutsu_kaisen.client.particle.FireParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.ParticleUtil;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FireballProjectile extends JujutsuProjectile implements GeoEntity {
   private static final RawAnimation SPIN = RawAnimation.begin().thenLoop("misc.spin");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final float DAMAGE = 30.0F;
   private static final float EXPLOSIVE_POWER = 2.5F;
   private static final float MAX_EXPLOSION = 15.0F;
   private static final float SPEED = 5.0F;
   public static final int DELAY = 20;

   public FireballProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public FireballProjectile(LivingEntity owner, float power) {
      super((EntityType<? extends Projectile>)JJKEntities.FIREBALL.get(), owner.level(), owner, power);
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - this.getBbHeight() / 2.0F, owner.getZ()).add(look));
   }

   protected void onHitEntity(@NotNull EntityHitResult pResult) {
      super.onHitEntity(pResult);
      Entity entity = pResult.getEntity();
      if (this.getOwner() instanceof LivingEntity owner) {
         ISorcererData var5 = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (entity != owner || var5.hasSelfHit()) {
            entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.FIREBALL.get()), 30.0F * this.getPower());
         }
      }
   }

   protected void onHit(@NotNull HitResult result) {
      super.onHit(result);
      if (!this.level().isClientSide) {
         this.playSound((SoundEvent)JJKSounds.FLAME_EXPLOSION.get(), 3.0F, 1.0F);
         Vec3 location = result.getLocation();
         Vec3 center = new Vec3(location.x, location.y + this.getBbHeight() / 2.0F, location.z);
         int pillarCount = (int)(this.getFlamePillarRadius() * Math.PI * 2.0) * 8;

         for (int i = 0; i < pillarCount; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0;
            double phi = this.random.nextDouble() * Math.PI;
            double xOffset = this.getFlamePillarRadius() * Math.sin(phi) * Math.cos(theta);
            double yOffset = this.getFlamePillarRadius() * Math.sin(phi) * Math.sin(theta);
            double zOffset = this.getFlamePillarRadius() * Math.cos(phi);
            int lifetime = 60;

            for (int j = 0; j < 2; j++) {
               double x = center.x + xOffset * this.getFlamePillarRadius() * this.random.nextDouble();
               double y = center.y + yOffset * (this.getFlamePillarRadius() * 10.0F) * this.random.nextDouble();
               double z = center.z + zOffset * this.getFlamePillarRadius() * this.random.nextDouble();
               Vec3 start = new Vec3(
                  center.x + xOffset * (this.getFlamePillarRadius() * 0.1F),
                  center.y,
                  center.z + zOffset * (this.getFlamePillarRadius() * 0.1F)
               );
               Vec3 end = new Vec3(x, y, z);
               Vec3 speed = start.subtract(end).scale(1.0 / lifetime * 2.0);
               switch (j) {
                  case 0:
                     ParticleUtil.sendParticles(
                        (ServerLevel)this.level(),
                        new FireParticle.FireParticleOptions(this.getFlamePillarRadius() * 0.3F, true, lifetime),
                        true,
                        start.x,
                        start.y,
                        start.z,
                        speed.x,
                        speed.y,
                        speed.z
                     );
                     break;
                  case 1:
                     ParticleUtil.sendParticles(
                        (ServerLevel)this.level(),
                        new BetterSmokeParticle.BetterSmokeParticleOptions(this.getFlamePillarRadius() * 0.3F, lifetime),
                        true,
                        start.x,
                        start.y,
                        start.z,
                        speed.x,
                        speed.y,
                        speed.z
                     );
               }
            }
         }

         int shockwaveCount = (int)(this.getFlamePillarRadius() * 2.0F * Math.PI * 2.0) * 8;

         for (int i = 0; i < shockwaveCount; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0;
            double phi = this.random.nextDouble() * Math.PI;
            double xOffset = this.getFlamePillarRadius() * 2.0F * Math.sin(phi) * Math.cos(theta);
            double zOffset = this.getFlamePillarRadius() * 2.0F * Math.cos(phi);
            int lifetime = 60;

            for (int j = 0; j < 2; j++) {
               double x = center.x + xOffset * this.getFlamePillarRadius() * 2.0 * this.random.nextDouble();
               double z = center.z + zOffset * this.getFlamePillarRadius() * 2.0 * this.random.nextDouble();
               Vec3 start = new Vec3(
                  center.x + xOffset * (this.getFlamePillarRadius() * 0.1F),
                  center.y,
                  center.z + zOffset * (this.getFlamePillarRadius() * 0.1F)
               );
               Vec3 end = new Vec3(x, start.y, z);
               Vec3 speed = start.subtract(end).scale(1.0 / lifetime * 2.0);
               switch (j) {
                  case 0:
                     ParticleUtil.sendParticles(
                        (ServerLevel)this.level(),
                        new FireParticle.FireParticleOptions(this.getFlamePillarRadius() * 0.3F, true, lifetime),
                        true,
                        start.x,
                        start.y,
                        start.z,
                        speed.x,
                        speed.y,
                        speed.z
                     );
                     break;
                  case 1:
                     ParticleUtil.sendParticles(
                        (ServerLevel)this.level(),
                        new BetterSmokeParticle.BetterSmokeParticleOptions(this.getFlamePillarRadius() * 0.3F, lifetime),
                        true,
                        start.x,
                        start.y,
                        start.z,
                        speed.x,
                        speed.y,
                        speed.z
                     );
               }
            }
         }

         if (this.getOwner() instanceof LivingEntity owner) {
            ExplosionHandler.spawn(
               this.level().dimension(),
               location,
               this.getExplosionRadius(),
               20,
               this.getPower() * 0.45F,
               owner,
               JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.FIREBALL.get()),
               true
            );
         }

         this.discard();
      }
   }

   private float getFlamePillarRadius() {
      return this.getExplosionRadius() * 0.25F;
   }

   private float getExplosionRadius() {
      return Math.min(15.0F, 2.5F * this.getPower());
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getOwner() instanceof LivingEntity owner) {
         for (int i = 0; i < 2; i++) {
            Vec3 dir = RotationUtil.getTargetAdjustedLookAngle(owner).reverse().scale(0.1);
            double dx = dir.x + (this.random.nextDouble() - 0.5) * 0.5;
            double dy = dir.y + (this.random.nextDouble() - 0.5) * 0.5;
            double dz = dir.z + (this.random.nextDouble() - 0.5) * 0.5;
            this.level().addParticle(ParticleTypes.FLAME, this.getX(), this.getY() + this.getBbHeight() / 2.0F, this.getZ(), dx, dy, dz);
         }

         if (this.getTime() < 20) {
            if (this.getTime() % 5 == 0) {
               owner.swing(InteractionHand.MAIN_HAND);
            }

            Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
            Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - this.getBbHeight() / 2.0F, owner.getZ()).add(look);
            this.setPos(spawn.x, spawn.y, spawn.z);
            double d0 = look.horizontalDistance();
            this.setYRot((float)(Mth.atan2(look.x, look.z) * 180.0F / (float)Math.PI));
            this.setXRot((float)(Mth.atan2(look.y, d0) * 180.0F / (float)Math.PI));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
         } else if (this.getTime() == 20) {
            this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(5.0));
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 1.0F, 1.0F);
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Spin", animationState -> animationState.setAndContinue(SPIN))});
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
