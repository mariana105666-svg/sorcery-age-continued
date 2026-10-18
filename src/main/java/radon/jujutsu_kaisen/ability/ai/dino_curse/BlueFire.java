package radon.jujutsu_kaisen.ability.ai.dino_curse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.curse.DinoCurseEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class BlueFire extends Ability implements Ability.IChannelened, Ability.IDurationable {
   public static final double RANGE = 32.0;
   private static final float SCALE = 5.0F;
   private static final float DAMAGE = 7.0F;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return owner.isVehicle()
         ? JJKAbilities.isChanneling(owner, this)
         : target != null && !target.isDeadOrDying() && owner.distanceTo(target) <= 32.0 && RotationUtil.hasLineOfSight(calculateSpawnPos(owner), owner, target);
   }

   private static Vec3 calculateSpawnPos(LivingEntity owner) {
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      return new Vec3(owner.getX(), owner.getEyeY() - 1.25, owner.getZ()).add(look);
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      return owner instanceof DinoCurseEntity;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   private Vec3 getCollision(LivingEntity owner, Vec3 from, Vec3 to) {
      BlockHitResult result = owner.level().clip(new ClipContext(from, to, Block.COLLIDER, Fluid.NONE, owner));
      double collidePosX;
      double collidePosY;
      double collidePosZ;
      if (result.getType() != Type.MISS) {
         Vec3 pos = result.getLocation();
         collidePosX = pos.x;
         collidePosY = pos.y;
         collidePosZ = pos.z;
      } else {
         collidePosX = to.x;
         collidePosY = to.y;
         collidePosZ = to.z;
      }

      return new Vec3(collidePosX, collidePosY, collidePosZ);
   }

   public List<Entity> checkCollisions(LivingEntity owner, Vec3 from, Vec3 to, Vec3 collision) {
      List<Entity> entities = new ArrayList<>();
      AABB bounds = new AABB(
            Math.min(from.x, collision.x),
            Math.min(from.y, collision.y),
            Math.min(from.z, collision.z),
            Math.max(from.x, collision.x),
            Math.max(from.y, collision.y),
            Math.max(from.z, collision.z)
         )
         .inflate(5.0);

      for (Entity entity : owner.level().getEntities(owner, bounds)) {
         float pad = entity.getPickRadius() + 0.5F;
         AABB padded = entity.getBoundingBox().inflate(pad, pad, pad);
         Optional<Vec3> hit = padded.clip(from, to);
         if (padded.contains(from)) {
            entities.add(entity);
         } else if (hit.isPresent()) {
            entities.add(entity);
         }
      }

      return entities;
   }

   @Override
   public void run(LivingEntity owner) {
      if (!owner.level().isClientSide) {
         Vec3 spawn = calculateSpawnPos(owner);
         float yaw = (float)((RotationUtil.getTargetAdjustedYRot(spawn, owner) + 90.0F) * Math.PI / 180.0);
         float pitch = (float)(-RotationUtil.getTargetAdjustedXRot(spawn, owner) * Math.PI / 180.0);
         double endPosX = spawn.x + 32.0 * Math.cos(yaw) * Math.cos(pitch);
         double endPosY = spawn.y + 32.0 * Math.sin(pitch);
         double endPosZ = spawn.z + 32.0 * Math.sin(yaw) * Math.cos(pitch);
         Vec3 end = new Vec3(endPosX, endPosY, endPosZ);
         Vec3 collision = this.getCollision(owner, spawn, end);
         Vec3 offset = collision.add(
            (HelperMethods.RANDOM.nextDouble() - 0.5) * 5.0, (HelperMethods.RANDOM.nextDouble() - 0.5) * 5.0, (HelperMethods.RANDOM.nextDouble() - 0.5) * 5.0
         );
         ParticleOptions particle = new TravelParticle.TravelParticleOptions(
            offset.toVector3f(), ParticleColors.BLUE_FIRE, 5.0F * HelperMethods.RANDOM.nextFloat() * 0.5F, 0.5F, true, true, (int)spawn.distanceTo(collision)
         );

         for (int i = 0; i < 160.0F; i++) {
            ((ServerLevel)owner.level())
               .sendParticles(
                  particle,
                  spawn.x + (HelperMethods.RANDOM.nextDouble() - 0.5) * 2.0,
                  spawn.y + (HelperMethods.RANDOM.nextDouble() - 0.5) * 2.0,
                  spawn.z + (HelperMethods.RANDOM.nextDouble() - 0.5) * 2.0,
                  0,
                  0.0,
                  0.0,
                  0.0,
                  1.0
               );
         }

         for (Entity entity : this.checkCollisions(owner, spawn, end, collision)) {
            ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if (!(entity instanceof LivingEntity living && !owner.canAttack(living))
               && (entity != owner || cap.hasSelfHit())
               && entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), 7.0F * this.getPower(owner))) {
               entity.setSecondsOnFire(5);
            }
         }

         double radius = 10.0;
         AABB bounds = new AABB(
            collision.x - radius,
            collision.y - radius,
            collision.z - radius,
            collision.x + radius,
            collision.y + radius,
            collision.z + radius
         );
         double centerX = bounds.getCenter().x;
         double centerY = bounds.getCenter().y;
         double centerZ = bounds.getCenter().z;

         for (int x = (int)bounds.minX; x <= bounds.maxX; x++) {
            for (int y = (int)bounds.minY; y <= bounds.maxY; y++) {
               for (int z = (int)bounds.minZ; z <= bounds.maxZ; z++) {
                  BlockPos pos = new BlockPos(x, y, z);
                  BlockState state = owner.level().getBlockState(pos);
                  double distance = Math.sqrt(Math.pow(x - centerX, 2.0) + Math.pow(y - centerY, 2.0) + Math.pow(z - centerZ, 2.0));
                  if (!(distance > radius)) {
                     if (!state.isSolidRender(owner.level(), pos)) {
                        owner.level().destroyBlock(pos, false);
                     }

                     if (HelperMethods.RANDOM.nextInt(3) == 0
                        && owner.level().getBlockState(pos).isAir()
                        && owner.level().getBlockState(pos.below()).isSolidRender(owner.level(), pos.below())) {
                        owner.level().setBlockAndUpdate(pos, BaseFireBlock.getState(owner.level(), pos));
                     }
                  }
               }
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
      return 100;
   }

   @Override
   public int getDuration() {
      return 40;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.FIRE;
   }
}
