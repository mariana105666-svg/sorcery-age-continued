package radon.jujutsu_kaisen.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.level.ExplosionEvent.Detonate;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.AbilityStopEvent;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.VeilBlock;
import radon.jujutsu_kaisen.block.VeilRodBlock;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.VeilEntity;
import radon.jujutsu_kaisen.entity.base.JJKPartEntity;
import radon.jujutsu_kaisen.entity.base.SummonEntity;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.entity.sorcerer.HeianSukunaEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.item.CursedEnergyFleshItem;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.PlayerUtil;
import radon.jujutsu_kaisen.util.SorcererUtil;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class JJKEventHandler {
   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class JJKEventHandlerForgeEvents {
      @SubscribeEvent
      public static void onExplosion(Detonate event) {
         Explosion explosion = event.getExplosion();
         LivingEntity instigator = explosion.getIndirectSourceEntity();
         Iterator<BlockPos> iter = explosion.getToBlow().iterator();

         while (iter.hasNext()) {
            BlockPos pos = iter.next();
            Vec3 center = pos.getCenter();
            if (!VeilHandler.canDestroy(instigator, event.getLevel(), center.x, center.y, center.z)) {
               iter.remove();
            }
         }
      }

      @SubscribeEvent
      public static void onLivingDrops(LivingDropsEvent event) {
         if (event.getEntity().getPersistentData().getBoolean("no_exp")) {
            event.setCanceled(true);
            System.out.println("no exp1");
         }
      }

      @SubscribeEvent
      public static void onLivingXpDrop(LivingExperienceDropEvent event) {
         LivingEntity entity = event.getEntity();
         if (entity.getPersistentData().getBoolean("no_exp")) {
            event.setDroppedExperience(0);
            System.out.println("no exp2");
         }
      }

      @SubscribeEvent
      public static void onEntityTeleport(EntityTeleportEvent event) {
         Level level = event.getEntity().level();
         if (!level.isClientSide) {
            BlockPos from = BlockPos.containing(event.getPrevX(), event.getPrevY(), event.getPrevZ());
            BlockPos to = BlockPos.containing(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            if (VeilHandler.isTeleportValid(level, from) && VeilHandler.isTeleportValid(level, to)) {
               ServerLevel serverLevel = (ServerLevel)level;
               if (!VeilHandler.getDomainBarriers(serverLevel, from).isEmpty() || !VeilHandler.getDomainBarriers(serverLevel, to).isEmpty()) {
                  event.setCanceled(true);
               }
            } else {
               System.out.println("invalid tp");
               event.setCanceled(true);
            }
         }
      }

      @SubscribeEvent
      public static void onDimensionTravel(EntityTravelToDimensionEvent event) {
         Entity entity = event.getEntity();
         System.out.println("no tp");
         if (!VeilHandler.isTeleportValid(entity.level(), entity.blockPosition())) {
            event.setCanceled(true);
         }
      }

      @SubscribeEvent
      public static void onLivingDestroyBlock(LivingDestroyBlockEvent event) {
         LivingEntity entity = event.getEntity();
         Vec3 center = event.getPos().getCenter();
         if (!VeilHandler.canDestroy(event.getEntity(), entity.level(), center.x, center.y, center.z)) {
            event.setCanceled(true);
         }
      }

      @SubscribeEvent
      public static void onBlockBreak(BreakEvent event) {
         Player player = event.getPlayer();
         BlockPos pos = event.getPos();
         Level level = (Level)event.getLevel();
         BlockState state = level.getBlockState(pos);
         Block block = state.getBlock();
         if (!(block instanceof VeilRodBlock) && !(block instanceof VeilBlock)) {
            Vec3 center = pos.getCenter();
            if (!VeilHandler.canDestroy(player, level, center.x, center.y, center.z, true)) {
               event.setCanceled(true);
               if (level instanceof ServerLevel serverLevel) {
                  serverLevel.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
               }
            }
         }
      }

      @SubscribeEvent
      public static void onBlockPlace(EntityPlaceEvent event) {
         Entity entity = event.getEntity();
         ServerLevel level = (ServerLevel)event.getLevel();
         BlockPos pos = event.getPos();
         if (!VeilHandler.getDomains(level, pos).isEmpty()) {
            event.setCanceled(true);
         }
      }

      @SubscribeEvent
      public static void onSleepFinished(SleepFinishedTimeEvent event) {
         if (event.getLevel() instanceof ServerLevel level) {
            for (ServerPlayer player : level.players()) {
               if (player.isSleeping() && player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                  ISorcererData cap = (ISorcererData)player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                  cap.setEnergy(cap.getMaxEnergy());
                  PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
               }
            }
         }
      }

      @SubscribeEvent
      public static void onAttackEntity(AttackEntityEvent event) {
         if (event.getTarget() instanceof JJKPartEntity) {
            Entity parent = ((JJKPartEntity)event.getTarget()).getParent();
            if (parent != null) {
               event.getEntity().attack(parent);
            }

            event.setCanceled(true);
         }
      }

      @SubscribeEvent
      public static void onPlayerRespawn(PlayerRespawnEvent event) {
         if (event.getEntity() instanceof ServerPlayer player) {
            if (player.getRespawnPosition() == null) {
               ServerLevel level = player.serverLevel();
               BlockPos spawn = level.getSharedSpawnPos();
               VeilEntity veil = VeilHandler.getVeilAt(level, spawn);
               if (veil != null) {
                  if (!veil.isBarrierOrInside(player.blockPosition())) {
                     player.teleportTo(level, spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, player.getYRot(), player.getXRot());
                  }
               }
            }
         }
      }

      @SubscribeEvent
      public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
         if (event.getEntity() instanceof ServerPlayer player) {
            for (SukunaEntity sukuna : player.level().getEntitiesOfClass(SukunaEntity.class, AABB.ofSize(player.position(), 8.0, 8.0, 8.0))) {
               if (sukuna.getOwner() == player) {
                  player.setGameMode(sukuna.getOriginal(player));
               }
            }
         }
      }

      @SubscribeEvent
      public static void onLivingDamage(LivingDamageEvent event) {
         LivingEntity victim = event.getEntity();
         if (event.getSource().getEntity() instanceof LivingEntity owner) {
            if (owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
               ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               if (cap.hasTrait(Trait.DEATH_PAINTING) && owner.getHealth() < owner.getMaxHealth() * 0.3F && HelperMethods.isMelee(event.getSource())) {
                  JJKEffectUtil.addEffect(victim, new MobEffectInstance(MobEffects.POISON, 200, 4));
                  JJKEffectUtil.addEffect(victim, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 0));
               }

               if (!(victim.getHealth() - event.getAmount() <= 0.0F)) {
                  cap.attack(event.getSource(), victim);
                  if (victim.isDeadOrDying()) {
                     event.setCanceled(true);
                  }
               }
            }
         }
      }

      @SubscribeEvent
      public static void onLivingTick(LivingTickEvent event) {
         LivingEntity owner = event.getEntity();
         if (!owner.isDeadOrDying()) {
            if (owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
               ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               cap.tick(owner);
               if (cap.hasTrait(Trait.SIX_EYES)
                     && !owner.getItemBySlot(EquipmentSlot.HEAD).is((Item)JJKItems.BLINDFOLD.get())
                     && !CuriosUtil.findSlot(owner, "head").is((Item)JJKItems.BLINDFOLD.get())
                  || cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                  JJKEffectUtil.addEffect(owner, new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
               }

               if (cap.hasTrait(Trait.DEATH_PAINTING) && owner.getHealth() / owner.getMaxHealth() < 0.3F) {
                  double x = owner.getX() + (owner.getRandom().nextDouble() - 0.5) * owner.getBbWidth();
                  double y = owner.getY() + owner.getBbHeight() * 0.5 + (owner.getRandom().nextDouble() - 0.5) * 0.5;
                  double z = owner.getZ() + (owner.getRandom().nextDouble() - 0.5) * owner.getBbWidth();
                  owner.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.defaultBlockState()), x, y, z, 0.0, 0.05, 0.0);
               }

               if (!owner.level().isClientSide) {
                  if (cap.hasToggled((Ability)JJKAbilities.SHRINK.get())) {
                     ScaleData baseScale = ScaleTypes.BASE.getScaleData(owner);
                     float targetScale = 0.5F;
                     float currentScale = baseScale.getScale();
                     float newScale = currentScale + (targetScale - currentScale) * 0.1F;
                     baseScale.setScale(newScale);
                     baseScale.markForSync(true);
                  } else if (cap.hasTrait(Trait.CURSED_WOMB)) {
                     if (owner instanceof Player && cap != null) {
                        float targetScale = 0.8F;
                        float targetWidth = 1.2F;
                        if (cap.checkWombAwakened()) {
                           targetScale = 1.1F;
                           targetWidth = 1.0F;
                        }

                        ScaleData baseScale = ScaleTypes.BASE.getScaleData(owner);
                        ScaleData baseWidth = ScaleTypes.WIDTH.getScaleData(owner);
                        baseScale.setScale(targetScale);
                        baseWidth.setScale(targetWidth);
                     }
                  } else {
                     ScaleData baseScale = ScaleTypes.BASE.getScaleData(owner);
                     ScaleData baseWidth = ScaleTypes.WIDTH.getScaleData(owner);
                     if (!baseScale.isReset() || !baseWidth.isReset()) {
                        baseWidth.resetScale(true);
                        baseScale.resetScale(true);
                     }
                  }
               }

               if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                  JJKEffectUtil.addEffect(owner, new MobEffectInstance(MobEffects.DIG_SPEED, 2, 1, false, false, false));
                  JJKEffectUtil.addEffect(owner, new MobEffectInstance(MobEffects.JUMP, 2, 2, false, false, false));
               }

               if (owner instanceof Player player
                  && (
                     cap.getType() == JujutsuType.SORCERER && (Boolean)ConfigHolder.SERVER.sorcererSaturation.get()
                        || cap.getType() == JujutsuType.CURSE && (Boolean)ConfigHolder.SERVER.curseSaturation.get()
                  )) {
                  player.getFoodData().setFoodLevel(20);
               }

               if (owner.getCapability(TenShadowsDataHandler.INSTANCE).isPresent()) {
                  ITenShadowsData shadowCap = (ITenShadowsData)owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
                  shadowCap.tick(owner);
               }
            }
         }
      }

      @SubscribeEvent
      public static void onLivingFall(LivingFallEvent event) {
         LivingEntity victim = event.getEntity();
         event.getEntity().getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
               event.setDistance(event.getDistance() * 0.1F);
            } else {
               event.setDistance(event.getDistance() * 0.33F);
            }
         });
      }

      @SubscribeEvent
      public static void onLivingAttack(LivingAttackEvent event) {
         DamageSource source = event.getSource();
         if (source.getEntity() instanceof LivingEntity attacker) {
            LivingEntity victim = event.getEntity();
            if (!victim.level().isClientSide) {
               if ((Boolean)ConfigHolder.SERVER.realisticCurses.get()) {
                  ItemStack stack = source.getDirectEntity() instanceof ThrownChainProjectile chain ? chain.getStack() : attacker.getItemInHand(InteractionHand.MAIN_HAND);
                  List<Item> stacks = new ArrayList<>();
                  stacks.add(stack.getItem());
                  stacks.addAll(
                     CuriosUtil.findSlots(attacker, attacker.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand")
                        .stream()
                        .map(ItemStack::getItem)
                        .toList()
                  );
                  if (!victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                     return;
                  }

                  ISorcererData cap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                  if (cap.getType() == JujutsuType.CURSE && !cap.hasTrait(Trait.DEATH_PAINTING)) {
                     boolean cursed = false;
                     if (event.getSource() instanceof JJKDamageSources.JujutsuDamageSource) {
                        cursed = true;
                     } else if (HelperMethods.isMelee(source) && stacks.stream().anyMatch(item -> item instanceof CursedToolItem)) {
                        cursed = true;
                     } else if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                        cursed = true;
                     }

                     if (!cursed) {
                        event.setCanceled(true);
                        return;
                     }
                  }
               }
            }
         }
      }

      @SubscribeEvent(
         priority = EventPriority.LOWEST
      )
      public static void onLivingHurt(LivingHurtEvent event) {
         LivingEntity victim = event.getEntity();
         if (!victim.level().isClientSide) {
            DamageSource source = event.getSource();
            if (!source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
               if (source instanceof JJKDamageSources.JujutsuDamageSource) {
                  if (source.getEntity() instanceof LivingEntity sourceUser) {
                     ISorcererData capSelf = (ISorcererData)sourceUser.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
                     if (capSelf != null && source.getEntity() == victim && !capSelf.hasSelfHit()) {
                        event.setAmount(event.getAmount() * 0.1F);
                     }
                  }
               } else if ((Boolean)ConfigHolder.SERVER.realisticCurses.get()
                  && source.getEntity() instanceof LivingEntity sourceUserx
                  && sourceUserx.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                  ISorcererData attackerCap = (ISorcererData)sourceUserx.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                  if (attackerCap.getEnergy() <= 0.0F) {
                     ItemStack stack = source.getDirectEntity() instanceof ThrownChainProjectile chain
                        ? chain.getStack()
                        : sourceUserx.getItemInHand(InteractionHand.MAIN_HAND);
                     List<Item> stacks = new ArrayList<>();
                     stacks.add(stack.getItem());
                     stacks.addAll(
                        CuriosUtil.findSlots(sourceUserx, sourceUserx.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand")
                           .stream()
                           .map(ItemStack::getItem)
                           .toList()
                     );
                     if (!stacks.stream().anyMatch(item -> item instanceof CursedToolItem)) {
                        ISorcererData victimCap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
                        if (victimCap != null && victimCap.getType() == JujutsuType.CURSE && !victimCap.hasTrait(Trait.DEATH_PAINTING)) {
                           event.setAmount(0.0F);
                           event.setCanceled(true);
                           return;
                        }
                     }
                  }
               } else if ((Boolean)ConfigHolder.SERVER.realisticCurses.get()) {
                  ISorcererData victimCap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
                  if (victimCap != null
                     && victimCap.getType() == JujutsuType.CURSE
                     && (!victimCap.hasTrait(Trait.DEATH_PAINTING) || source.is(DamageTypes.MAGIC))) {
                     event.setAmount(0.0F);
                     event.setCanceled(true);
                     return;
                  }
               }

               Entity attackerEntity = source.getEntity();
               if (attackerEntity instanceof Projectile projectile) {
                  attackerEntity = projectile.getOwner();
               }

               if (attackerEntity instanceof LivingEntity attacker) {
                  if (!(attackerEntity instanceof Player) && attackerEntity instanceof CursedSpirit) {
                     event.setAmount(event.getAmount() * ((Double)ConfigHolder.SERVER.curseDamageMult.get()).floatValue());
                  }

                  if (!(victim instanceof Player) && victim instanceof CursedSpirit) {
                     event.setAmount(event.getAmount() * ((Double)ConfigHolder.SERVER.curseDefenseMult.get()).floatValue());
                  }

                  if (!(attackerEntity instanceof Player) && attackerEntity instanceof SorcererEntity sorc && sorc.getJujutsuType() == JujutsuType.SORCERER) {
                     event.setAmount(event.getAmount() * ((Double)ConfigHolder.SERVER.sorcererDamageMult.get()).floatValue());
                  }

                  if (!(victim instanceof Player) && victim instanceof SorcererEntity sorc && sorc.getJujutsuType() == JujutsuType.SORCERER) {
                     event.setAmount(event.getAmount() * ((Double)ConfigHolder.SERVER.sorcererDefenseMult.get()).floatValue());
                  }

                  if (attackerEntity instanceof Player
                     || attackerEntity instanceof SummonEntity sum && sum.isTame() && sum.getOwner() instanceof Player
                     || attackerEntity instanceof CursedSpirit curse && curse.isTame() && curse.getOwner() instanceof Player) {
                     event.setAmount(event.getAmount() * ((Double)ConfigHolder.SERVER.playerDamageMult.get()).floatValue());
                  }

                  if (JJKAbilities.hasTrait(attacker, Trait.PERFECT_BODY)) {
                     attacker.getCapability(SorcererDataHandler.INSTANCE).ifPresent(capSelf -> {
                        if (HelperMethods.isMelee(source) && !capSelf.hasToggled((Ability)JJKAbilities.HOLLOW_WICKER_BASKET.get())) {
                           event.setAmount(event.getAmount() * ((Double)ConfigHolder.SERVER.perfectBodyMult.get()).floatValue());
                        }
                     });
                  }

                  if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                     ISorcererData victimcap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                     if (victimcap != null && victimcap.hasExtraMeleeTaken() && HelperMethods.isMelee(source)) {
                        event.setAmount(event.getAmount() * 1.35F);
                     }
                  }

                  if (!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
                     if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                        ISorcererData cap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                        float armor = SorcererUtil.getDefense(cap.getExperience());
                        if (victim instanceof Player) {
                           armor *= ((Double)ConfigHolder.SERVER.jujutsuDefenseMult.get()).floatValue();
                        }

                        if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                           armor = SorcererUtil.getDefenseHR(cap.getExperience());
                           if (victim instanceof Player) {
                              armor *= ((Double)ConfigHolder.SERVER.hrDefenseMult.get()).floatValue();
                           }
                        }

                        if (!(victim instanceof Player)
                           && !(attackerEntity instanceof Player)
                           && !(victim instanceof CursedSpirit cursex && cursex.isTame())
                           && !(victim instanceof SummonEntity summonx && summonx.isTame())
                           && !(attackerEntity instanceof CursedSpirit curse && curse.isTame())
                           && !(attackerEntity instanceof SummonEntity summon && summon.isTame())) {
                           event.setAmount(event.getAmount() * ((Double)ConfigHolder.SERVER.npcvsnpcDamageMult.get()).floatValue());
                        }

                        float blocked = event.getAmount() / armor;
                        event.setAmount(blocked);
                     }
                  }
               }
            }
         }
      }

      @SubscribeEvent
      public static void onLivingDeath(LivingDeathEvent event) {
         LivingEntity victim = event.getEntity();
         if (!victim.level().isClientSide) {
            if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
               ISorcererData victimCap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               switch (victimCap.getType()) {
                  case SORCERER:
                     if (HelperMethods.RANDOM.nextInt((Integer)ConfigHolder.SERVER.sorcererFleshRarity.get()) == 0) {
                        ItemStack stack = new ItemStack((ItemLike)JJKItems.SORCERER_FLESH.get());
                        CursedEnergyFleshItem.setGrade(stack, SorcererUtil.getGrade(victimCap.getExperience()));
                        victim.spawnAtLocation(stack);
                     }
                     break;
                  case CURSE:
                     if (HelperMethods.RANDOM.nextInt((Integer)ConfigHolder.SERVER.curseFleshRarity.get()) == 0) {
                        ItemStack stack = new ItemStack((ItemLike)JJKItems.CURSE_FLESH.get());
                        CursedEnergyFleshItem.setGrade(stack, SorcererUtil.getGrade(victimCap.getExperience()));
                        victim.spawnAtLocation(stack);
                     }
               }

               if (victimCap.hasTrait(Trait.DEATH_PAINTING) && victim.level() instanceof ServerLevel serverLevel) {
                  for (ServerPlayer player : serverLevel.players()) {
                     if (player != victim && player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                        ISorcererData cap = (ISorcererData)player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                        if (cap.hasTrait(Trait.DEATH_PAINTING)) {
                           player.sendSystemMessage(Component.translatable(String.format("chat.%s.siblingdeath", "jujutsu_kaisen"), new Object[]{victim.getName()}));
                        }
                     }
                  }
               }

               DamageSource source = event.getSource();
               if (source.getEntity() instanceof LivingEntity attacker) {
                  if (attacker instanceof ServerPlayer playerx && victim instanceof HeianSukunaEntity && victimCap.getFingers() == 20) {
                     PlayerUtil.giveAdvancement(playerx, "the_strongest_of_all_time");
                  }
               }
            }
         }
      }

      @SubscribeEvent
      public static void onAbilityStop(AbilityStopEvent event) {
         Ability ability = event.getAbility();
         CursedTechnique technique = JJKAbilities.getTechnique(ability);
         LivingEntity owner = event.getEntity();
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (event.getAbility() instanceof Ability.IPosedMove posed && !posed.getArmPose(owner).autocancel()) {
            cap.removeAbilityPose(owner, posed);
         }

         if (technique != null && cap.getAbsorbed().contains(technique)) {
            cap.unabsorb(technique);
         }
      }

      @SubscribeEvent
      public static void onAbilityTrigger(AbilityTriggerEvent.Pre event) {
         Ability ability = event.getAbility();
         CursedTechnique technique = JJKAbilities.getTechnique(ability);
         LivingEntity owner = event.getEntity();
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (event.getAbility() instanceof Ability.IPosedMove posed) {
            cap.addAbilityPose(owner, posed, posed.getHand(owner), posed.poseTimer(owner));
         }

         if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT && technique != null && cap.getAbsorbed().contains(technique)) {
            cap.unabsorb(technique);
         }
      }
   }
}
