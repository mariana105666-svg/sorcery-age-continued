package radon.jujutsu_kaisen.ability.boogie_woogie;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.AbstractMap.SimpleEntry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.IncompleteDomainEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.RotationUtil;

public class SwapOthers extends Ability {
   public static final double RANGE = 50.0;
   private static final int EXPIRATION = 100;
   public Entity enemy = null;
   private static final Map<UUID, SimpleEntry<UUID, Long>> TARGETS = new HashMap<>();

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Nullable
   private Entity getTarget(LivingEntity owner) {
      LivingEntity target = RotationUtil.getExpandedLookAt(owner, 50.0);
      if (target != null) {
         return SwapSelf.canSwap(target) ? target : null;
      } else {
         return null;
      }
   }

   public static void setTarget(LivingEntity owner, Entity target) {
      TARGETS.put(owner.getUUID(), new SimpleEntry<>(target.getUUID(), owner.level().getGameTime()));
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      if (owner.level() instanceof ServerLevel level) {
         IncompleteDomainEntity domain = IncompleteDomainEntity.boogieWoogie(owner);
         if (domain != null && domain.swap(owner)) {
            this.enemy = null;
         } else {
            Entity first = this.enemy;
            if (first != null) {
               if (TARGETS.containsKey(owner.getUUID())) {
                  SimpleEntry<UUID, Long> entry = TARGETS.get(owner.getUUID());
                  if (owner.level().getGameTime() - entry.getValue() >= 100L) {
                     TARGETS.put(owner.getUUID(), new SimpleEntry<>(first.getUUID(), owner.level().getGameTime()));
                     return;
                  }

                  Entity second = level.getEntity(entry.getKey());
                  if (second == null) {
                     return;
                  }

                  owner.level()
                     .playSound(null, second.getX(), second.getY(), second.getZ(), (SoundEvent)JJKSounds.CLAP.get(), SoundSource.MASTER, 2.0F, 1.0F);
                  owner.level()
                     .playSound(null, first.getX(), first.getY(), first.getZ(), (SoundEvent)JJKSounds.CLAP.get(), SoundSource.MASTER, 1.0F, 1.0F);
                  Vec3 pos = second.position();
                  Vec2 firstRot = first.getRotationVector();
                  Vec2 secondRot = second.getRotationVector();
                  first.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                     cap.setSelfHit(15);
                     cap.setExtraMeleeTaken(25);
                  });
                  second.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                     cap.setSelfHit(15);
                     cap.setExtraMeleeTaken(25);
                  });
                  if (VeilHandler.isTeleportValid(second.level(), first.blockPosition())) {
                     second.teleportTo(first.getX(), first.getY(), first.getZ());
                  }

                  if (VeilHandler.isTeleportValid(first.level(), second.blockPosition())) {
                     first.teleportTo(pos.x, pos.y, pos.z);
                  }

                  second.setYRot(firstRot.y);
                  second.setXRot(firstRot.x);
                  first.setYRot(secondRot.y);
                  first.setXRot(secondRot.x);
                  TARGETS.remove(owner.getUUID());
               } else {
                  TARGETS.put(owner.getUUID(), new SimpleEntry<>(first.getUUID(), owner.level().getGameTime()));
               }

               this.enemy = null;
            }
         }
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 25.0F;
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      Entity target = this.getTarget(owner);
      this.enemy = target;
      if (target == null && IncompleteDomainEntity.boogieWoogie(owner) != null) {
         return super.isTriggerable(owner);
      } else {
         return target == null ? Ability.Status.FAILURE : super.isTriggerable(owner);
      }
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
