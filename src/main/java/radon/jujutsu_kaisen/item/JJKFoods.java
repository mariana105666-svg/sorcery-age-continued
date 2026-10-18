package radon.jujutsu_kaisen.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;

public class JJKFoods {
   public static final FoodProperties CURSED_OBJECT = new Builder().nutrition(8).saturationMod(0.8F).meat().alwaysEat().build();
   public static final FoodProperties SORCERER_FLESH = new Builder().nutrition(3).saturationMod(0.3F).meat().alwaysEat().build();
   public static final FoodProperties CURSE_FLESH = new Builder().nutrition(3).saturationMod(0.3F).meat().alwaysEat().build();
   public static final FoodProperties MERGED_FLESH = new Builder().nutrition(3).saturationMod(0.3F).meat().alwaysEat().build();
   public static final FoodProperties TRANSFIGURED_SOUL = new Builder().nutrition(3).saturationMod(0.3F).meat().alwaysEat().build();
}
