package radon.jujutsu_kaisen.ability.limitless;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class UnlimitedVoid extends DomainExpansion implements DomainExpansion.IClosedDomain {
   @Override
   public List<Block> getBlocks() {
      return List.of((Block)JJKBlocks.DOMAIN_SKY.get());
   }

   @Override
   public List<Block> getBottomFloorBlocks() {
      return List.of((Block)JJKBlocks.DOMAIN_FILLER.get());
   }

   @Override
   public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
      super.onHitEntity(domain, owner, entity, instant);
      JJKEffectUtil.addEffect(
         entity, new MobEffectInstance((MobEffect)JJKEffects.UNLIMITED_VOID.get(), Math.round(200.0F * getStrength(owner, instant)), 0, false, false, false)
      );
      JJKEffectUtil.addEffect(
         entity, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), Math.round(200.0F * getStrength(owner, instant)), 0, false, false, false)
      );
      JJKEffectUtil.addEffect(entity, new MobEffectInstance(MobEffects.BLINDNESS, Math.round(200.0F * getStrength(owner, instant)), 4, false, false, false));
      if (domain.getTime() % 20 == 0 && entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         ISorcererData entityCap = (ISorcererData)entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         entityCap.increaseBrainDamage();
         if (entity instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(entityCap.serializeNBT()), player);
         }
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.DOMAIN;
   }

   @Override
   public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {
   }

   @Override
   protected DomainExpansionEntity createBarrier(LivingEntity owner) {
      int radius = Math.round(this.getRadius(owner));
      ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this, radius);
      owner.level().addFreshEntity(domain);
      return domain;
   }
}
