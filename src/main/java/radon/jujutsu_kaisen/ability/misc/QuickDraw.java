package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class QuickDraw extends Ability implements Ability.IToggled {
   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.TOGGLED;
   }

   private static void attack(LivingEntity owner, LivingEntity entity) {
      if (entity.invulnerableTime <= 0) {
         owner.lookAt(Anchor.EYES, entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0));
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

         for (int i = 0; i < Barrage.DURATION; i++) {
            boolean last = i == Barrage.DURATION - 1;
            cap.delayTickEvent(
               () -> {
                  SimpleDomainEntity domain = cap.getSummonByClass(SimpleDomainEntity.class);
                  if (domain != null) {
                     if (cap.hasToggled((Ability)JJKAbilities.QUICK_DRAW.get()) && entity.distanceTo(domain) <= domain.getRadius()) {
                        cap.useEnergy(3.0F);
                        if (owner instanceof ServerPlayer player) {
                           PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                        }

                        owner.swing(InteractionHand.MAIN_HAND, true);
                        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);

                        for (int j = 0; j < 4; j++) {
                           Vec3 pos = owner.getEyePosition().add(look.scale(owner.distanceTo(entity)));
                           ((ServerLevel)owner.level())
                              .sendParticles(
                                 owner.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SwordItem ? ParticleTypes.SWEEP_ATTACK : ParticleTypes.CLOUD,
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

                        for (int j = 0; j < 4; j++) {
                           Vec3 pos = owner.getEyePosition().add(look.scale(owner.distanceTo(entity)));
                           ((ServerLevel)owner.level())
                              .sendParticles(
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
                        owner.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_SMALL_FALL, SoundSource.MASTER, 1.0F, 0.3F);
                        if (owner instanceof Player player) {
                           player.attack(entity);
                        } else {
                           owner.doHurtTarget(entity);
                        }

                        if (!last) {
                           entity.invulnerableTime = 1;
                        }
                     }
                  }
               },
               i
            );
         }
      }
   }

   @Override
   public void run(LivingEntity owner) {
      if (!owner.level().isClientSide) {
         JJKEffectUtil.addEffect(owner, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), 2, 1, false, false, false));
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.SIMPLE_DOMAIN.get())) {
            SimpleDomainEntity domain = cap.getSummonByClass(SimpleDomainEntity.class);
            if (domain == null) {
               return;
            }

            for (Entity entity : owner.level().getEntities(owner, domain.getBoundingBox())) {
               if (entity != domain && !(entity.distanceTo(domain) > domain.getRadius()) && entity instanceof LivingEntity living && !living.isDeadOrDying()) {
                  attack(owner, living);
               }
            }
         }
      }
   }

   @Override
   public void onEnabled(LivingEntity owner) {
   }

   @Override
   public void onDisabled(LivingEntity owner) {
   }

   @Override
   public int getCooldown() {
      return 100;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.5F;
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      return (
            JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.SIMPLE_DOMAIN.get())
               || JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.FALLING_BLOSSOM_EMOTION.get())
         )
         && super.isValid(owner);
   }

   @Nullable
   @Override
   public Ability getParent(LivingEntity owner) {
      return (Ability)JJKAbilities.SIMPLE_DOMAIN.get();
   }

   @Override
   public int getPointsCost() {
      return (Integer)ConfigHolder.SERVER.quickDrawCost.get();
   }

   @Override
   public Vec2 getDisplayCoordinates() {
      return new Vec2(5.0F, 5.0F);
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.DOMAIN;
   }

   @Override
   public boolean isDisplayed(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      CursedTechnique technique = cap.getTechnique();
      CursedEnergyNature nature = cap.getNature();
      return ((Boolean)ConfigHolder.SERVER.newShadowStyleForAll.get() || technique == CursedTechnique.TECHNIQUELESS && nature != CursedEnergyNature.DIVERGENT)
         && super.isDisplayed(owner);
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class ForgeEvents {
      @SubscribeEvent
      public static void onLivingAttack(LivingAttackEvent event) {
         LivingEntity victim = event.getEntity();
         if (!victim.level().isClientSide && victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData cap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if (cap.hasToggled((Ability)JJKAbilities.QUICK_DRAW.get()) || cap.hasToggled((Ability)JJKAbilities.FALLING_BLOSSOM_EMOTION.get())) {
               if (event.getSource().getDirectEntity() instanceof Projectile projectile
                  && !(event.getSource().getDirectEntity() instanceof JujutsuProjectile pro && !pro.canDeflect())) {
                  ItemStack stack = victim.getItemInHand(InteractionHand.MAIN_HAND);
                  if (stack.getItem() instanceof SwordItem) {
                     int amount = Math.round(event.getAmount());
                     int remaining = stack.getMaxDamage() - stack.getDamageValue();
                     int blocked = Math.min(remaining, amount);
                     float reduced = amount - blocked;
                     if (!(reduced > 0.0F)) {
                        victim.lookAt(Anchor.EYES, projectile.position().add(0.0, projectile.getBbHeight() / 2.0F, 0.0));
                        victim.swing(InteractionHand.MAIN_HAND, true);
                        stack.hurtAndBreak(1, victim, entity -> entity.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                        cap.useEnergy(3.0F);
                        if (victim instanceof ServerPlayer player) {
                           PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                        }

                        event.setCanceled(true);
                     }
                  }
               }
            }
         }
      }

      @SubscribeEvent
      public static void onLivingHurt(LivingHurtEvent event) {
         LivingEntity victim = event.getEntity();
         if (!victim.level().isClientSide && victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData cap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if (cap.hasToggled((Ability)JJKAbilities.QUICK_DRAW.get()) || cap.hasToggled((Ability)JJKAbilities.FALLING_BLOSSOM_EMOTION.get())) {
               if (event.getSource().getDirectEntity() instanceof Projectile projectile
                  && !(event.getSource().getDirectEntity() instanceof JujutsuProjectile pro && !pro.canDeflect())) {
                  ItemStack stack = victim.getItemInHand(InteractionHand.MAIN_HAND);
                  if (stack.getItem() instanceof SwordItem) {
                     int amount = Math.round(event.getAmount());
                     int remaining = stack.getMaxDamage() - stack.getDamageValue();
                     int blocked = Math.min(remaining, amount);
                     victim.lookAt(Anchor.EYES, projectile.position().add(0.0, projectile.getBbHeight() / 2.0F, 0.0));
                     victim.swing(InteractionHand.MAIN_HAND, true);
                     stack.hurtAndBreak(1, victim, entity -> entity.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                     cap.useEnergy(3.0F);
                     if (victim instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                     }

                     float reduced = amount - blocked;
                     event.setAmount(reduced);
                  }
               }
            }
         }
      }
   }
}
