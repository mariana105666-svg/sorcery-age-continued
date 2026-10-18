package radon.jujutsu_kaisen.client.model.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.TogeInumakiEntity;

public class TogeInumakiModel extends SkinModel<TogeInumakiEntity> {
   public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation("jujutsu_kaisen", "toge_inumaki"), "main");
   public static ModelLayerLocation INNER_LAYER = new ModelLayerLocation(new ResourceLocation("jujutsu_kaisen", "toge_inumaki"), "inner_armor");
   public static ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(new ResourceLocation("jujutsu_kaisen", "toge_inumaki"), "outer_armor");

   public TogeInumakiModel(ModelPart pRoot) {
      super(pRoot);
   }
}
