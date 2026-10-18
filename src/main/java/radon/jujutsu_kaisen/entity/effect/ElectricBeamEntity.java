package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.effect.base.BeamEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ElectricBeamEntity extends BeamEntity {
   public static final double RANGE = 50.0;

   public ElectricBeamEntity(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public ElectricBeamEntity(LivingEntity owner, float power) {
      super((EntityType<? extends Projectile>)JJKEntities.ELECTRIC_BEAM.get(), owner, power);
   }

   @Override
   public boolean canDeflect() {
      return false;
   }

   @Override
   public int getFrames() {
      return 3;
   }

   @Override
   public float getScale() {
      return 1.0F;
   }

   @Override
   protected double getRange() {
      return 50.0;
   }

   @Override
   protected boolean causesElec() {
      return true;
   }

   @Override
   protected float getDamage() {
      return 10.0F;
   }

   @Override
   public int getDuration() {
      return 6;
   }

   @Override
   public int getCharge() {
      return 10;
   }

   @Nullable
   @Override
   protected Ability getSource() {
      return (Ability)JJKAbilities.ELECTRIC_BEAM.get();
   }

   @Override
   protected Vec3 calculateSpawnPos(LivingEntity owner) {
      return new Vec3(owner.getX(), owner.getY() + owner.getBbHeight() * 0.75F - this.getBbHeight() / 2.0F, owner.getZ())
         .add(RotationUtil.getTargetAdjustedLookAngle(owner));
   }
}
