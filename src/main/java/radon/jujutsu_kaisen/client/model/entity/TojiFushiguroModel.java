package radon.jujutsu_kaisen.client.model.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.TojiFushiguroEntity;

public class TojiFushiguroModel extends SkinModel<TojiFushiguroEntity> {
   public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation("jujutsu_kaisen", "toji_fushiguro"), "main");
   public static ModelLayerLocation INNER_LAYER = new ModelLayerLocation(new ResourceLocation("jujutsu_kaisen", "toji_fushiguro"), "inner_armor");
   public static ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(new ResourceLocation("jujutsu_kaisen", "toji_fushiguro"), "outer_armor");

   public TojiFushiguroModel(ModelPart pRoot) {
      super(pRoot);
   }
}
