package radon.jujutsu_kaisen.item.cursed_object;

import net.minecraft.world.item.Item.Properties;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.base.CursedObjectItem;

public class CursedEyeOfEnderItem extends CursedObjectItem {
   public CursedEyeOfEnderItem(Properties pProperties) {
      super(pProperties);
   }

   @Override
   public SorcererGrade getGrade() {
      return SorcererGrade.GRADE_2;
   }
}
