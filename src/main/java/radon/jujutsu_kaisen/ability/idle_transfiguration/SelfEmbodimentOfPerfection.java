package radon.jujutsu_kaisen.ability.idle_transfiguration;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.SelfEmbodimentOfPerfectionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public class SelfEmbodimentOfPerfection extends DomainExpansion implements DomainExpansion.IClosedDomain {
   @Override
   public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
      super.onHitEntity(domain, owner, entity, instant);
      if (!IdleTransfiguration.checkSukuna(owner, entity)) {
         int amplifier = 2;
         MobEffectInstance existing = entity.getEffect((MobEffect)JJKEffects.TRANSFIGURED_SOUL.get());
         if (existing != null) {
            amplifier = existing.getAmplifier() + 1;
         }

         if (amplifier > 6) {
            amplifier = 6;
         }

         MobEffectInstance instance = new MobEffectInstance(
            (MobEffect)JJKEffects.TRANSFIGURED_SOUL.get(), Math.round(400.0F * getStrength(owner, instant)), amplifier, false, true, true
         );
         JJKEffectUtil.addEffect(entity, instance);
         if (!owner.level().isClientSide) {
            PacketDistributor.TRACKING_ENTITY.with(() -> entity).send(new ClientboundUpdateMobEffectPacket(entity.getId(), instance));
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
      SelfEmbodimentOfPerfectionEntity entity = new SelfEmbodimentOfPerfectionEntity(domain);
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      Vec3 pos = owner.position().add(owner.getUpVector(1.0F).scale(entity.getBbHeight())).subtract(look.multiply(entity.getBbWidth(), 0.0, entity.getBbWidth()));
      entity.moveTo(pos.x, pos.y, pos.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));
      double d0 = look.horizontalDistance();
      entity.setYRot((float)(Mth.atan2(look.x, look.z) * 180.0F / (float)Math.PI));
      entity.setXRot((float)(Mth.atan2(look.y, d0) * 180.0F / (float)Math.PI));
      owner.level().addFreshEntity(entity);
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
