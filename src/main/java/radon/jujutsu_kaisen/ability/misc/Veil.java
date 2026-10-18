package radon.jujutsu_kaisen.ability.misc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.VeilEntity;
import radon.jujutsu_kaisen.item.veil.modifier.AdminModifier;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.OpenVeilConfigurationS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Veil extends Ability implements Ability.IToggled {
   public static final double RANGE = 16.0;
   private static final int RADIUS = 64;
   private static final int ADMIN_RADIUS_MULTIPLIER = 2;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return false;
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Nullable
   private static Veil.Placement resolve(LivingEntity owner) {
      if (RotationUtil.getLookAtHit(owner, 16.0) instanceof BlockHitResult blockHit) {
         BlockPos var8 = blockHit.getBlockPos();
         if (owner.level().getBlockEntity(var8) instanceof VeilRodBlockEntity be && be.getOwnerUUID() != null && be.getOwnerUUID().equals(owner.getUUID())) {
            return new Veil.Placement(var8.getCenter(), be.getSize(), be.getModifiers(), var8, true);
         } else {
            Vec3 start = owner.getEyePosition();
            Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
            Vec3 end = start.add(look.scale(16.0));
            HitResult result = RotationUtil.getHitResult(owner, start, end);
            Vec3 center = result.getType() == Type.MISS ? end : result.getLocation();
            return new Veil.Placement(center, 64, List.of(), var8, false);
         }
      } else {
         return null;
      }
   }

   private static boolean canPlace(ServerLevel level, LivingEntity owner, Vec3 center, int radius) {
      return !VeilHandler.getVeils(level, owner.blockPosition()).isEmpty() ? false : !VeilHandler.wouldOverlapVeil(level, center, radius);
   }

   private static int radiusFor(Veil.Placement placement, List<Modifier> modifiers, int size) {
      if (placement.anchored()) {
         return placement.radius();
      } else {
         int radius = Mth.clamp(size, (Integer)ConfigHolder.SERVER.minimumVeilSize.get(), (Integer)ConfigHolder.SERVER.maximumVeilSize.get());

         for (Modifier modifier : modifiers) {
            if (modifier.getType() == Modifier.Type.ADMIN) {
               return radius * 2;
            }
         }

         return radius;
      }
   }

   public static void place(LivingEntity owner, List<Modifier> modifiers, int size) {
      if (owner.level() instanceof ServerLevel level) {
         if (((Ability)JJKAbilities.VEIL.get()).isValid(owner)) {
            Veil.Placement placement = resolve(owner);
            if (placement != null) {
               List<Modifier> effective = vet(owner, modifiers);
               int radius = radiusFor(placement, effective, size);
               if (canPlace(level, owner, placement.center(), radius)) {
                  level.addFreshEntity(new VeilEntity(owner, placement.center(), radius, effective, placement.rod()));
                  announce(owner, level);
               }
            }
         }
      }
   }

   private static List<Modifier> vet(LivingEntity owner, List<Modifier> modifiers) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
      boolean permitted = cap != null && cap.hasAdminVeil();
      boolean admin = false;
      Modifier transparent = null;
      List<Modifier> result = new ArrayList<>();

      for (Modifier modifier : modifiers) {
         if (modifier.getType() == Modifier.Type.ADMIN) {
            admin |= permitted;
         } else {
            if (modifier.getType() == Modifier.Type.TRANSPARENT) {
               transparent = modifier;
            }

            result.add(modifier);
         }
      }

      if (admin) {
         List<Modifier> sealed = new ArrayList<>();
         sealed.add(new AdminModifier(Modifier.Action.ALLOW));
         if (transparent != null) {
            sealed.add(transparent);
         }

         for (Modifier modifierx : modifiers) {
            switch (modifierx.getType()) {
               case CURSE:
               case SORCERER:
               case DOMAIN:
               case ABILITY:
                  sealed.add(modifierx);
            }
         }

         return sealed;
      } else {
         return result;
      }
   }

   private static void announce(LivingEntity owner, ServerLevel level) {
      for (ServerPlayer player : level.players()) {
         if (!(owner instanceof Mob) || !(player.distanceTo(owner) > owner.getAttributeValue(Attributes.FOLLOW_RANGE))) {
            player.sendSystemMessage(Component.translatable(String.format("chat.%s.veil", "jujutsu_kaisen"), new Object[]{owner.getName().getString()}));
         }
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         Veil.Placement placement = resolve(owner);
         if (placement == null || !canPlace(level, owner, placement.center(), placement.radius())) {
            return Ability.Status.FAILURE;
         }
      }

      return super.isTriggerable(owner);
   }

   @Override
   public void run(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         Veil.Placement placement = resolve(owner);
         if (placement != null && canPlace(level, owner, placement.center(), placement.radius())) {
            if (placement.anchored()) {
               level.addFreshEntity(new VeilEntity(owner, placement.center(), placement.radius(), placement.modifiers(), placement.rod()));
               announce(owner, level);
            } else {
               if (owner instanceof ServerPlayer player) {
                  PacketHandler.sendToClient(new OpenVeilConfigurationS2CPacket(), player);
               }
            }
         }
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.6F;
   }

   @Override
   public void onEnabled(LivingEntity owner) {
   }

   @Override
   public void onDisabled(LivingEntity owner) {
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.DOMAIN;
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @Nullable
   @Override
   public Ability getParent(LivingEntity owner) {
      return (Ability)JJKAbilities.INCOMPLETE_DOMAIN.get();
   }

   @Override
   public Vec2 getDisplayCoordinates() {
      return new Vec2(2.0F, 0.0F);
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public int getPointsCost() {
      return 60;
   }

   private record Placement(Vec3 center, int radius, List<Modifier> modifiers, BlockPos rod, boolean anchored) {
   }
}
