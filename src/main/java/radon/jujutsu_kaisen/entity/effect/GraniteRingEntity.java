package radon.jujutsu_kaisen.entity.effect;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.util.RotationUtil;

public class GraniteRingEntity extends Entity {
   public static final double DIAMETER = 16.0;
   public static final double RADIUS = 8.0;
   private static final int DURATION = 500;
   private static final double CONTACT = 0.9;
   public static final double HEIGHT = 4.0;
   private static final float DAMAGE = 2.0F;
   private static final double INNER_KNOCKBACK = 0.25;
   private static final double OUTER_KNOCKBACK = 0.5;
   private static final double CLEARANCE = 0.7;
   private static final int DAMAGE_INTERVAL = 10;
   private static final int SEGMENTS = 64;
   private static final double OVERLAP = 1.05;
   private static final double RIDE_HEIGHT = 1.0;
   private static final int PIECE_LIFETIME = 500;
   @Nullable
   private UUID ownerUUID;
   private final Set<UUID> caught = new HashSet<>();

   public GraniteRingEntity(EntityType<?> pType, Level pLevel) {
      super(pType, pLevel);
      this.noCulling = true;
   }

   public GraniteRingEntity(LivingEntity owner) {
      this((EntityType<?>)JJKEntities.GRANITE_RING.get(), owner.level());
      this.ownerUUID = owner.getUUID();
      this.moveTo(owner.getX(), owner.getY(), owner.getZ(), owner.getYRot(), 0.0F);
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.ownerUUID != null && this.level() instanceof ServerLevel level) {
         return level.getEntity(this.ownerUUID) instanceof LivingEntity owner ? owner : null;
      } else {
         return null;
      }
   }

   public static boolean isCaged(LivingEntity entity) {
      for (GraniteRingEntity ring : entity.level().getEntitiesOfClass(GraniteRingEntity.class, entity.getBoundingBox().inflate(16.0))) {
         if (!ring.isRemoved() && ring.contains(entity)) {
            return true;
         }
      }

      return false;
   }

   public static boolean suppresses(LivingEntity owner, Ability ability) {
      if (ability == JJKAbilities.PUNCH.get() || ability.isMelee()) {
         return false;
      } else {
         return !ability.isTechnique() && !ability.isDomain() ? false : isCaged(owner);
      }
   }

   public boolean contains(Entity entity) {
      double vertical = entity.getY() - this.getY();
      return !(vertical < -1.0) && !(vertical > 4.0) ? this.horizontalDistance(entity) < 8.0 : false;
   }

   private double horizontalDistance(Entity entity) {
      double dx = entity.getX() - this.getX();
      double dz = entity.getZ() - this.getZ();
      return Math.sqrt(dx * dx + dz * dz);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         if (this.tickCount <= 500 && this.getOwner() != null) {
            if (this.tickCount == 1) {
               this.draw();

               for (LivingEntity entity : this.level()
                  .getEntitiesOfClass(
                     LivingEntity.class,
                     new AABB(
                        this.getX() - 8.0,
                        this.getY() - 1.0,
                        this.getZ() - 8.0,
                        this.getX() + 8.0,
                        this.getY() + 4.0,
                        this.getZ() + 8.0
                     )
                  )) {
                  if (this.contains(entity)) {
                     this.caught.add(entity.getUUID());
                  }
               }
            }

            AABB bounds = new AABB(
               this.getX() - 8.0 - 2.0,
               this.getY() - 1.0,
               this.getZ() - 8.0 - 2.0,
               this.getX() + 8.0 + 2.0,
               this.getY() + 4.0,
               this.getZ() + 8.0 + 2.0
            );

            for (LivingEntity entityx : this.level().getEntitiesOfClass(LivingEntity.class, bounds)) {
               double distance = this.horizontalDistance(entityx);
               if (!(Math.abs(distance - 8.0) > 0.9)) {
                  double vertical = entityx.getY() - this.getY();
                  if (!(vertical < -1.0) && !(vertical > 4.0)) {
                     boolean inside = this.caught.contains(entityx.getUUID());
                     Vec3 outward = distance < 1.0E-4
                        ? new Vec3(1.0, 0.0, 0.0)
                        : new Vec3(entityx.getX() - this.getX(), 0.0, entityx.getZ() - this.getZ()).normalize();
                     Vec3 through = inside ? outward : outward.scale(-1.0);
                     Vec3 motion = entityx.getDeltaMovement();
                     double radial = motion.x * through.x + motion.z * through.z;
                     double strength = inside ? 0.25 : 0.5;
                     if (radial > 0.0) {
                        motion = motion.subtract(through.scale(radial + strength));
                        entityx.setDeltaMovement(motion.x, motion.y, motion.z);
                        entityx.hurtMarked = true;
                     }

                     if (inside ? !(distance <= 8.0) : !(distance >= 8.0)) {
                        double limit = inside ? 7.3 : 8.7;
                        entityx.teleportTo(this.getX() + outward.x * limit, entityx.getY(), this.getZ() + outward.z * limit);
                        if (inside && this.tickCount % 10 == 0) {
                           LivingEntity owner = this.getOwner();
                           if (entityx != owner) {
                              entityx.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.GRANITE_RING.get()), 2.0F);
                           }
                        }
                     }
                  }
               }
            }
         } else {
            this.discard();
         }
      }
   }

   private void draw() {
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         Vec3 head = new Vec3(owner.getX(), owner.getY() + owner.getBbHeight(), owner.getZ()).add(RotationUtil.getTargetAdjustedLookAngle(owner));
         float baseYaw = (float)((RotationUtil.getTargetAdjustedYRot(owner) + 90.0F) * Math.PI / 180.0);
         float basePitch = (float)(-RotationUtil.getTargetAdjustedXRot(owner) * Math.PI / 180.0);
         double step = 0.09817477042468103;
         double chord = 16.0 * Math.sin(step / 2.0) * 1.05;

         for (int i = 0; i < 64; i++) {
            double angle = i * step;
            Vec3 point = this.position().add(Math.cos(angle) * 8.0, 1.0, Math.sin(angle) * 8.0);
            double ahead = angle + step;
            Vec3 next = this.position().add(Math.cos(ahead) * 8.0, 1.0, Math.sin(ahead) * 8.0);
            Vec3 direction = next.subtract(point).normalize();
            float yaw = (float)(Math.atan2(direction.z, direction.x) - baseYaw);
            float pitch = -basePitch;
            GraniteBlastVolleyEntity piece = new GraniteBlastVolleyEntity(owner, 1.0F, yaw, pitch, 0.0F, 0.0F, point.subtract(head), chord, true, true);
            piece.setLifetime(500);
            this.level().addFreshEntity(piece);
         }
      }
   }

   protected void defineSynchedData() {
   }

   protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (pCompound.hasUUID("owner")) {
         this.ownerUUID = pCompound.getUUID("owner");
      }
   }

   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (this.ownerUUID != null) {
         pCompound.putUUID("owner", this.ownerUUID);
      }
   }

   public boolean shouldRenderAtSqrDistance(double distance) {
      return true;
   }

   @NotNull
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }
}
