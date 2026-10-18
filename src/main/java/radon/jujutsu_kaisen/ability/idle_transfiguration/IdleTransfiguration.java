package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.IRMBAttack;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.idle_transfiguration.PolymorphicSoulIsomerEntity;
import radon.jujutsu_kaisen.entity.idle_transfiguration.TransfiguredSoulLargeEntity;
import radon.jujutsu_kaisen.entity.idle_transfiguration.TransfiguredSoulNormalEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

public class IdleTransfiguration extends Ability implements Ability.IToggled, Ability.IAttack, IRMBAttack {
   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return false;
   }

   public static boolean checkSukuna(LivingEntity owner, LivingEntity target) {
      ISorcererData ownerCap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return false;
      } else {
         ISorcererData targetCap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (targetCap.hasTrait(Trait.VESSEL) && targetCap.getFingers() != 0) {
            float experience = targetCap.getFingers() * (SorcererGrade.SPECIAL_GRADE.getRequiredExperience() * 5.0F / 20.0F);
            if (experience <= ownerCap.getExperience()) {
               return false;
            } else {
               ((Ability.IDomainAttack)JJKAbilities.CLEAVE.get()).performEntity(target, owner, null);
               return true;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.TOGGLED;
   }

   public static float calculateStrength(LivingEntity entity) {
      float strength = Math.min(1.0F, entity.getHealth());
      if (!(entity instanceof Player player) && strength <= 30.0F) {
         strength = 1.0F;
      }

      if (entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         if (entity.level().isClientSide) {
            ClientVisualHandler.ClientData data = ClientVisualHandler.get(entity);
            if (data != null) {
               strength += data.experience * 0.1F;
            }
         } else {
            ISorcererData cap = (ISorcererData)entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            strength += cap.getExperience() * 0.1F;
         }
      }

      return strength;
   }

   @Override
   public void run(LivingEntity owner) {
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (cap.hasToggled((Ability)JJKAbilities.SOUL_DECIMATION.get())) {
         cap.toggle((Ability)JJKAbilities.SOUL_DECIMATION.get());
      }

      return super.isTriggerable(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 60.0F;
   }

   @Override
   public int getCooldown() {
      return 80;
   }

   @Override
   public void onEnabled(LivingEntity owner) {
   }

   @Override
   public void onDisabled(LivingEntity owner) {
   }

   private static boolean canAbsorb(LivingEntity owner, LivingEntity target) {
      float attackerStrength = calculateStrength(owner);
      float victimStrength = calculateStrength(target);
      int required = Math.round(victimStrength / Math.round(attackerStrength * 2.0F / 11.0F) * 2.0F);
      if (victimStrength <= 1.0F) {
         required = 0;
      }

      MobEffectInstance existing = target.getEffect((MobEffect)JJKEffects.TRANSFIGURED_SOUL.get());
      if (existing == null && !target.isDeadOrDying()) {
         return false;
      } else {
         int amplifier = existing != null ? existing.getAmplifier() : 0;
         if (amplifier >= required || target.isDeadOrDying()) {
            if (target instanceof Mob && !(target instanceof Monster) && !(target instanceof CursedSpirit)) {
               return true;
            }

            if (target instanceof Player) {
               if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                  return false;
               }

               ISorcererData targetCap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               return targetCap.getType() == JujutsuType.SORCERER;
            }
         }

         return false;
      }
   }

   private static void check(LivingEntity victim, DamageSource source) {
      if (HelperMethods.isMelee(source)) {
         if (source.getEntity() instanceof LivingEntity attacker) {
            if (canAbsorb(attacker, victim)) {
               if (JJKAbilities.hasToggled(attacker, (Ability)JJKAbilities.IDLE_TRANSFIGURATION.get())) {
                  attacker.swing(InteractionHand.MAIN_HAND, true);
                  absorb(attacker, victim);
               }
            }
         }
      }
   }

   public static void absorb(LivingEntity owner, LivingEntity target) {
      int stackcount = 1;
      if (target instanceof TransfiguredSoulNormalEntity) {
         stackcount = 2;
      } else if (target instanceof TransfiguredSoulLargeEntity) {
         stackcount = 3;
      } else if (target instanceof PolymorphicSoulIsomerEntity) {
         stackcount = 10;
      }

      ItemStack stack = new ItemStack((ItemLike)JJKItems.TRANSFIGURED_SOUL.get(), stackcount);
      ISorcererData targetCap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
      if (targetCap != null) {
         targetCap.setRevivable(false);
      }

      if (owner instanceof Player player) {
         player.addItem(stack);
      } else {
         owner.setItemSlot(EquipmentSlot.MAINHAND, stack);
      }

      EntityUtil.makePoofParticles(target);
      if (!(target instanceof Player)) {
         target.discard();
      } else if (!owner.isDeadOrDying()) {
         target.kill();
      }
   }

   public static void retrieve(LivingEntity owner, LivingEntity target) {
      absorb(owner, target);
      if (!(target instanceof Player)) {
         target.discard();
      } else if (!owner.isDeadOrDying()) {
         target.kill();
      }
   }

   @Override
   public void perform(LivingEntity owner, LivingEntity target) {
      MobEffectInstance existing = target.getEffect((MobEffect)JJKEffects.TRANSFIGURED_SOUL.get());
      int amplifier = 0;
      if (existing != null) {
         amplifier = existing.getAmplifier() + 1;
      }

      MobEffectInstance instance = new MobEffectInstance((MobEffect)JJKEffects.TRANSFIGURED_SOUL.get(), 240, amplifier, false, true, true);
      JJKEffectUtil.addEffect(target, instance);
      if (!owner.level().isClientSide) {
         PacketDistributor.TRACKING_ENTITY.with(() -> target).send(new ClientboundUpdateMobEffectPacket(target.getId(), instance));
      }
   }

   @Override
   public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
      if (owner.level().isClientSide) {
         return false;
      } else if (!HelperMethods.isMelee(source)) {
         return false;
      } else {
         this.perform(owner, target);
         return true;
      }
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class IdleTransfigurationForgeEvents {
      @SubscribeEvent
      public static void onLivingDeath(LivingDeathEvent event) {
         IdleTransfiguration.check(event.getEntity(), event.getSource());
      }
   }
}
