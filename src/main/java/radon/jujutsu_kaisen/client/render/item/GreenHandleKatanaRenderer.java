package radon.jujutsu_kaisen.client.render.item;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.item.cursed_tool.GreenHandleKatana;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GreenHandleKatanaRenderer extends GeoItemRenderer<GreenHandleKatana> {
   public GreenHandleKatanaRenderer() {
      super(new DefaultedItemGeoModel(new ResourceLocation("jujutsu_kaisen", "green_handle_katana")));
   }
}
