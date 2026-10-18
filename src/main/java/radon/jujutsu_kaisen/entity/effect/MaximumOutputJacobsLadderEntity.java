package radon.jujutsu_kaisen.entity.effect;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;

public class MaximumOutputJacobsLadderEntity extends JujutsuProjectile {
   private static final float DAMAGE = 13.0F;
   public static final int HITBOX_START = 20;
   public static final int STRIKE_EXPLOSION = 20;
   private static final int STRIKE_LENGTH = 118;
   private float chanted;
   private int strikeTimeO;
   private int strikeTime;

   public MaximumOutputJacobsLadderEntity(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
      this.noCulling = true;
   }

   public MaximumOutputJacobsLadderEntity(LivingEntity owner, float power, Vec3 pos, float chant) {
      this((EntityType<? extends Projectile>)JJKEntities.MAXIMUM_OUTPUT_JACOBS_LADDER.get(), owner.level());
      this.setOwner(owner);
      this.setPower(power);
      this.chanted = chant;
      this.setPos(pos.x, pos.y + 1.0625, pos.z);
   }

   public float getStrikeDrawTime(float partialTicks) {
      return this.getActualStrikeTime(partialTicks) / 20.0F;
   }

   public float getStrikeDamageTime(float partialTicks) {
      return (this.getActualStrikeTime(partialTicks) - 20.0F) / 98.0F;
   }

   public boolean isStrikeDrawing(float partialTicks) {
      return this.getActualStrikeTime(partialTicks) < 20.0F;
   }

   public boolean isStriking(float partialTicks) {
      return this.getActualStrikeTime(partialTicks) < 118.0F;
   }

   private float getActualStrikeTime(float delta) {
      return this.strikeTimeO + (this.strikeTime - this.strikeTimeO) * delta;
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double distance) {
      return distance < 2048.0;
   }

   @Override
   public void tick() {
      super.tick();
      this.strikeTimeO = this.strikeTime;
      if (this.strikeTime == 0) {
         this.playSound((SoundEvent)JJKSounds.JACOBS_STRONG_CHARGE.get(), 4.0F, 1.0F);
      }

      if (this.strikeTime >= 20) {
         this.hurtEntities(1.824 * this.getPower());
      }

      this.moveDownToGround();
      if (this.strikeTime >= 118) {
         this.discard();
      } else if (this.strikeTime == 20) {
         this.playSound((SoundEvent)JJKSounds.JACOBS_STRONG_FIRE.get(), 4.0F, 1.0F);
      }

      this.strikeTime++;
   }

   public void moveDownToGround() {
      HitResult hit = this.getHitResult();
      if (hit.getType() == Type.BLOCK) {
         BlockHitResult blockHit = (BlockHitResult)hit;
         if (blockHit.getDirection() == Direction.UP) {
            BlockState state = this.level().getBlockState(blockHit.getBlockPos());
            if (this.strikeTime >= 118) {
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
         AABB var12 = new AABB(
            this.getX() - radius,
            this.getY() - 0.5,
            this.getZ() - radius,
            this.getX() + radius,
            this.getY() + 40.0,
            this.getZ() + radius
         );
         List<Entity> entities = this.level().getEntities(this, var12);
         double radiusSq = radius * radius;
         ISorcererData selfCap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

         for (Entity entity : entities) {
            if ((entity != owner || selfCap.hasSelfHit()) && this.getDistanceSqXZToEntity(entity) < radiusSq) {
               int[] mult = new int[]{1};
               entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                  if (cap.hasTrait(Trait.INCARNATED)) {
                     mult[0] = (int)(mult[0] * 2.4);
                  }

                  cap.setDisable((int)(100.0F * this.chanted * (mult[0] - 0.5)));
                  if (cap.hasTechnique(CursedTechnique.BRAIN_TRANSPLANT) && entity instanceof LivingEntity living) {
                     JJKEffectUtil.addEffect(living, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), 15, 0, false, false, false));
                  }

                  if (entity instanceof ServerPlayer player) {
                     PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                  }
               });
               entity.hurt(
                  JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.MAXIMUM_OUTPUT_JACOBS_LADDER.get()),
                  mult[0] * 13.0F * this.getPower()
               );
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
