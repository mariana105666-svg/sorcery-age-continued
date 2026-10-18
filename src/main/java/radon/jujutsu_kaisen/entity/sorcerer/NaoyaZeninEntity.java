package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;

public class NaoyaZeninEntity extends SorcererEntity {
   public NaoyaZeninEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
      super(pType, pLevel);
   }

   @Override
   protected boolean isCustom() {
      return false;
   }

   @Override
   public float getExperience() {
      return (SorcererGrade.GRADE_1.getRequiredExperience() + SorcererGrade.SPECIAL_GRADE.getRequiredExperience()) / 2.0F;
   }

   public static Builder createAttributes() {
      return createMobAttributes()
         .add(Attributes.ATTACK_DAMAGE)
         .add(Attributes.FOLLOW_RANGE, 140.0)
         .add(Attributes.ARMOR_TOUGHNESS, 10.0)
         .add(Attributes.ARMOR, 20.0);
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return CursedTechnique.PROJECTION_SORCERY;
   }

   @Override
   public JujutsuType getJujutsuType() {
      return JujutsuType.SORCERER;
   }
}
