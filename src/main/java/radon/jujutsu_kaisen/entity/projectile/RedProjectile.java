package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.HollowPurpleExplosion;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

public class RedProjectile extends JujutsuProjectile {
   private static final double LAUNCH_POWER = 10.0;
   private static final float EXPLOSIVE_POWER = 5.0F;
   private static final float MAX_EXPLOSION = 17.0F;
   public static final int DELAY = 15;
   private static final int DURATION = 60;
   private static final float SPEED = 5.0F;
   private static final float DAMAGE = 26.0F;
   private boolean chanted;

   public RedProjectile(EntityType<? extends Projectile> pType, Level level) {
      super(pType, level);
   }

   public RedProjectile(LivingEntity owner, float power, boolean chanted) {
      super((EntityType<? extends Projectile>)JJKEntities.RED.get(), owner.level(), owner, power);
      this.chanted = chanted;
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - this.getBbHeight() / 2.0F, owner.getZ()).add(look));
   }

   @Override
   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putBoolean("chanted", this.chanted);
   }

   @Override
   protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.chanted = pCompound.getBoolean("chanted");
   }

   protected void onHitEntity(@NotNull EntityHitResult pResult) {
      super.onHitEntity(pResult);
      if (!this.level().isClientSide) {
         Entity entity = pResult.getEntity();
         if (this.getOwner() instanceof LivingEntity owner) {
            ISorcererData var6 = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if (entity != owner || var6.hasSelfHit()) {
               float factor = 1.0F - (this.getTime() - 15.0F) / 60.0F;
               if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.RED.get()), 26.0F * factor * this.getPower())) {
                  entity.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(this).multiply(1.0, 0.25, 1.0).scale(10.0));
                  entity.hurtMarked = true;
               }
            }
         }
      }
   }

   protected void onHit(@NotNull HitResult pResult) {
      super.onHit(pResult);
      if (!this.level().isClientSide) {
         if (this.getOwner() instanceof LivingEntity owner) {
            ISorcererData var6 = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if (pResult.getType() != Type.ENTITY || ((EntityHitResult)pResult).getEntity() != owner || var6.hasSelfHit()) {
               this.playSound((SoundEvent)JJKSounds.RED_EXPLOSION.get(), 3.0F, 1.0F);
               float radius = Math.min(17.0F, 5.0F * this.getPower());
               Vec3 offset = new Vec3(this.getX(), this.getY() + this.getBbHeight() / 2.0F, this.getZ());
               ExplosionHandler.spawn(
                  this.level().dimension(),
                  offset,
                  radius,
                  20,
                  this.getPower() * 0.33F,
                  owner,
                  JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.RED.get()),
                  false,
                  false
               );
               this.discard();
            }
         }
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getOwner() instanceof LivingEntity owner) {
         if (this.getTime() < 15) {
            if (!owner.isAlive()) {
               this.discard();
            } else {
               if (this.getTime() % 5 == 0) {
                  owner.swing(InteractionHand.MAIN_HAND);
               }

               Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
               EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - this.getBbHeight() / 2.0F, owner.getZ()).add(look));
            }
         } else if (this.getTime() >= 60) {
            this.discard();
         } else if (this.getTime() >= 15) {
            if (!this.level().isClientSide && this.chanted) {
               ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

               for (BlueProjectile blue : this.level().getEntitiesOfClass(BlueProjectile.class, this.getBoundingBox().expandTowards(this.getDeltaMovement()))) {
                  if (owner instanceof Player player && player.getAbilities().instabuild
                     || ((Ability)JJKAbilities.HOLLOW_PURPLE.get()).getStatus(owner) == Ability.Status.SUCCESS) {
                     if (owner instanceof ServerPlayer playerx) {
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), playerx);
                     }

                     HollowPurpleExplosion explosion = new HollowPurpleExplosion(
                        owner, this.getPower(), blue.position().add(0.0, blue.getBbHeight() / 2.0F, 0.0)
                     );
                     this.level().addFreshEntity(explosion);
                     cap.addCooldown((Ability)JJKAbilities.HOLLOW_PURPLE.get());
                     blue.discard();
                     this.discard();
                     break;
                  }
               }
            }

            if (this.getTime() == 15) {
               this.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(5.0));
            }
         }
      }
   }
}
