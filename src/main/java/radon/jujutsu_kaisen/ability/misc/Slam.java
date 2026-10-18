package radon.jujutsu_kaisen.ability.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ExplosionHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.ten_shadows.RabbitEscapeEntity;
import radon.jujutsu_kaisen.item.cursed_tool.SlaughterDemonItem;
import radon.jujutsu_kaisen.item.cursed_tool.SteelGauntletItem;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Slam extends Ability implements Ability.ICharged {
   private static final double RANGE = 50.0;
   private static final double LAUNCH_POWER = 2.0;
   private static final float MAX_EXPLOSION = 5.0F;
   public static Map<UUID, Float> TARGETS = new HashMap<>();

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target != null && !target.isDeadOrDying() ? owner.hasLineOfSight(target) && HelperMethods.RANDOM.nextInt(8) == 0 : false;
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   private Vec3 getTarget(LivingEntity owner) {
      Vec3 start = owner.getEyePosition();
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      Vec3 end = start.add(look.scale(50.0));
      HitResult result = RotationUtil.getHitResult(owner, start, end);
      return result.getType() == Type.MISS ? end : result.getLocation();
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
   public boolean isValid(LivingEntity owner) {
      return !(owner instanceof ISorcerer sorcerer && (!sorcerer.hasMeleeAttack() || !sorcerer.canJump())) && super.isValid(owner);
   }

   @Override
   public Ability.Status isStillUsable(LivingEntity owner) {
      return owner.hasEffect((MobEffect)JJKEffects.STUN.get()) ? Ability.Status.FAILURE : super.isStillUsable(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION) ? 0.0F : 30.0F;
   }

   @Override
   public int getCooldown() {
      return 200;
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

   @Override
   public boolean usesHands() {
      return false;
   }

   public static void onHitGround(LivingEntity owner, float distance) {
      slamCrater(owner, distance);
   }

   public static void slamCrater(LivingEntity owner, float distance) {
      if (!owner.level().isClientSide) {
         float radius = 5.0F;
         float dmgMult = 0.75F;
         if (JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION)) {
            dmgMult = 0.8F;
            radius = radius * 1.35F + 1.5F;
         }

         if (owner instanceof RabbitEscapeEntity) {
            radius = 1.0F;
            dmgMult = 0.3F;
         }

         boolean steeled = false;
         if (owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SteelGauntletItem) {
            steeled = true;
            radius = radius * 1.2F + 1.0F;
         }

         if (owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if (cap.hasToggled((Ability)JJKAbilities.RATIO_RULE.get())) {
               int cooldown = cap.getRemainingCooldown((Ability)JJKAbilities.RATIO_RULE.get());
               if (cooldown <= 0) {
                  dmgMult = 0.7F;
                  radius *= 1.5F;
               }
            }
         }

         owner.swing(InteractionHand.MAIN_HAND);
         if (!owner.level().isClientSide) {
            for (LivingEntity entity : owner.level()
               .getEntitiesOfClass(LivingEntity.class, AABB.ofSize(owner.position(), radius * 2.0F, radius * 2.0F, radius * 2.0F), entityx -> entityx != owner)) {
               int stunDuration = 40;
               int staggerDuration = 0;
               if (owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SteelGauntletItem) {
                  stunDuration = 50;
               }

               if (owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SlaughterDemonItem) {
                  staggerDuration = 20;
               }

               JJKEffectUtil.addEffect(entity, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), stunDuration, 0, false, false, false));
               JJKEffectUtil.addEffect(entity, new MobEffectInstance((MobEffect)JJKEffects.STAGGER.get(), staggerDuration, 0, false, false, false));
            }

            ExplosionHandler.spawn(
               owner.level().dimension(),
               owner.position(),
               radius,
               5,
               Ability.getPower((Ability)JJKAbilities.SLAM.get(), owner) * dmgMult,
               owner,
               owner instanceof Player player ? owner.damageSources().playerAttack(player) : owner.damageSources().mobAttack(owner),
               false,
               true
            );
         }

         if (steeled) {
            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.MASTER, 3.0F, 0.8F);
         }

         owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.SLAM.get(), SoundSource.MASTER, 1.0F, 1.0F);
         owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.SNIFFER_EGG_CRACK, SoundSource.MASTER, 2.0F, 1.0F);
         TARGETS.remove(owner.getUUID());
      }
   }

   @Override
   public boolean onRelease(LivingEntity owner) {
      if (owner.hasEffect((MobEffect)JJKEffects.STAGGER.get())) {
         return false;
      } else {
         double launchPower = 2.0 + 2.0 * (Math.min(20, this.getCharge(owner)) / 20);
         float checkcharge = Math.min(20, this.getCharge(owner)) / 20.0F;
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (checkcharge >= 0.65F && checkcharge <= 0.75F && cap.hasToggled((Ability)JJKAbilities.RATIO_RULE.get())) {
            int cooldown = cap.getRemainingCooldown((Ability)JJKAbilities.RATIO_RULE.get());
            if (cooldown <= 0) {
               launchPower = 16.0;
               cap.moreBlackFlash(true);
               cap.delayTickEvent(() -> cap.moreBlackFlash(false), 40);
            }
         }

         double reallaunch = launchPower;
         if (!owner.onGround()) {
            if (!owner.level().isClientSide) {
               TARGETS.put(owner.getUUID(), Math.min(20, this.getCharge(owner)) / 20.0F);
            }

            Vec3 target = this.getTarget(owner);
            Vec3 velocity = target.subtract(owner.position()).normalize().scale(reallaunch);
            if (velocity.y > 0.0) {
               velocity = velocity.multiply(1.0, 0.0, 1.0);
            } else {
               velocity = velocity.multiply(1.2, 1.7, 1.2);
            }

            owner.setDeltaMovement(velocity);
            owner.swing(InteractionHand.MAIN_HAND);
            cap.delayTickEvent(() -> TARGETS.remove(owner.getUUID()), 60);
         } else if (owner.isShiftKeyDown()) {
            if (!owner.level().isClientSide) {
               TARGETS.put(owner.getUUID(), Math.min(20, this.getCharge(owner)) / 20.0F);
            }

            owner.swing(InteractionHand.MAIN_HAND);
            slamCrater(owner, 1.0F);
         } else {
            Vec3 direction = new Vec3(0.0, Math.min(3.0, reallaunch * 0.65), 0.0);
            owner.setDeltaMovement(owner.getDeltaMovement().add(direction));
            float power = Math.min(20, this.getCharge(owner)) / 20.0F;
            cap.delayTickEvent(() -> {
               if (!owner.level().isClientSide) {
                  TARGETS.put(owner.getUUID(), power);
               }

               Vec3 target = this.getTarget(owner);
               Vec3 velocity = target.subtract(owner.position()).normalize().scale(reallaunch);
               if (velocity.y > 0.0) {
                  velocity = velocity.multiply(1.0, 0.0, 1.0);
               } else {
                  velocity = velocity.multiply(1.0, 2.0, 1.0);
               }

               owner.setDeltaMovement(velocity);
               cap.delayTickEvent(() -> TARGETS.remove(owner.getUUID()), 60);
            }, 15);
         }

         return true;
      }
   }
}
