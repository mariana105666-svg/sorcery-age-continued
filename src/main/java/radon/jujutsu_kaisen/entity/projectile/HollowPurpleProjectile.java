package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.EmittingLightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleAnimator;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class HollowPurpleProjectile extends JujutsuProjectile {
   private static final int DELAY = 40;
   private static final float SPEED = 5.0F;
   private static final int DURATION = 100;
   private static final float DAMAGE = 35.0F;
   private static final float MAX_RADIUS = 15.0F;
   private static final float RADIUS = 1.0F;
   private static final int ANIMATION = 20;

   public HollowPurpleProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public HollowPurpleProjectile(LivingEntity owner, float power) {
      super((EntityType<? extends Projectile>)JJKEntities.HOLLOW_PURPLE.get(), owner.level(), owner, power);
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      EntityUtil.offset(
         this, look, new Vec3(owner.getX(), owner.getEyeY() - this.getBbHeight() / 2.0F, owner.getZ()).add(look.scale(this.getRadius() * 0.5F))
      );
   }

   public float getRadius() {
      return Math.min(15.0F, 1.0F * this.getPower());
   }

   private void hurtEntities() {
      double radius = Math.max(Math.PI, (double)this.getRadius());
      AABB bounds = this.getBoundingBox().inflate(radius * 0.71F);
      if (this.getOwner() instanceof LivingEntity owner) {
         for (Entity entity : this.level().getEntities(owner, bounds)) {
            ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if (entity != this && (entity != owner || cap.hasSelfHit())) {
               if (entity instanceof Projectile) {
                  entity.discard();
               } else {
                  entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.HOLLOW_PURPLE.get()), 35.0F * this.getPower());
               }
            }
         }
      }
   }

   private void breakBlocks() {
      if (this.getOwner() instanceof LivingEntity owner) {
         for (int var19 = 0; var19 < 5.0F; var19++) {
            double radius = Math.max(Math.PI, (double)this.getRadius());
            AABB bounds = this.getBoundingBox().inflate(radius);
            double centerX = bounds.getCenter().x;
            double centerY = bounds.getCenter().y;
            double centerZ = bounds.getCenter().z;

            for (int x = (int)bounds.minX; x <= bounds.maxX; x++) {
               for (int y = (int)bounds.minY; y <= bounds.maxY; y++) {
                  for (int z = (int)bounds.minZ; z <= bounds.maxZ; z++) {
                     BlockPos pos = new BlockPos(x, y, z);
                     BlockState state = this.level().getBlockState(pos);
                     double distance = Math.sqrt(Math.pow(x - centerX, 2.0) + Math.pow(y - centerY, 2.0) + Math.pow(z - centerZ, 2.0));
                     if (distance <= radius
                        && HelperMethods.isDestroyable(
                           this.level(),
                           owner,
                           pos,
                           JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.HOLLOW_PURPLE.get()),
                           35.0F * this.getPower()
                        )) {
                        if (state.getFluidState().isEmpty()) {
                           owner.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 18);
                        } else {
                           this.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @NotNull
   public EntityDimensions getDimensions(@NotNull Pose pPose) {
      return EntityDimensions.fixed(this.getRadius(), this.getRadius());
   }

   @Override
   public boolean canDeflect() {
      return false;
   }

   private void renderBlue(Vec3 center) {
      float radius = (float)Math.max(Math.PI, (double)this.getRadius()) * 0.5F;
      int count = (int)(radius * Math.PI * 2.0);

      for (int i = 0; i < count; i++) {
         double theta = this.random.nextDouble() * Math.PI * 2.0;
         double phi = this.random.nextDouble() * Math.PI;
         double xOffset = radius * 0.75F * Math.sin(phi) * Math.cos(theta);
         double yOffset = radius * 0.75F * Math.sin(phi) * Math.sin(theta);
         double zOffset = radius * 0.75F * Math.cos(phi);
         double x = center.x + xOffset * (radius * 0.1F);
         double y = center.y + yOffset * (radius * 0.1F);
         double z = center.z + zOffset * (radius * 0.1F);
         this.level()
            .addParticle(
               new TravelParticle.TravelParticleOptions(new Vec3(x, y, z).toVector3f(), ParticleColors.DARK_BLUE, radius * 0.2F, 1.0F, true, true, 5),
               true,
               center.x,
               center.y,
               center.z,
               0.0,
               0.0,
               0.0
            );
      }

      for (int i = 0; i < count; i++) {
         double theta = this.random.nextDouble() * Math.PI * 2.0;
         double phi = this.random.nextDouble() * Math.PI;
         double xOffset = radius * 0.5F * Math.sin(phi) * Math.cos(theta);
         double yOffset = radius * 0.5F * Math.sin(phi) * Math.sin(theta);
         double zOffset = radius * 0.5F * Math.cos(phi);
         double x = center.x + xOffset * 0.1F;
         double y = center.y + yOffset * 0.1F;
         double z = center.z + zOffset * 0.1F;
         this.level()
            .addParticle(
               new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.LIGHT_BLUE, radius * 0.1F, 1.0F, true, true, 5),
               true,
               x,
               y,
               z,
               0.0,
               0.0,
               0.0
            );
      }
   }

   private void renderRed(Vec3 center) {
      float radius = (float)Math.max(Math.PI, (double)this.getRadius()) * 0.5F;
      int count = (int)(radius * Math.PI * 2.0);

      for (int i = 0; i < count; i++) {
         double theta = this.random.nextDouble() * Math.PI * 2.0;
         double phi = this.random.nextDouble() * Math.PI;
         double xOffset = radius * 0.75F * Math.sin(phi) * Math.cos(theta);
         double yOffset = radius * 0.75F * Math.sin(phi) * Math.sin(theta);
         double zOffset = radius * 0.75F * Math.cos(phi);
         double x = center.x + xOffset * (radius * 0.1F);
         double y = center.y + yOffset * (radius * 0.1F);
         double z = center.z + zOffset * (radius * 0.1F);
         this.level()
            .addParticle(
               new TravelParticle.TravelParticleOptions(new Vec3(x, y, z).toVector3f(), ParticleColors.DARK_RED, radius * 0.2F, 1.0F, true, true, 5),
               true,
               center.x,
               center.y,
               center.z,
               0.0,
               0.0,
               0.0
            );
      }

      for (int i = 0; i < count; i++) {
         double theta = this.random.nextDouble() * Math.PI * 2.0;
         double phi = this.random.nextDouble() * Math.PI;
         double xOffset = radius * 0.5F * Math.sin(phi) * Math.cos(theta);
         double yOffset = radius * 0.5F * Math.sin(phi) * Math.sin(theta);
         double zOffset = radius * 0.5F * Math.cos(phi);
         double x = center.x + xOffset * 0.1F;
         double y = center.y + yOffset * 0.1F;
         double z = center.z + zOffset * 0.1F;
         this.level()
            .addParticle(
               new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.LIGHT_RED, radius * 0.1F, 1.0F, true, true, 5),
               true,
               x,
               y,
               z,
               0.0,
               0.0,
               0.0
            );
      }
   }

   private void animate() {
      float size = this.getRadius() / (float) Math.PI;
      float fraction = this.getTime() / 20.0F;
      fraction = fraction < 0.5F ? 2.0F * fraction * fraction : fraction;
      float offset = Mth.lerp(fraction, size * 2.0F, 0.0F);
      Entity owner = this.getOwner();
      if (owner != null) {
         Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
         float yaw = owner.getYRot();
         Vec3 right = new Vec3(-Math.sin(Math.toRadians(yaw)), 0.0, Math.cos(Math.toRadians(yaw)));
         Vec3 pos = look.cross(right).normalize().scale(offset);
         Vec3 center = new Vec3(this.getX(), this.getY() + this.getBbHeight() / 2.0F, this.getZ());
         this.renderRed(center.add(pos));
         this.renderBlue(center.subtract(pos));
      }
   }

   private void spawnParticles() {
      if (this.getTime() <= 20) {
         this.animate();
      } else {
         Vec3 center = new Vec3(this.getX(), this.getY() + this.getBbHeight() / 2.0F, this.getZ());
         float radius = (float)Math.max(Math.PI, (double)this.getRadius());
         int count = (int)(radius * Math.PI * 2.0);

         for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0;
            double phi = this.random.nextDouble() * Math.PI;
            double xOffset = radius * 0.75F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.75F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.75F * Math.cos(phi);
            double x = center.x + xOffset * (radius * 0.1F);
            double y = center.y + yOffset * (radius * 0.1F);
            double z = center.z + zOffset * (radius * 0.1F);
            this.level()
               .addParticle(
                  new TravelParticle.TravelParticleOptions(new Vec3(x, y, z).toVector3f(), ParticleColors.DARK_PURPLE, radius * 0.2F, 1.0F, true, true, 5),
                  true,
                  center.x,
                  center.y,
                  center.z,
                  0.0,
                  0.0,
                  0.0
               );
         }

         for (int i = 0; i < count; i++) {
            double theta = this.random.nextDouble() * Math.PI * 2.0;
            double phi = this.random.nextDouble() * Math.PI;
            double xOffset = radius * 0.75F * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * 0.75F * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * 0.75F * Math.cos(phi);
            double x = center.x + xOffset * 0.1F;
            double y = center.y + yOffset * 0.1F;
            double z = center.z + zOffset * 0.1F;
            this.level()
               .addParticle(
                  new TravelParticle.TravelParticleOptions(center.toVector3f(), ParticleColors.LIGHT_PURPLE, radius * 0.2F, 1.0F, true, true, 5),
                  true,
                  x,
                  y,
                  z,
                  0.0,
                  0.0,
                  0.0
               );
         }

         if (this.getTime() < 40) {
            for (int i = 0; i < 4; i++) {
               double theta = HelperMethods.RANDOM.nextDouble() * Math.PI * 2.0;
               double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
               Vec3 direction = new Vec3(Math.sin(phi) * Math.cos(theta), Math.sin(phi) * Math.sin(theta), Math.cos(phi));
               this.level()
                  .addParticle(
                     new EmittingLightningParticle.EmittingLightningParticleOptions(ParticleColors.LIGHT_PURPLE, direction, this.getRadius() * 1.25F, 1, false),
                     true,
                     this.getX(),
                     this.getY() + this.getBbHeight() / 2.0F,
                     this.getZ(),
                     0.0,
                     0.0,
                     0.0
                  );
            }
         } else {
            ParticleAnimator.lightning(this.level(), center, radius * 1.25F, () -> radius, count / 16, 2, ParticleColors.LIGHT_PURPLE);
         }
      }
   }

   @Override
   public void tick() {
      if (this.getTime() == 0) {
         this.playSound((SoundEvent)JJKSounds.HOLLOW_PURPLE.get(), 3.0F, 1.0F);
      }

      super.tick();
      this.refreshDimensions();
      if (this.getTime() >= 100) {
         this.discard();
      } else if (this.getOwner() instanceof LivingEntity owner) {
         this.spawnParticles();
         if (this.getTime() < 40) {
            if (!owner.isAlive()) {
               this.discard();
            } else {
               if (this.getTime() % 5 == 0) {
                  owner.swing(InteractionHand.MAIN_HAND);
               }

               Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
               Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - this.getBbHeight() / 2.0F, owner.getZ())
                  .add(look.scale(this.getRadius() * 0.5F));
               this.setPos(spawn.x, spawn.y, spawn.z);
               double d0 = look.horizontalDistance();
               this.setYRot((float)(Mth.atan2(look.x, look.z) * 180.0F / (float)Math.PI));
               this.setXRot((float)(Mth.atan2(look.y, d0) * 180.0F / (float)Math.PI));
               this.yRotO = this.getYRot();
               this.xRotO = this.getXRot();
            }
         } else {
            if (!this.level().isClientSide) {
               this.hurtEntities();
               this.breakBlocks();
            }

            if (this.getTime() == 40) {
               this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(5.0));
               this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 1.0F, 1.0F);
            }
         }
      }
   }
}
