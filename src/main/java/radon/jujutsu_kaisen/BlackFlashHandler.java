package radon.jujutsu_kaisen;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.effect.BlackFlashEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.RabbitEscapeEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.PlayerUtil;
import radon.jujutsu_kaisen.util.SorcererUtil;

public class BlackFlashHandler {
   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class BlackFlashHandlerForgeEvents {
      @SubscribeEvent(
         priority = EventPriority.LOWEST
      )
      public static void onLivingDamage(LivingDamageEvent event) {
         DamageSource source = event.getSource();
         if (source.getEntity() instanceof LivingEntity attacker) {
            LivingEntity victim = event.getEntity();
            if (!victim.level().isClientSide) {
               if (HelperMethods.isMelee(source)) {
                  if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                     ISorcererData cap = (ISorcererData)attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                     if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                        ISorcererData victimcap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                        if (!(attacker instanceof ISorcerer sorcerer && !sorcerer.hasArms())) {
                           if (SorcererUtil.getGrade(cap.getExperience()).ordinal() >= SorcererGrade.SEMI_GRADE_2.ordinal()
                              && (
                                 source instanceof JJKDamageSources.JujutsuDamageSource
                                    || cap.hasToggled((Ability)JJKAbilities.CURSED_ENERGY_FLOW.get())
                                    || cap.hasToggled((Ability)JJKAbilities.BLUE_FISTS.get())
                              )) {
                              long lastBlackFlashTime = cap.getLastBlackFlashTime();
                              int seconds = (int)(attacker.level().getGameTime() - lastBlackFlashTime) / 20;
                              if (lastBlackFlashTime == 0L || seconds >= 1) {
                                 int rng = (Integer)ConfigHolder.SERVER.blackFlashChanceRNG.get();
                                 if (cap.addBlackFlash()) {
                                    rng = Math.round(((Integer)ConfigHolder.SERVER.blackFlashChanceRNG.get()).floatValue() * 0.75F);
                                    if (cap.hasToggled((Ability)JJKAbilities.RATIO_RULE.get())) {
                                       rng = Math.round(((Integer)ConfigHolder.SERVER.blackFlashChanceRNG.get()).floatValue() * 0.1F);
                                    }
                                 }

                                 if (cap.getNature() == CursedEnergyNature.DIVERGENT) {
                                    rng = Math.round(((Integer)ConfigHolder.SERVER.blackFlashChanceRNG.get()).floatValue() * 0.75F);
                                    if (cap.addBlackFlash()) {
                                       rng = Math.round(((Integer)ConfigHolder.SERVER.blackFlashChanceRNG.get()).floatValue() * 0.5F);
                                    }
                                 }

                                 if (attacker instanceof Player player && cap.isInZone()) {
                                    rng = Math.round(((Integer)ConfigHolder.SERVER.blackFlashChanceRNG.get()).floatValue() * 0.15F);
                                    if (cap.getNature() == CursedEnergyNature.DIVERGENT) {
                                       rng = Math.round(((Integer)ConfigHolder.SERVER.blackFlashChanceRNG.get()).floatValue() * 0.1F);
                                    }

                                    if (cap.addBlackFlash()) {
                                       rng = Math.round(((Integer)ConfigHolder.SERVER.blackFlashChanceRNG.get()).floatValue() * 0.075F);
                                       if (cap.getNature() == CursedEnergyNature.DIVERGENT) {
                                          rng = Math.round(((Integer)ConfigHolder.SERVER.blackFlashChanceRNG.get()).floatValue() * 0.05F);
                                       }

                                       if (cap.hasToggled((Ability)JJKAbilities.RATIO_RULE.get())) {
                                          rng = Math.round(((Integer)ConfigHolder.SERVER.blackFlashChanceRNG.get()).floatValue() * 0.015F);
                                       }
                                    }
                                 }

                                 if (attacker instanceof ServerPlayer player
                                    && !PlayerUtil.hasAdvancement(player, "black_flash")
                                    && cap.hasTrait(Trait.PRODIGY)) {
                                    rng = (int)(rng / 1.5);
                                 }

                                 if (victimcap.getType() == JujutsuType.SHIKIGAMI) {
                                    rng = (int)(rng * 1.5);
                                    if (victim instanceof RabbitEscapeEntity) {
                                       rng = (int)(rng * 20.0);
                                    } else if (victimcap.getType() == JujutsuType.CURSE && victim instanceof CursedSpirit curse && curse.isTame()) {
                                       rng = (int)(rng * 1.5);
                                    }
                                 }

                                 if (cap.hasToggled((Ability)JJKAbilities.QUICK_DRAW.get())) {
                                    rng = (int)(rng * 1.5);
                                 }

                                 if (cap.hasGuaranteedBlackFlash()) {
                                    rng = 0;
                                 }

                                 if (rng < 1 || HelperMethods.RANDOM.nextInt(rng) == 0) {
                                    cap.onBlackFlash();
                                    if (attacker instanceof ServerPlayer player) {
                                       PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                                    }

                                    event.setAmount(
                                       Math.min(
                                          ((Double)ConfigHolder.SERVER.blackFlashDmgCap.get()).floatValue(),
                                          (float)Math.pow(event.getAmount(), ((Double)ConfigHolder.SERVER.blackFlashPower.get()).floatValue())
                                       )
                                    );
                                    attacker.level().addFreshEntity(new BlackFlashEntity(attacker, victim));
                                    victim.level()
                                       .playSound(
                                          null,
                                          victim.getX(),
                                          victim.getY(),
                                          victim.getZ(),
                                          SoundEvents.LIGHTNING_BOLT_THUNDER,
                                          SoundSource.MASTER,
                                          2.0F,
                                          0.8F + HelperMethods.RANDOM.nextFloat() * 0.2F
                                       );
                                    victim.level()
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
