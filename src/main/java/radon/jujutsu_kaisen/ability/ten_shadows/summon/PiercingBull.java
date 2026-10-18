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
import radon.jujutsu_kaisen.entity.ten_shadows.PiercingBullEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class PiercingBull extends Summon<PiercingBullEntity> {
   public PiercingBull() {
      super(PiercingBullEntity.class);
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
      float normalcost = 0.7F;
      return this.isTamed(owner) ? normalcost : 250.0F;
   }

   @Override
   public int getCooldown() {
      return 800;
   }

   @Override
   public List<EntityType<?>> getTypes() {
      return List.of((EntityType<?>)JJKEntities.PIERCING_BULL.get());
   }

   protected PiercingBullEntity summon(LivingEntity owner) {
      return new PiercingBullEntity(owner, this.isTamed(owner));
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
   public boolean canDisable() {
      return false;
   }

   @Override
   protected boolean canTame() {
      return true;
   }
}
