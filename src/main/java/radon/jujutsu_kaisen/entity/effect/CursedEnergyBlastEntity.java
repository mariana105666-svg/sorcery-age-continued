package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.RotationUtil;

public class CursedEnergyBlastEntity extends JujutsuProjectile {
   public static final int DURATION = 25;
   private static final float DAMAGE = 7.0F;
   public static final float RANGE = (float)Math.pow(25.0, 0.5) * 8.0F;

   public CursedEnergyBlastEntity(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public CursedEnergyBlastEntity(LivingEntity owner, float power) {
      super((EntityType<? extends Projectile>)JJKEntities.CURSED_ENERGY_BLAST.get(), owner.level(), owner, power);
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getY() + owner.getBbHeight() / 2.0F - this.getBbHeight() / 2.0F, owner.getZ()));
   }

   @NotNull
   public EntityDimensions getDimensions(@NotNull Pose pPose) {
      float age = this.getTime();
      float scale = (float)Math.pow(age, 0.5) * 8.0F;
      return super.getDimensions(pPose).scale(scale);
   }

   @Override
   public void tick() {
      super.tick();
      this.refreshDimensions();
      if (this.getTime() >= 25) {
         this.discard();
      } else if (!this.level().isClientSide) {
         if (this.getOwner() instanceof LivingEntity owner) {
            for (Entity entity : this.level().getEntities(owner, this.getBoundingBox())) {
               if (!entity.hurt(JJKDamageSources.jujutsuAttack(owner, (Ability)JJKAbilities.CURSED_ENERGY_BLAST.get()), 7.0F * this.getPower())) {
               }
            }
         }
      }
   }

   @Override
   protected boolean isProjectile() {
      return false;
   }
}
