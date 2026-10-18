package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.WormCurseEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class WormCurseGrab extends Ability {
   public static final double RANGE = 60.0;
   public LivingEntity enemy = null;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return HelperMethods.RANDOM.nextInt(3) == 0 && target != null && this.getTarget(owner) == target;
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      if (!super.isValid(owner)) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.hasCurse((EntityType<?>)JJKEntities.WORM_CURSE.get());
      }
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
      LivingEntity target = this.enemy;
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      AbsorbedCurse curse = cap.getCurse((EntityType<?>)JJKEntities.WORM_CURSE.get());
      if (curse != null && !curse.equals(null) && JJKAbilities.summonCurse(owner, curse, false) instanceof WormCurseEntity worm) {
         worm.grab(target);
         this.enemy = null;
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
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      AbsorbedCurse curse = cap.getCurse((EntityType<?>)JJKEntities.WORM_CURSE.get());
      return curse == null ? 0.0F : JJKAbilities.getCurseCost(curse);
   }

   @Override
   protected int getCooldown() {
      return 200;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
