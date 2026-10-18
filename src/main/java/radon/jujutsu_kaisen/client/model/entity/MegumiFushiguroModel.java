package radon.jujutsu_kaisen.client.model.entity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import radon.jujutsu_kaisen.client.model.base.SkinModel;
import radon.jujutsu_kaisen.entity.sorcerer.MegumiFushiguroEntity;

public class MegumiFushiguroModel extends SkinModel<MegumiFushiguroEntity> {
   public static ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation("jujutsu_kaisen", "megumi_fushiguro"), "main");
   public static ModelLayerLocation INNER_LAYER = new ModelLayerLocation(new ResourceLocation("jujutsu_kaisen", "megumi_fushiguro"), "inner_armor");
   public static ModelLayerLocation OUTER_LAYER = new ModelLayerLocation(new ResourceLocation("jujutsu_kaisen", "megumi_fushiguro"), "outer_armor");

   public MegumiFushiguroModel(ModelPart pRoot) {
      super(pRoot);
   }
}
