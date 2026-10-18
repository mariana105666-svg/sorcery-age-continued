package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;

public class CursedEnergyShield extends Ability implements Ability.IChannelened {
   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return false;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.NONE;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   @Override
   public void run(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel) {
         JJKEffectUtil.addEffect(owner, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), 2, 1, false, false, false));
      }
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      return JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.CURSED_ENERGY_FLOW.get()) && super.isValid(owner) && owner instanceof Player player;
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
   public float getCost(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return cap.hasTrait(Trait.HEAVENLY_RESTRICTION) ? 0.0F : 2.0F;
   }

   @Override
   public boolean shouldLog(LivingEntity owner) {
      return true;
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      return !this.checkCost(owner) ? Ability.Status.ENERGY : super.isTriggerable(owner);
   }

   @Override
   public void onStart(LivingEntity owner) {
      if (!this.checkCost(owner)) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         cap.useEnergy(50.0F);
      }
   }
}
