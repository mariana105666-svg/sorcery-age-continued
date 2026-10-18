package radon.jujutsu_kaisen.ability.disaster_tides;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.projectile.EelShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.PiranhaShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.SharkShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.base.FishShikigamiProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class FishShikigami extends Ability {
   public static final double RANGE = 40.0;
   public LivingEntity enemy = null;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return HelperMethods.RANDOM.nextInt(3) == 0 && target != null && !target.isDeadOrDying() && this.getTarget(owner) == target;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Nullable
   private LivingEntity getTarget(LivingEntity owner) {
      LivingEntity target = RotationUtil.getExpandedLookAt(owner, 40.0);
      if (target != null) {
         return !owner.canAttack(target) ? null : target;
      } else {
         return null;
      }
   }

   @Override
   public void run(LivingEntity owner) {
      LivingEntity target = this.enemy;
      float xOffset = (HelperMethods.RANDOM.nextFloat() - 0.5F) * 5.0F;
      float yOffset = owner.getBbHeight() + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 5.0F;
      FishShikigamiProjectile[] projectiles = new FishShikigamiProjectile[]{
         new EelShikigamiProjectile(owner, this.getPower(owner) * 1.15F, target, xOffset, yOffset),
         new SharkShikigamiProjectile(owner, this.getPower(owner) * 1.15F, target, xOffset, yOffset),
         new PiranhaShikigamiProjectile(owner, this.getPower(owner) * 1.15F, target, xOffset, yOffset)
      };
      FishShikigamiProjectile selected = projectiles[HelperMethods.RANDOM.nextInt(projectiles.length)];
      owner.level().addFreshEntity(selected);
      this.enemy = null;
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      LivingEntity target = this.getTarget(owner);
      this.enemy = target;
      return target == null ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 100.0F;
   }

   @Override
   public int getCooldown() {
      return 240;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
