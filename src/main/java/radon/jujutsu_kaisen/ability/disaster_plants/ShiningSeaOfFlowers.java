package radon.jujutsu_kaisen.ability.disaster_plants;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public class ShiningSeaOfFlowers extends DomainExpansion implements DomainExpansion.IClosedDomain {
   @Override
   public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
      super.onHitEntity(domain, owner, entity, instant);
      JJKEffectUtil.addEffect(entity, new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Math.round(200.0F * getStrength(owner, instant)), 4, false, false, false));
      JJKEffectUtil.addEffect(entity, new MobEffectInstance(MobEffects.DIG_SLOWDOWN, Math.round(200.0F * getStrength(owner, instant)), 4, false, false, false));
      JJKEffectUtil.addEffect(entity, new MobEffectInstance(MobEffects.WEAKNESS, Math.round(200.0F * getStrength(owner, instant)), 4, false, false, false));
      if (JJKAbilities.hasToggled(entity, (Ability)JJKAbilities.CURSED_ENERGY_FLOW.get())
         && !JJKAbilities.hasToggled(entity, (Ability)JJKAbilities.FALLING_BLOSSOM_EMOTION.get())) {
         JJKEffectUtil.addEffect(entity, new MobEffectInstance((MobEffect)JJKEffects.CURSED_BUD.get(), Math.round(200.0F * getStrength(owner, instant)), 0));
      }
   }

   @Override
   public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.DOMAIN;
   }

   @Override
   protected DomainExpansionEntity createBarrier(LivingEntity owner) {
      int radius = Math.round(this.getRadius(owner));
      ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this, radius);
      owner.level().addFreshEntity(domain);
      return domain;
   }

   @Override
   public List<Block> getBlocks() {
      return List.of((Block)JJKBlocks.FAKE_SKY.get());
   }

   @Override
   public List<Block> getFillBlocks() {
      return List.of((Block)JJKBlocks.SHINING_SEA_OF_FLOWERS_FILL.get());
   }

   @Override
   public List<Block> getFloorBlocks() {
      return List.of((Block)JJKBlocks.SHINING_SEA_OF_FLOWERS_FLOOR.get());
   }

   @Override
   public List<Block> getDecorationBlocks() {
      return List.of(
         (Block)JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_ONE.get(),
         (Block)JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_TWO.get(),
         (Block)JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_THREE.get(),
         (Block)JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_FOUR.get()
      );
   }

   @Override
   public boolean canPlaceFloor(ClosedDomainExpansionEntity domain, BlockPos pos) {
      return !domain.level().getBlockState(pos).isAir() && domain.level().getBlockState(pos.above()).isAir();
   }

   @Override
   public boolean canPlaceDecoration(ClosedDomainExpansionEntity domain, BlockPos pos) {
      return domain.level().getBlockState(pos.below()).is((Block)JJKBlocks.SHINING_SEA_OF_FLOWERS_FLOOR.get());
   }
}
