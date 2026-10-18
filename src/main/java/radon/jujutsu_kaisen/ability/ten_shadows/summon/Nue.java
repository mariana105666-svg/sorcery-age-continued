package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.NueEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Nue extends Summon<NueEntity> {
   public Nue() {
      super(NueEntity.class);
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (!this.isTamed(owner)) {
         return false;
      } else {
         return JJKAbilities.hasToggled(owner, this)
            ? target != null && !target.isDeadOrDying() && HelperMethods.RANDOM.nextInt(1) == 0
            : target != null && !target.isDeadOrDying() && HelperMethods.RANDOM.nextInt(5) == 0;
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return this.isTamed(owner) ? Ability.ActivationType.TOGGLED : Ability.ActivationType.INSTANT;
   }

   @Override
   public float getCost(LivingEntity owner) {
      float normalcost = 0.4F;
      return this.isTamed(owner) ? normalcost : 100.0F;
   }

   @Override
   public List<EntityType<?>> getTypes() {
      return List.of((EntityType<?>)JJKEntities.NUE.get());
   }

   protected NueEntity summon(LivingEntity owner) {
      return new NueEntity(owner, this.isTamed(owner));
   }

   @Override
   public boolean canFly() {
      return true;
   }

   @Override
   public boolean canDie() {
      return true;
   }

   @Override
   public boolean isTenShadows() {
      return true;
   }

   @Override
   protected boolean canTame() {
      return true;
   }

   @Override
   public boolean canDisable() {
      return false;
   }

   @Override
   public int getCooldown() {
      return 600;
   }
}
