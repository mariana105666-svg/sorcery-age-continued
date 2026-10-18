package radon.jujutsu_kaisen.util;

import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;

public class SorcererUtil {
   public static SorcererGrade getGrade(float experience) {
      SorcererGrade result = SorcererGrade.GRADE_4;

      for (SorcererGrade grade : SorcererGrade.values()) {
         if (experience < grade.getRequiredExperience()) {
            break;
         }

         result = grade;
      }

      return result;
   }

   public static float getPower(float experience) {
      return 1.6F + experience / 1340.0F;
   }

   public static float getDefense(float experience) {
      return 1.3F + experience / 5000.0F;
   }

   public static float getDefenseHR(float experience) {
      return 2.6F + experience / 5000.0F;
   }
}
