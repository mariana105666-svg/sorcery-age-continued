package radon.jujutsu_kaisen.ability.base;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

public interface ITransformation {
   boolean isReplacement();

   Item getItem();

   ITransformation.Part getBodyPart();

   default float getSlimTranslation() {
      return 0.0F;
   }

   public static enum Part {
      HEAD,
      RIGHT_ARM,
      LEFT_ARM,
      LEGS,
      BODY;

      public EquipmentSlot getSlot() {
         return switch (this) {
            case HEAD -> EquipmentSlot.HEAD;
            case BODY, RIGHT_ARM, LEFT_ARM -> EquipmentSlot.CHEST;
            case LEGS -> EquipmentSlot.LEGS;
         };
      }
   }
}
