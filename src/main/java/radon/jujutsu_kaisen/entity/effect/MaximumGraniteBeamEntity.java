package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.JJKEntities;

public class MaximumGraniteBeamEntity extends GraniteBlastEntity {
   public static final double RANGE = 80.0;
   private static final float WIDTH = 4.0F;
   private static final float DAMAGE = 8.0F;
   private static final int DURATION = 200;
   private static final int WIND_UP = 20;
   private static final float TURN_SPEED = 0.375F;
   private static final int ROOTED = 7;
   private float aimYRot;
   private float aimXRot;
   private boolean aimed;
   private long aimTick = -1L;
   private static final EntityDataAccessor<Float> DATA_OUTPUT = SynchedEntityData.defineId(MaximumGraniteBeamEntity.class, EntityDataSerializers.FLOAT);
   private static final float LOCKED_OUTPUT = 2.0F;

   public MaximumGraniteBeamEntity(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public MaximumGraniteBeamEntity(LivingEntity owner, float power, float output) {
      super((EntityType<? extends Projectile>)JJKEntities.MAXIMUM_GRANITE_BEAM.get(), owner, power, 1.0F);
      this.entityData.set(DATA_OUTPUT, output);
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_OUTPUT, 1.0F);
   }

   private float share() {
      return Mth.clamp((Float)this.entityData.get(DATA_OUTPUT), 0.0F, 2.0F);
   }

   @Override
   public float getScale() {
      return 4.0F * this.share();
   }

   @Override
   protected double getRange() {
      return 80.0;
   }

   @Override
   protected float getBaseDamage() {
      return 8.0F * this.share();
   }

   @Override
   public int getDuration() {
      return 200;
   }

   @Override
   public int getCharge() {
      return 20;
   }

   @Override
   protected double getDestructionRadius() {
      return this.getScale();
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putFloat("output", (Float)this.entityData.get(DATA_OUTPUT));
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      if (pCompound.contains("output")) {
         this.entityData.set(DATA_OUTPUT, pCompound.getFloat("output"));
      }
   }

   @Override
   protected boolean shouldExplode() {
      return false;
   }

   @Override
   protected boolean hasAimAssist() {
      return false;
   }

   private void settleAim(LivingEntity owner) {
      long now = this.level().getGameTime();
      if (this.aimTick != now) {
         this.aimTick = now;
         float wantedY = super.getAimYRot(owner);
         float wantedX = super.getAimXRot(owner);
         if (!this.aimed) {
            this.aimYRot = wantedY;
            this.aimXRot = wantedX;
            this.aimed = true;
         } else if (!(this.share() >= 2.0F)) {
            this.aimYRot = this.aimYRot + Mth.clamp(Mth.wrapDegrees(wantedY - this.aimYRot), -0.375F, 0.375F);
            this.aimXRot = Mth.clamp(this.aimXRot + Mth.clamp(wantedX - this.aimXRot, -0.375F, 0.375F), -90.0F, 90.0F);
         }
      }
   }

   @Override
   protected float getAimYRot(LivingEntity owner) {
      this.settleAim(owner);
      return this.aimYRot;
   }

   @Override
   protected float getAimXRot(LivingEntity owner) {
      this.settleAim(owner);
      return this.aimXRot;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         if (this.getOwner() instanceof LivingEntity owner) {
            owner.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 7, false, false, false));
         }
      }
   }

   @Nullable
   @Override
   protected Ability getSource() {
      return (Ability)JJKAbilities.MAXIMUM_GRANITE_BEAM.get();
   }
}
