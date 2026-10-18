package radon.jujutsu_kaisen.event;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.cursed_tool.DragonBoneItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class WeaponEventHandler {
   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class WeaponEventHandlerForgeEvents {
      @SubscribeEvent
      public static void onLivingAttack(LivingAttackEvent event) {
         DamageSource source = event.getSource();
         if (source.getEntity() instanceof LivingEntity attacker) {
            LivingEntity victim = event.getEntity();
            if (!victim.level().isClientSide) {
               ItemStack stack = source.getDirectEntity() instanceof ThrownChainProjectile chain ? chain.getStack() : attacker.getItemInHand(InteractionHand.MAIN_HAND);
               List<Item> stacks = new ArrayList<>();
               stacks.add(stack.getItem());
               stacks.addAll(
                  CuriosUtil.findSlots(attacker, attacker.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand")
                     .stream()
                     .map(ItemStack::getItem)
                     .toList()
               );
               if (HelperMethods.isMelee(source)) {
                  if (stacks.contains(JJKItems.PLAYFUL_CLOUD.get())) {
                     Vec3 pos = attacker.getEyePosition().add(RotationUtil.getTargetAdjustedLookAngle(attacker));
                     attacker.level()
                        .explode(
                           attacker,
                           attacker.damageSources().explosion(attacker, null),
                           null,
                           pos.x,
                           pos.y,
                           pos.z,
                           1.0F,
                           false,
                           ExplosionInteraction.NONE
                        );
                  }

                  if (stacks.contains(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get())
                     && (!(Boolean)ConfigHolder.SERVER.hrRequiredForISOH.get() || JJKAbilities.hasTrait(attacker, Trait.HEAVENLY_RESTRICTION))) {
                     victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                        cap.setDisable(5);
                        if (cap.hasTechnique(CursedTechnique.BRAIN_TRANSPLANT)) {
                           JJKEffectUtil.addEffect(victim, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), 4, 0, false, false, false));
                        }

                        if (victim instanceof ServerPlayer playerx) {
                           PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), playerx);
                        }
                     });
                  }

                  if (stacks.contains(JJKItems.KAMUTOKE_DAGGER.get()) && attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                     ISorcererData attackerCap = (ISorcererData)attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                     if (!(attacker instanceof Player player && player.getAbilities().instabuild)) {
                        float cost = 20.0F;
                        if (attackerCap.getEnergy() < cost) {
                           return;
                        }

                        attackerCap.useEnergy(cost);
                     }

                     JJKEffectUtil.addEffect(victim, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), 5, 0, false, false, false));
                     attacker.level()
                        .playSound(
                           null,
                           victim.getX(),
                           victim.getY(),
                           victim.getZ(),
                           SoundEvents.LIGHTNING_BOLT_IMPACT,
                           SoundSource.MASTER,
                           1.0F,
                           0.5F + HelperMethods.RANDOM.nextFloat() * 0.2F
                        );

                     for (int i = 0; i < 32; i++) {
                        double offsetX = HelperMethods.RANDOM.nextGaussian() * 1.5;
                        double offsetY = HelperMethods.RANDOM.nextGaussian() * 1.5;
                        double offsetZ = HelperMethods.RANDOM.nextGaussian() * 1.5;
                        ((ServerLevel)attacker.level())
                           .sendParticles(
                              new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColorBright(attacker), 0.5F, 1),
                              victim.getX() + offsetX,
                              victim.getY() + offsetY,
                              victim.getZ() + offsetZ,
                              0,
                              0.0,
                              0.0,
                              0.0,
                              0.0
                           );
                     }

                     if (attacker instanceof ServerPlayer player) {
                        PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(attackerCap.serializeNBT()), player);
                     }
                  }
               }
            }
         }
      }

      @SubscribeEvent
      public static void onLivingDamage(LivingDamageEvent event) {
         DamageSource source = event.getSource();
         if (source.getEntity() instanceof LivingEntity attacker) {
            LivingEntity victim = event.getEntity();
            if (!victim.level().isClientSide) {
               ItemStack stack = source.getDirectEntity() instanceof ThrownChainProjectile chain ? chain.getStack() : attacker.getItemInHand(InteractionHand.MAIN_HAND);
               List<Item> stacks = new ArrayList<>();
               stacks.add(stack.getItem());
               stacks.addAll(
                  CuriosUtil.findSlots(attacker, attacker.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand")
                     .stream()
                     .map(ItemStack::getItem)
                     .toList()
               );
               if (HelperMethods.isMelee(source)) {
                  if (stacks.contains(JJKItems.SPLIT_SOUL_KATANA.get())
                     && JJKAbilities.hasTrait(attacker, Trait.HEAVENLY_RESTRICTION)
                     && !source.is(JJKDamageSources.SPLIT_SOUL_KATANA)
                     && victim.hurt(JJKDamageSources.splitSoulKatanaAttack(attacker), event.getAmount() * 1.0F)) {
                     event.setAmount(event.getAmount() * 0.1F);
                  }

                  if (stacks.contains(JJKItems.STEEL_GAUNTLET.get())) {
                     if (JJKAbilities.hasTrait(attacker, Trait.HEAVENLY_RESTRICTION)) {
                        event.setAmount(event.getAmount() * 1.1F);
                     } else {
                        event.setAmount(event.getAmount() * 1.03F);
                     }
                  }

                  if (stacks.contains(JJKItems.PLAYFUL_CLOUD.get())) {
                     if (JJKAbilities.hasTrait(attacker, Trait.HEAVENLY_RESTRICTION)) {
                        event.setAmount(event.getAmount() * 1.2F);
                     } else {
                        event.setAmount(event.getAmount() * 1.05F);
                     }
                  }
               }

               if (stacks.contains(JJKItems.DRAGON_BONE.get())
                  && (
                     JJKAbilities.hasToggled(victim, (Ability)JJKAbilities.CURSED_ENERGY_FLOW.get())
                        || JJKAbilities.hasToggled(victim, (Ability)JJKAbilities.FALLING_BLOSSOM_EMOTION.get())
                  )) {
                  float Amount = Math.min(6.0F, event.getAmount() * 0.8F);
                  DragonBoneItem.addEnergy(stack, Amount);
               }
            }
         }
      }
   }
}
