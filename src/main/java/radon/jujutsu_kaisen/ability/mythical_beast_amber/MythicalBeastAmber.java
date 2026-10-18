package radon.jujutsu_kaisen.ability.mythical_beast_amber;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.ability.base.Transformation;
import radon.jujutsu_kaisen.block.entity.IDomainBarrier;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.sorcerer.HeianSukunaEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

public class MythicalBeastAmber extends Transformation {
   private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("81461f5f-89d5-4cc9-8b25-17e7caac9255");
   private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("84341016-e56a-4b95-9fd5-42b36154c885");
   private static final UUID STEP_HEIGHT_UUID = UUID.fromString("654c65b5-dc0f-4092-8423-59cbe3d19682");
   private static final UUID ARMOR_UUID = UUID.fromString("486fd273-fdbc-4876-b0b8-af5a64bfb08a");
   private static final UUID ARMOR_TOUGHNESS_UUID = UUID.fromString("0be71dde-8aeb-4c5d-955f-d37325c31a94");
   private final Set<LivingEntity> glowingEntities = new HashSet<>();
   private int r;
   private int g;
   private int b;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return JJKAbilities.hasToggled(owner, this)
         ? true
         : target != null && (target instanceof SukunaEntity sukun || target instanceof HeianSukunaEntity bigsuku);
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.TOGGLED;
   }

   @Override
   public void run(LivingEntity owner) {
      Set<LivingEntity> newGlows = new HashSet<>();

      for (Entity entity : owner.level().getEntities(null, owner.getBoundingBox().inflate(15.0))) {
         if (entity.isAlive() && entity instanceof LivingEntity living && living != owner) {
            if (!this.glowingEntities.contains(living)) {
               living.setGlowingTag(true);
            }

            newGlows.add(living);
         }
      }

      for (LivingEntity prev : this.glowingEntities) {
         if (!newGlows.contains(prev) && prev.isAlive()) {
            prev.setGlowingTag(false);
         }
      }

      this.glowingEntities.clear();
      this.glowingEntities.addAll(newGlows);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 2.0F;
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @Override
   public boolean isReplacement() {
      return false;
   }

   @Override
   public Item getItem() {
      return (Item)JJKItems.MYTHICAL_BEAST_AMBER.get();
   }

   @Override
   public ITransformation.Part getBodyPart() {
      return ITransformation.Part.BODY;
   }

   @Override
   public void applyModifiers(LivingEntity owner) {
      EntityUtil.applyModifier(owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", 1.75, Operation.MULTIPLY_TOTAL);
      EntityUtil.applyModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed", 0.4, Operation.MULTIPLY_TOTAL);
      EntityUtil.applyModifier(owner, (Attribute)ForgeMod.STEP_HEIGHT_ADDITION.get(), STEP_HEIGHT_UUID, "Step height addition", 2.0, Operation.ADDITION);
   }

   @Override
   public void removeModifiers(LivingEntity owner) {
      EntityUtil.removeModifier(owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
      EntityUtil.removeModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID);
      EntityUtil.removeModifier(owner, (Attribute)ForgeMod.STEP_HEIGHT_ADDITION.get(), STEP_HEIGHT_UUID);
   }

   @Override
   public void onEnabled(LivingEntity owner) {
      Vector3f oldcolor = ParticleColors.getCursedEnergyColor(owner);
      this.r = (int)(oldcolor.x * 255.0);
      this.g = (int)(oldcolor.y * 255.0);
      this.b = (int)(oldcolor.z * 255.0);
      this.r = Math.max(0, Math.min(255, this.r));
      this.g = Math.max(0, Math.min(255, this.g));
      this.b = Math.max(0, Math.min(255, this.b));
      int color = ARGB32.color(255, Math.round(156.0F), Math.round(95.0F), Math.round(255.0F));
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      cap.setCursedEnergyColor(color);
      cap.maxOutput();
   }

   @Override
   public void onDisabled(LivingEntity owner) {
      for (LivingEntity entity : this.glowingEntities) {
         if (entity.isAlive()) {
            entity.setGlowingTag(false);
         }
      }

      this.glowingEntities.clear();
      ISorcererData ownerCap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if ((Boolean)ConfigHolder.SERVER.MBAEXPReset.get()) {
         ownerCap.setExperience(0.0F);
      }

      int color = ARGB32.color(255, Math.round((float)this.r), Math.round((float)this.g), Math.round((float)this.b));
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      cap.setCursedEnergyColor(color);
      if (owner instanceof ServerPlayer play && (Boolean)ConfigHolder.SERVER.MBAReroll.get()) {
         cap.wipe(play);
      }

      if ((Boolean)ConfigHolder.SERVER.MBADeath.get() && !owner.isDeadOrDying()) {
         owner.kill();
      }
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class SoulReinforcementForgeEvents {
      @SubscribeEvent(
         priority = EventPriority.LOWEST
      )
      public static void onLivingDamage(LivingDamageEvent event) {
         DamageSource source = event.getSource();
         LivingEntity victim = event.getEntity();
         if (!victim.level().isClientSide) {
            if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
               ISorcererData victimCap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               if (victimCap.hasToggled((Ability)JJKAbilities.MYTHICAL_BEAST_AMBER.get())) {
                  if (source.getEntity() instanceof LivingEntity attacker) {
                     if (!attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                        return;
                     }

                     ISorcererData attackerCap = (ISorcererData)attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                     if (HelperMethods.isMelee(source) && attackerCap.hasToggled((Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                        return;
                     }
                  }

                  if (!source.is(JJKDamageSources.SOUL)
                     && !source.is(JJKDamageSources.SPLIT_SOUL_KATANA)
                     && !(source instanceof JJKDamageSources.JujutsuDamageSource jujutsu && jujutsu.getAbility() == JJKAbilities.OUTPUT_RCT.get())) {
                     for (IDomainBarrier domain : VeilHandler.getDomainBarriers((ServerLevel)victim.level(), victim.blockPosition())) {
                        for (DomainExpansionEntity d : domain.getClashers()) {
                           if (d.getOwner() == source.getEntity()) {
                              return;
                           }
                        }
                     }

                     float cost = event.getAmount()
                        * 10.0F
                        * (victimCap.hasTrait(Trait.SIX_EYES) ? ((Double)ConfigHolder.SERVER.sixEyesMult.get()).floatValue() : 1.0F);
                     if (!(victimCap.getEnergy() < cost)) {
                        victimCap.useEnergy(cost);
                        int count = 8 + (int)(victim.getBbWidth() * victim.getBbHeight()) * 16;

                        for (int i = 0; i < count; i++) {
                           double x = victim.getX()
                              + (HelperMethods.RANDOM.nextDouble() - 0.5) * (victim.getBbWidth() * 2.0F)
                              - victim.getLookAngle().scale(0.35).x;
                           double y = victim.getY() + HelperMethods.RANDOM.nextDouble() * victim.getBbHeight();
                           double z = victim.getZ()
                              + (HelperMethods.RANDOM.nextDouble() - 0.5) * (victim.getBbWidth() * 2.0F)
                              - victim.getLookAngle().scale(0.35).z;
                           ((ServerLevel)victim.level())
                              .sendParticles(
                                 new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColorBright(victim), 0.2F, 1),
                                 x,
                                 y,
                                 z,
                                 0,
                                 0.0,
                                 0.0,
                                 0.0,
                                 0.0
                              );
                        }

                        victim.level()
                           .playSound(
                              null,
                              victim.getX(),
                              victim.getY(),
                              victim.getZ(),
                              (SoundEvent)JJKSounds.ELECTRICITY.get(),
                              SoundSource.MASTER,
                              1.0F,
                              1.0F
                           );
                        if (victim instanceof ServerPlayer player) {
                           PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(victimCap.serializeNBT()), player);
                        }

                        event.setCanceled(true);
                     }
                  }
               }
            }
         }
      }
   }
}
