package radon.jujutsu_kaisen.entity.ten_shadows.base;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.entity.ai.goal.BetterFollowOwnerGoal;
import radon.jujutsu_kaisen.entity.ai.goal.SorcererGoal;
import radon.jujutsu_kaisen.entity.base.ICommandable;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.base.SummonEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncTenShadowsDataS2CPacket;

public abstract class TenShadowsSummon extends SummonEntity implements ICommandable, ISorcerer {
   private static final int MAX_DISTANCE = 128;
   private static final double RITUAL_RANGE = 32.0;
   private static final EntityDataAccessor<Boolean> DATA_CLONE = SynchedEntityData.defineId(TenShadowsSummon.class, EntityDataSerializers.BOOLEAN);
   protected final Set<UUID> participants = new HashSet<>();
   protected boolean ritualNullified = false;
   private int participantCheckCooldown = 0;

   protected TenShadowsSummon(EntityType<? extends TamableAnimal> pType, Level pLevel) {
      super(pType, pLevel);
      AttributeInstance attribute = this.getAttribute(Attributes.FOLLOW_RANGE);
      if (attribute != null) {
         attribute.setBaseValue(128.0);
      }
   }

   protected abstract boolean isCustom();

   protected abstract boolean canFly();

   protected boolean shouldDespawn() {
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (!JJKAbilities.hasToggled(owner, this.getAbility()) || cap.getEnergy() <= 0.0F) {
            return true;
         }
      }

