package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ScissorEntity extends JujutsuProjectile implements GeoEntity {
   private static final float DAMAGE = 20.0F;
   private static final int CUT_DURATION = 5;
   private static final int DELAY = 20;
   private static final double SPEED = 2.5;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final RawAnimation CUT = RawAnimation.begin().thenPlay("misc.cut");
   private LivingEntity target;

   public ScissorEntity(EntityType<? extends Projectile> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public ScissorEntity(LivingEntity owner, float power, LivingEntity target) {
      super((EntityType<? extends Projectile>)JJKEntities.SCISSOR.get(), owner.level(), owner, power);
      this.target = target;
      double offsetX = this.random.nextDouble() * 4.0 - 2.0;
      double offsetZ = this.random.nextDouble() * 4.0 - 2.0;
      this.setPos(target.position().add(offsetX, target.getBbHeight() * 1.5F, offsetZ));
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
   }

   private PlayState cutPredicate(AnimationState<ScissorEntity> animationState) {
      return this.getTime() - 5 > 0 ? animationState.setAndContinue(CUT) : PlayState.STOP;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Cut", this::cutPredicate)});
   }

   protected void onHitEntity(@NotNull EntityHitResult pResult) {
      if (this.getOwner() instanceof LivingEntity owner) {
         pResult.getEntity().hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.SCISSORS.get()), 20.0F * this.getPower());
         this.discard();
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         if (this.target != null && !this.target.isRemoved() && this.target.isAlive()) {
            Vec3 direction = this.target.position().subtract(this.position()).normalize();
            double pitch = Math.asin(direction.y);
            double yaw = Math.atan2(direction.x, direction.z);
            this.setRot((float)Math.toDegrees(yaw), (float)Math.toDegrees(pitch));
            if (this.getTime() == 20) {
               this.setDeltaMovement(this.target.position().subtract(this.position()).normalize().scale(2.5));
            }
         } else {
            this.discard();
         }
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
