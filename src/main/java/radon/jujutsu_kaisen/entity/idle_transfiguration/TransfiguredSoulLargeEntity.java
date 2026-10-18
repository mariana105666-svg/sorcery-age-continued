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

public class TransfiguredSoulLargeEntity extends TransfiguredSoulVariantEntity {
   public TransfiguredSoulLargeEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public TransfiguredSoulLargeEntity(LivingEntity owner) {
      super((EntityType<? extends TamableAnimal>)JJKEntities.TRANSFIGURED_SOUL_LARGE.get(), owner);
   }

   public static Builder createAttributes() {
      return SorcererEntity.createAttributes()
         .add(Attributes.MAX_HEALTH, 10.0)
         .add(Attributes.ARMOR, 16.0)
         .add(Attributes.ARMOR_TOUGHNESS, 10.0)
         .add(Attributes.ATTACK_DAMAGE, 7.5);
   }

   @Override
   public Summon<?> getAbility() {
      return (Summon<?>)JJKAbilities.TRANSFIGURED_SOUL_LARGE.get();
   }
}
