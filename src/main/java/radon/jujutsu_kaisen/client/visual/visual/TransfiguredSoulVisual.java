package radon.jujutsu_kaisen.client.visual.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.client.visual.base.IVisual;
import radon.jujutsu_kaisen.effect.JJKEffects;

public class TransfiguredSoulVisual implements IVisual {
   public static final ThreadLocal<RandomSource> RANDOM = ThreadLocal.withInitial(RandomSource::createThreadSafe);

   @Override
   public boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData data) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player == null) {
         return false;
      } else {
         return !JJKAbilities.getTechniques(mc.player).contains(CursedTechnique.IDLE_TRANSFIGURATION)
            ? false
            : entity.hasEffect((MobEffect)JJKEffects.TRANSFIGURED_SOUL.get());
      }
   }

   @Override
   public void tick(LivingEntity entity, ClientVisualHandler.ClientData data) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null && mc.player != null) {
         MobEffectInstance instance = entity.getEffect((MobEffect)JJKEffects.TRANSFIGURED_SOUL.get());
         if (instance != null) {
            int amplifier = Math.min(6, instance.getAmplifier());
            if (amplifier > 0) {
               int count = Math.max(1, Math.round((entity.getBbWidth() + entity.getBbHeight()) / 2.0F) * (1 + amplifier / 3));
               RandomSource random = RANDOM.get();
               mc.execute(() -> {
                  for (int i = 0; i < count; i++) {
                     double x = entity.getX() + (random.nextDouble() - 0.5) * (entity.getBbWidth() * 2.0F);
                     double y = entity.getY() + random.nextDouble() * entity.getBbHeight();
                     double z = entity.getZ() + (random.nextDouble() - 0.5) * (entity.getBbWidth() * 2.0F);
                     mc.level.addParticle(ParticleTypes.SOUL, x, y, z, 0.0, random.nextDouble() * 0.1, 0.0);
                  }
               });
            }
         }
      }
   }
}
