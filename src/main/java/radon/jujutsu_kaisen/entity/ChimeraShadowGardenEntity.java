package radon.jujutsu_kaisen.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.entity.base.DomainExpansionCenterEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ChimeraShadowGardenEntity extends DomainExpansionCenterEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public ChimeraShadowGardenEntity(EntityType<?> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public ChimeraShadowGardenEntity(DomainExpansionEntity domain) {
      super((EntityType<?>)JJKEntities.CHIMERA_SHADOW_GARDEN.get(), domain);
      this.setDomain(domain);
   }
}
