package radon.jujutsu_kaisen.ability.ten_shadows;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.ChimeraShadowGardenEntity;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ChimeraShadowGarden extends DomainExpansion implements DomainExpansion.IClosedDomain {
   @Override
   public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
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
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (cap == null) {
         return null;
      } else {
         int radius = Math.round(this.getRadius(owner));
         ClosedDomainExpansionEntity domain = new ClosedDomainExpansionEntity(owner, this, radius);
         owner.level().addFreshEntity(domain);
         ChimeraShadowGardenEntity center = new ChimeraShadowGardenEntity(domain);
         Vec3 pos = owner.position()
            .subtract(RotationUtil.calculateViewVector(0.0F, owner.getYRot()).multiply(center.getBbWidth() / 2.0F, 0.0, center.getBbWidth() / 2.0F));
         center.moveTo(pos.x, pos.y, pos.z, 180.0F - RotationUtil.getTargetAdjustedYRot(owner), 0.0F);
         owner.level().addFreshEntity(center);
         if (owner.level() instanceof ServerLevel) {
            List<TenShadowsSummon> summons = new ArrayList<>();

            for (Entity entity : cap.getSummons()) {
               if (entity instanceof TenShadowsSummon summon && summon.isTame()) {
                  summons.add(summon);
               }
            }

            for (TenShadowsSummon summon : summons) {
               if (summons.stream().noneMatch(x -> x.getJujutsuType() == summon.getJujutsuType() && x.isClone())) {
                  summon.getAbility().spawn(owner, true);
               }
            }
         }

         return domain;
      }
   }

   @Override
   public List<Block> getBlocks() {
      return List.of((Block)JJKBlocks.DOMAIN_SKY.get());
   }

   @Override
   public List<Block> getFloorBlocks() {
      return List.of((Block)JJKBlocks.DOMAIN_FILLER.get());
   }

   @Override
   public List<Block> getFillBlocks() {
      return List.of((Block)JJKBlocks.DOMAIN_FILLER.get());
   }

   @Override
   public List<Block> getDecorationBlocks() {
      return List.of((Block)JJKBlocks.CHIMERA_SHADOW_GARDEN.get());
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class ChimeraShadowGardenForgeEvents {
      @SubscribeEvent
      public static void onAbilityTrigger(AbilityTriggerEvent.Pre event) {
         LivingEntity owner = event.getEntity();
         if (JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.CHIMERA_SHADOW_GARDEN.get())
            && event.getAbility() instanceof Summon<?> summon
            && summon.isTamed(owner)) {
            summon.spawn(owner, true);
         }
      }
   }
}
