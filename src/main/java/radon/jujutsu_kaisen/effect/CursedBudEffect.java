package radon.jujutsu_kaisen.effect;

import java.util.HashMap;
import java.util.UUID;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.effect.base.JJKEffect;

public class CursedBudEffect extends JJKEffect {
   private static final HashMap<UUID, Float> AMOUNTS = new HashMap<>();

   protected CursedBudEffect(MobEffectCategory pCategory, int pColor) {
      super(pCategory, pColor);
   }

   public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
      return true;
   }

   public void addAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
      super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
      if (pLivingEntity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         ISorcererData cap = (ISorcererData)pLivingEntity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         AMOUNTS.put(pLivingEntity.getUUID(), cap.getEnergy());
      }
   }

   public void removeAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
      super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
      AMOUNTS.remove(pLivingEntity.getUUID());
   }

   public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
      super.applyEffectTick(pLivingEntity, pAmplifier);
      if (!pLivingEntity.level().isClientSide) {
         if (!pLivingEntity.isDeadOrDying()) {
            if (pLivingEntity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
               ISorcererData cap = (ISorcererData)pLivingEntity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               float previous = AMOUNTS.getOrDefault(pLivingEntity.getUUID(), cap.getEnergy());
               float dmg = (previous - cap.getEnergy()) * 0.05F;
               if (previous > cap.getEnergy()) {
                  pLivingEntity.hurt(pLivingEntity.level().damageSources().generic(), dmg + 1.0F);
               }

               AMOUNTS.put(pLivingEntity.getUUID(), cap.getEnergy());
            }
         }
      }
   }
}
