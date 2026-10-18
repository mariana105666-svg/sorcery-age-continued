package radon.jujutsu_kaisen.ability.cursed_speech;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.chant.ChantHandler;
import radon.jujutsu_kaisen.client.particle.CursedSpeechParticle;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class BlastAway extends CursedSpeech {
   private static final double RANGE = 30.0;
   private static final double RADIUS = 2.5;
   private static final float DAMAGE = 13.0F;
   private static final double LAUNCH_POWER = 1.0;
   double REALRADIUS = 2.5;
   double REALRANGE = 30.0;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return this.getEntities(owner).contains(target) && HelperMethods.RANDOM.nextInt(5) == 0 && target != null && owner.hasLineOfSight(target);
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   private List<Entity> getEntities(LivingEntity owner) {
      List<Entity> audience = this.getAudience(owner);
      if (audience != null) {
         return audience;
      } else {
         Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
         Vec3 src = owner.getEyePosition();
         if (ChantHandler.isChanted(owner, this)) {
            float output = ChantHandler.getOutput(owner, this);
            this.REALRANGE = 30.0 * output;
            this.REALRADIUS = 2.5 * output;
            AABB bounds = AABB.ofSize(src, 1.0, 1.0, 1.0).expandTowards(look.scale(this.REALRANGE)).inflate(this.REALRADIUS);
            return owner.level().getEntities(owner, bounds, entity -> !(entity instanceof LivingEntity living && !owner.canAttack(living)));
         } else {
            AABB bounds = AABB.ofSize(src, 1.0, 1.0, 1.0).expandTowards(look.scale(30.0)).inflate(2.5);
            return owner.level().getEntities(owner, bounds, entity -> !(entity instanceof LivingEntity living && !owner.canAttack(living)));
         }
      }
   }

   @Override
   public void run(LivingEntity owner) {
      if (!owner.level().isClientSide) {
         Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
         Vec3 src = owner.getEyePosition();

         for (int i = 1; i < this.REALRANGE + 7.0; i++) {
            Vec3 dst = src.add(look.scale(i));
            ((ServerLevel)owner.level())
               .sendParticles(
                  new CursedSpeechParticle.CursedSpeechParticleOptions(new Vector3f(0.976F, 0.976F, 0.976F), (float)(src.distanceTo(dst) * 0.5)),
                  dst.x,
                  dst.y,
                  dst.z,
                  0,
                  0.0,
                  0.0,
                  0.0,
                  1.0
               );
         }

         owner.level()
            .playSound(
               null,
               src.x,
               src.y,
               src.z,
               (SoundEvent)JJKSounds.CURSED_SPEECH.get(),
               SoundSource.MASTER,
               2.0F,
               0.8F + HelperMethods.RANDOM.nextFloat() * 0.2F
            );
         owner.level()
            .playSound(
               null, src.x, src.y, src.z, SoundEvents.VEX_CHARGE, SoundSource.MASTER, 1.0F, 0.5F + HelperMethods.RANDOM.nextFloat() * 0.2F
            );
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

         for (Entity entity : this.getEntities(owner)) {
            if (!(entity instanceof LivingEntity living && JJKAbilities.hasToggled(living, (Ability)JJKAbilities.INFINITY.get()))) {
               if (entity instanceof Player player) {
                  player.sendSystemMessage(Component.translatable(String.format("chat.%s.blast_away", "jujutsu_kaisen"), new Object[]{owner.getName()}));
               }

               cap.delayTickEvent(
                  () -> {
                     if (entity != null && entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), 13.0F * this.getPower(owner))) {
                        Vec3 center = entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0);
                        ((ServerLevel)owner.level())
                           .sendParticles(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, 1.0, 0.0, 0.0, 1.0);
                        ((ServerLevel)owner.level())
                           .sendParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 0, 1.0, 0.0, 0.0, 1.0);
                        owner.level()
                           .playSound(
                              null,
                              center.x,
                              center.y,
                              center.z,
                              SoundEvents.GENERIC_EXPLODE,
                              SoundSource.BLOCKS,
                              4.0F,
                              (1.0F + (HelperMethods.RANDOM.nextFloat() - HelperMethods.RANDOM.nextFloat()) * 0.2F) * 0.7F
                           );
                        ISorcererData capHit = null;
                        if (entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                           capHit = (ISorcererData)entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                        }

                        if (capHit == null || !capHit.isChanneling((Ability)JJKAbilities.CURSED_ENERGY_SHIELD.get())) {
                           double power = 1.0 * this.getPower(owner);
                           entity.setDeltaMovement(look.scale(power).multiply(1.0, 0.501, 1.0));
                        }

                        entity.hurtMarked = true;
                     }
                  },
                  15
               );
            }
         }
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 75.0F;
   }

   @Override
   public int getCooldown() {
      return 160;
   }

   @Override
   public int getThroatDamage() {
      return 40;
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.CURSED_SPEECH;
   }
}
