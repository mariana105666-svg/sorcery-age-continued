package radon.jujutsu_kaisen.client.render.item.armor;

import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.item.armor.MythicalBeastAmberItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class MythicalBeastAmberRenderer extends GeoArmorRenderer<MythicalBeastAmberItem> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("jujutsu_kaisen", "textures/item/armor/mythical_beast_amber.png");

   public MythicalBeastAmberRenderer() {
      super(new DefaultedItemGeoModel(new ResourceLocation("jujutsu_kaisen", "armor/mythical_beast_amber")));
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
