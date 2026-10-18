package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.projectile.MaximumUzumakiProjectile;

public class MaximumUzumaki extends Ability {
   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return target != null
         && !target.isDeadOrDying()
         && owner.hasLineOfSight(target)
         && (
            cap.getType() != JujutsuType.CURSE && !cap.isUnlocked((Ability)JJKAbilities.RCT1.get())
               ? owner.getHealth() / owner.getMaxHealth() < 0.4F
               : owner.getHealth() / owner.getMaxHealth() < 0.9F
         );
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.GHAST_WARN, SoundSource.MASTER, 3.0F, 0.5F);
      owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.MASTER, 2.5F, 1.0F);
      owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.ENDERMAN_SCREAM, SoundSource.MASTER, 2.0F, 0.75F);
      MaximumUzumakiProjectile uzumaki = new MaximumUzumakiProjectile(owner, this.getPower(owner));
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
   public float getCost(LivingEntity owner) {
      return 0.0F;
   }

   @Override
   public int getPointsCost() {
      return (Integer)ConfigHolder.SERVER.maximumUzumakiCost.get();
   }

   @Override
   public boolean isDisplayed(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      CursedTechnique technique = cap.getTechnique();
      return (technique == CursedTechnique.CURSE_MANIPULATION || cap.hasStolen(CursedTechnique.CURSE_MANIPULATION)) && super.isDisplayed(owner);
   }

   @Override
   public Vec2 getDisplayCoordinates() {
      return new Vec2(0.0F, 3.0F);
   }

   @Override
   public int getCooldown() {
      return 600;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
