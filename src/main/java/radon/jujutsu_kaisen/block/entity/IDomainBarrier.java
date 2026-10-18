package radon.jujutsu_kaisen.block.entity;

import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public interface IDomainBarrier extends IBarrier {
   int getRadius();

   List<DomainExpansionEntity> getClashers();

   DomainExpansionEntity checkSureHitEffect();

   default DomainExpansionEntity sureHitTarget(LivingEntity target) {
      DomainExpansionEntity surehit = this.checkSureHitEffect();
      return surehit != null && surehit.isAffected(target, false) ? surehit : null;
   }
}
