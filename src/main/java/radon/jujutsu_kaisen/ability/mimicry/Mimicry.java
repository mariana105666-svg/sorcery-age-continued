package radon.jujutsu_kaisen.ability.mimicry;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Mimicry extends Ability implements Ability.IToggled, Ability.IAttack {
   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target != null && !target.isDeadOrDying() && owner.hasLineOfSight(target);
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.TOGGLED;
   }

   @Override
   public void run(LivingEntity owner) {
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return cap.getCopied().size() < (Integer)ConfigHolder.SERVER.maximumCopiedTechniques.get()
         && JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.RIKA.get())
         && super.isValid(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 10.0F;
   }

   @Override
   public void onEnabled(LivingEntity owner) {
   }

   @Override
   public void onDisabled(LivingEntity owner) {
   }

   @Override
   public int getCooldown() {
      return 600;
   }

   @Override
   public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
      if (owner.level().isClientSide) {
         return false;
      } else if (!HelperMethods.isMelee(source)) {
         return false;
      } else if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return false;
      } else {
         ISorcererData ownerCap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         ISorcererData targetCap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         CursedTechnique current = ownerCap.getTechnique();
         CursedTechnique copied = targetCap.getTechnique();
         if (copied == null
            || current == null
            || ownerCap.hasTechnique(copied)
            || copied == CursedTechnique.BRAIN_TRANSPLANT && !(Boolean)ConfigHolder.SERVER.mimicryBodyStealCompat.get()
            || copied == CursedTechnique.MYTHICAL_BEAST_AMBER) {
            return false;
         } else if (owner instanceof Player && (Boolean)ConfigHolder.SERVER.playerMimicry.get() && !(target instanceof Player)) {
            return false;
         } else if (current != copied) {
            owner.sendSystemMessage(Component.translatable(String.format("chat.%s.mimicry", "jujutsu_kaisen"), new Object[]{copied.getName()}));
            ownerCap.copy(copied);
            if (owner instanceof ServerPlayer player) {
               PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(ownerCap.serializeNBT()), player);
            }

            return true;
         } else {
            return false;
         }
      }
   }
}
