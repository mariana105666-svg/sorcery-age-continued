package radon.jujutsu_kaisen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.IBarrier;
import radon.jujutsu_kaisen.block.entity.IDomain;
import radon.jujutsu_kaisen.block.entity.IDomainBarrier;
import radon.jujutsu_kaisen.block.entity.IOpenDomainBarrier;
import radon.jujutsu_kaisen.block.entity.IVeil;
import radon.jujutsu_kaisen.block.entity.VeilBlockEntity;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.VeilEntity;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.PlayerModifier;

@EventBusSubscriber(
   modid = "jujutsu_kaisen",
   bus = Bus.FORGE
)
public class VeilHandler {
   private static final Map<ResourceKey<Level>, Set<UUID>> barriers = new ConcurrentHashMap<>();
   private static final Map<ResourceKey<Level>, Map<UUID, IBarrier>> cache = new ConcurrentHashMap<>();

   public static void barrier(ResourceKey<Level> dimension, UUID identifier) {
      barriers.computeIfAbsent(dimension, ignored -> new HashSet<>()).add(identifier);
   }

   @Nullable
   private static IBarrier get(ServerLevel level, UUID identifier) {
      ResourceKey<Level> dimension = level.dimension();
      Map<UUID, IBarrier> slot = cache.computeIfAbsent(dimension, k -> new HashMap<>());
      IBarrier cached = slot.get(identifier);
      if (cached instanceof Entity entity && entity.isAlive()) {
         return cached;
      } else if (level.getEntity(identifier) instanceof IBarrier barrier) {
         slot.put(identifier, barrier);
         return barrier;
      } else {
         slot.remove(identifier);
         return null;
      }
   }

   public static Set<IBarrier> getBarriers(ServerLevel level, BlockPos target) {
      Set<IBarrier> result = new HashSet<>();
      if (!barriers.containsKey(level.dimension())) {
         return result;
      } else {
         for (UUID identifier : barriers.get(level.dimension())) {
            IBarrier barrier = get(level, identifier);
            if (barrier != null && barrier.isInsideBarrier(target)) {
               result.add(barrier);
            }
         }

         return result;
      }
   }

   public static Set<IDomainBarrier> getDomainBarriers(ServerLevel level, AABB bounds) {
      Set<IDomainBarrier> result = new HashSet<>();
      if (!barriers.containsKey(level.dimension())) {
         return result;
      } else {
         for (UUID identifier : barriers.get(level.dimension())) {
            IBarrier barrier = get(level, identifier);
            if (barrier != null && barrier instanceof IDomainBarrier barrierEntity && bounds.intersects(barrierEntity.getBounds())) {
               result.add(barrierEntity);
            }
         }

         return result;
      }
   }

   public static Set<IDomainBarrier> getDomainBarriers(ServerLevel level) {
      Set<IDomainBarrier> result = new HashSet<>();
      if (!barriers.containsKey(level.dimension())) {
         return result;
      } else {
         for (UUID identifier : barriers.get(level.dimension())) {
            if (get(level, identifier) instanceof IDomainBarrier barrierEntity) {
               result.add(barrierEntity);
            }
         }

         return result;
      }
   }

   public static Set<IDomainBarrier> getDomainBarriers(ServerLevel level, BlockPos target) {
      Set<IDomainBarrier> result = new HashSet<>();
      if (!barriers.containsKey(level.dimension())) {
         return result;
      } else {
         for (UUID identifier : barriers.get(level.dimension())) {
            IBarrier barrier = get(level, identifier);
            if (barrier != null && barrier instanceof IDomainBarrier barrierEntity && barrierEntity.isInsideBarrier(target)) {
               result.add(barrierEntity);
            }
         }

         return result;
      }
   }

   public static Set<IDomainBarrier> getDomainShellBarriers(ServerLevel level, AABB bounds) {
      Set<IDomainBarrier> result = new HashSet<>();
      if (!barriers.containsKey(level.dimension())) {
         return result;
      } else {
         for (UUID identifier : barriers.get(level.dimension())) {
            IBarrier barrier = get(level, identifier);
            if (barrier != null
               && !(barrier instanceof IOpenDomainBarrier)
               && barrier instanceof IDomainBarrier barrierEntity
               && bounds.intersects(barrierEntity.getBounds())) {
               result.add(barrierEntity);
            }
         }

         return result;
      }
   }

