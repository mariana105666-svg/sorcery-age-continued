package radon.jujutsu_kaisen.ability.misc;

import java.util.List;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.block.entity.IVeil;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.IncompleteDomainEntity;
import radon.jujutsu_kaisen.entity.VeilEntity;

public class DispelVeil extends Ability {
   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return false;
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Nullable
   private VeilEntity getOwnedVeil(LivingEntity owner) {
      if (!(owner.level() instanceof ServerLevel level)) {
         return null;
      } else {
         VeilEntity var8 = null;

         for (IVeil current : VeilHandler.getVeils(level, owner.blockPosition())) {
            VeilEntity veil = current.getVeil();
            if (veil != null && !veil.isRemoved()) {
               UUID creator = veil.getOwnerUUID();
               if (creator != null
                  && creator.equals(owner.getUUID())
                  && !IncompleteDomainEntity.isClaimed(level, veil)
                  && (var8 == null || veil.getRadius() < var8.getRadius())) {
                  var8 = veil;
               }
            }
         }

         return var8;
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      return !owner.level().isClientSide && this.getOwnedVeil(owner) == null ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public void run(LivingEntity owner) {
      if (!owner.level().isClientSide) {
         VeilEntity veil = this.getOwnedVeil(owner);
         if (veil != null) {
            veil.discard();
            if (owner instanceof ServerPlayer player) {
               player.sendSystemMessage(Component.translatable(String.format("chat.%s.dispel_veil", "jujutsu_kaisen")));
            }
         }
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.0F;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.DOMAIN;
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public List<Ability> getRequirements() {
      return List.of((Ability)JJKAbilities.VEIL.get());
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      if (!super.isValid(owner)) {
         return false;
      } else if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return !cap.hasTrait(Trait.HEAVENLY_RESTRICTION);
      }
   }

   @Override
   public Vec2 getDisplayCoordinates() {
      return new Vec2(2.0F, 0.0F);
   }
}
