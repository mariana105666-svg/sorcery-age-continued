package radon.jujutsu_kaisen.ability.limitless;

import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.util.HelperMethods;

public class BlueFists extends Ability implements Ability.IToggled, Ability.IAttack {
   private static final float DAMAGE = 4.0F;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return true;
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
      return 30.0F;
   }

   @Override
   public void onEnabled(LivingEntity owner) {
   }

   @Override
   public void onDisabled(LivingEntity owner) {
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.BLUE;
   }

   @Override
   public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
      if (owner.level().isClientSide) {
         return false;
      } else if (!HelperMethods.isMelee(source)) {
         return false;
      } else {
         target.setDeltaMovement(owner.position().subtract(target.position()).normalize());
         target.hurtMarked = true;
         owner.level().playSound(null, target.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.MASTER, 1.0F, 1.0F);
         ((ServerLevel)owner.level()).getChunkSource().broadcastAndSend(owner, new ClientboundAnimatePacket(target, 4));
         target.hurt(JJKDamageSources.jujutsuAttack(owner, (Ability)JJKAbilities.BLUE_FISTS.get()), 4.0F * this.getPower(owner));
         return true;
      }
   }
}
