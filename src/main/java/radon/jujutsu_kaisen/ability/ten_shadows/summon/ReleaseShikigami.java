package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;

public class ReleaseShikigami extends Ability {
   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target == null) {
         if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            return false;
         } else {
            ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            List<TenShadowsSummon> summons = new ArrayList<>();

            for (Entity entity : cap.getSummons()) {
               if (entity instanceof TenShadowsSummon summon && summon.isTame()) {
                  summons.add(summon);
               }
            }

            return !summons.isEmpty();
         }
      } else {
         return false;
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      if (!owner.level().isClientSide) {
         owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            for (Entity entity : cap.getSummons()) {
               if (entity instanceof TenShadowsSummon summon && summon.isTame()) {
                  summon.discard();
               }
            }
         });
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.0F;
   }
}
