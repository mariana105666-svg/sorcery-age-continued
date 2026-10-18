package radon.jujutsu_kaisen.ability.projection_sorcery;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.IRMBAble;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.effect.ProjectionFrameEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.ScreenFlashS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class TwentyFourFrameRule extends Ability implements Ability.IToggled, Ability.IAttack, IRMBAble {
   private static final float DAMAGE = 11.0F;
   public static final double RANGE = 5.0;

   @Override
   public boolean isScalable(LivingEntity owner) {
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
   public int getCooldown() {
      return 100;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 100.0F;
   }

   @Override
   public void onEnabled(LivingEntity owner) {
   }

   @Override
   public void onDisabled(LivingEntity owner) {
   }

   public boolean frameTrap(LivingEntity owner, LivingEntity target) {
      for (ProjectionFrameEntity frame : owner.level().getEntitiesOfClass(ProjectionFrameEntity.class, AABB.ofSize(target.position(), 8.0, 8.0, 8.0))) {
         if (frame.getVictim() == target) {
            return false;
         }
      }

      owner.level().addFreshEntity(new ProjectionFrameEntity(owner, target, Ability.getPower((Ability)JJKAbilities.TWENTY_FOUR_FRAME_RULE.get(), owner)));
      if (target instanceof ServerPlayer player) {
         PacketHandler.sendToClient(new ScreenFlashS2CPacket(), player);
      }

      return true;
   }

   @Override
   public boolean onRightClick(LivingEntity owner) {
      owner.swing(InteractionHand.OFF_HAND);
      if (!(
         RotationUtil.getLookAtHit(owner, 5.0) instanceof EntityHitResult hit
            && hit.getEntity() instanceof LivingEntity target
            && !owner.hasEffect((MobEffect)JJKEffects.STAGGER.get())
      )) {
         return false;
      } else if (!owner.canAttack(target)) {
         return false;
      } else {
         owner.swing(InteractionHand.OFF_HAND);
         if (!owner.level().isClientSide) {
            this.frameTrap(owner, target);
         }

         return true;
      }
   }

   @Override
   public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
      if (owner.level().isClientSide) {
         return false;
      } else if (!HelperMethods.isMelee(source)) {
         return false;
      } else {
         this.frameTrap(owner, target);
         return true;
      }
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.PROJECTION;
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class TwentyFourFrameRuleForgeEvents {
      @SubscribeEvent
      public static void onLivingAttack(LivingAttackEvent event) {
         DamageSource source = event.getSource();
         if (source.getEntity() instanceof LivingEntity attacker) {
            LivingEntity victim = event.getEntity();
            if (!victim.level().isClientSide) {
               for (ProjectionFrameEntity frame : victim.level().getEntitiesOfClass(ProjectionFrameEntity.class, AABB.ofSize(victim.position(), 8.0, 8.0, 8.0))) {
                  if (frame.getVictim() == victim) {
                     Vec3 center = new Vec3(frame.getX(), frame.getY(), frame.getZ());
                     ((ServerLevel)frame.level()).sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, 1.0, 0.0, 0.0, 1.0);
                     frame.level().playSound(null, frame.getX(), frame.getY(), frame.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.MASTER, 1.0F, 1.0F);
                     frame.level().playSound(null, frame.getX(), frame.getY(), frame.getZ(), SoundEvents.GLASS_BREAK, SoundSource.MASTER, 1.0F, 1.0F);
                     frame.discard();
                     LivingEntity owner = frame.getOwner();
                     if (owner != null) {
                        if (victim.hurt(
                              JJKDamageSources.indirectJujutsuAttack(frame, attacker, (Ability)JJKAbilities.TWENTY_FOUR_FRAME_RULE.get()),
                              11.0F * frame.getPower()
                           )
                           && victim.isDeadOrDying()) {
                           event.setCanceled(true);
                        }
                        break;
                     }
                  }
               }
            }
         }
      }
   }
}
