package radon.jujutsu_kaisen.ability.shrine;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.misc.RCT1;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.chant.ChantHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.projectile.WorldSlashProjectile;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class WorldSlash extends Ability {
   public static final float SPEED = 5.0F;
   private static final double RANGE = 64.0;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return true;
   }

   @Override
   public boolean isChantable() {
      return true;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target == null || target.isDeadOrDying()) {
         return false;
      } else if (!owner.hasLineOfSight(target)) {
         return false;
      } else if (owner instanceof MahoragaEntity) {
         return HelperMethods.RANDOM.nextInt(20) == 0;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getType() != JujutsuType.CURSE && !((RCT1)JJKAbilities.RCT1.get()).isUnlocked(owner)
            ? owner.getHealth() / owner.getMaxHealth() < 0.8F || target.getHealth() > owner.getHealth() * 2.0F
            : owner.getHealth() / owner.getMaxHealth() < 0.9F;
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      return ConfigHolder.SERVER.chantRequiredForWCS.get() && owner instanceof Player && ChantHandler.getOutput(owner, this) < 1.25F
         ? Ability.Status.CHANT
         : super.isTriggerable(owner);
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return !(owner instanceof MahoragaEntity) && !cap.hasTechnique(CursedTechnique.SHRINE) ? false : super.isValid(owner);
   }

   @Override
   public boolean isUnlockable() {
      return true;
   }

   @Override
   public boolean isUnlocked(LivingEntity owner) {
      if (owner instanceof MahoragaEntity) {
         ITenShadowsData cap = (ITenShadowsData)owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
         if (cap.getAdaptation((Ability)JJKAbilities.INFINITY.get()) > 1) {
            return true;
         }
      } else if (owner instanceof Player player && player.getAbilities().instabuild) {
         return true;
      }

      return super.isUnlocked(owner);
   }

   @Override
   public boolean isTechnique() {
      return true;
   }

   @Override
   public boolean specialObtainment() {
      return true;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      if (!owner.level().isClientSide) {
         float output = ChantHandler.getOutput(owner, this);
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         output *= cap.getOutput();
         WorldSlashProjectile slash = new WorldSlashProjectile(
            owner, output, (owner.isShiftKeyDown() ? 90.0F : 0.0F) + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 60.0F
         );
         slash.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(5.0));
         owner.level().addFreshEntity(slash);
         owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.MASTER, 1.0F, 1.0F);
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 750.0F;
   }

   @Override
   public int getCooldown() {
      return 1200;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.SLASHING;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }

   public static boolean hasVisualOn(Level level, LivingEntity viewer, LivingEntity target) {
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(viewer);
      Vec3 start = viewer.getEyePosition();
      Vec3 result = target.getEyePosition().subtract(start);
      double angle = Math.acos(look.normalize().dot(result.normalize()));
      double threshold = 1.0;
      return angle <= threshold;
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class WorldSlashForgeEvents {
      @SubscribeEvent
      public static void onAbilityTrigger(AbilityTriggerEvent.Post event) {
         Ability ability = event.getAbility();
         if (ability == JJKAbilities.WORLD_SLASH.get()) {
            LivingEntity owner = event.getEntity();

            for (Entity entity : owner.level().getEntities(owner, AABB.ofSize(owner.position(), 64.0, 64.0, 64.0))) {
               if (entity instanceof LivingEntity living
                  && (!(living instanceof Player) || WorldSlash.hasVisualOn(owner.level(), living, owner))
                  && living.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                  ISorcererData cap = (ISorcererData)living.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                  if (!cap.isUnlocked((Ability)JJKAbilities.WORLD_SLASH.get())) {
                     living.level()
                        .playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.MASTER, 1.0F, 1.0F);
                     cap.unlock((Ability)JJKAbilities.WORLD_SLASH.get());
                     if (entity instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                     }
                  }
               }
            }
         }
      }
   }
}
