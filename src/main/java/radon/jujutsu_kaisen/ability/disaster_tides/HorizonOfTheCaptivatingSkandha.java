package radon.jujutsu_kaisen.ability.disaster_tides;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public class HorizonOfTheCaptivatingSkandha extends DomainExpansion implements DomainExpansion.IClosedDomain {
   private static final float DAMAGE = 10.0F;

   @Override
   public List<Block> getBlocks() {
      return List.of((Block)JJKBlocks.FAKE_SKY.get());
   }

   @Override
   public List<Block> getFillBlocks() {
      return List.of((Block)JJKBlocks.HORIZON_OF_THE_CAPTIVATING_SKANDHA_FILL.get());
   }

   @Override
   public List<Block> getDecorationBlocks() {
      return List.of((Block)JJKBlocks.FAKE_WATER_DOMAIN.get());
   }

   @Override
   public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
      if (instant || owner.level().getGameTime() % 20L == 0L) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         entity.hurt(
            JJKDamageSources.indirectJujutsuAttack(domain, owner, (Ability)JJKAbilities.DEATH_SWARM.get()),
            10.0F * this.getPower(owner) * getStrength(owner, false)
         );
         if (owner.hasLineOfSight(entity)) {
            Ability fish = (Ability)JJKAbilities.DEATH_SWARM.get();
            ((Ability.IDomainAttack)fish).performEntity(owner, entity, domain);
         }
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
   public boolean canPlaceDecoration(ClosedDomainExpansionEntity domain, BlockPos pos) {
      LivingEntity owner = domain.getOwner();
      return owner != null && pos.getY() < owner.getBlockY();
   }
}
