package radon.jujutsu_kaisen.ability.cursed_speech;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.chant.ChantHandler;
import radon.jujutsu_kaisen.client.particle.CursedSpeechParticle;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.BlinkS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Blink extends CursedSpeech {
   private static final double RANGE = 25.0;
   private static final double RADIUS = 2.5;
   private static final int DURATION = 15;
   double REALRADIUS = 2.5;
   double REALRANGE = 25.0;

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
                  new CursedSpeechParticle.CursedSpeechParticleOptions(new Vector3f(0.115F, 0.115F, 0.115F), (float)(src.distanceTo(dst) * 0.5)),
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
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

         for (Entity entity : this.getEntities(owner)) {
            if (entity instanceof LivingEntity living && !JJKAbilities.hasToggled(living, (Ability)JJKAbilities.INFINITY.get())) {
               if (entity instanceof Player player) {
                  player.sendSystemMessage(Component.translatable(String.format("chat.%s.blink", "jujutsu_kaisen"), new Object[]{owner.getName()}));
               }

               cap.delayTickEvent(() -> {
                  if (entity != null && entity instanceof ServerPlayer playerx) {
                     ISorcererData capHit = (ISorcererData)entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                     if (!capHit.isChanneling((Ability)JJKAbilities.CURSED_ENERGY_SHIELD.get())) {
                        PacketHandler.sendToClient(new BlinkS2CPacket(Mth.clamp(Math.round(15.0F * this.getPower(owner)), 80, 100)), playerx);
                     }
                  }
               }, 15);
            }
         }
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 50.0F;
   }

   @Override
   public int getCooldown() {
      return 200;
   }

   @Override
   public int getThroatDamage() {
      return 20;
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
