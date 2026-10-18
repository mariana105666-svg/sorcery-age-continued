package radon.jujutsu_kaisen.client.render.item;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.item.JetBlackShadowSwordItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PolearmStaffRenderer extends GeoItemRenderer<JetBlackShadowSwordItem> {
   public PolearmStaffRenderer() {
      super(new DefaultedItemGeoModel(new ResourceLocation("jujutsu_kaisen", "polearm_staff")));
   }
}
