package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.client.JJKPose;
import radon.jujutsu_kaisen.client.JJKPoses;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;

public class Mahoraga extends Summon<MahoragaEntity> implements Ability.IPosedMove {
   public Mahoraga() {
      super(MahoragaEntity.class);
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean canDisable() {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target == null) {
         return false;
      } else {
         ITenShadowsData ownerCap = (ITenShadowsData)owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
         if (!this.isTamed(owner)) {
            return target.getHealth() > owner.getHealth() * 4.0F || owner.getHealth() / owner.getMaxHealth() <= 0.1F;
         } else if (JJKAbilities.hasToggled(owner, this)) {
            return true;
         } else {
            if (target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
               ISorcererData targetCap = (ISorcererData)target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

               for (CursedTechnique technique : targetCap.getTechniques()) {
                  if (ownerCap.isAdaptedTo(technique)) {
                     return true;
                  }
               }
            }

            return owner.getHealth() / owner.getMaxHealth() <= 0.3;
         }
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return this.isTamed(owner) ? Ability.ActivationType.TOGGLED : Ability.ActivationType.INSTANT;
   }

   @Override
   public float getCost(LivingEntity owner) {
      float normalcost = 1.5F;
      return this.isTamed(owner) ? normalcost : 1000.0F;
   }

   @Override
   public int getCooldown() {
      return 6000;
   }

   @Override
   public List<EntityType<?>> getTypes() {
      return List.of((EntityType<?>)JJKEntities.MAHORAGA.get());
   }

   @Override
   protected boolean canTame() {
      return true;
   }

   @Override
   public boolean canDie() {
      return true;
   }

   @Override
   public boolean isTenShadows() {
      return true;
   }

   protected MahoragaEntity summon(LivingEntity owner) {
      return new MahoragaEntity(owner, this.isTamed(owner));
   }

   @Override
   public JJKPose getArmPose(LivingEntity entityLiving) {
      return JJKPoses.MAHORAGA_WHEEL;
   }

   @Override
   public int poseTimer(LivingEntity owner) {
      return 20;
   }
}
