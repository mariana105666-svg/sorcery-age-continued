package radon.jujutsu_kaisen.client.effect;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import radon.jujutsu_kaisen.client.effect.base.PostEffect;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnnaEntity;

public class KuchisakeOnnaPostEffect extends PostEffect {
   private static final ResourceLocation EFFECT = new ResourceLocation("jujutsu_kaisen", "shaders/post/kuchisake_onna.json");

   @Override
   protected ResourceLocation getEffect() {
      return EFFECT;
   }

   @Override
   public boolean shouldRender(LocalPlayer player) {
      for (KuchisakeOnnaEntity curse : player.level().getEntitiesOfClass(KuchisakeOnnaEntity.class, AABB.ofSize(player.position(), 64.0, 64.0, 64.0))) {
         Optional<UUID> identifier = curse.getCurrent();
         if (!identifier.isEmpty() && identifier.get().equals(player.getUUID())) {
            return true;
         }
      }

      return false;
   }
}
