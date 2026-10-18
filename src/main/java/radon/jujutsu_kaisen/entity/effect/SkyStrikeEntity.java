package radon.jujutsu_kaisen.entity.effect;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;

public class SkyStrikeEntity extends JujutsuProjectile {
   private static final float DAMAGE = 17.0F;
   public static final int STRIKE_EXPLOSION = 17;
   private static final int STRIKE_LENGTH = 21;
   private int strikeTimeO;
   private int strikeTime;

   public SkyStrikeEntity(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
      this.noCulling = true;
   }

   public SkyStrikeEntity(LivingEntity owner, float power, Vec3 pos) {
      this((EntityType<? extends Projectile>)JJKEntities.SKY_STRIKE.get(), owner.level());
      this.setOwner(owner);
      this.setPower(power);
      this.setPos(pos.x, pos.y + 1.0625, pos.z);
   }

   public float getStrikeDrawTime(float partialTicks) {
      return this.getActualStrikeTime(partialTicks) / 17.0F;
   }

   public float getStrikeDamageTime(float partialTicks) {
      return (this.getActualStrikeTime(partialTicks) - 17.0F) / 4.0F;
   }

   public boolean isStrikeDrawing(float partialTicks) {
      return this.getActualStrikeTime(partialTicks) < 17.0F;
   }

   public boolean isStriking(float partialTicks) {
      return this.getActualStrikeTime(partialTicks) < 21.0F;
   }

   private float getActualStrikeTime(float delta) {
      return this.strikeTimeO + (this.strikeTime - this.strikeTimeO) * delta;
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double distance) {
      return distance < 1024.0;
   }

   @Override
   public void tick() {
      super.tick();
      this.strikeTimeO = this.strikeTime;
      if (this.strikeTime == 0) {
         this.playSound(SoundEvents.WARDEN_SONIC_CHARGE);
      }

      this.moveDownToGround();
      if (this.strikeTime >= 21 || !this.level().canSeeSkyFromBelowWater(this.blockPosition())) {
         this.discard();
      } else if (this.strikeTime == 17) {
         this.playSound(SoundEvents.WARDEN_SONIC_BOOM);
         this.hurtEntities(3.0);
      }

      this.strikeTime++;
   }

   public void moveDownToGround() {
      HitResult hit = this.getHitResult();
      if (hit.getType() == Type.BLOCK) {
         BlockHitResult blockHit = (BlockHitResult)hit;
         if (blockHit.getDirection() == Direction.UP) {
            BlockState state = this.level().getBlockState(blockHit.getBlockPos());
            if (this.strikeTime > 21 && state != this.level().getBlockState(this.blockPosition().below())) {
               this.discard();
            }

            if (state.getBlock() instanceof SlabBlock && state.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM) {
               this.setPos(this.getX(), blockHit.getBlockPos().getY() + 1.0625F - 0.5F, this.getZ());
            } else {
               this.setPos(this.getX(), blockHit.getBlockPos().getY() + 1.0625F, this.getZ());
            }

            if (this.level() instanceof ServerLevel) {
               ((ServerLevel)this.level()).getChunkSource().broadcast(this, new ClientboundTeleportEntityPacket(this));
            }
         }
      }
   }

   public void hurtEntities(double radius) {
      if (this.getOwner() instanceof LivingEntity owner) {
         AABB var11 = new AABB(
            this.getX() - radius,
            this.getY() - 0.5,
            this.getZ() - radius,
            this.getX() + radius,
            this.getY() + 20.0,
            this.getZ() + radius
         );
         List<Entity> entities = this.level().getEntities(this, var11);
         double radiusSq = radius * radius;
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

         for (Entity entity : entities) {
            if ((entity != owner || cap.hasSelfHit()) && this.getDistanceSqXZToEntity(entity) < radiusSq) {
               entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.SKY_STRIKE.get()), 17.0F * this.getPower());
            }
         }
      }
   }

   public double getDistanceSqXZToEntity(Entity entity) {
      double d0 = this.getX() - entity.getX();
      double d2 = this.getZ() - entity.getZ();
      return d0 * d0 + d2 * d2;
   }

   private HitResult getHitResult() {
      Vec3 startPos = new Vec3(this.getX(), this.getY(), this.getZ());
      Vec3 endPos = new Vec3(this.getX(), this.level().getMinBuildHeight(), this.getZ());
      return this.level().clip(new ClipContext(startPos, endPos, Block.COLLIDER, Fluid.ANY, this));
   }
}
