package radon.jujutsu_kaisen.ability.boogie_woogie;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.projectile.CursedEnergyImbuedItemProjectile;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class SwapSelf extends Ability {
   public static final double RANGE = 50.0;
   public Entity enemy = null;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return this.getTarget(owner) == target && HelperMethods.RANDOM.nextInt(3) == 0;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   public static boolean canSwap(Entity target) {
      return (
            target.isPickable()
               || target instanceof ItemEntity item && item.getItem().getItem() instanceof CursedToolItem
               || target instanceof CursedEnergyImbuedItemProjectile
               || target instanceof JujutsuProjectile
         )
         && !(target instanceof LivingEntity living && JJKAbilities.hasTrait(living, Trait.HEAVENLY_RESTRICTION));
   }

   @Nullable
   private Entity getTarget(LivingEntity owner) {
      LivingEntity target = RotationUtil.getExpandedLookAt(owner, 50.0);
      if (target != null) {
         return canSwap(target) ? target : null;
      } else {
         return null;
      }
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      if (!owner.level().isClientSide) {
         Entity target = this.enemy;
         if (target != null) {
            owner.level()
               .playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.CLAP.get(), SoundSource.MASTER, 2.0F, 1.0F);
            owner.level()
               .playSound(null, target.getX(), target.getY(), target.getZ(), (SoundEvent)JJKSounds.CLAP.get(), SoundSource.MASTER, 1.0F, 1.0F);
            Vec3 pos = target.position();
            Vec2 ownerRot = owner.getRotationVector();
            Vec2 targetRot = target.getRotationVector();
            target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
               cap.setSelfHit(15);
               cap.setExtraMeleeTaken(20);
            });
            if (VeilHandler.isTeleportValid(target.level(), owner.blockPosition())) {
               target.teleportTo(owner.getX(), owner.getY(), owner.getZ());
            }

            if (VeilHandler.isTeleportValid(owner.level(), target.blockPosition())) {
               owner.teleportTo(pos.x, pos.y, pos.z);
            }

            target.setYRot(ownerRot.y);
            target.setXRot(ownerRot.x);
            owner.setYRot(targetRot.y);
            owner.setXRot(targetRot.x);
         }

         this.enemy = null;
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 20.0F;
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      Entity target = this.getTarget(owner);
      this.enemy = target;
      return target == null ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }

   @Override
   public int getCooldown() {
      return 2;
   }
}
