package radon.jujutsu_kaisen.ability.idle_transfiguration;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.idle_transfiguration.base.TransfiguredSoul;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.idle_transfiguration.PolymorphicSoulIsomerEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class PolymorphicSoulIsomer extends TransfiguredSoul<PolymorphicSoulIsomerEntity> {
   public PolymorphicSoulIsomer() {
      super(PolymorphicSoulIsomerEntity.class);
   }

   @Override
   public boolean canDisable() {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target == null ? false : HelperMethods.RANDOM.nextInt(10) == 0;
   }

   @Override
   public List<EntityType<?>> getTypes() {
      return List.of((EntityType<?>)JJKEntities.POLYMORPHIC_SOUL_ISOMER.get());
   }

   @Override
   public boolean isTenShadows() {
      return false;
   }

   protected PolymorphicSoulIsomerEntity summon(LivingEntity owner) {
      return new PolymorphicSoulIsomerEntity(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 200.0F;
   }

   @Override
   public int getSoulCost() {
      return 10;
   }
}
