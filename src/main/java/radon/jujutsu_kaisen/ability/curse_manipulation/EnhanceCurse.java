package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

public class EnhanceCurse extends Ability implements Ability.IChannelened {
   private static final double RANGE = 32.0;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return this.getTarget(owner) != null;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   @Nullable
   private CursedSpirit getTarget(LivingEntity owner) {
      if (!(RotationUtil.getLookAtHit(owner, 32.0) instanceof EntityHitResult hit && hit.getEntity() instanceof CursedSpirit curse)) {
         return null;
      } else if (curse.getOwner() != owner) {
         return null;
      } else if (!curse.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return null;
      } else {
         ISorcererData ownerCap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         float experience;
         if (owner.level().isClientSide) {
            ClientVisualHandler.ClientData data = ClientVisualHandler.get(curse);
            if (data == null) {
               return null;
            }

            experience = data.experience;
         } else {
            ISorcererData curseCap = (ISorcererData)curse.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            experience = curseCap.getExperience();
         }

         return !(experience >= ownerCap.getExperience()) && experience != ConfigHolder.SERVER.maximumExperienceAmount.get() ? curse : null;
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      CursedSpirit target = this.getTarget(owner);
      return target == null ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public Ability.Status isStillUsable(LivingEntity owner) {
      CursedSpirit target = this.getTarget(owner);
      return target == null ? Ability.Status.FAILURE : super.isStillUsable(owner);
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      if (!owner.level().isClientSide) {
         CursedSpirit target = this.getTarget(owner);
         if (target != null) {
            ISorcererData cap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            cap.addExperience(50.0F);
            if (owner instanceof ServerPlayer player) {
               PacketHandler.sendToClient(
                  new SetOverlayMessageS2CPacket(
                     Component.translatable(
                        String.format("chat.%s.enhance_curse", "jujutsu_kaisen"),
                        new Object[]{cap.getExperience(), ConfigHolder.SERVER.maximumExperienceAmount.get()}
                     ),
                     false
                  ),
                  player
               );
            }
         }
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 1.0F;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
