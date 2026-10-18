package radon.jujutsu_kaisen.ability.misc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.item.cursed_tool.PolearmStaffItem;
import radon.jujutsu_kaisen.item.cursed_tool.SlaughterDemonItem;
import radon.jujutsu_kaisen.item.cursed_tool.SteelGauntletItem;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Punch extends Ability implements Ability.ICharged {
   private static final float DAMAGE = 7.5F;
   private static final double RANGE = 7.5;
   private static final double LAUNCH_POWER = 3.0;
   private static final int STAGGER = 0;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (((BooleanValue)owner.level().getGameRules().getRule(GameRules.RULE_MOBGRIEFING)).get() && owner.isInWall()) {
         return true;
      } else if (target == null || target.isDeadOrDying()) {
         return false;
      } else if (owner.hasEffect((MobEffect)JJKEffects.STAGGER.get())) {
         return false;
      } else {
         if (((BooleanValue)owner.level().getGameRules().getRule(GameRules.RULE_MOBGRIEFING)).get()) {
            if (owner.getNavigation().isStuck() && !owner.isInFluidType()) {
               return true;
            }

            HitResult hit = RotationUtil.getLookAtHit(owner, 1.0);
            if (hit.getType() == Type.BLOCK && owner.level().getBlockState(((BlockHitResult)hit).getBlockPos()).getBlock().defaultDestroyTime() > -1.0F) {
               return true;
            }
         }

         return owner.distanceTo(target) > 7.5 ? false : HelperMethods.RANDOM.nextInt(1) == 0;
      }
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public boolean isMelee() {
      return true;
   }

   @Override
   public void run(LivingEntity owner) {
      if (owner instanceof Player && owner.level().isClientSide) {
         ClientWrapper.setOverlayMessage(
            Component.translatable(
               String.format("chat.%s.charge", "jujutsu_kaisen"), new Object[]{Math.round(Math.min(20, this.getCharge(owner)) / 20.0F * 100.0F)}
            ),
            false
         );
      }
   }

   @Override
   public Ability.Status isStillUsable(LivingEntity owner) {
      return super.isStillUsable(owner);
   }

   @Override
   public boolean onRelease(LivingEntity owner) {
      if (owner.hasEffect((MobEffect)JJKEffects.STAGGER.get())) {
         return false;
      } else {
         owner.swing(InteractionHand.MAIN_HAND);
         if (!(owner.level() instanceof ServerLevel level)) {
            return false;
         } else {
            float var19 = Math.min(20, this.getCharge(owner)) / 20.0F;
            ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
            float num = 3.0F;
            if (var19 >= 0.25) {
               num = 4.0F;
            }

            double newRange = 7.5;
            int newStagger = 0;
            int dash = cap.getDash();
            if (dash > 0) {
               num += 2.0F;
               newRange++;
               Vec3 pos = owner.getEyePosition().add(look);
               owner.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.TRIDENT_RIPTIDE_1, SoundSource.MASTER, 1.0F, 1.5F);
            }

            if (owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof PolearmStaffItem) {
               newRange++;
            }

            if (owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SlaughterDemonItem) {
               newStagger = (int)(newStagger + 10.0);
            }

            if (cap.getSpeedStacks() > 0) {
               newRange = 7.5 + cap.getSpeedStacks() / 2.0;
            }

            List<String> targets = new ArrayList<>();
            Level level1 = owner.level();
            boolean[] broke = new boolean[]{false};

            for (int i = 0; i < num; i++) {
               int finalStagger = newStagger;
               int copyIndex = i;
               double finalRange = newRange;
               cap.delayTickEvent(
                  () -> {
                     Vec3 offset = owner.getEyePosition().add(look.scale(finalRange / 2.0 - 2.0));

                     for (LivingEntity entity : owner.level()
                        .getEntitiesOfClass(LivingEntity.class, AABB.ofSize(offset, finalRange, finalRange + 2.0, finalRange), entityx -> entityx != owner)) {
                        broke[0] = true;
                        System.out.println("hurt pig");
                        boolean found = false;

                        for (String target : targets) {
                           if (target == entity.getStringUUID()) {
                              found = true;
                           }
                        }

                        if (!found) {
                           targets.add(entity.getStringUUID());
                           if (!HelperMethods.friendsCheck(owner, entity)) {
                              if (level1 instanceof ServerLevel) {
                                 Vec3 center = entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0);
                                 entity.level()
                                    .playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 0.9F, 1.2F);
                                 entity.level()
                                    .playSound(null, center.x, center.y, center.z, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.MASTER, 1.5F, 1.3F);
                                 if (owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SteelGauntletItem) {
                                    entity.level()
                                       .playSound(null, center.x, center.y, center.z, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.MASTER, 1.5F, 0.8F);
                                 }

                                 ((ServerLevel)level1)
                                    .sendParticles(ParticleTypes.FLASH, center.x, center.y, center.z, 0, 1.0, 0.0, 0.0, 1.0);
                              }

                              int tim = 8;
                              if (var19 == 1.0F) {
                                 if (!cap.hasToggled((Ability)JJKAbilities.RATIO_RULE.get()) && !JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION)) {
                                    cap.moreBlackFlash(true);
                                 }

                                 if (owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SteelGauntletItem) {
                                    tim = 10;
                                 }

                                 cap.delayTickEvent(() -> cap.moreBlackFlash(false), 3);
                              }

                              if (var19 >= 0.65 && var19 <= 0.75 && cap.hasToggled((Ability)JJKAbilities.RATIO_RULE.get())) {
                                 int cooldown = cap.getRemainingCooldown((Ability)JJKAbilities.RATIO_RULE.get());
                                 if (cooldown <= 0) {
                                    cap.moreBlackFlash(true);
                                    tim = 14;
                                    if (owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SteelGauntletItem) {
                                       tim = 16;
                                    }

                                    cap.delayTickEvent(() -> cap.moreBlackFlash(false), 3);
                                 }
                              }

                              float newDMG = 7.5F;
                              if (!(owner instanceof Player player)) {
                                 newDMG /= 1.65F;
                              }

                              if (owner instanceof Player player) {
                                 player.attack(entity);
                              } else {
                                 owner.doHurtTarget(entity);
                              }

                              entity.invulnerableTime = 0;
                              float newPower = (float)(3.0 * (0.8 + 0.4 * var19));
                              newDMG *= (float)(1.0 + 0.75 * var19);
                              float totalDmg = newDMG * this.getPower(owner);
                              if (JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION)) {
                                 if (entity.hurt(
                                    owner instanceof Player player ? owner.damageSources().playerAttack(player) : owner.damageSources().mobAttack(owner), 1.25F * totalDmg
                                 )) {
                                    entity.setDeltaMovement(look.scale(newPower * (1.0F + this.getPower(owner) * 0.1F) * 1.5F).multiply(1.0, 0.25, 1.0));
                                    JJKEffectUtil.addEffect(entity, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), tim, 0, false, false, false));
                                    JJKEffectUtil.addEffect(
                                       entity, new MobEffectInstance((MobEffect)JJKEffects.STAGGER.get(), finalStagger, 0, false, false, false)
                                    );
                                 }
                              } else if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), totalDmg)) {
                                 if (owner instanceof Player player) {
                                    entity.setDeltaMovement(look.scale(newPower * (1.0F + this.getPower(owner) * 0.1F)).multiply(1.0, 0.25, 1.0));
                                 } else {
                                    entity.setDeltaMovement(look.scale(newPower * (1.0F + this.getPower(owner) * 0.1F)).multiply(2.0, 0.5, 2.0));
                                 }

                                 JJKEffectUtil.addEffect(entity, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), tim, 0, false, false, false));
                                 JJKEffectUtil.addEffect(
                                    entity, new MobEffectInstance((MobEffect)JJKEffects.STAGGER.get(), finalStagger, 0, false, false, false)
                                 );
                              }
                           }
                        }
                     }

                     if (var19 == 1.0F && copyIndex == 1 && !broke[0]) {
                        broke[0] = true;
                        float newDMGx = 7.5F;
                        if (!(owner instanceof Player player)) {
                           newDMGx /= 1.65F;
                        }

                        newDMGx *= (float)(1.0 + 0.75 * var19);
                        float totalDMG = newDMGx * this.getPower(owner);
                        Vec3 end = owner.getEyePosition().add(look.scale(3.75));
                        AABB bounds = AABB.ofSize(end, 3.0, 3.0, 3.0).inflate(1.0);
                        BlockPos.betweenClosedStream(bounds)
                           .forEach(
                              posSelf -> {
                                 if (!(posSelf.getCenter().distanceTo(bounds.getCenter()) > 7.5)) {
                                    BlockHitResult blockHit = owner.level()
                                       .clip(new ClipContext(owner.getEyePosition(), posSelf.getCenter(), Block.COLLIDER, Fluid.NONE, owner));
                                    if (blockHit.getType() != Type.BLOCK || blockHit.getBlockPos().equals(posSelf)) {
                                       if (JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION)) {
                                          if (!HelperMethods.isDestroyable(
                                             level,
                                             owner,
                                             posSelf,
                                             owner instanceof Player playerx ? owner.damageSources().playerAttack(playerx) : owner.damageSources().mobAttack(owner),
                                             1.25F * totalDMG
                                          )) {
                                             return;
                                          }
                                       } else if (!HelperMethods.isDestroyable(level, owner, posSelf, JJKDamageSources.jujutsuAttack(owner, this), totalDMG)) {
                                          return;
                                       }

                                       owner.level().destroyBlock(posSelf, true, owner);
                                    }
                                 }
                              }
                           );
                     }
                  },
                  i * 1
               );
            }

            if (dash > 0) {
               Vec3 pos = owner.getEyePosition().add(look.scale(2.5));
               level.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y + 1.0, pos.z, 0, 0.0, 0.0, 0.0, 1.0);
            }

            for (int i = 0; i < 4; i++) {
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

            for (int i = 0; i < 4; i++) {
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
                  1.0F + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 0.2F
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
                  1.6F + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 0.4F
               );
            return true;
         }
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      return owner.isUsingItem() ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      return !(owner instanceof ISorcerer sorcerer && (!sorcerer.hasMeleeAttack() || !sorcerer.hasArms())) && super.isValid(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION) ? 0.0F : 20.0F;
   }

   @Override
   public int getCooldown() {
      return 10;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.MELEE;
   }
}
