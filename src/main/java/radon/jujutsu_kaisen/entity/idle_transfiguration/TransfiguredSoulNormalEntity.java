package radon.jujutsu_kaisen.entity.idle_transfiguration;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.idle_transfiguration.base.TransfiguredSoulVariantEntity;

public class TransfiguredSoulNormalEntity extends TransfiguredSoulVariantEntity {
   public TransfiguredSoulNormalEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public TransfiguredSoulNormalEntity(LivingEntity owner) {
      super((EntityType<? extends TamableAnimal>)JJKEntities.TRANSFIGURED_SOUL_NORMAL.get(), owner);
   }

   @Override
   public Summon<?> getAbility() {
      return (Summon<?>)JJKAbilities.TRANSFIGURED_SOUL_NORMAL.get();
   }
}