      return this.isDeadOrDying();
   }

   private void createGoals() {
      int target = 1;
      int goal = 1;
      this.goalSelector.addGoal(goal++, new FloatGoal(this));
      if (this.hasMeleeAttack()) {
         this.goalSelector.addGoal(goal++, new MeleeAttackGoal(this, 0.8, true));
      }

      this.goalSelector.addGoal(goal++, new SorcererGoal(this));
      this.targetSelector.addGoal(target++, new HurtByTargetGoal(this, new Class[0]));
      if (this.isTame()) {
         this.goalSelector.addGoal(goal++, new BetterFollowOwnerGoal(this, 1.0, 25.0F, 10.0F, this.canFly()));
         this.targetSelector.addGoal(target++, new OwnerHurtByTargetGoal(this));
         this.targetSelector.addGoal(target, new OwnerHurtTargetGoal(this));
      } else {
         this.targetSelector
            .addGoal(target, new NearestAttackableTargetGoal<>(this, LivingEntity.class, false, entity -> this.participants.contains(entity.getUUID())));
      }

      this.goalSelector.addGoal(goal, new RandomLookAroundGoal(this));
   }

   @Override
   public boolean canAttack(@NotNull LivingEntity pTarget) {
      return pTarget == this.getOwner() && this.isTame()
         ? false
         : super.canAttack(pTarget)
            && (pTarget.getType() != this.getType() || ((TenShadowsSummon)pTarget).isClone())
            && !(pTarget instanceof TamableAnimal tamable && tamable.getOwner() == this.getOwner() && tamable.isTame() == this.isTame());
   }

   public void setClone(boolean clone) {
      this.entityData.set(DATA_CLONE, clone);
   }

   @NotNull
   public EntityDimensions getDimensions(@NotNull Pose pPose) {
      return super.getDimensions(pPose).scale(this.getScale());
   }

   private void spawnParticles() {
      this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < this.getBbHeight() * this.getBbHeight(); j++) {
            this.level()
               .addParticle(
                  ParticleTypes.SMOKE,
                  this.getX() + this.getBbWidth() * this.random.nextGaussian() * 0.1F,
                  this.getY(),
                  this.getZ() + this.getBbWidth() * this.random.nextGaussian() * 0.1F,
                  this.random.nextGaussian() * 0.075,
                  this.random.nextGaussian() * 0.25,
                  this.random.nextGaussian() * 0.075
               );
            this.level()
               .addParticle(
                  ParticleTypes.LARGE_SMOKE,
                  this.getX() + this.getBbWidth() * this.random.nextGaussian() * 0.1F,
                  this.getY(),
                  this.getZ() + this.getBbWidth() * this.random.nextGaussian() * 0.1F,
                  this.random.nextGaussian() * 0.075,
                  this.random.nextGaussian() * 0.25,
                  this.random.nextGaussian() * 0.075
               );
         }
      }
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_CLONE, false);
   }

   @Override
   public boolean canChangeTarget() {
      return this.isTame();
   }

   @Override
   public void changeTarget(LivingEntity target) {
      if (!(target instanceof TenShadowsSummon sum && (!sum.isTame() || sum.getTarget() != this.getOwner()))) {
         this.setTarget(target);
      }
   }

   @Override
   public void onAddedToWorld() {
      super.onAddedToWorld();
      if (!this.isCustom()) {
         this.createGoals();
      }

      this.spawnParticles();
      if (!this.isTame()) {
         Vec3 center = this.position();
         LivingEntity owner = this.getOwner();
         this.addParticipants(center, owner);
      }
   }

   public boolean isClone() {
      return (Boolean)this.entityData.get(DATA_CLONE);
   }

   public void onRemovedFromWorld() {
      super.onRemovedFromWorld();
      this.spawnParticles();
   }

   public void die(@NotNull DamageSource pCause) {
      super.die(pCause);
      if (!this.isClone()) {
         LivingEntity owner = this.getOwner();
         if (owner != null && !owner.level().isClientSide) {
            ITenShadowsData cap = (ITenShadowsData)owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
            if (!this.isTame()) {
               if (pCause.getEntity() == owner || pCause.getEntity() instanceof TamableAnimal tamable && tamable.isTame() && tamable.getOwner() == owner) {
                  cap.tame(this.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE), this.getType());
                  if (owner instanceof ServerPlayer player) {
                     PacketHandler.sendToClient(new SyncTenShadowsDataS2CPacket(cap.serializeNBT()), player);
                  }
               }
            } else {
               Summon<?> ability = this.getAbility();
               if (ability.isTotality()) {
                  for (EntityType<?> fusion : ability.getFusions()) {
                     if (!JJKAbilities.isDead(owner, fusion)) {
                        cap.kill(this.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE), fusion);
                        if (owner instanceof ServerPlayer player) {
                           PacketHandler.sendToClient(new SyncTenShadowsDataS2CPacket(cap.serializeNBT()), player);
                        }
                     }
                  }
               } else if (ability.canDie()) {
                  cap.kill(this.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE), this.getType());
                  if (owner instanceof ServerPlayer player) {
                     PacketHandler.sendToClient(new SyncTenShadowsDataS2CPacket(cap.serializeNBT()), player);
                  }
               }
            }
         }
      }
   }

   private void addParticipants(Vec3 center, LivingEntity owner) {
      AABB area = new AABB(
         center.x - 32.0, center.y - 32.0, center.z - 32.0, center.x + 32.0, center.y + 32.0, center.z + 32.0
      );

      for (LivingEntity participant : this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            area,
            participantx -> participantx != this
               && (!(participantx.getType() == this.getType() && participantx instanceof TenShadowsSummon sum) || !sum.isClone())
               && participantx.getCapability(SorcererDataHandler.INSTANCE).isPresent()
         )) {
         if (participant != owner && !(participant instanceof TamableAnimal tamable && tamable.getOwner() == owner)) {
            this.ritualNullified = true;
         }

         if (!this.participants.contains(participant.getUUID())) {
            this.participants.add(participant.getUUID());
         }
      }
   }

   private void checkParticipants() {
      Iterator<UUID> iter = this.participants.iterator();

      while (iter.hasNext()) {
         UUID identifier = iter.next();
         LivingEntity participant = (LivingEntity)((ServerLevel)this.level()).getEntity(identifier);
         if (participant == null || participant.isRemoved() || !participant.isAlive() || participant.distanceTo(this) > 128.0F) {
            iter.remove();
         }
      }
   }

   public boolean hurt(@NotNull DamageSource source, float amount) {
      boolean result = super.hurt(source, amount);
      if (!this.level().isClientSide && result && source.getEntity() instanceof LivingEntity livingAttacker && livingAttacker != this) {
         this.participants.add(livingAttacker.getUUID());
      }

      return result;
   }

   @Override
   public void tick() {
      LivingEntity owner = this.getOwner();
      if (!this.isTame() || this.level().isClientSide || owner != null && !owner.isRemoved() && owner.isAlive() && !this.shouldDespawn()) {
         super.tick();
         if (this.level().isClientSide) {
            return;
         }

         if (!this.isTame()) {
            if (this.participantCheckCooldown-- <= 0) {
               this.participantCheckCooldown = 20;
               this.checkParticipants();
               if (!this.participants.isEmpty()) {
                  this.addParticipants(this.position(), owner);
               } else {
                  this.discard();
               }
            }
         } else {
            LivingEntity target = this.getTarget();
            this.setOrderedToSit(target != null && !target.isRemoved() && target.isAlive());
         }

         if (owner != null && this.isClone() && !JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.CHIMERA_SHADOW_GARDEN.get())) {
            this.discard();
         }
      } else {
         this.discard();
      }
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      ListTag participantsTag = new ListTag();

      for (UUID identifier : this.participants) {
         participantsTag.add(NbtUtils.createUUID(identifier));
      }

      pCompound.put("participants", participantsTag);
      pCompound.putBoolean("clone", (Boolean)this.entityData.get(DATA_CLONE));
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.participants.clear();

      for (Tag key : pCompound.getList("participants", 11)) {
         this.participants.add(NbtUtils.loadUUID(key));
      }

      this.entityData.set(DATA_CLONE, pCompound.getBoolean("clone"));
   }

   public boolean isOwnedBy(@NotNull LivingEntity pEntity) {
      return this.isTame() && super.is(pEntity);
   }

   @Override
   public JujutsuType getJujutsuType() {
      return JujutsuType.SHIKIGAMI;
   }

   @Override
   public float getExperience() {
      LivingEntity owner = this.getOwner();
      if (owner == null) {
         return 0.0F;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getExperience() * 0.9F;
      }
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return null;
   }
}
