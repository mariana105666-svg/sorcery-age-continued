package radon.jujutsu_kaisen.item.cursed_tool;

import java.util.function.Consumer;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import radon.jujutsu_kaisen.client.render.item.RedHandleKatanaRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RedHandleKatana extends SwordItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public RedHandleKatana(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
      super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(new IClientItemExtensions() {
         private RedHandleKatanaRenderer renderer;

         public RedHandleKatanaRenderer getCustomRenderer() {
            if (this.renderer == null) {
               this.renderer = new RedHandleKatanaRenderer();
            }

            return this.renderer;
         }
      });
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
