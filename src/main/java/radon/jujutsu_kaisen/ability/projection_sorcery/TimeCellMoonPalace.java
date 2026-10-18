package radon.jujutsu_kaisen.ability.projection_sorcery;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.TimeCellMoonPalaceEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionCenterEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.projectile.FilmGaugeProjectile;
import radon.jujutsu_kaisen.util.RotationUtil;

public class TimeCellMoonPalace extends DomainExpansion implements DomainExpansion.IClosedDomain {
   @Nullable
   @Override
   public ParticleOptions getEnvironmentParticle() {
      return ParticleTypes.WHITE_ASH;
   }

   @Override
   public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
      super.onHitEntity(domain, owner, entity, instant);
      if (instant || owner.level().getGameTime() % 40L == 0L) {
         DomainExpansionCenterEntity center = domain.getDomainCenter();
         if (center == null) {
            return;
         }

         owner.level().addFreshEntity(new FilmGaugeProjectile(owner, this.getPower(owner), entity, center));
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
      TimeCellMoonPalaceEntity entity = new TimeCellMoonPalaceEntity(domain);
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      Vec3 pos = owner.position().subtract(RotationUtil.getTargetAdjustedLookAngle(owner).multiply(entity.getBbWidth(), 0.0, entity.getBbWidth()));
      entity.moveTo(pos.x, pos.y, pos.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));
      double d0 = look.horizontalDistance();
      entity.setYRot((float)(Mth.atan2(look.x, look.z) * 180.0F / (float)Math.PI));
      entity.setXRot((float)(Mth.atan2(look.y, d0) * 180.0F / (float)Math.PI));
      if (!owner.level().isClientSide) {
         owner.level().addFreshEntity(entity);
      }

      return domain;
   }

   @Override
   public List<Block> getBottomFloorBlocks() {
      return List.of((Block)JJKBlocks.DOMAIN_FILLER.get());
   }

   @Override
   public List<Block> getBlocks() {
      return List.of((Block)JJKBlocks.DOMAIN_SKY.get());
   }
}
