package radon.jujutsu_kaisen.entity.effect;

import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.util.HelperMethods;

public class AirFrameEntity extends Entity {
   private static final int DURATION = 8;
   private static final float DAMAGE = 18.0F;
   private static final double RANGE = 9.0;
   @Nullable
   private UUID ownerUUID;
   @Nullable
   private LivingEntity cachedOwner;
   private float power;

   public AirFrameEntity(EntityType<?> pType, Level pLevel) {
      super(pType, pLevel);
      this.noCulling = true;
   }

   public AirFrameEntity(LivingEntity owner, float power) {
      this((EntityType<?>)JJKEntities.AIR_FRAME.get(), owner.level());
      this.setOwner(owner);
      this.power = power;
   }

   protected void defineSynchedData() {
   }

   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (this.ownerUUID != null) {
         pCompound.putUUID("owner", this.ownerUUID);
      }

      pCompound.putFloat("power", this.power);
   }

   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (pCompound.hasUUID("owner")) {
         this.ownerUUID = pCompound.getUUID("owner");
      }

      this.power = pCompound.getFloat("power");
   }

   public float getPower() {
      return this.power;
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      double d0 = this.getBoundingBox().getSize() * 10.0;
      if (Double.isNaN(d0)) {
         d0 = 1.0;
      }

      d0 *= 64.0 * getViewScale();
      return pDistance < d0 * d0;
   }

   public void tick() {
      super.tick();
      LivingEntity owner = this.getOwner();
      if (this.level().isClientSide || owner != null && !owner.isRemoved() && !owner.isDeadOrDying()) {
         super.tick();
         if (this.level().isClientSide) {
            return;
         }

         if (this.tickCount >= 8) {
            if (owner != null) {
               Vec3 center = this.position().add(0.0, this.getBbHeight() / 2.0F, 0.0);
               ((ServerLevel)owner.level()).sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, 1.0, 0.0, 0.0, 1.0);
               ((ServerLevel)owner.level()).sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 0, 1.0, 0.0, 0.0, 1.0);
               owner.level()
                  .playSound(
                     null,
                     center.x,
                     center.y,
                     center.z,
                     SoundEvents.GENERIC_EXPLODE,
                     SoundSource.BLOCKS,
                     4.0F,
                     (1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.2F) * 0.7F
                  );

               for (Entity entity : owner.level().getEntities(null, AABB.ofSize(center, 9.0, 9.0, 9.0))) {
                  ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                  if (entity != owner || cap.hasSelfHit()) {
                     entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.AIR_FRAME.get()), 18.0F * this.getPower());
                  }
               }
            }

            this.discard();
         }
      } else {
         this.discard();
      }
   }

   public void setOwner(@Nullable LivingEntity pOwner) {
      if (pOwner != null) {
         this.ownerUUID = pOwner.getUUID();
         this.cachedOwner = pOwner;
      }
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
         return this.cachedOwner;
      } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
         this.cachedOwner = (LivingEntity)((ServerLevel)this.level()).getEntity(this.ownerUUID);
         return this.cachedOwner;
      } else {
         return null;
      }
   }
}
