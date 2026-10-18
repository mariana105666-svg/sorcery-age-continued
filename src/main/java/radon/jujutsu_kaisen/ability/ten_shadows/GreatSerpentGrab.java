package radon.jujutsu_kaisen.ability.ten_shadows;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.GreatSerpentEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class GreatSerpentGrab extends Ability {
   public static final double RANGE = 60.0;
   public LivingEntity enemy = null;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return HelperMethods.RANDOM.nextInt(3) == 0 && target != null && this.getTarget(owner) == target;
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      return !super.isValid(owner)
         ? false
         : JJKAbilities.hasTamed(owner, (EntityType<?>)JJKEntities.GREAT_SERPENT.get())
            && ((Summon)JJKAbilities.GREAT_SERPENT.get()).getStatus(owner) == Ability.Status.SUCCESS;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Nullable
   private LivingEntity getTarget(LivingEntity owner) {
      LivingEntity target = RotationUtil.getExpandedLookAt(owner, 60.0);
      if (target != null) {
         return !owner.canAttack(target) ? null : target;
      } else {
         return null;
      }
   }

   @Override
   public void run(LivingEntity owner) {
      if (!owner.level().isClientSide) {
         LivingEntity target = this.enemy;
         AbilityHandler.trigger(owner, (Ability)JJKAbilities.GREAT_SERPENT.get());
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         GreatSerpentEntity serpent = cap.getSummonByClass(GreatSerpentEntity.class);
         if (serpent != null) {
            serpent.grab(target);
            target = null;
         }
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      LivingEntity target = this.getTarget(owner);
      this.enemy = target;
      return target == null ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.0F;
   }

   @Override
   protected int getCooldown() {
      return 400;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