   public static Set<IDomainBarrier> getDomainShellBarriers(ServerLevel level, BlockPos target) {
      Set<IDomainBarrier> result = new HashSet<>();
      if (!barriers.containsKey(level.dimension())) {
         return result;
      } else {
         for (UUID identifier : barriers.get(level.dimension())) {
            IBarrier barrier = get(level, identifier);
            if (barrier != null
               && !(barrier instanceof IOpenDomainBarrier)
               && barrier instanceof IDomainBarrier barrierEntity
               && barrierEntity.isInsideBarrier(target)) {
               result.add(barrierEntity);
            }
         }

         return result;
      }
   }

   public static Set<IBarrier> getBarriers(ServerLevel level, AABB bounds) {
      Set<IBarrier> result = new HashSet<>();
      if (!barriers.containsKey(level.dimension())) {
         return result;
      } else {
         for (UUID identifier : barriers.get(level.dimension())) {
            IBarrier barrier = get(level, identifier);
            if (barrier != null && bounds.intersects(barrier.getBounds())) {
               result.add(barrier);
            }
         }

         return result;
      }
   }

   public static Set<IDomain> getDomains(ServerLevel level, BlockPos target) {
      Set<IDomain> result = new HashSet<>();

      for (IBarrier barrier : getBarriers(level, target)) {
         if (barrier instanceof IDomain domain) {
            result.add(domain);
         }
      }

      return result;
   }

   public static Set<IDomain> getDomains(ServerLevel level, AABB bounds) {
      Set<IDomain> result = new HashSet<>();

      for (IBarrier barrier : getBarriers(level, bounds)) {
         if (barrier instanceof IDomain domain) {
            result.add(domain);
         }
      }

      return result;
   }

   public static Set<IVeil> getVeils(ServerLevel level, BlockPos target) {
      Set<IVeil> result = new HashSet<>();

      for (IBarrier barrier : getBarriers(level, target)) {
         if (barrier instanceof IVeil veil) {
            result.add(veil);
         }
      }

      return result;
   }

   public static Set<IVeil> getVeils(ServerLevel level, AABB bounds) {
      Set<IVeil> result = new HashSet<>();

      for (IBarrier barrier : getBarriers(level, bounds)) {
         if (barrier instanceof IVeil veil) {
            result.add(veil);
         }
      }

      return result;
   }

   @Nullable
   public static IBarrier getSurroundingBarrier(ServerLevel level, BlockPos target) {
      Set<IBarrier> all = getBarriers(level, target);
      if (all.isEmpty()) {
         return null;
      } else {
         IBarrier innermost = null;
         double bestDepth = -Double.MAX_VALUE;

         for (IBarrier barrier : all) {
            AABB bounds = barrier.getBounds();
            double dx = Math.min(target.getX() - bounds.minX, bounds.maxX - target.getX());
            double dy = Math.min(target.getY() - bounds.minY, bounds.maxY - target.getY());
            double dz = Math.min(target.getZ() - bounds.minZ, bounds.maxZ - target.getZ());
            double depth = Math.min(dx, Math.min(dy, dz));
            if (depth > bestDepth) {
               bestDepth = depth;
               innermost = barrier;
            }
         }

         return innermost;
      }
   }

