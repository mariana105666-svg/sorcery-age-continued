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
import radon.jujutsu_kaisen.entity.idle_transfiguration.base.TransfiguredSoulEntity;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;

public class PolymorphicSoulIsomerEntity extends TransfiguredSoulEntity {
   public PolymorphicSoulIsomerEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public PolymorphicSoulIsomerEntity(LivingEntity owner) {
      super((EntityType<? extends TamableAnimal>)JJKEntities.POLYMORPHIC_SOUL_ISOMER.get(), owner);
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes()
         .add(Attributes.MAX_HEALTH, 9.0)
         .add(Attributes.ARMOR, 12.0)
         .add(Attributes.ARMOR_TOUGHNESS, 8.0)
         .add(Attributes.ATTACK_DAMAGE, 14.0);
   }

   @Override
   public Summon<?> getAbility() {
      return (Summon<?>)JJKAbilities.POLYMORPHIC_SOUl_ISOMER.get();
   }
}
