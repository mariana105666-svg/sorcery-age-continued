package radon.jujutsu_kaisen.ability.misc;

import java.util.Iterator;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public class SimpleDomain extends Summon<SimpleDomainEntity> {
   public SimpleDomain() {
      super(SimpleDomainEntity.class);
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
   public boolean canUnlock(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return (
            !cap.hasTrait(Trait.INCARNATED) || cap.getTechnique() == CursedTechnique.TECHNIQUELESS || (Boolean)ConfigHolder.SERVER.incarnatedSimpleDomain.get()
         )
         && super.canUnlock(owner);
   }

   @Override
   public boolean isDisplayed(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return (
            !cap.hasTrait(Trait.INCARNATED) || cap.getTechnique() == CursedTechnique.TECHNIQUELESS || (Boolean)ConfigHolder.SERVER.incarnatedSimpleDomain.get()
         )
         && super.isDisplayed(owner);
   }

   @Override
   public boolean isTechnique() {
      return false;
   }

   @Override
   public List<EntityType<?>> getTypes() {
      return List.of((EntityType<?>)JJKEntities.SIMPLE_DOMAIN.get());
   }

   @Override
   public boolean isTenShadows() {
      return false;
   }

   protected SimpleDomainEntity summon(LivingEntity owner) {
      return new SimpleDomainEntity(owner);
   }

   @Override
   public int getCooldown() {
      return 600;
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
   public boolean display() {
      return false;
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
   public int getPointsCost() {
      return (Integer)ConfigHolder.SERVER.simpleDomainCost.get();
   }

   @Override
   public Vec2 getDisplayCoordinates() {
      return new Vec2(3.0F, 4.0F);
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class SimpleDomainForgeEvents {
      @SubscribeEvent
      public static void onLivingHurt(LivingHurtEvent event) {
         if (event.getSource() instanceof JJKDamageSources.JujutsuDamageSource jjkdmg) {
            LivingEntity var8 = event.getEntity();
            Ability ability = jjkdmg.getAbility();
            if (ability != null) {
               if (ability.isTechnique()) {
                  for (SimpleDomainEntity simple : var8.level().getEntitiesOfClass(SimpleDomainEntity.class, AABB.ofSize(var8.position(), 8.0, 8.0, 8.0))) {
                     if (var8.distanceTo(simple) < simple.getRadius()) {
                        DamageSource source = event.getSource();
                        LivingEntity causingEntity = source.getEntity() instanceof LivingEntity ? (LivingEntity)source.getEntity() : null;
                        if (causingEntity != simple.getOwner() && causingEntity != var8) {
                           event.setAmount(event.getAmount() * 0.85F);
                           simple.hurt(event.getSource(), event.getAmount() * 1.25F, false);
                        }
                     }
                  }
               }
            }
         }
      }

      @SubscribeEvent
      public static void onLivingAttack(LivingAttackEvent event) {
         if (event.getSource().getDirectEntity() instanceof DomainExpansionEntity) {
            LivingEntity victim = event.getEntity();
            if (!victim.level().isClientSide && JJKAbilities.hasToggled(victim, (Ability)JJKAbilities.SIMPLE_DOMAIN.get())) {
               ISorcererData cap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               SimpleDomainEntity domain = cap.getSummonByClass(SimpleDomainEntity.class);
               if (domain != null) {
                  domain.hurt(event.getSource(), event.getAmount());
                  event.setCanceled(true);
               }
            }
         }
      }
   }
}
