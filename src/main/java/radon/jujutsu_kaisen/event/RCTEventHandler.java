package radon.jujutsu_kaisen.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.SorcererUtil;

public class RCTEventHandler {
   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class RCTEventHandlerForgeEvents {
      @SubscribeEvent
      public static void onLivingDamage(LivingDamageEvent event) {
         DamageSource source = event.getSource();
         if (source.getEntity() instanceof LivingEntity) {
            LivingEntity victim = event.getEntity();
            if (source.getEntity() instanceof Player player || !(Boolean)ConfigHolder.SERVER.playerRequiredForRCT.get()) {
               if (!victim.level().isClientSide) {
                  if (!(victim.getHealth() - event.getAmount() > 0.0F)) {
                     if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                        ISorcererData cap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                        if ((!cap.isUnlocked((Ability)JJKAbilities.RCT1.get()) || cap.getType() != JujutsuType.SORCERER)
                           && (!cap.hasTrait(Trait.CURSED_WOMB) || !cap.checkWombAwakened())) {
                           if (!(victim instanceof TamableAnimal tamable && tamable.isTame())) {
                              if (!cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
                                 if (cap.getType() == JujutsuType.SORCERER || cap.hasTrait(Trait.CURSED_WOMB)) {
                                    if (SorcererUtil.getGrade(cap.getExperience()).ordinal() >= SorcererGrade.SEMI_GRADE_2.ordinal()) {
                                       int chance = (Integer)ConfigHolder.SERVER.reverseCursedTechniqueChance.get();
                                       if (cap.hasTrait(Trait.RCT_OUTPUT)) {
                                          chance = 2;
                                       } else if (cap.hasTrait(Trait.PRODIGY)) {
                                          chance /= 2;
                                       }

                                       for (InteractionHand hand : InteractionHand.values()) {
                                          ItemStack stack = victim.getItemInHand(hand);
                                          if (stack.is(Items.TOTEM_OF_UNDYING)) {
                                             chance /= ConfigHolder.SERVER.totemRCTChanceMult.get();
                                          }
                                       }

                                       if (cap.hasTrait(Trait.CURSED_WOMB)) {
                                          chance = 0;
                                       }

                                       if (chance == 0 || HelperMethods.RANDOM.nextInt(chance) == 0) {
                                          victim.setHealth(victim.getMaxHealth() / 2.0F);
                                          if (!cap.hasTrait(Trait.CURSED_WOMB)) {
                                             cap.unlock((Ability)JJKAbilities.RCT1.get());
                                          } else {
                                             cap.setWombAwakened(true);
                                             cap.addExperience(SorcererGrade.SPECIAL_GRADE.getRequiredExperience());
                                             if (cap.getTechnique() != null && cap.getTechnique().getDomain() != null) {
                                                cap.unlock(cap.getTechnique().getDomain());
                                             }
                                          }

                                          if (victim instanceof ServerPlayer playerx) {
                                             PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), playerx);
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
               }
            }
         }
      }
   }
}
