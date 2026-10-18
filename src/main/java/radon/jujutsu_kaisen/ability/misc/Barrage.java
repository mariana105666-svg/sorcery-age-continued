package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.item.cursed_tool.PolearmStaffItem;
import radon.jujutsu_kaisen.item.cursed_tool.SlaughterDemonItem;
import radon.jujutsu_kaisen.item.cursed_tool.SteelGauntletItem;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Barrage extends Ability {
   private static final double RANGE = 9.0;
   public static int DURATION = 8;
   private static final int STAGGER = 7;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target != null && !target.isDeadOrDying()) {
         if (owner.distanceTo(target) > 9.0) {
            return false;
         } else {
            return owner.hasEffect((MobEffect)JJKEffects.STAGGER.get()) ? false : HelperMethods.RANDOM.nextInt(5) == 0;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         byte var15 = 2;
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         int duration2 = DURATION;
         if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            duration2 = 10;
         }

         double newRange = 9.0;
         int newStagger = 7;
         Vec3 look1 = RotationUtil.getTargetAdjustedLookAngle(owner);
         int dash = cap.getDash();
         if (dash > 0) {
            newRange++;
            duration2 += 2;
            Vec3 pos = owner.getEyePosition().add(look1);
            owner.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.TRIDENT_RIPTIDE_1, SoundSource.MASTER, 1.0F, 1.5F);
         }

         if (cap.getSpeedStacks() > 0) {
            newRange = 9.0 + cap.getSpeedStacks() / 3.0;
         }

         if (owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof PolearmStaffItem) {
            newRange++;
         }

         if (owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SlaughterDemonItem) {
            newStagger += 4;
         }

         for (int i = 0; i < duration2; i++) {
            double finalNewRange = newRange;
            int finalStagger = newStagger;
            cap.delayTickEvent(
               () -> {
                  owner.swing(InteractionHand.MAIN_HAND, true);
                  Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
                  if (level instanceof ServerLevel) {
                     for (int j = 0; j < 4; j++) {
                        Vec3 pos = owner.getEyePosition().add(look.scale(2.5));
                        Item item = owner.getItemInHand(InteractionHand.MAIN_HAND).getItem();
                        level.sendParticles(
                           !JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING.get())
                                 && !JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.ARM_BLADE.get())
                                 && (!(item instanceof SwordItem) || item instanceof SteelGauntletItem)
                              ? ParticleTypes.CLOUD
                              : ParticleTypes.SWEEP_ATTACK,
                           pos.x + (HelperMethods.RANDOM.nextDouble() - 0.5) * 2.5,
                           pos.y + (HelperMethods.RANDOM.nextDouble() - 0.5) * 2.5,
                           pos.z + (HelperMethods.RANDOM.nextDouble() - 0.5) * 2.5,
                           0,
                           0.0,
                           0.0,
                           0.0,
                           1.0
                        );
                     }

                     for (int j = 0; j < 4; j++) {
                        Vec3 pos = owner.getEyePosition().add(look.scale(2.5));
                        level.sendParticles(
                           ParticleTypes.CRIT,
                           pos.x + (HelperMethods.RANDOM.nextDouble() - 0.5) * 2.5,
                           pos.y + (HelperMethods.RANDOM.nextDouble() - 0.5) * 2.5,
                           pos.z + (HelperMethods.RANDOM.nextDouble() - 0.5) * 2.5,
                           0,
                           0.0,
                           0.0,
                           0.0,
                           1.0
                        );
                     }

                     Vec3 pos = owner.getEyePosition().add(look);
                     owner.level()
                        .playSound(
                           null,
                           pos.x,
                           pos.y,
                           pos.z,
                           SoundEvents.PLAYER_ATTACK_SWEEP,
                           SoundSource.MASTER,
                           1.0F,
                           1.3F + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 0.2F
                        );
                     owner.level()
                        .playSound(
                           null,
                           pos.x,
                           pos.y,
                           pos.z,
                           SoundEvents.BUNDLE_DROP_CONTENTS,
                           SoundSource.MASTER,
                           1.0F,
                           1.9F + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 0.4F
                        );
                  }

                  Vec3 offset = owner.getEyePosition().add(look.scale(finalNewRange / 2.0 - 2.0));

                  for (LivingEntity entity : owner.level()
                     .getEntitiesOfClass(LivingEntity.class, AABB.ofSize(offset, finalNewRange, finalNewRange + 2.0, finalNewRange), entityx -> entityx != owner)) {
                     Vec3 center = entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0);
                     if (!HelperMethods.friendsCheck(owner, entity)) {
                        if (owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SteelGauntletItem) {
                           entity.level()
                              .playSound(null, center.x, center.y, center.z, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.MASTER, 1.5F, 0.8F);
                        }

                        JJKEffectUtil.addEffect(entity, new MobEffectInstance((MobEffect)JJKEffects.STAGGER.get(), finalStagger, 0, false, false, false));
                        if (owner instanceof Player player) {
                           player.attack(entity);
                        } else {
                           owner.doHurtTarget(entity);
                        }

                        entity.invulnerableTime = 0;
                     }
                  }
               },
               i * var15
            );
         }
      }
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      return !(owner instanceof ISorcerer sorcerer && (!sorcerer.hasMeleeAttack() || !sorcerer.hasArms())) && super.isValid(owner);
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      return owner.isUsingItem() ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION) ? 0.0F : 25.0F;
   }

   @Override
   public int getCooldown() {
      return 100;
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean isMelee() {
      return true;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.MELEE;
   }
}
