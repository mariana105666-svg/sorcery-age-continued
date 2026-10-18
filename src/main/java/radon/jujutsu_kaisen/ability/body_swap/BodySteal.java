package radon.jujutsu_kaisen.ability.body_swap;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

public class BodySteal extends Ability implements Ability.IToggled {
   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target == null) {
         return false;
      } else if (!(target instanceof Player player) && (Boolean)ConfigHolder.SERVER.playerBodySteal.get()) {
         return false;
      } else if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getType() == JujutsuType.SORCERER && cap.getExperience() >= (Double)ConfigHolder.SERVER.minimumBodyStealEXP.get();
      }
   }

   private static boolean canSteal(LivingEntity owner, LivingEntity target) {
      if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return false;
      } else {
         ISorcererData targetCap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return (target instanceof Player player || !(Boolean)ConfigHolder.SERVER.playerBodySteal.get())
            && targetCap.getType() == JujutsuType.SORCERER
            && target.isDeadOrDying()
            && targetCap.getExperience() >= (Double)ConfigHolder.SERVER.minimumBodyStealEXP.get();
      }
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
      return 0.0F;
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

   private static void check(LivingEntity victim, DamageSource source) {
      if (victim instanceof Player player || !(Boolean)ConfigHolder.SERVER.playerBodySteal.get()) {
         if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            if (HelperMethods.isMelee(source)) {
               if (source.getEntity() instanceof LivingEntity attacker) {
                  if (canSteal(attacker, victim)) {
                     if (JJKAbilities.hasToggled(attacker, (Ability)JJKAbilities.BODY_STEAL.get())) {
                        attacker.swing(InteractionHand.MAIN_HAND, true);
                        ISorcererData ownerCap = (ISorcererData)attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                        ISorcererData targetCap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                        if (!(targetCap.getExperience() <= (Double)ConfigHolder.SERVER.minimumBodyStealEXP.get())) {
                           CursedTechnique current = ownerCap.getTechnique();
                           CursedTechnique steal = targetCap.getTechnique();
                           CursedEnergyNature nature = targetCap.getNature();
                           if (current != null) {
                              if (current != steal && (steal != CursedTechnique.MIMICRY || (Boolean)ConfigHolder.SERVER.mimicryBodyStealCompat.get())) {
                                 if (steal != null) {
                                    ownerCap.addStolen(steal);
                                 }

                                 ownerCap.setNature(nature);
                                 if ((Boolean)ConfigHolder.SERVER.bodyStealTraits.get() && ownerCap.getTraits() != null && targetCap.getTraits() != null) {
                                    for (Trait t : ownerCap.getTraits()) {
                                       if (t != Trait.RCT_OUTPUT) {
                                          ownerCap.removeTrait(t);
                                       }
                                    }

                                    for (Trait tx : targetCap.getTraits()) {
                                       if (tx != Trait.RCT_OUTPUT && tx != Trait.HEAVENLY_RESTRICTION) {
                                          ownerCap.addTrait(tx);
                                       }
                                    }
                                 }

                                 if ((Boolean)ConfigHolder.SERVER.bodyStealEXPReset.get()) {
                                    targetCap.setExperience(0.0F);
                                 }

                                 if (attacker instanceof ServerPlayer servOwner) {
                                    if ((Boolean)ConfigHolder.SERVER.bodyStealReroll.get()) {
                                       targetCap.wipe(servOwner);
                                    }

                                    attacker.sendSystemMessage(
                                       Component.translatable(String.format("chat.%s.bodysteal", "jujutsu_kaisen"), new Object[]{victim.getName()})
                                    );
                                    SyncSorcererDataS2CPacket packet = new SyncSorcererDataS2CPacket(ownerCap.serializeNBT());
                                    PacketHandler.sendToClient(packet, servOwner);
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

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class BodyStealForgeEvents {
      @SubscribeEvent
      public static void onLivingDeath(LivingDeathEvent event) {
         BodySteal.check(event.getEntity(), event.getSource());
      }
   }
}
