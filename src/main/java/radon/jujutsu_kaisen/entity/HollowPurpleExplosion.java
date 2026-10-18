package radon.jujutsu_kaisen.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.sound.JJKSounds;

public class HollowPurpleExplosion extends JujutsuProjectile {
   public static final int DURATION = 60;
   private static final float RADIUS = 8.0F;
   private static final float MAX_EXPLOSION = 30.0F;
   private boolean exploded;

   public HollowPurpleExplosion(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
      this.noCulling = true;
   }

   public HollowPurpleExplosion(LivingEntity owner, float power, Vec3 pos) {
      this((EntityType<? extends Projectile>)JJKEntities.HOLLOW_PURPLE_EXPLOSION.get(), owner.level());
      this.setOwner(owner);
      this.setPower(power);
      float radius = 8.0F * this.getPower();
      this.setPos(pos);
   }

   @NotNull
   public Vec3 getDeltaMovement() {
      return Vec3.ZERO;
   }

   @NotNull
   public EntityDimensions getDimensions(@NotNull Pose pPose) {
      float radius = 8.0F * this.getPower();
      return EntityDimensions.fixed(0.1F, 0.1F);
   }

   public boolean isPickable() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public void push(Entity entity) {
   }

   @Override
   public boolean canDeflect() {
      return false;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.tickCount == 1) {
         this.refreshDimensions();
      }

      if (!this.level().isClientSide) {
         if (this.getTime() >= 60) {
            this.discard();
         } else {
            if (this.getTime() - 1 == 0) {
               if (!(this.getOwner() instanceof LivingEntity owner)) {
                  return;
               }

               this.playSound((SoundEvent)JJKSounds.HOLLOW_PURPLE_EXPLOSION.get(), 3.0F, 1.0F);
               float radius = Math.min(30.0F, 8.0F * this.getPower());
               int duration = (int)(radius / 5.0F * 20.0F);
               ExplosionHandler.spawn(
                  this.level().dimension(),
                  this.position().add(0.0, this.getBbHeight() / 2.0F, 0.0),
                  radius,
                  60,
                  this.getPower() * 0.4F,
                  owner,
                  JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.HOLLOW_PURPLE.get()),
                  false
               );
            }
         }
      }
   }
}
