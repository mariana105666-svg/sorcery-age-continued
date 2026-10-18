package radon.jujutsu_kaisen.ability.misc;

import java.util.Iterator;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.block.entity.IDomainBarrier;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.JJKPose;
import radon.jujutsu_kaisen.client.JJKPoses;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.HollowWickerBasketEntity;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public class HollowWickerBasket extends Summon<HollowWickerBasketEntity> implements Ability.IPosedMove {
   public HollowWickerBasket() {
      super(HollowWickerBasketEntity.class);
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      Iterator var3 = VeilHandler.getDomainBarriers((ServerLevel)owner.level(), owner.blockPosition()).iterator();
      if (var3.hasNext()) {
         IDomainBarrier domain = (IDomainBarrier)var3.next();

         for (DomainExpansionEntity d : domain.getClashers()) {
            if (d.getOwner() == owner) {
               return false;
            }
         }

         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         CursedTechnique ct = cap.getTechnique();
         return ct == null || ct.getDomain() == null || cap.hasBurnout();
      } else {
         return false;
      }
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return (cap.hasTrait(Trait.INCARNATED) || (Boolean)ConfigHolder.SERVER.hwbForAll.get()) && super.isValid(owner);
   }

   @Override
   public int getCooldown() {
      return 400;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 1.0F;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.DOMAIN;
   }

   @Override
   public JJKPose getArmPose(LivingEntity entityLiving) {
      return JJKPoses.HOLLOW_WICKER_BASKET;
   }

   @Override
   public boolean canUnlock(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return (cap.hasTrait(Trait.INCARNATED) || (Boolean)ConfigHolder.SERVER.hwbForAll.get()) && super.canUnlock(owner);
   }

   @Override
   public boolean isDisplayed(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return (cap.hasTrait(Trait.INCARNATED) || (Boolean)ConfigHolder.SERVER.hwbForAll.get()) && super.isDisplayed(owner);
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Nullable
   @Override
   public Ability getParent(LivingEntity owner) {
      return (Ability)JJKAbilities.CURSED_ENERGY_FLOW.get();
   }

   @Override
   public Vec2 getDisplayCoordinates() {
      return new Vec2(3.0F, 6.0F);
   }

   @Override
   public int getPointsCost() {
      return (Integer)ConfigHolder.SERVER.hollowWickerBasketCost.get();
   }

   @Override
   public boolean display() {
      return false;
   }

   @Override
   public List<EntityType<?>> getTypes() {
      return List.of((EntityType<?>)JJKEntities.HOLLOW_WICKER_BASKET.get());
   }

   @Override
   public boolean isTenShadows() {
      return false;
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   protected HollowWickerBasketEntity summon(LivingEntity owner) {
      return new HollowWickerBasketEntity(owner);
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class SimpleDomainForgeEvents {
      @SubscribeEvent
      public static void onLivingAttack(LivingAttackEvent event) {
         if (event.getSource().getDirectEntity() instanceof DomainExpansionEntity) {
            LivingEntity victim = event.getEntity();
            if (!victim.level().isClientSide && JJKAbilities.hasToggled(victim, (Ability)JJKAbilities.HOLLOW_WICKER_BASKET.get())) {
               ISorcererData cap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               HollowWickerBasketEntity domain = cap.getSummonByClass(HollowWickerBasketEntity.class);
               if (domain != null) {
                  event.setCanceled(true);
               }
            }
         }
      }
   }
}
