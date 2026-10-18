package radon.jujutsu_kaisen.ability.cursed_speech;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.chant.ChantHandler;
import radon.jujutsu_kaisen.client.particle.CursedSpeechParticle;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class RunAway extends CursedSpeech {
   private static final double RANGE = 25.0;
   private static final double RADIUS = 2.5;
   private static final int DURATION = 35;
   double REALRADIUS = 2.5;
   double REALRANGE = 25.0;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return false;
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
            this.REALRANGE = 25.0 * output;
            this.REALRADIUS = 2.5 * output;
            AABB bounds = AABB.ofSize(src, 1.0, 1.0, 1.0).expandTowards(look.scale(this.REALRANGE)).inflate(this.REALRADIUS);
            return owner.level().getEntities(owner, bounds, entity -> !(entity instanceof LivingEntity living && !owner.canAttack(living)));
         } else {
            AABB bounds = AABB.ofSize(src, 1.0, 1.0, 1.0).expandTowards(look.scale(25.0)).inflate(2.5);
            return owner.level().getEntities(owner, bounds, entity -> !(entity instanceof LivingEntity living && !owner.canAttack(living)));
         }
      }
   }

   @Override
   public boolean usesHands() {
      return false;
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
                  new CursedSpeechParticle.CursedSpeechParticleOptions(new Vector3f(0.376F, 1.0F, 0.376F), (float)(src.distanceTo(dst) * 0.5)),
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

         for (Entity entity : this.getEntities(owner)) {
            if (entity instanceof LivingEntity living) {
               JJKEffectUtil.addEffect(
                  living, new MobEffectInstance(MobEffects.JUMP, Mth.clamp(Math.round(35.0F * this.getPower(owner)), 200, 400), 1, false, false, false)
               );
               JJKEffectUtil.addEffect(
                  living, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Mth.clamp(Math.round(35.0F * this.getPower(owner)), 200, 400), 5, false, false, false)
               );
               JJKEffectUtil.addEffect(
                  living, new MobEffectInstance(MobEffects.DIG_SPEED, Mth.clamp(Math.round(35.0F * this.getPower(owner)), 200, 400), 0, false, false, false)
               );
               if (living.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                  ISorcererData cap = (ISorcererData)living.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                  cap.setDisarmed(Math.round(35.0F * this.getPower(owner)));
               }

               if (entity instanceof Player player) {
                  player.sendSystemMessage(Component.translatable(String.format("chat.%s.run_away", "jujutsu_kaisen"), new Object[]{owner.getName()}));
               }
            }
         }
      }
   }

   @Override
   public int getThroatDamage() {
      return 40;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 100.0F;
   }

   @Override
   public int getCooldown() {
      return 480;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.CURSED_SPEECH;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
