package radon.jujutsu_kaisen.entity.idle_transfiguration;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.idle_transfiguration.base.TransfiguredSoulVariantEntity;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;

public class TransfiguredSoulSmallEntity extends TransfiguredSoulVariantEntity {
   public TransfiguredSoulSmallEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public TransfiguredSoulSmallEntity(LivingEntity owner) {
      super((EntityType<? extends TamableAnimal>)JJKEntities.TRANSFIGURED_SOUL_SMALL.get(), owner);
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes()
         .add(Attributes.MAX_HEALTH, 3.0)
         .add(Attributes.ARMOR, 6.0)
         .add(Attributes.ARMOR_TOUGHNESS, 4.0)
         .add(Attributes.MOVEMENT_SPEED, 0.33)
         .add(Attributes.ATTACK_DAMAGE, 4.5);
   }

   @Override
   public Summon<?> getAbility() {
      return (Summon<?>)JJKAbilities.TRANSFIGURED_SOUL_SMALL.get();
   }
}
