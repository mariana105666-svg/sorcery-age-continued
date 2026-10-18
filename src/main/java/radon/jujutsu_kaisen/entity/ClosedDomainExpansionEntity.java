package radon.jujutsu_kaisen.entity;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ClosedDomainExpansionEntity extends DomainExpansionEntity {
   private DomainBarrierEntity domainBarrier;

   public ClosedDomainExpansionEntity(EntityType<?> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public ClosedDomainExpansionEntity(LivingEntity owner, DomainExpansion ability, int radius) {
      this((EntityType<?>)JJKEntities.CLOSED_DOMAIN_EXPANSION.get(), owner, ability, radius);
   }

   public ClosedDomainExpansionEntity(EntityType<?> pType, LivingEntity owner, DomainExpansion ability, int radius) {
      super(pType, owner, ability);
      DomainBarrierEntity existing = null;
      Vec3 rawDir = RotationUtil.getTargetAdjustedLookAngle(owner);
      Vec3 horizontal = new Vec3(rawDir.x, 0.0, rawDir.z).normalize();
      Vec3 base = owner.position().add(horizontal.scale(radius - 5));
      Vec3 finalPos = base.subtract(0.0, radius, 0.0);
      this.moveTo(
         owner.position().x,
         owner.position().y,
         owner.position().z,
         RotationUtil.getTargetAdjustedYRot(owner),
         RotationUtil.getTargetAdjustedXRot(owner)
      );
      existing = this.getClosestDomainBarrier(owner.blockPosition());
      if (existing == null) {
         this.domainBarrier = new DomainBarrierEntity(owner, finalPos, radius);
         owner.level().addFreshEntity(this.domainBarrier);
      } else {
         this.domainBarrier = existing;
      }

      this.domainBarrier.registerClasher(this);
   }

   public void onAddedToWorld() {
      super.onAddedToWorld();
      this.refreshDimensions();
   }

   @Override
   public boolean isBarrier(BlockPos pos) {
      return this.domainBarrier == null ? false : this.domainBarrier.isBarrier(pos);
   }

   @Override
   public boolean isBarrierOrInside(BlockPos pos) {
      return this.isBarrier(pos) || this.isInsideBarrier(pos);
   }

   @Override
   public boolean isInsideBarrier(BlockPos pos) {
      return this.domainBarrier == null ? false : this.domainBarrier.isInsideBarrier(pos);
   }

   @Override
   public DomainExpansionEntity checkSureHitEffect() {
      if (this.domainBarrier == null) {
         return null;
      } else {
         return this.isClashing() ? null : this.domainBarrier.checkSureHitEffect();
      }
   }

   @Override
   public DomainExpansionEntity sureHitTarget(LivingEntity target) {
      return this.domainBarrier == null ? null : this.domainBarrier.sureHitTarget(target);
   }

   @Override
   public AABB getBounds() {
      return this.domainBarrier == null ? null : this.domainBarrier.getBounds();
   }

   @Override
   public List<LivingEntity> getAffected() {
      return this.domainBarrier == null ? List.of() : this.level().getEntitiesOfClass(LivingEntity.class, this.getBounds(), entity -> this.isAffected(entity));
   }

   public boolean isCompleted() {
      return this.domainBarrier == null ? false : this.domainBarrier.isCompleted();
   }

   @Override
   public void remove(@NotNull RemovalReason pReason) {
      super.remove(pReason);
      if (this.domainBarrier != null) {
         this.domainBarrier.unregisterClasher(this, true);
      }
   }

   @Override
   public void tick() {
      super.tick();
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         if (!this.level().isClientSide) {
            if (this.domainBarrier == null || this.domainBarrier.isCompleted() && !this.isInsideBarrier(owner.blockPosition())) {
               this.discard();
            }
         }
      }
   }
}
