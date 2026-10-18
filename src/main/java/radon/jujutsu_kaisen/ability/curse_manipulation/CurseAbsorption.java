package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.item.CursedSpiritOrbItem;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

public class CurseAbsorption extends Ability implements Ability.IToggled {
   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target == null) {
         return false;
      } else if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getType() == JujutsuType.CURSE && !cap.hasTrait(Trait.DEATH_PAINTING);
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.TOGGLED;
   }

   private static boolean canAbsorb(LivingEntity owner, LivingEntity target) {
      if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return false;
      } else if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return false;
      } else {
         ISorcererData ownerCap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         ISorcererData targetCap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return targetCap.getType() == JujutsuType.CURSE
            && !targetCap.hasTrait(Trait.DEATH_PAINTING)
            && !(target instanceof TamableAnimal tamable && tamable.isTame())
            && (ownerCap.getExperience() / targetCap.getExperience() >= 2.0F || target.isDeadOrDying());
      }
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
   public boolean usesHands() {
      return false;
   }

   private static void check(LivingEntity victim, DamageSource source) {
      if (HelperMethods.isMelee(source)) {
         if (source.getEntity() instanceof LivingEntity attacker) {
            if (canAbsorb(attacker, victim)) {
               if (JJKAbilities.hasToggled(attacker, (Ability)JJKAbilities.CURSE_ABSORPTION.get())) {
                  ISorcererData victimCap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                  attacker.swing(InteractionHand.MAIN_HAND, true);
                  victimCap.setRevivable(false);
                  ItemStack stack = new ItemStack((ItemLike)JJKItems.CURSED_SPIRIT_ORB.get());
                  if (victim instanceof Player player) {
                     CursedSpiritOrbItem.setAbsorbed(stack, new AbsorbedCurse(victim.getName(), victim.getType(), victimCap.serializeNBT(), player.getGameProfile()));
                  } else {
                     CursedSpiritOrbItem.setAbsorbed(stack, new AbsorbedCurse(victim.getName(), victim.getType(), victimCap.serializeNBT()));
                  }

                  if (attacker instanceof Player player) {
                     player.addItem(stack);
                  } else {
                     attacker.setItemSlot(EquipmentSlot.MAINHAND, stack);
                  }

                  EntityUtil.makePoofParticles(victim);
                  if (!(victim instanceof Player)) {
                     victim.discard();
                  } else if (!victim.isDeadOrDying()) {
                     victim.kill();
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
   public static class CurseAbsorptionForgeEvents {
      @SubscribeEvent
      public static void onLivingDamage(LivingDamageEvent event) {
         CurseAbsorption.check(event.getEntity(), event.getSource());
      }

      @SubscribeEvent
      public static void onLivingDeath(LivingDeathEvent event) {
         CurseAbsorption.check(event.getEntity(), event.getSource());
      }
   }
}
