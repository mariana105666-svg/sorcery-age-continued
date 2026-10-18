package radon.jujutsu_kaisen.ability.cursed_speech;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import radon.jujutsu_kaisen.util.SorcererUtil;

public class Die extends CursedSpeech {
   private static final double RANGE = 30.0;
   private static final double RADIUS = 2.5;
   private static final float DAMAGE = 28.0F;
   double REALRADIUS = 2.5;
   double REALRANGE = 30.0;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return this.getEntities(owner).contains(target)
         && target != null
         && (owner.getHealth() / owner.getMaxHealth() <= 0.25F || HelperMethods.RANDOM.nextInt(10) == 0 && owner.hasLineOfSight(target));
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

   private static float calculateDamage(DamageSource source, LivingEntity target) {
      float damage = target.getMaxHealth();
      float armor = target.getArmorValue();
      float toughness = (float)target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
      float f = 2.0F + toughness / 4.0F;
      float f1 = Mth.clamp(armor - damage / f, armor * 0.2F, 20.0F);
      damage /= 1.0F - f1 / 25.0F;
      MobEffectInstance instance = target.getEffect(MobEffects.DAMAGE_RESISTANCE);
      if (instance != null) {
         int resistance = instance.getAmplifier();
         int i = (resistance + 1) * 5;
         int j = 25 - i;
         if (j == 0) {
            return damage;
         }

         float x = 25.0F / j;
         damage *= x;
      }

      return damage;
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
                  new CursedSpeechParticle.CursedSpeechParticleOptions(new Vector3f(0.337F, 0.0F, 0.0F), (float)(src.distanceTo(dst) * 0.5)),
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
         ISorcererData ownerCap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

         for (Entity entity : this.getEntities(owner)) {
            if (entity instanceof LivingEntity living && !JJKAbilities.hasToggled(living, (Ability)JJKAbilities.INFINITY.get())) {
               ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               if (living instanceof Player player) {
                  player.sendSystemMessage(Component.translatable(String.format("chat.%s.die", "jujutsu_kaisen"), new Object[]{owner.getName()}));
               }

               cap.delayTickEvent(() -> {
                  if (entity != null) {
                     living.hurt(JJKDamageSources.jujutsuAttack(owner, this), 28.0F * this.getPower(owner));
                     living.getCapability(SorcererDataHandler.INSTANCE).ifPresent(targetCap -> {
                        if (SorcererUtil.getGrade(ownerCap.getExperience()).ordinal() - SorcererUtil.getGrade(targetCap.getExperience()).ordinal() >= 2) {
                           DamageSource source = JJKDamageSources.jujutsuAttack(owner, this);
                           entity.hurt(source, calculateDamage(source, living));
                        }
                     });
                  }
               }, 15);
            }
         }
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 450.0F;
   }

   @Override
   public int getCooldown() {
      return 600;
   }

   @Override
   public int getThroatDamage() {
      return 100;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.CURSED_SPEECH;
   }
}
