package radon.jujutsu_kaisen.ability.shrine;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.IRMBAttack;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.JJKParticles;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Cleave extends Ability implements Ability.IDomainAttack, Ability.IAttack, Ability.IToggled, IRMBAttack {
   public static final double RANGE = 30.0;
   private static final float DAMAGE = 30.0F;
   private static final float DAMAGE_SCALE = 0.05F;

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

   private DamageSource getSource(LivingEntity owner, @Nullable DomainExpansionEntity domain) {
      return domain == null ? JJKDamageSources.jujutsuAttack(owner, this) : JJKDamageSources.indirectJujutsuAttack(domain, owner, this);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 200.0F;
   }

   @Override
   public int getCooldown() {
      return 400;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.SLASHING;
   }

   @Override
   public void perform(LivingEntity owner, LivingEntity attacker) {
      this.perform(owner, attacker, null);
   }

   private void perform(LivingEntity owner, LivingEntity target, @Nullable DomainExpansionEntity domain) {
      if (owner.level() instanceof ServerLevel level) {
         owner.level()
            .playSound(null, target.getX(), target.getY(), target.getZ(), (SoundEvent)JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F);
         ISorcererData var7 = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

         for (int i = 1; i <= 20; i++) {
            var7.delayTickEvent(
               () -> {
                  if (!target.isDeadOrDying()) {
                     level.sendParticles(
                        (SimpleParticleType)JJKParticles.SLASH.get(),
                        target.getX(),
                        target.getY(),
                        target.getZ(),
                        0,
                        target.getId(),
                        0.0,
                        0.0,
                        1.0
                     );
                     Vec3 center = target.position().add(0.0, target.getBbHeight() / 2.0F, 0.0);
                     Vec3 offset = center.add(
                        (HelperMethods.RANDOM.nextDouble() - 0.5) * target.getBbWidth(),
                        (HelperMethods.RANDOM.nextDouble() - 0.5) * target.getBbHeight(),
                        (HelperMethods.RANDOM.nextDouble() - 0.5) * target.getBbWidth()
                     );
                     ((ServerLevel)owner.level()).sendParticles(ParticleTypes.EXPLOSION, offset.x, offset.y, offset.z, 0, 1.0, 0.0, 0.0, 1.0);
                  }
               },
               i
            );
         }

         for (int i = 1; i <= 10; i++) {
            var7.delayTickEvent(
               () -> {
                  if (!target.isDeadOrDying()) {
                     owner.level()
                        .playSound(
                           null, target.getX(), target.getY(), target.getZ(), (SoundEvent)JJKSounds.SLASH.get(), SoundSource.MASTER, 1.0F, 1.0F
                        );
                  }
               },
               i * 2
            );
         }

         var7.delayTickEvent(
            () -> {
               DamageSource source = this.getSource(owner, domain);
               float addeddmg = target.getMaxHealth() * 0.05F;
               float realdamage = this.getPower(owner) * 30.0F + addeddmg;
               if (domain != null) {
                  float size = var7.getDomainSize();
                  realdamage *= ((Double)ConfigHolder.SERVER.maximumDomainSize.get()).floatValue() + 0.1F - size;
               }

               if (domain != null && !(target instanceof Player) && !(target instanceof SorcererEntity) && target.getHealth() <= realdamage) {
                  System.out.println("no exp");
                  target.getPersistentData().putBoolean("no_exp", true);
               }

               boolean success = target.hurt(source, realdamage);
               if (success && (target instanceof Mob || target instanceof Player)) {
                  owner.level()
                     .playSound(null, target.getX(), target.getY(), target.getZ(), (SoundEvent)JJKSounds.CLEAVE.get(), SoundSource.MASTER, 1.0F, 1.0F);
               }
            },
            25
         );
      }
   }

   @Override
   public void performEntity(LivingEntity owner, LivingEntity target, DomainExpansionEntity domain) {
      this.perform(owner, target, domain);
   }

   @Override
   public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
      if (!HelperMethods.isMelee(source)) {
         return false;
      } else {
         this.perform(owner, target, null);
         return true;
      }
   }

   @Override
   public void onEnabled(LivingEntity owner) {
   }

   @Override
   public void onDisabled(LivingEntity owner) {
   }
}
