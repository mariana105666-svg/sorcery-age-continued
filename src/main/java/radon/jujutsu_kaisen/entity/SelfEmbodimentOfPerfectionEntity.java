package radon.jujutsu_kaisen.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.base.DomainExpansionCenterEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;

public class SelfEmbodimentOfPerfectionEntity extends DomainExpansionCenterEntity {
   private static final RawAnimation ACTIVATE = RawAnimation.begin().thenPlay("misc.activate");
   private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("misc.idle");
   private static final int ACTIVATION_DURATION = 45;

   public SelfEmbodimentOfPerfectionEntity(EntityType<?> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public SelfEmbodimentOfPerfectionEntity(DomainExpansionEntity domain) {
      super((EntityType<?>)JJKEntities.SELF_EMBODIMENT_OF_PERFECTION.get(), domain);
   }

   private PlayState activateIdlePredicate(AnimationState<SelfEmbodimentOfPerfectionEntity> animationState) {
      return animationState.setAndContinue(this.getTime() <= 45 ? ACTIVATE : IDLE);
   }

   @Override
   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Activate/Idle", this::activateIdlePredicate)});
   }
}
