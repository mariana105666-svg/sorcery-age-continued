package radon.jujutsu_kaisen.ability.idle_transfiguration;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.idle_transfiguration.base.TransfiguredSoul;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.idle_transfiguration.TransfiguredSoulNormalEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class TransfiguredSoulNormal extends TransfiguredSoul<TransfiguredSoulNormalEntity> {
   public TransfiguredSoulNormal() {
      super(TransfiguredSoulNormalEntity.class);
   }

   @Override
   public boolean canDisable() {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target == null ? false : HelperMethods.RANDOM.nextInt(5) == 0;
   }

   @Override
   public List<EntityType<?>> getTypes() {
      return List.of((EntityType<?>)JJKEntities.TRANSFIGURED_SOUL_NORMAL.get());
   }

   @Override
   public boolean isTenShadows() {
      return false;
   }

   protected TransfiguredSoulNormalEntity summon(LivingEntity owner) {
      return new TransfiguredSoulNormalEntity(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 50.0F;
   }

   @Override
   public int getSoulCost() {
      return 2;
   }
}
