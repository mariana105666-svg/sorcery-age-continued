package radon.jujutsu_kaisen.client.gui.screen;

import java.util.AbstractMap.SimpleEntry;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;

public class DisplayItem {
   public final DisplayItem.Type type;
   public Ability ability;
   public SimpleEntry<AbsorbedCurse, Integer> curse;
   public CursedTechnique copied;
   public CursedTechnique absorbed;
   public CursedTechnique stolen;

   public DisplayItem(DisplayItem.Type type) {
      this.type = type;
   }

   public DisplayItem(Ability ability) {
      this(DisplayItem.Type.ABILITY);
      this.ability = ability;
   }

   public DisplayItem(AbsorbedCurse curse, int index) {
      this(DisplayItem.Type.CURSE);
      this.curse = new SimpleEntry<>(curse, index);
   }

   public DisplayItem(DisplayItem.Type type, CursedTechnique technique) {
      this(type);
      if (this.type == DisplayItem.Type.COPIED) {
         this.copied = technique;
      } else if (this.type == DisplayItem.Type.STOLEN) {
         this.stolen = technique;
      } else {
         this.absorbed = technique;
      }
   }

   public static enum Type {
      ABILITY,
      CURSE,
      COPIED,
      ABSORBED,
      STOLEN;
   }
}
