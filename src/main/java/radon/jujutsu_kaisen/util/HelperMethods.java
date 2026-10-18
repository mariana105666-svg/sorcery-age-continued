package radon.jujutsu_kaisen.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.DomainBarrierEntity;
import radon.jujutsu_kaisen.entity.LimboCloneEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.entity.projectile.WorldSlashProjectile;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.item.JJKItems;

public class HelperMethods {
   public static final RandomSource RANDOM = RandomSource.createThreadSafe();
   private static final String[] WORDS = new String[]{
      "blossoms",
      "thorns",
      "roots",
      "petals",
      "embers",
      "ashes",
      "seeds",
      "vines",
      "branches",
      "stones",
      "crystals",
      "mist",
      "bloom",
      "dew",
      "tide",
      "drift",
      "flame",
      "soil",
      "omens",
      "sigils",
      "wards",
      "bindings",
      "seals",
      "echoes",
      "spirits",
      "whispers",
      "truths",
      "mirrors",
      "phantoms",
      "visions",
      "relics",
      "runes",
      "shadows",
      "dreams",
      "grace",
      "essence",
      "fate",
      "threshold",
      "judgments",
      "chains",
      "ropes",
      "knives",
      "crowns",
      "blades",
      "scars",
      "brands",
      "vows",
      "marks",
      "wounds",
      "oaths",
      "banners",
      "arrows",
      "rings",
      "keys",
      "grips",
      "callings",
      "rifts",
      "trials",
      "silence",
      "memory",
      "echo",
      "stillness",
      "pulse",
      "void",
      "origin",
      "fracture",
      "balance",
      "cycle",
      "breath",
      "motion",
      "reflection",
      "spark",
      "moment",
      "hollow",
      "weight",
      "current",
      "name",
      "end",
      "rot",
      "decay",
      "curse",
      "wither",
      "lament",
      "sin",
      "hunger",
      "feast",
      "shroud",
      "wrath",
      "abyss",
      "void",
      "grief",
      "scar",
      "torment",
      "ruin",
      "dread",
      "flesh",
      "night"
   };

   public static boolean expCheck(LivingEntity entity) {
      return !(entity instanceof LimboCloneEntity);
   }

   @Nullable
   public static LivingEntity getRootOwner(LivingEntity entity) {
      while (entity instanceof TamableAnimal tamable && tamable.isTame()) {
         entity = tamable.getOwner();
         if (entity == null) {
            return null;
         }
      }

      return entity;
   }

   public static boolean friendsCheck(LivingEntity a, LivingEntity b) {
      LivingEntity ownerA = getRootOwner(a);
      LivingEntity ownerB = getRootOwner(b);
      if (ownerA == null || ownerB == null) {
         return false;
      } else if (ownerA == ownerB) {
         return true;
      } else {
         ISorcererData capA = (ISorcererData)ownerA.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         ISorcererData capB = (ISorcererData)ownerB.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         return capA != null && capB != null ? capA.hasPact(ownerB.getUUID(), Pact.FRIENDS) && capB.hasPact(ownerA.getUUID(), Pact.FRIENDS) : false;
      }
   }

   public static boolean wouldBeFilled(LivingEntity owner, Vec3 target) {
      AABB newBox = owner.getBoundingBox().move(target.subtract(owner.position()));
      return owner.level().noCollision(newBox);
   }

   public static boolean isBlocked(LivingEntity owner, Vec3 prev, Vec3 target) {
      double height = owner.getBbHeight();
      Vec3 feetStart = new Vec3(prev.x, prev.y, prev.z);
      Vec3 feetEnd = new Vec3(target.x, target.y, target.z);
      Vec3 headStart = new Vec3(prev.x, prev.y + height * 0.9, prev.z);
      Vec3 headEnd = new Vec3(target.x, target.y + height * 0.9, target.z);
      ClipContext ctxFeet = new ClipContext(feetStart, feetEnd, Block.COLLIDER, Fluid.NONE, owner);
      ClipContext ctxHead = new ClipContext(headStart, headEnd, Block.COLLIDER, Fluid.NONE, owner);
      HitResult hitFeet = owner.level().clip(ctxFeet);
      HitResult hitHead = owner.level().clip(ctxHead);
      return hitFeet.getType() == Type.BLOCK && hitHead.getType() == Type.BLOCK;
   }

   public static boolean isBlockable(LivingEntity target, Projectile projectile) {
      if (projectile instanceof WorldSlashProjectile) {
         return false;
      } else if (projectile.getOwner() == target) {
         return false;
      } else if (projectile instanceof ThrownChainProjectile chain && chain.getBypassInfinity()) {
         return false;
      } else {
         return projectile instanceof JujutsuProjectile jujutsu ? !jujutsu.isDomain() : true;
      }
   }

