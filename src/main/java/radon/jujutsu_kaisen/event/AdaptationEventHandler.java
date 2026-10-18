package radon.jujutsu_kaisen.event;

import java.util.HashSet;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.LivingHitByDomainEvent;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

public class AdaptationEventHandler {
   private static final int DISRUPTION_DURATION = 20;

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class AdaptationEventHandlerForgeEvents {
      public static boolean isAdaptBlacklisted(Ability ability) {
         return ability == JJKAbilities.CURSED_ENERGY_FLOW.get()
            || ability == JJKAbilities.SIMPLE_DOMAIN.get()
            || ability == JJKAbilities.CURSED_ENERGY_SHIELD.get()
            || ability == JJKAbilities.FALLING_BLOSSOM_EMOTION.get()
            || ability == JJKAbilities.DOMAIN_AMPLIFICATION.get();
      }

      @SubscribeEvent
      public static void onLivingHitByDomain(LivingHitByDomainEvent event) {
         LivingEntity victim = event.getEntity();
         if (JJKAbilities.hasToggled(victim, (Ability)JJKAbilities.WHEEL.get())) {
            ITenShadowsData cap = (ITenShadowsData)victim.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
            if (!cap.isAdaptedTo(event.getAbility())) {
               cap.tryAdapt(event.getAbility());
            }
         }
      }

      @SubscribeEvent
      public static void onLivingHurt(LivingHurtEvent event) {
         LivingEntity victim = event.getEntity();
         if (!victim.level().isClientSide) {
            if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
               DamageSource source = event.getSource();
               ISorcererData victimCap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               if (!victimCap.hasToggled((Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get()) && victimCap.hasToggled((Ability)JJKAbilities.WHEEL.get())) {
                  if (victim.getCapability(TenShadowsDataHandler.INSTANCE).isPresent()) {
                     ITenShadowsData shadowCap = (ITenShadowsData)victim.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
                     if (!shadowCap.isAdaptedTo(source) && !HelperMethods.isMelee(source)) {
                        shadowCap.tryAdapt(source);
                     }

                     if (victim instanceof MahoragaEntity) {
                        if (HelperMethods.isMelee(source)) {
                           event.setAmount(event.getAmount() * 0.1F);
                        } else {
                           float process = 1.0F - shadowCap.getAdaptationProgress(source);
                           switch (shadowCap.getAdaptationType(source)) {
                              case DAMAGE:
                                 if (shadowCap.isAdaptedTo(source)) {
                                    victim.level()
                                       .playSound(
                                          null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.MASTER, 1.0F, 1.0F
                                       );
                                 }

                                 event.setAmount(event.getAmount() * process);
                                 break;
                              case COUNTER:
                                 event.setAmount(event.getAmount() * process);
                                 if (HelperMethods.RANDOM.nextInt(Math.max(1, Math.round(20.0F * process))) == 0) {
                                    Entity attacker = source.getEntity();
                                    if (attacker != null) {
                                       victim.lookAt(Anchor.EYES, attacker.position());
                                       victim.swing(InteractionHand.MAIN_HAND);
                                       victim.level()
                                          .playSound(
                                             null,
                                             victim.getX(),
                                             victim.getY(),
                                             victim.getZ(),
                                             SoundEvents.SHIELD_BLOCK,
                                             SoundSource.MASTER,
                                             1.0F,
                                             1.0F
                                          );
                                       if (victim.doHurtTarget(attacker)) {
                                          victim.invulnerableTime = 0;
                                       }
                                    }
                                 }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      @SubscribeEvent
      public static void onLivingAttack(LivingAttackEvent event) {
         LivingEntity victim = event.getEntity();
         if (!victim.level().isClientSide) {
            if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
               ISorcererData victimCap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               DamageSource source = event.getSource();
               if (source.getEntity() instanceof LivingEntity attacker && !HelperMethods.isMelee(source)) {
                  if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                     ISorcererData attackerCap = (ISorcererData)attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                     if (victim instanceof MahoragaEntity) {
                        ITenShadowsData shadowerCap = (ITenShadowsData)victim.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
                        switch (shadowerCap.getAdaptationType(source)) {
                           case DAMAGE:
                              if (shadowerCap.isAdaptedTo(source)) {
                                 victim.level()
                                    .playSound(
                                       null, victim.getX(), victim.getY(), victim.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.MASTER, 1.0F, 1.0F
                                    );
                                 event.setCanceled(true);
                              }
                           case COUNTER:
                        }
                     }

                     if (!attackerCap.hasToggled((Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get()) && attackerCap.hasToggled((Ability)JJKAbilities.WHEEL.get())
                        )
                      {
                        if (attacker.getCapability(TenShadowsDataHandler.INSTANCE).isPresent()) {
                           ITenShadowsData shadowCap = (ITenShadowsData)attacker.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
                           if (JJKAbilities.hasToggled(attacker, (Ability)JJKAbilities.WHEEL.get())) {
                              if (victimCap.hasToggled((Ability)JJKAbilities.INFINITY.get())) {
                                 shadowCap.tryAdapt((Ability)JJKAbilities.INFINITY.get());
                              }

                              if (victimCap.hasToggled((Ability)JJKAbilities.SOUL_REINFORCEMENT.get())) {
                                 shadowCap.tryAdapt((Ability)JJKAbilities.SOUL_REINFORCEMENT.get());
                              }
                           }

                           if (attacker instanceof MahoragaEntity) {
                              for (Ability ability : new HashSet<>(victimCap.getToggled())) {
                                 if (!(ability instanceof DomainExpansion.IOpenDomain)) {
                                    System.out.println(ability.getName());
                                    System.out.println(isAdaptBlacklisted(ability));
                                    if (shadowCap.isAdaptedTo(ability) && !isAdaptBlacklisted(ability)) {
                                       victimCap.disrupt(ability, 20 * shadowCap.getAdaptation(ability));
                                       victim.level()
                                          .playSound(
                                             null,
                                             victim.getX(),
                                             victim.getY(),
                                             victim.getZ(),
                                             SoundEvents.BLAZE_SHOOT,
                                             SoundSource.MASTER,
                                             1.0F,
                                             1.0F
                                          );
                                    }
                                 }
                              }

                              Ability channeled = victimCap.getChanneled();
                              if (channeled != null && shadowCap.isAdaptedTo(channeled)) {
                                 victimCap.disrupt(channeled, 20 * shadowCap.getAdaptation(channeled));
                              }

                              if (victim instanceof ServerPlayer player) {
                                 PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(victimCap.serializeNBT()), player);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
