package radon.jujutsu_kaisen.entity.sorcerer.base;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.ai.goal.NearestAttackableCurseGoal;
import radon.jujutsu_kaisen.entity.ai.goal.NearestAttackableShikigamiGoal;
import radon.jujutsu_kaisen.entity.ai.goal.NearestAttackableSorcererGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.ai.goal.WaterWalkingFloatGoal;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class SorcererEntity extends PathfinderMob implements GeoEntity, ISorcerer {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   protected SorcererEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
      super(pType, pLevel);
      Arrays.fill(this.armorDropChances, 1.0F);
      Arrays.fill(this.handDropChances, 1.0F);
   }

   public boolean startRiding(Entity vehicle, boolean force) {
      return !(vehicle instanceof Boat) && !(vehicle instanceof AbstractMinecart) ? super.startRiding(vehicle, force) : false;
   }

   public boolean canBeLeashed(@Nullable Player player) {
      return false;
   }

   @Override
   public boolean hasMeleeAttack() {
      return true;
   }

   @Override
   public boolean hasArms() {
      return true;
   }

   @Override
   public boolean canJump() {
      return true;
   }

   @Override
   public boolean canChant() {
      return true;
   }

   protected abstract boolean isCustom();

   protected boolean canFly() {
      return false;
   }

   protected boolean targetsCurses() {
      return true;
   }

   protected boolean targetsSorcerers() {
      return false;
   }

   protected boolean targetsShikigami() {
      return false;
   }

   private void createGoals() {
      int target = 1;
      int goal = 1;
      this.goalSelector.addGoal(goal++, new WaterWalkingFloatGoal(this));
      if (this.hasMeleeAttack()) {
         this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 0.75, true));
      }

      this.goalSelector.addGoal(goal++, new SorcererGoal(this));
      this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(target++, new HurtByTargetGoal(this, new Class[0]));
      if (this.targetsSorcerers()) {
         this.targetSelector.addGoal(target++, new NearestAttackableSorcererGoal(this, true));
      }

      if (this.targetsCurses()) {
         this.targetSelector.addGoal(target, new NearestAttackableCurseGoal(this, true));
      }

      if (this.targetsShikigami()) {
         this.targetSelector.addGoal(target, new NearestAttackableShikigamiGoal(this, true));
      }
   }

   public boolean isPersistenceRequired() {
      return this.getGrade().ordinal() > SorcererGrade.GRADE_1.ordinal();
   }

   protected void actuallyHurt(@NotNull DamageSource pDamageSource, float pDamageAmount) {
      super.actuallyHurt(pDamageSource, pDamageAmount);
      if (pDamageSource.getEntity() instanceof LivingEntity attacker && this.canAttack(attacker) && attacker != this) {
         this.setTarget(attacker);
      }
   }

   private boolean isInVillage() {
      ServerLevel serverLevel = (ServerLevel)this.level();
      BlockPos pos = this.blockPosition();

      for (Holder<Structure> holder : serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE).getTag(StructureTags.VILLAGE).orElseThrow()) {
         if (serverLevel.structureManager().getStructureAt(pos, (Structure)holder.get()).isValid()) {
            return true;
         }
      }

      return false;
   }

   public boolean checkSpawnRules(@NotNull LevelAccessor pLevel, @NotNull MobSpawnType pSpawnReason) {
      if (pSpawnReason == MobSpawnType.NATURAL || pSpawnReason == MobSpawnType.CHUNK_GENERATION) {
         if ((Integer)ConfigHolder.SERVER.sorcererVillageSpawnRate.get() == 0) {
            return false;
         }

         if (this.random
               .nextInt(
                  Math.max(
                     1,
                     Mth.floor(
                        ((Integer)ConfigHolder.SERVER.sorcererVillageSpawnRate.get()).intValue()
                           * ((SorcererGrade.GRADE_3.getRequiredExperience() + this.getExperience()) / SorcererGrade.SPECIAL_GRADE.getRequiredExperience())
                     )
                  )
               )
            != 0) {
            return false;
         }

         if (!this.isInVillage()) {
            return false;
         }
      }

      if (!pLevel.getEntitiesOfClass(this.getClass(), AABB.ofSize(this.position(), 256.0, 64.0, 256.0)).isEmpty()) {
         return false;
      } else {
         return !pLevel.getEntitiesOfClass(SorcererEntity.class, AABB.ofSize(this.position(), 32.0, 8.0, 32.0)).isEmpty()
            ? false
            : super.checkSpawnRules(pLevel, pSpawnReason);
      }
   }

   @NotNull
   protected PathNavigation createNavigation(@NotNull Level pLevel) {
      LivingEntity target = this.getTarget();
      GroundPathNavigation navigation = new GroundPathNavigation(this, pLevel);
      navigation.setCanOpenDoors(false);
      navigation.setCanFloat(false);
      navigation.setCanPassDoors(true);
      return navigation;
   }

   public void aiStep() {
      this.updateSwingTime();
      super.aiStep();
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      LivingEntity passenger = this.getControllingPassenger();
      if (this.getTarget() != null) {
         ISorcererData cap = (ISorcererData)this.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (!cap.hasBurnout()) {
            this.moveControl.setWantedPosition(this.getTarget().getX(), this.getTarget().getY(), this.getTarget().getZ(), 0.9F);
         }

         if (this.random.nextInt(5) == 0 || cap.hasBurnout() && this.random.nextInt(3) == 0) {
            this.moveControl.setWantedPosition(this.getTarget().getX() * -1.5, this.getTarget().getY() * -1.1F, this.getTarget().getZ() * -1.1F, 1.4F);
         }
      }

      if (passenger != null) {
         this.setSprinting(new Vec3(passenger.xxa, passenger.yya, passenger.zza).lengthSqr() > 0.01);
      } else {
         this.setSprinting(this.getDeltaMovement().lengthSqr() > 0.01 && this.moveControl.getSpeedModifier() > 1.0);
      }
   }

   public static Builder createAttributes() {
      return createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.ATTACK_DAMAGE).add(Attributes.FOLLOW_RANGE, 96.0);
   }

   public void onAddedToWorld() {
      super.onAddedToWorld();
      if (!this.isCustom()) {
         this.createGoals();
      }

      this.getCapability(SorcererDataHandler.INSTANCE).ifPresent(this::init);
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
