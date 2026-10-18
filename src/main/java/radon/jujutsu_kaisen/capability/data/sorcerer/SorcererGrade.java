package radon.jujutsu_kaisen.capability.data.sorcerer;

import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.config.ConfigHolder;

public enum SorcererGrade {
   GRADE_4(((Double)ConfigHolder.SERVER.Grade4Exp.get()).floatValue()),
   GRADE_3(((Double)ConfigHolder.SERVER.Grade3Exp.get()).floatValue()),
   SEMI_GRADE_2(((Double)ConfigHolder.SERVER.SemiGrade2Exp.get()).floatValue()),
   GRADE_2(((Double)ConfigHolder.SERVER.Grade2Exp.get()).floatValue()),
   SEMI_GRADE_1(((Double)ConfigHolder.SERVER.SemiGrade1Exp.get()).floatValue()),
   GRADE_1(((Double)ConfigHolder.SERVER.Grade1Exp.get()).floatValue()),
   SPECIAL_GRADE(((Double)ConfigHolder.SERVER.SpecialGradeExp.get()).floatValue());

   private final float required;

   private SorcererGrade(float required) {
      this.required = required;
   }

   public float getRequiredExperience() {
      return this.required;
   }

   public Component getName() {
      return Component.translatable(String.format("grade.%s.%s", "jujutsu_kaisen", this.name().toLowerCase()));
   }

   public SorcererGrade getNext() {
      SorcererGrade[] grades = values();
      int index = this.ordinal();
      return index < grades.length - 1 ? grades[index + 1] : grades[index];
   }
}
