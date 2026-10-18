package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.FishCurseEntity;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class FishSwarm extends Ability {
   public static final double RANGE = 80.0;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target != null && !target.isDeadOrDying() ? HelperMethods.RANDOM.nextInt(40) == 0 && this.getTarget(owner) == target : false;
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      if (!super.isValid(owner)) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         return cap == null ? false : cap.hasCurse((EntityType<?>)JJKEntities.FISH_CURSE.get());
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Nullable
   private LivingEntity getTarget(LivingEntity owner) {
      return RotationUtil.getLookAtHit(owner, 80.0) instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity target ? target : null;
   }

   @Override
   public void run(LivingEntity owner) {
      LivingEntity target = this.getTarget(owner);
      if (target != null) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (cap != null) {
            for (int i = 0; i < 5; i++) {
               AbsorbedCurse curse = cap.getCurse((EntityType<?>)JJKEntities.FISH_CURSE.get());
               if (curse == null || curse.equals(null) || !(JJKAbilities.summonCurse(owner, curse, false) instanceof FishCurseEntity fish)) {
                  return;
               }

               Vec3 var9 = owner.getEyePosition();
               fish.setPos(var9.add(-4 + i, 4.0, 1.0));
               Vec3 velocity = target.position().subtract(owner.position()).normalize().scale(2.5);
               fish.setDeltaMovement(velocity);
               fish.setTarget(target);
            }
         }
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      LivingEntity target = this.getTarget(owner);
      return target == null ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (cap == null) {
         return 0.0F;
      } else {
         AbsorbedCurse curse = cap.getCurse((EntityType<?>)JJKEntities.FISH_CURSE.get());
         return curse == null ? 0.0F : JJKAbilities.getCurseCost(curse);
      }
   }

   @Override
   public int getCooldown() {
      return 100;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
