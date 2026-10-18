package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;

public class ForestWaveEntity extends JujutsuProjectile {
   private static final float DAMAGE = 17.0F;
   private static final int DURATION = 80;
   private boolean damage;

   public ForestWaveEntity(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public ForestWaveEntity(LivingEntity owner, float power) {
      super((EntityType<? extends Projectile>)JJKEntities.FOREST_WAVE.get(), owner.level(), owner, power);
   }

   public void push(@NotNull Entity pEntity) {
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public void setDamage(boolean damage) {
      this.damage = damage;
   }

   public void onAddedToWorld() {
      super.onAddedToWorld();
      if (!this.level().isClientSide) {
         if (this.damage) {
            if (this.getOwner() instanceof LivingEntity owner) {
               for (Entity entity : this.level().getEntities(this.getOwner(), this.getBoundingBox().inflate(1.0))) {
                  if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.FOREST_WAVE.get()), 17.0F * this.getPower())) {
                     entity.setDeltaMovement(this.position().subtract(entity.position()).normalize().reverse());
                     entity.hurtMarked = true;
                  }
               }
            }
         }
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getTime() >= 80) {
         this.discard();
      }
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putBoolean("damage", this.damage);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.damage = pCompound.getBoolean("damage");
   }
}