   public static boolean isBlockable(LivingEntity target, DamageSource source) {
      if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !source.is(DamageTypes.STARVE) && !source.is(JJKDamageSources.SOUL)) {
         if (source.getEntity() == target) {
            return false;
         } else {
            if (source.getEntity() instanceof LivingEntity living && isMelee(source)) {
               if (JJKAbilities.hasToggled(living, (Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                  return false;
               }

               ItemStack stack = source.getDirectEntity() instanceof ThrownChainProjectile chain ? chain.getStack() : living.getItemInHand(InteractionHand.MAIN_HAND);
               List<Item> stacks = new ArrayList<>();
               stacks.add(stack.getItem());
               stacks.addAll(
                  CuriosUtil.findSlots(living, living.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand").stream().map(ItemStack::getItem).toList()
               );
               if (stacks.contains(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())
                  || source.getDirectEntity() instanceof ThrownChainProjectile chainx && chainx.getBypassInfinity()) {
                  return false;
               }
            }

            if (source.getDirectEntity() instanceof Projectile projectile && !isBlockable(target, projectile)) {
               return false;
            } else {
               return source.getDirectEntity() instanceof DomainExpansionEntity ? false : source.getEntity() != target;
            }
         }
      } else {
         return false;
      }
   }

   public static boolean isMelee(DamageSource source) {
      return !source.isIndirect()
            && (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK) || source.is(JJKDamageSources.SPLIT_SOUL_KATANA))
         || source instanceof JJKDamageSources.JujutsuDamageSource jujutsu && jujutsu.getAbility() != null && jujutsu.getAbility().isMelee();
   }

   private static BlockPos getTopNonCollidingPos(LevelReader level, EntityType<?> type, int x, int z) {
      int i = level.getHeight(SpawnPlacements.getHeightmapType(type), x, z);
      MutableBlockPos pos = new MutableBlockPos(x, i, z);
      if (level.dimensionType().hasCeiling()) {
         do {
            pos.move(Direction.DOWN);
         } while (!level.getBlockState(pos).isAir());

         do {
            pos.move(Direction.DOWN);
         } while (level.getBlockState(pos).isAir() && pos.getY() > level.getMinBuildHeight());
      }

      if (SpawnPlacements.getPlacementType(type) == net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND) {
         BlockPos below = pos.below();
         if (level.getBlockState(below).isPathfindable(level, below, PathComputationType.LAND)) {
            return below;
         }
      }

      return pos.immutable();
   }

   public static BlockPos findSafePos(ServerLevel level, LivingEntity entity) {
      MutableBlockPos pos = entity.blockPosition().mutable();
      level.getPoiManager().ensureLoadedAndValid(level, pos, 16);
      double minX = level.getWorldBorder().getMinX();
      double maxX = level.getWorldBorder().getMaxX();
      double minZ = level.getWorldBorder().getMinZ();
      double maxZ = level.getWorldBorder().getMaxZ();
      EntityType<?> type = entity.getType();

      while (!NaturalSpawner.isSpawnPositionOk(SpawnPlacements.getPlacementType(type), level, pos, type)) {
         pos.set(minX + RANDOM.nextDouble() * (maxX - minX), 0.0, minZ + RANDOM.nextDouble() * (maxZ - minZ));
         level.getPoiManager().ensureLoadedAndValid(level, pos, 16);
         pos.set(getTopNonCollidingPos(level, type, pos.getX(), pos.getZ()));
      }

      return pos.immutable();
   }

   public static int getRGB24(Vector3f rgb) {
      return ARGB32.color(255, Math.round(rgb.x * 255.0F), Math.round(rgb.y * 255.0F), Math.round(rgb.z * 255.0F));
   }

   public static boolean isInSpawnProtection(ServerLevel level, BlockPos pos) {
      MinecraftServer server = level.getServer();
      if (level.dimension() != Level.OVERWORLD) {
         return false;
      } else {
         int protectionRadius = server.getSpawnProtectionRadius();
         if (protectionRadius <= 0) {
            return false;
         } else {
            BlockPos spawnPos = level.getSharedSpawnPos();
            int dx = Math.abs(pos.getX() - spawnPos.getX());
            int dz = Math.abs(pos.getZ() - spawnPos.getZ());
            return Math.max(dx, dz) <= protectionRadius;
         }
      }
   }

   public static boolean barrierHurt(BlockGetter getter, @Nullable LivingEntity entity, BlockPos pos, DamageSource src, float damage) {
      if (getter.getBlockEntity(pos) instanceof DomainBlockEntity be
         && be.getIdentifier() != null
         && entity != null
         && entity.level() instanceof ServerLevel level
         && level.getEntity(be.getIdentifier()) instanceof DomainBarrierEntity domain) {
         UUID identifier = be.getIdentifier();
         BlockPos currentPos = entity.blockPosition();
         boolean destroyable = identifier == null
            || !domain.isInsideBarrier(currentPos) && !domain.getShellBalance()
            || domain.isInsideBarrier(currentPos) && domain.getShellBalance();
         if (destroyable) {
            boolean success = domain.hurt(src, damage, entity, pos);
            return !(domain.getHealth() <= 0.0F);
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean isDestroyable(BlockGetter getter, @Nullable LivingEntity source, BlockPos pos) {
      if (!(Boolean)ConfigHolder.SERVER.destruction.get()) {
         return false;
      } else if (source != null && !(source instanceof Player) && !source.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         return false;
      } else if (source != null && !VeilHandler.canDestroy(source, source.level(), pos.getX(), pos.getY(), pos.getZ())) {
         return false;
      } else {
         BlockState state = getter.getBlockState(pos);
         boolean destroyable = !state.isAir() && !(source.level() instanceof ServerLevel level && isInSpawnProtection(level, pos));
         if (state.getBlock().defaultDestroyTime() <= -1.0F) {
            if (getter.getBlockEntity(pos) instanceof DomainBlockEntity be) {
               if (source != null
                  && source.level() instanceof ServerLevel levelx
                  && levelx.getEntity(be.getIdentifier()) instanceof DomainBarrierEntity domain
                  && domain.getHealth() <= 0.0F) {
                  destroyable = true;
               } else {
                  destroyable = false;
               }
            } else {
               destroyable = false;
            }
         }

         return destroyable;
      }
   }

   public static boolean isDestroyable(BlockGetter getter, @Nullable LivingEntity source, BlockPos pos, DamageSource src, float damage) {
      return barrierHurt(getter, source, pos, src, damage) ? false : isDestroyable(getter, source, pos);
   }

   public static Set<String> getRandomWordCombo(int count) {
      if (count > WORDS.length) {
         throw new IllegalArgumentException("Number of words requested exceeds the available word list.");
      } else {
         Set<String> combo = new HashSet<>();

         while (combo.size() < count) {
            combo.add(WORDS[RANDOM.nextInt(WORDS.length)]);
         }

         return combo;
      }
   }

   public static <E> E getWeightedRandom(Map<E, Double> weights, RandomSource random) {
      E result = null;
      double bestValue = Double.MAX_VALUE;

      for (E element : weights.keySet()) {
         double value = -Math.log(random.nextDouble()) / weights.get(element);
         if (value < bestValue) {
            bestValue = value;
            result = element;
         }
      }

      return result;
   }

   public static int getLevenshteinDistance(String x, String y) {
      int m = x.length();
      int n = y.length();
      int[][] T = new int[m + 1][n + 1];
      int i = 1;

      while (i <= m) {
         T[i][0] = i++;
      }

      i = 1;

      while (i <= n) {
         T[0][i] = i++;
      }

      for (int ix = 1; ix <= m; ix++) {
         for (int j = 1; j <= n; j++) {
            i = x.charAt(ix - 1) == y.charAt(j - 1) ? 0 : 1;
            T[ix][j] = Integer.min(Integer.min(T[ix - 1][j] + 1, T[ix][j - 1] + 1), T[ix - 1][j - 1] + i);
         }
      }

      return T[m][n];
   }

   public static float strcmp(String x, String y) {
      float max = Float.max(x.length(), y.length());
      return max > 0.0F ? 1.0F - (max - getLevenshteinDistance(x, y)) / max : 0.0F;
   }

   public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
      return clazz.getEnumConstants()[RANDOM.nextInt(((Enum[])clazz.getEnumConstants()).length)];
   }

   public static <T extends Enum<T>> T randomEnum(Class<T> clazz, Set<T> excluded) {
      if (!excluded.isEmpty()) {
         EnumSet<T> available = EnumSet.complementOf(EnumSet.copyOf(excluded));
         if (!available.isEmpty()) {
            return (T)available.toArray()[RANDOM.nextInt(available.size())];
         }
      }

      return clazz.getEnumConstants()[RANDOM.nextInt(((Enum[])clazz.getEnumConstants()).length)];
   }

   public static int toRGB24(int r, int g, int b, int a) {
      return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
   }
}
