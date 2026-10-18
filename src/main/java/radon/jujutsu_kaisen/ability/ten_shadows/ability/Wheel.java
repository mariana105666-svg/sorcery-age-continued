package radon.jujutsu_kaisen.ability.ten_shadows.ability;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsMode;
import radon.jujutsu_kaisen.client.JJKPose;
import radon.jujutsu_kaisen.client.JJKPoses;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.WheelEntity;

public class Wheel extends Summon<WheelEntity> implements Ability.IPosedMove {
   public Wheel() {
      super(WheelEntity.class);
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   protected boolean isNotDisabledFromDA() {
      return true;
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return owner instanceof MahoragaEntity ? true : target != null;
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      if (!super.isValid(owner)) {
         return false;
      } else {
         ITenShadowsData cap = (ITenShadowsData)owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
         ISorcererData ownercap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return !ownercap.hasToggled((Ability)JJKAbilities.MAHORAGA.get())
            && cap.hasTamed(owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE), (EntityType<?>)JJKEntities.MAHORAGA.get())
            && (ownercap.hasToggled(this) || cap.getMode() == TenShadowsMode.ABILITY)
            && (
               ownercap.getExperience() >= (Double)ConfigHolder.SERVER.requiredExperienceForExperienced.get()
                  || !ownercap.hasToggled((Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get())
            );
      }
   }

   @Override
   public List<EntityType<?>> getTypes() {
      return List.of((EntityType<?>)JJKEntities.WHEEL.get());
   }

   @Override
   public boolean isTenShadows() {
      return false;
   }

   protected WheelEntity summon(LivingEntity owner) {
      return new WheelEntity(owner);
   }

   @Override
   public void run(LivingEntity owner) {
   }

   @Override
   public float getCost(LivingEntity owner) {
      return JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get()) ? 0.0F : 0.5F;
   }

   @Override
   public boolean shouldLog(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean display() {
      return false;
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