   public static boolean wouldOverlapVeil(ServerLevel level, Vec3 center, int radius) {
      AABB bounds = AABB.ofSize(center, radius * 2.0, radius * 2.0, radius * 2.0);

      for (IVeil veil : getVeils(level, bounds)) {
         VeilEntity existing = veil.getVeil();
         if (existing != null && !existing.isRemoved()) {
            double reach = radius + existing.getRadius();
            if (existing.getBarrierCenter().getCenter().distanceToSqr(center) < reach * reach) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean checkIntersect(Level level, BlockPos center, int radius) {
      if (level instanceof ServerLevel servLevel) {
         Set<IVeil> veils = getVeils(servLevel, center);
         if (veils == null) {
            return false;
         } else {
            for (IVeil veil : veils) {
               BlockPos otherPos = veil.getVeil().getCenter();
               if (!otherPos.equals(center)) {
                  int otherRadius = veil.getRadius();
                  double dist = Math.sqrt(center.distSqr(otherPos));
                  if (dist <= radius + otherRadius && dist >= Math.abs(radius - otherRadius)) {
                     return true;
                  }
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean isWhitelisted(@Nullable BlockPos parent, Entity entity) {
      if (entity != null && parent != null && entity.level().getBlockEntity(parent) instanceof VeilBlockEntity be) {
         UUID owner = be.getOwnerUUID();
         if (owner != null && owner.equals(entity.getUUID())) {
            return true;
         } else {
            List<Modifier> modifiers = be.getModifiers();
            if (modifiers == null) {
               return false;
            } else {
               if (entity instanceof Player player) {
                  for (Modifier modifier : modifiers) {
                     if (modifier.getAction() == Modifier.Action.ALLOW
                        && modifier.getType() == Modifier.Type.OWNER_BYPASS
                        && entity.getUUID().equals(be.getOwnerUUID())) {
                        return true;
                     }

                     if ((modifier.getAction() == Modifier.Action.ALLOW || modifier.getType() == Modifier.Type.PLAYER)
                        && modifier.getType() == Modifier.Type.PLAYER
                        && (
                           player.getUUID().equals(((PlayerModifier)modifier).getUUID())
                              || ((PlayerModifier)modifier).getName().equals(player.getDisplayName().getString())
                        )) {
                        return modifier.getAction() == Modifier.Action.ALLOW;
                     }
                  }
               }

               if (!entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                  return false;
               } else {
                  ISorcererData cap = (ISorcererData)entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                  Modifier.Type wanted = cap.getType() == JujutsuType.CURSE ? Modifier.Type.CURSE : Modifier.Type.SORCERER;

                  for (Modifier modifier : modifiers) {
                     if (modifier.getType() == wanted && modifier.getAction() == Modifier.Action.ALLOW) {
                        return true;
                     }
                  }

                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   @Nullable
   public static VeilEntity getVeil(Level pLevel, VeilBlockEntity be) {
      return ((ServerLevel)pLevel).getEntity(be.getParentUUID()) instanceof VeilEntity veil && !veil.isRemoved() && veil.isAlive() ? veil : null;
   }

   public static boolean canDestroy(LivingEntity entity, Level level, double x, double y, double z) {
      return canDestroy(entity, level, x, y, z, false);
   }

   @Nullable
   public static VeilEntity getVeilAt(Level level, BlockPos target) {
      if (level instanceof ServerLevel serverLevel) {
         if (!barriers.containsKey(serverLevel.dimension())) {
            return null;
         } else {
            for (UUID identifier : barriers.get(serverLevel.dimension())) {
               IBarrier barrier = get(serverLevel, identifier);
               if (barrier instanceof IVeil veil && barrier.isBarrierOrInside(target)) {
                  return veil.getVeil();
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   @Nullable
   public static VeilEntity getAdminVeil(Level level, BlockPos target) {
      if (level instanceof ServerLevel serverLevel) {
         if (!barriers.containsKey(serverLevel.dimension())) {
            return null;
         } else {
            for (UUID identifier : barriers.get(serverLevel.dimension())) {
               IBarrier barrier = get(serverLevel, identifier);
               if (barrier instanceof IVeil veil && barrier.isBarrierOrInside(target) && isAdminVeil(veil)) {
                  return veil.getVeil();
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private static boolean isAdminVeil(IVeil veil) {
      List<Modifier> modifiers = veil.getModifiers();
      if (modifiers == null) {
         return false;
      } else {
         for (Modifier modifier : modifiers) {
            if (modifier.getType() == Modifier.Type.ADMIN) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isAdminBlocked(Level level, BlockPos target, @Nullable Entity actor) {
      VeilEntity veil = getAdminVeil(level, target);
      if (veil == null) {
         return false;
      } else if (actor == null) {
         return true;
      } else {
         UUID owner = veil.getOwnerUUID();
         return owner == null || !owner.equals(actor.getUUID());
      }
   }

   public static boolean isAbilityBlocked(LivingEntity owner, Ability ability) {
      VeilEntity veil = getAdminVeil(owner.level(), owner.blockPosition());
      if (veil == null) {
         return false;
      } else {
         UUID creator = veil.getOwnerUUID();
         if (creator != null && creator.equals(owner.getUUID())) {
            return false;
         } else {
            List<Modifier> modifiers = veil.getModifiers();
            if (modifiers == null) {
               return false;
            } else {
               for (Modifier modifier : modifiers) {
                  if (modifier.getAction() == Modifier.Action.DENY) {
                     if (modifier.getType() == Modifier.Type.ABILITY) {
                        return true;
                     }

                     if (modifier.getType() == Modifier.Type.DOMAIN
                        && (ability.isDomain() || ability == JJKAbilities.ZERO_POINT_TWO_SECOND_DOMAIN_EXPANSION.get())) {
                        return true;
                     }
                  }
               }

               return false;
            }
         }
      }
   }

   public static boolean canDestroy(LivingEntity entity, Level level, double x, double y, double z, boolean manualBreak) {
      if (!(level instanceof ServerLevel servLevel)) {
         return false;
      } else {
         BlockPos target = BlockPos.containing(x, y, z);
         if (isAdminBlocked(level, target, entity)) {
            return false;
         } else {
            Set<IVeil> veils = getVeils(servLevel, target);
            if (veils == null) {
               return true;
            } else {
               for (IVeil veil : veils) {
                  if (manualBreak && entity.level().getBlockState(target).is((Block)JJKBlocks.VEIL_ROD.get())) {
                     return true;
                  }

                  int radius = veil.getRadius();
                  if (!(target.distSqr(veil.getVeil().getCenter()) >= radius * radius)) {
                     boolean ownerFlag = false;
                     boolean destroyFlag = true;

                     for (Modifier modifier : veil.getModifiers()) {
                        if (modifier.getAction() == Modifier.Action.ALLOW && modifier.getType() == Modifier.Type.OWNER_BYPASS) {
                           ownerFlag = true;
                        } else if (modifier.getAction() == Modifier.Action.DENY && modifier.getType() == Modifier.Type.GRIEFING) {
                           destroyFlag = false;
                        }
                     }

                     VeilEntity veilObj = veil.getVeil();
                     if (!ownerFlag || entity == null || veilObj.getUUID() == null || !veilObj.getUUID().equals(entity.getUUID())) {
                        return destroyFlag;
                     }
                  }
               }

               return true;
            }
         }
      }
   }

   public static boolean canSpawn(ServerLevel level, Mob mob, double x, double y, double z) {
      BlockPos target = BlockPos.containing(x, y, z);
      if (getAdminVeil(level, target) != null) {
         return false;
      } else {
         IBarrier owner = getSurroundingBarrier(level, target);
         if (!(owner instanceof IVeil veil)) {
            if (owner instanceof IDomainBarrier) {
               return false;
            }
         } else {
            boolean curseSpawnFlag = false;
            boolean sorcererSpawnFlag = false;

            for (Modifier modifier : veil.getModifiers()) {
               if (modifier.getAction() == Modifier.Action.ALLOW && modifier.getType() == Modifier.Type.CURSE_SPAWN) {
                  curseSpawnFlag = true;
               } else if (modifier.getAction() == Modifier.Action.ALLOW && modifier.getType() == Modifier.Type.SORCERER_SPAWN) {
                  sorcererSpawnFlag = true;
               }
            }

            if ((!sorcererSpawnFlag || !(mob instanceof SorcererEntity)) && (!curseSpawnFlag || !(mob instanceof CursedSpirit))) {
               return false;
            }
         }

         return true;
      }
   }

   @SubscribeEvent
   public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
      Level level = event.getLevel();
      Entity entity = event.getEntity();
      if (barriers.containsKey(level.dimension())) {
         Set<UUID> identifiers = barriers.get(level.dimension());
         if (!identifiers.remove(entity.getUUID())) {
            return;
         }

         if (identifiers.isEmpty()) {
            barriers.remove(level.dimension());
            cache.remove(level.dimension());
         } else {
            Map<UUID, IBarrier> levelCache = cache.get(level.dimension());
            if (levelCache != null) {
               levelCache.remove(entity.getUUID());
               if (levelCache.isEmpty()) {
                  cache.remove(level.dimension());
               }
            }
         }
      }
   }

   public static boolean isProtected(Level level, BlockPos target) {
      if (level instanceof ServerLevel servLevel) {
         Set<IVeil> veils = getVeils(servLevel, target);
         if (veils.isEmpty()) {
            return false;
         } else {
            for (IVeil veil : veils) {
               int radius = veil.getRadius();
               if (target.distSqr(veil.getVeil().getCenter()) < radius * radius) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean isTeleportValid(Level level, BlockPos target) {
      if (!(level instanceof ServerLevel servLevel)) {
         return true;
      } else {
         Set<IVeil> veils = getVeils(servLevel, target);
         if (veils.isEmpty()) {
            return true;
         } else {
            for (IVeil veil : veils) {
               boolean ownerFlag = false;
               boolean teleportFlag = false;

               for (Modifier modifier : veil.getModifiers()) {
                  if (modifier.getAction() == Modifier.Action.ALLOW && modifier.getType() == Modifier.Type.OWNER_BYPASS) {
                     ownerFlag = true;
                  } else if (modifier.getAction() == Modifier.Action.DENY && modifier.getType() == Modifier.Type.TELEPORT) {
                     teleportFlag = true;
                  }
               }

               int radius = veil.getRadius();
               if (teleportFlag && target.distSqr(veil.getVeil().getCenter()) <= (radius + 2) * (radius + 2) && !ownerFlag) {
                  return false;
               }
            }

            return true;
         }
      }
   }
}
