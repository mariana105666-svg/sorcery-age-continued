package radon.jujutsu_kaisen.entity.ten_shadows;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import radon.jujutsu_kaisen.entity.base.JJKPartEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GreatSerpentSegmentEntity extends JJKPartEntity<GreatSerpentEntity> implements GeoEntity {
   public static final ResourceLocation RENDERER = new ResourceLocation("jujutsu_kaisen", "great_serpent_segment");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public GreatSerpentSegmentEntity(GreatSerpentEntity parent) {
      super(parent);
      this.setSize(EntityDimensions.fixed(0.9375F, 0.8125F));
   }

   @Override
   public void tick() {
      super.tick();
      this.collideWithOthers();
   }

   private void collideWithOthers() {
      for (Entity entity : this.level().getEntities(this, this.getBoundingBox())) {
         if (entity.isPushable()) {
            this.collideWithEntity(entity);
         }
      }
   }

   private void collideWithEntity(Entity entity) {
      if (!(entity instanceof GreatSerpentEntity)) {
         entity.push(this);
      }
   }

   @Override
   public ResourceLocation getRenderer() {
      return RENDERER;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public float getStepHeight() {
      return 2.0F;
   }
}
