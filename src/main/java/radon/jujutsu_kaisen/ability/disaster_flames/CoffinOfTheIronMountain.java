package radon.jujutsu_kaisen.ability.disaster_flames;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.LavaRockProjectile;

public class CoffinOfTheIronMountain extends DomainExpansion implements DomainExpansion.IClosedDomain {
   private static final float DAMAGE = 10.0F;

   @Override
   public List<Block> getBlocks() {
      return List.of(
         (Block)JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_ONE.get(),
         (Block)JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_TWO.get(),
         (Block)JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_THREE.get()
      );
   }

   @Nullable
   @Override
   public ParticleOptions getEnvironmentParticle() {
      return ParticleTypes.LARGE_SMOKE;
   }

   @Override
   public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
      super.onHitEntity(domain, owner, entity, instant);
      if (instant || owner.level().getGameTime() % 20L == 0L) {
         if (entity.hurt(JJKDamageSources.indirectJujutsuAttack(domain, owner, this), 10.0F * this.getPower(owner) * getStrength(owner, false))) {
            entity.setSecondsOnFire(15);
         }

         if (owner.hasLineOfSight(entity)) {
            LavaRockProjectile rock = new LavaRockProjectile(owner, this.getPower(owner) * getStrength(owner, false), entity);
            rock.setDomain(true);
            owner.level().addFreshEntity(rock);
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
}
