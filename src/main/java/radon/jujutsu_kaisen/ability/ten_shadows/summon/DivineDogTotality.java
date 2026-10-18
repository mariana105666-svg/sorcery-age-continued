package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogTotalityEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class DivineDogTotality extends Summon<DivineDogTotalityEntity> {
   public DivineDogTotality() {
      super(DivineDogTotalityEntity.class);
   }

   @Override
   public boolean canDisable() {
      return false;
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return JJKAbilities.hasToggled(owner, this)
         ? target != null && !target.isDeadOrDying() && HelperMethods.RANDOM.nextInt(20) != 0
         : target != null && !target.isDeadOrDying() && HelperMethods.RANDOM.nextInt(10) == 0;
   }

   @Override
   public boolean isTotality() {
      return true;
   }

   @Override
   public List<EntityType<?>> getFusions() {
      return List.of((EntityType<?>)JJKEntities.DIVINE_DOG_WHITE.get(), (EntityType<?>)JJKEntities.DIVINE_DOG_BLACK.get());
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.6F;
   }

   @Override
   public List<EntityType<?>> getTypes() {
      return List.of((EntityType<?>)JJKEntities.DIVINE_DOG_TOTALITY.get());
   }

   protected DivineDogTotalityEntity summon(LivingEntity owner) {
      return new DivineDogTotalityEntity(owner);
   }

   @Override
   public boolean isTenShadows() {
      return true;
   }

   @Override
   public boolean isSpecificFusion() {
      return false;
   }

   @Override
   public int getCooldown() {
      return 600;
   }
}
