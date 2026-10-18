package radon.jujutsu_kaisen.ability.misc.lightning;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.EmittingLightningParticle;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Discharge extends Ability implements Ability.IChannelened, Ability.IDurationable {
   private static final float DAMAGE = 12.0F;
   private static final float MAX_RADIUS = 20.0F;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target != null && !target.isDeadOrDying() && owner.hasLineOfSight(target) && !(owner.distanceTo(target) > this.getRadius(owner) / 2.0F)) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getType() != JujutsuType.CURSE && !cap.isUnlocked((Ability)JJKAbilities.RCT1.get())
            ? owner.getHealth() / owner.getMaxHealth() < 0.4F
            : owner.getHealth() / owner.getMaxHealth() < 0.9F;
      } else {
         return false;
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   private float getRadius(LivingEntity owner) {
      return Math.min(20.0F, this.getPower(owner) * 4.0F);
   }

   @Override
   public void run(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         owner.level()
            .playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.ELECTRICITY.get(), SoundSource.MASTER, 3.0F, 1.0F);
         float radius = this.getRadius(owner);
         int count = (int)(radius / 8.0F * 0.1F * Math.PI * 2.0);
         Vec3 center = owner.position().add(0.0, owner.getBbHeight() / 2.0F, 0.0);

         for (int i = 0; i < count; i++) {
            double theta = HelperMethods.RANDOM.nextDouble() * Math.PI * 2.0;
            double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
            Vec3 direction = new Vec3(Math.sin(phi) * Math.cos(theta), Math.sin(phi) * Math.sin(theta), Math.cos(phi));
            Vec3 offset = center.add(direction.multiply(owner.getBbWidth() / 2.0F, owner.getBbHeight() / 2.0F, owner.getBbWidth() / 2.0F));
            level.sendParticles(
               new EmittingLightningParticle.EmittingLightningParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), direction, radius, 4, false),
               offset.x,
               offset.y,
               offset.z,
               0,
               0.0,
               0.0,
               0.0,
               0.0
            );
         }

         for (Entity entity : owner.level().getEntities(owner, AABB.ofSize(owner.position(), radius, radius, radius))) {
            if (entity.hurt(JJKDamageSources.jujutsuAttack(owner, this), 12.0F * this.getPower(owner))) {
               for (int i = 0; i < 4; i++) {
                  double x = entity.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5) * (entity.getBbWidth() * 2.0F);
                  double y = entity.getY() + HelperMethods.RANDOM.nextDouble() * (entity.getBbHeight() * 1.25F);
                  double z = entity.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5) * (entity.getBbWidth() * 2.0F);
                  level.sendParticles(
                     new LightningParticle.LightningParticleOptions(ParticleColors.getCursedEnergyColorBright(owner), 0.2F, 1), x, y, z, 0, 0.0, 0.0, 0.0, 0.0
                  );
               }

               owner.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)JJKSounds.ELECTRICITY.get(), SoundSource.MASTER, 1.0F, 1.0F
                  );
            }
         }
      }
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return cap.getNature() == CursedEnergyNature.LIGHTNING
         && JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.CURSED_ENERGY_FLOW.get())
         && super.isValid(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 15.0F;
   }

   @Override
   public int getDuration() {
      return 5;
   }

   @Override
   public int getCooldown() {
      return 200;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.LIGHTNING;
   }
}
