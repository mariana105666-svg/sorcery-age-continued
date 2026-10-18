package radon.jujutsu_kaisen.ability.shrine;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.entity.IDomain;
import radon.jujutsu_kaisen.block.entity.IDomainBarrier;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.MalevolentShrineClosedEntity;
import radon.jujutsu_kaisen.entity.MalevolentShrineEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class MalevolentShrine extends DomainExpansion implements DomainExpansion.IClosedDomain {
   public static final int DELAY = 34;
   private static final int INTERVAL = 5;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      boolean enemyDomain = false;
      IDomain selfDomain = null;

      for (IDomainBarrier domain : VeilHandler.getDomainBarriers((ServerLevel)owner.level(), owner.blockPosition())) {
         for (DomainExpansionEntity d : domain.getClashers()) {
            if (d.getOwner() == owner) {
               selfDomain = d;
            } else if (d.getOwner() != owner) {
               enemyDomain = true;
            }
         }
      }

      if (enemyDomain) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (cap.hasToggled((Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
            cap.toggle((Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get());
         }

         if (!cap.hasToggled(this)) {
            return true;
         }

         if (cap.hasToggled(this)) {
            return false;
         }
      } else if (selfDomain != null && !enemyDomain) {
         if (target != null) {
            return selfDomain.getDomain().distanceTo(target) >= 130.0;
         }

         if (target == null) {
            return HelperMethods.RANDOM.nextInt(15) == 0;
         }
      }

      return target != null && owner.distanceTo(target) <= 25.0 && owner.getHealth() / owner.getMaxHealth() < 0.9F && HelperMethods.RANDOM.nextInt(4) == 0;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.DOMAIN;
   }

   @Override
   public void onHitEntity(DomainExpansionEntity domain, LivingEntity owner, LivingEntity entity, boolean instant) {
      super.onHitEntity(domain, owner, entity, instant);
      if (instant || domain.getTime() == 34 || domain.level().getGameTime() % 5L == 0L && domain.getTime() >= 34) {
         if (this.getHitEnvironment() && JJKAbilities.hasTrait(entity, Trait.HEAVENLY_RESTRICTION)) {
            Ability dismantle = (Ability)JJKAbilities.DISMANTLE.get();
            ((Ability.IDomainAttack)dismantle).performEntity(owner, entity, domain);
         } else if (!JJKAbilities.hasTrait(entity, Trait.HEAVENLY_RESTRICTION)) {
            Ability cleave = (Ability)JJKAbilities.CLEAVE.get();
            ((Ability.IDomainAttack)cleave).performEntity(owner, entity, domain);
         }
      }
   }

   @Override
   public boolean getHitEnvironment() {
      return true;
   }

   @Override
   public void onHitBlock(DomainExpansionEntity domain, LivingEntity owner, BlockPos pos) {
      if (HelperMethods.RANDOM.nextInt(10) == 0) {
         Ability dismantle = (Ability)JJKAbilities.DISMANTLE.get();
         ((Ability.IDomainAttack)dismantle).performBlock(owner, domain, pos);
      }
   }

   @Override
   protected DomainExpansionEntity createBarrier(LivingEntity owner) {
      int radius = Math.round(this.getRadius(owner));
      MalevolentShrineClosedEntity domain = new MalevolentShrineClosedEntity(owner, this, radius);
      owner.level().addFreshEntity(domain);
      return domain;
   }

   @Override
   protected DomainExpansionEntity createOpenBarrier(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      int width = Math.round(this.getWidth() * cap.getDomainSize());
      int height = Math.round(this.getHeight() * cap.getDomainSize());
      MalevolentShrineEntity domain = new MalevolentShrineEntity(owner, this, width, height);
      owner.level().addFreshEntity(domain);
      return domain;
   }

   public int getWidth() {
      return 112;
   }

   public int getHeight() {
      return 85;
   }

   @Override
   public List<Block> getBlocks() {
      return List.of((Block)JJKBlocks.DOMAIN_SKY.get());
   }

   @Override
   public List<Block> getBottomFloorBlocks() {
      return List.of((Block)JJKBlocks.DOMAIN_FILLER.get());
   }
}
