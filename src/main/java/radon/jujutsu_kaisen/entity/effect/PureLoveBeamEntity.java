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
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.entity.effect.base.BeamEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public class PureLoveBeamEntity extends BeamEntity {
   public static final int FRAMES = 3;
   public static final float SCALE = 2.0F;
   private static final float DAMAGE = 7.0F;
   public static final int CHARGE = 50;
   public static final int DURATION = 100;

   public PureLoveBeamEntity(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public PureLoveBeamEntity(LivingEntity owner, float power) {
      super((EntityType<? extends Projectile>)JJKEntities.PURE_LOVE.get(), owner, power);
   }

   @Override
   protected boolean shouldSwing() {
      return false;
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
      return this.getOwner() instanceof RikaEntity rika ? 2.0F * (rika.isOpen() ? 1.0F : 0.5F) : 2.0F;
   }

   @Override
   protected double getRange() {
      return 40.0;
   }

   @Override
   protected float getDamage() {
      return this.getOwner() instanceof RikaEntity rika ? 7.0F * (rika.isOpen() ? 1.5F : 0.5F) : 7.0F;
   }

   @Override
   public int getDuration() {
      return this.getOwner() instanceof RikaEntity rika ? (int)(100.0F * (rika.isOpen() ? 1.0F : 0.5F)) : 100;
   }

   @Override
   public int getCharge() {
      return 50;
   }

   @Nullable
   @Override
   protected Ability getSource() {
      return (Ability)JJKAbilities.SHOOT_PURE_LOVE.get();
   }

   @Override
   protected Vec3 calculateSpawnPos(LivingEntity owner) {
      return new Vec3(owner.getX(), owner.getEyeY() - 0.2 - this.getBbHeight() / 2.0F, owner.getZ())
         .add(RotationUtil.getTargetAdjustedLookAngle(owner));
   }
}
