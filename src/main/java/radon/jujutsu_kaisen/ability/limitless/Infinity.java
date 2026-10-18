package radon.jujutsu_kaisen.ability.limitless;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.IAdditionalAdaptation;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Infinity extends Ability implements Ability.IToggled, IAdditionalAdaptation {
   private static final double SLOWING_FACTOR = 1.0E-4;
   private static final double RANGE = 2.0;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return true;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.TOGGLED;
   }

   @Override
   public void run(LivingEntity owner) {
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.8F;
   }

   @Override
   public void onEnabled(LivingEntity owner) {
   }

   @Override
   public int getAdditional() {
      return 1;
   }

   @Override
   public void onDisabled(LivingEntity owner) {
   }

   public static class FrozenProjectileData extends SavedData {
      public static final String IDENTIFIER = "frozen_projectile_data";
      private final Map<UUID, Infinity.FrozenProjectileData.FrozenProjectileNBT> frozen = new HashMap<>();

      public static Infinity.FrozenProjectileData load(CompoundTag pCompoundTag) {
         Infinity.FrozenProjectileData data = new Infinity.FrozenProjectileData();

         for (Tag tag : pCompoundTag.getList("frozen", 10)) {
            Infinity.FrozenProjectileData.FrozenProjectileNBT nbt = new Infinity.FrozenProjectileData.FrozenProjectileNBT((CompoundTag)tag);
            data.frozen.put(nbt.getTarget(), nbt);
         }

         return data;
      }

      @NotNull
      public CompoundTag save(CompoundTag pCompoundTag) {
         ListTag frozenTag = new ListTag();
         frozenTag.addAll(this.frozen.values());
         pCompoundTag.put("frozen", frozenTag);
         return pCompoundTag;
      }

      public void add(LivingEntity source, Projectile target) {
         if (!this.frozen.containsKey(target.getUUID())) {
            this.frozen.put(target.getUUID(), new Infinity.FrozenProjectileData.FrozenProjectileNBT(source, target));
            this.setDirty();
         }
      }

      public void tick(ServerLevel level) {
         Iterator<Infinity.FrozenProjectileData.FrozenProjectileNBT> iter = this.frozen.values().iterator();

         while (iter.hasNext()) {
            Infinity.FrozenProjectileData.FrozenProjectileNBT nbt = iter.next();
            Entity projectile = level.getEntity(nbt.getTarget());
            if (level.getEntity(nbt.getSource()) instanceof LivingEntity owner && !owner.isDeadOrDying()) {
               if (projectile == null) {
                  iter.remove();
                  this.setDirty();
               } else {
                  Vec3 forward = projectile.getLookAngle();
                  Vec3 start = owner.position().add(forward.scale(owner.getBbWidth() / 2.0F));
                  Vec3 end = projectile.position().add(forward.scale(-projectile.getBbWidth() / 2.0F));
                  float dx = (float)(start.x - end.x);
                  float dy = (float)(start.y - end.y);
                  float dz = (float)(start.z - end.z);
                  float distance = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
                  if (JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.INFINITY.get()) && distance <= 2.0) {
                     Vec3 original = nbt.getMovement();
                     projectile.setDeltaMovement(original.scale(Math.min(1.0E-4, distance * 1.0E-4)));
                     projectile.hurtMarked = true;
                  } else {
                     projectile.setDeltaMovement(nbt.getMovement());
                     projectile.hurtMarked = true;
                     iter.remove();
                     this.setDirty();
                  }
               }
            } else {
               if (projectile != null) {
                  projectile.setDeltaMovement(nbt.getMovement());
                  projectile.hurtMarked = true;
               }

               iter.remove();
               this.setDirty();
            }
         }
      }

      private static class FrozenProjectileNBT extends CompoundTag {
         public FrozenProjectileNBT(LivingEntity source, Projectile target) {
            this.putUUID("source", source.getUUID());
            this.putUUID("target", target.getUUID());
            Vec3 movement = target.getDeltaMovement();
            this.putDouble("movement_x", movement.x);
            this.putDouble("movement_y", movement.y);
            this.putDouble("movement_z", movement.z);
         }

         public FrozenProjectileNBT(CompoundTag nbt) {
            this.putUUID("source", nbt.getUUID("source"));
            this.putUUID("target", nbt.getUUID("target"));
            this.putDouble("movement_x", nbt.getDouble("movement_x"));
            this.putDouble("movement_y", nbt.getDouble("movement_y"));
            this.putDouble("movement_z", nbt.getDouble("movement_z"));
         }

         public UUID getSource() {
            return this.getUUID("source");
         }

         public UUID getTarget() {
            return this.getUUID("target");
         }

         public Vec3 getMovement() {
            double x = this.getDouble("movement_x");
            double y = this.getDouble("movement_y");
            double z = this.getDouble("movement_z");
            return new Vec3(x, y, z);
         }
      }
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class InfinityForgeEvents {
      @SubscribeEvent
      public static void onLevelTick(LevelTickEvent event) {
         if (event.phase != Phase.START) {
            if (event.level instanceof ServerLevel level) {
               Infinity.FrozenProjectileData data = (Infinity.FrozenProjectileData)level.getDataStorage()
                  .computeIfAbsent(Infinity.FrozenProjectileData::load, Infinity.FrozenProjectileData::new, "frozen_projectile_data");
               data.tick(level);
            }
         }
      }

      @SubscribeEvent
      public static void onProjectileImpact(ProjectileImpactEvent event) {
         if (event.getRayTraceResult() instanceof EntityHitResult hit) {
            if (hit.getEntity() instanceof LivingEntity owner) {
               if (owner.level() instanceof ServerLevel level) {
                  if (JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.INFINITY.get())) {
                     Infinity.FrozenProjectileData data = (Infinity.FrozenProjectileData)level.getDataStorage()
                        .computeIfAbsent(Infinity.FrozenProjectileData::load, Infinity.FrozenProjectileData::new, "frozen_projectile_data");
                     Projectile projectile = event.getProjectile();
                     if (HelperMethods.isBlockable(owner, projectile)) {
                        data.add(owner, projectile);
                        event.setCanceled(true);
                     }
                  }
               }
            }
         }
      }

      @SubscribeEvent
      public static void onLivingTick(LivingTickEvent event) {
         LivingEntity target = event.getEntity();
         if (target.level() instanceof ServerLevel level) {
            Infinity.FrozenProjectileData var6 = (Infinity.FrozenProjectileData)level.getDataStorage()
               .computeIfAbsent(Infinity.FrozenProjectileData::load, Infinity.FrozenProjectileData::new, "frozen_projectile_data");
            if (JJKAbilities.hasToggled(target, (Ability)JJKAbilities.INFINITY.get())) {
               for (Projectile projectile : target.level().getEntitiesOfClass(Projectile.class, target.getBoundingBox().inflate(2.0))) {
                  if (HelperMethods.isBlockable(target, projectile)) {
                     var6.add(target, projectile);
                  }
               }
            }
         }
      }

      @SubscribeEvent(
         priority = EventPriority.LOW
      )
      public static void onLivingAttack(LivingAttackEvent event) {
         LivingEntity target = event.getEntity();
         if (!target.level().isClientSide) {
            if (JJKAbilities.hasToggled(target, (Ability)JJKAbilities.INFINITY.get())) {
               DamageSource source = event.getSource();
               if (HelperMethods.isBlockable(target, source)) {
                  if (!(source.getDirectEntity() instanceof Projectile)) {
                     if (source.getEntity() instanceof LivingEntity attacker) {
                        ISorcererData cap = (ISorcererData)attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
                        if (cap != null) {
                           if (cap.hasToggled((Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                              target.level()
                                 .playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.MASTER, 1.0F, 1.0F);
                              return;
                           }

                           if (attacker.getCapability(TenShadowsDataHandler.INSTANCE).isPresent() && attacker instanceof MahoragaEntity maho) {
                              ITenShadowsData shadowCap = (ITenShadowsData)maho.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
                              if (shadowCap.isAdaptedTo((Ability)JJKAbilities.INFINITY.get())) {
                                 return;
                              }
                           }
                        }
                     }

                     target.level()
                        .playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.MASTER, 1.0F, 1.0F);
                  }

                  event.setCanceled(true);
               }
            }
         }
      }
   }
}
