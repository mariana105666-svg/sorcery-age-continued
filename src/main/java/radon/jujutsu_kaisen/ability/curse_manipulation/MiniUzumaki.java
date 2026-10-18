package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.projectile.MiniUzumakiProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

public class MiniUzumaki extends Ability {
   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target == null ? false : HelperMethods.RANDOM.nextInt(10) == 0 && owner.hasLineOfSight(target) && owner.distanceTo(target) <= 24.0;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      MiniUzumakiProjectile uzumaki = new MiniUzumakiProjectile(owner, this.getPower(owner));
      owner.level().addFreshEntity(uzumaki);
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      if (!owner.level().isClientSide) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (!cap.hasSummonOfClass(CursedSpirit.class)) {
            return false;
         }
      }

      return super.isValid(owner);
   }

   @Override
   public boolean isDisplayed(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      CursedTechnique technique = cap.getTechnique();
      return (technique == CursedTechnique.CURSE_MANIPULATION || cap.hasStolen(CursedTechnique.CURSE_MANIPULATION)) && super.isDisplayed(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.0F;
   }

   @Override
   public int getPointsCost() {
      return (Integer)ConfigHolder.SERVER.miniUzumakiCost.get();
   }

   @Nullable
   @Override
   public Ability getParent(LivingEntity owner) {
      return (Ability)JJKAbilities.MAXIMUM_UZUMAKI.get();
   }

   @Override
   public Vec2 getDisplayCoordinates() {
      return new Vec2(-1.0F, 3.0F);
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
