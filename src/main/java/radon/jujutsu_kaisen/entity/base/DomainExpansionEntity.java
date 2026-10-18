package radon.jujutsu_kaisen.entity.base;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.block.entity.IBarrier;
import radon.jujutsu_kaisen.block.entity.IDomain;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.ClosedDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.DomainBarrierEntity;
import radon.jujutsu_kaisen.entity.IncompleteDomainEntity;
import radon.jujutsu_kaisen.entity.SimpleDomainEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public abstract class DomainExpansionEntity extends Entity implements IDomain {
   private static final EntityDataAccessor<Integer> DATA_TIME = SynchedEntityData.defineId(DomainExpansionEntity.class, EntityDataSerializers.INT);
   public static final int OFFSET = 5;
   public static final int INITIAL_COST = 1000;
   public static final float MAX_CLASH_HEALTH = 100.0F;
   public boolean first;
   @Nullable
   private UUID ownerUUID;
   @Nullable
   private LivingEntity cachedOwner;
   public DomainExpansion ability;
   protected boolean instant;
   protected boolean sureHitToggled;
   protected boolean shellBalance;
   protected boolean sureHitAllies;
   private float clashHealth = 100.0F;
   private boolean clashing;

   protected DomainExpansionEntity(EntityType<?> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public DomainExpansionEntity(EntityType<?> pType, LivingEntity owner, DomainExpansion ability) {
      super(pType, owner.level());
      this.setOwner(owner);
      this.instant = false;
      this.first = true;
      this.ability = ability;
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
      if (cap != null) {
         this.shellBalance = cap.getShellBalance();
         this.sureHitToggled = cap.getToggleSureHit();
         this.sureHitAllies = cap.getAlliedSureHit();
      }
   }

   public boolean ignoreExplosion() {
      return true;
   }

   protected DomainBarrierEntity getClosestDomainBarrier(BlockPos pos) {
      Set<IBarrier> domains = VeilHandler.getBarriers((ServerLevel)this.level(), pos);
      DomainBarrierEntity closest = null;
      double closestDistSqr = Double.MAX_VALUE;
      Vec3 selfPos = this.position();

      for (IBarrier domain : domains) {
         if (domain instanceof DomainBarrierEntity barrier) {
            double distSqr = selfPos.distanceToSqr(barrier.position());
            if (distSqr < closestDistSqr) {
               closestDistSqr = distSqr;
               closest = barrier;
            }
         }
      }

      return closest;
   }

   protected boolean isOccupiedByBarrier(BlockPos pos) {
      for (IDomain domain : VeilHandler.getDomains((ServerLevel)this.level(), pos)) {
         if (domain.getDomain() instanceof ClosedDomainExpansionEntity barrier && domain != this) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void doSureHitEffect() {
      if (this.ability != null) {
         LivingEntity owner = this.getOwner();

         for (LivingEntity entity : this.getAffected()) {
            if (!JJKAbilities.hasTrait(entity, Trait.HEAVENLY_RESTRICTION) || this.ability.getHitEnvironment()) {
               this.ability.onHitEntity(this, owner, entity, false);
            }
         }
      }
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_TIME, 0);
   }

   public int getTime() {
      return (Integer)this.entityData.get(DATA_TIME);
   }

   public void setTime(int time) {
      this.entityData.set(DATA_TIME, time);
   }

   @Override
   public boolean isInstant() {
      return this.instant;
   }

   @Override
   public void setInstant(boolean yn) {
      this.instant = true;
   }

   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (this.ownerUUID != null) {
         pCompound.putUUID("owner", this.ownerUUID);
      }

      if (this.ability != null) {
         pCompound.putString("ability", JJKAbilities.getKey(this.ability).toString());
      }

      pCompound.putBoolean("first", this.first);
      pCompound.putInt("time", this.getTime());
      pCompound.putBoolean("instant", this.instant);
      pCompound.putBoolean("sure_hit_toggled", this.sureHitToggled);
      pCompound.putBoolean("sure_hit_allies", this.sureHitAllies);
      pCompound.putBoolean("shell_balance", this.shellBalance);
      pCompound.putFloat("clash_health", this.clashHealth);
   }

   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (pCompound.hasUUID("owner")) {
         this.ownerUUID = pCompound.getUUID("owner");
      }

      this.ability = pCompound.contains("ability") ? (DomainExpansion)JJKAbilities.getValue(new ResourceLocation(pCompound.getString("ability"))) : null;
      this.first = pCompound.getBoolean("first");
      this.setTime(pCompound.getInt("time"));
      this.instant = pCompound.getBoolean("instant");
      this.sureHitToggled = pCompound.getBoolean("sure_hit_toggled");
      this.sureHitAllies = pCompound.getBoolean("sure_hit_allies");
      this.shellBalance = pCompound.getBoolean("shell_balance");
      this.clashHealth = pCompound.contains("clash_health") ? pCompound.getFloat("clash_health") : 100.0F;
   }

   @NotNull
   public Vec3 getDeltaMovement() {
      return Vec3.ZERO;
   }

   public List<LivingEntity> getAffected() {
      return this.level().getEntitiesOfClass(LivingEntity.class, this.getBounds(), this::isAffected);
   }

   public boolean hasSureHitEffect() {
      return true;
   }

   public abstract DomainExpansionEntity checkSureHitEffect();

   public abstract DomainExpansionEntity sureHitTarget(LivingEntity var1);

   @Override
   public Ability getAbility() {
      return this.ability;
   }

   @Nullable
   public Ability getToggledAbility() {
      return this.ability;
   }

   public DomainExpansion getDomainAbility() {
      return this.ability;
   }

   protected boolean updateInWaterStateAndDoFluidPushing() {
      return false;
   }

   @Override
   public DomainExpansionEntity getDomain() {
      return this;
   }

   @Nullable
   public DomainExpansionCenterEntity getDomainCenter() {
      for (DomainExpansionCenterEntity collision : this.level().getEntitiesOfClass(DomainExpansionCenterEntity.class, this.getBounds())) {
         if (collision.getDomain() == this) {
            return collision;
         }
      }

      return null;
   }

   public void setOwner(@Nullable LivingEntity pOwner) {
      if (pOwner != null) {
         this.ownerUUID = pOwner.getUUID();
         this.cachedOwner = pOwner;
      }
   }

   @Nullable
   @Override
   public LivingEntity getOwner() {
      if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
         return this.cachedOwner;
      } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
         this.cachedOwner = (LivingEntity)((ServerLevel)this.level()).getEntity(this.ownerUUID);
         return this.cachedOwner;
      } else {
         return null;
      }
   }

   public UUID getOwnerUUID() {
      return this.ownerUUID;
   }

   @Override
   public abstract boolean isInsideBarrier(BlockPos var1);

   public float getClashStrength() {
      LivingEntity owner = this.getOwner();
      if (owner == null) {
         return 0.0F;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         return cap == null ? 0.0F : cap.getRealPower();
      }
   }

   public boolean isClashing() {
      return this.clashing;
   }

   public void setClashing(boolean clashing) {
      this.clashing = clashing;
   }

   public float getClashHealth() {
      return this.clashHealth;
   }

   public float getMaxClashHealth() {
      return 100.0F;
   }

   public void setClashHealth(float clashHealth) {
      this.clashHealth = Mth.clamp(clashHealth, 0.0F, this.getMaxClashHealth());
   }

   public float getClashFraction() {
      return this.clashHealth / 100.0F;
   }

   public boolean getSureHitToggled() {
      return this.sureHitToggled;
   }

   public boolean getAlliedSureHit() {
      return this.sureHitAllies;
   }

   public boolean getShellBalance() {
      return this.shellBalance;
   }

   public boolean isInWall() {
      return false;
   }

   public void tick() {
      this.setTime(this.getTime() + 1);
      LivingEntity owner = this.getOwner();
      if (!this.level().isClientSide && owner != null && !(owner instanceof Player player && player.getAbilities().instabuild) && this.getTime() == 1 && !this.instant
         )
       {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         float cost = this.ability == null ? 1000.0F : this.getAbility().getRealCost(owner, 1000.0F);
         if (cap == null || cap.getEnergy() - cost < 0.0F) {
            this.discard();
            return;
         }

         cap.useEnergy(cost);
         if (owner instanceof ServerPlayer playerx) {
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), playerx);
         }
      }

      if (this.level().isClientSide || owner != null && !owner.isRemoved() && owner.isAlive()) {
         super.tick();
      } else {
         this.discard();
      }
   }

   public boolean isAffected(BlockPos pos) {
      return this.isInsideBarrier(pos);
   }

   private boolean isAlly(LivingEntity owner, LivingEntity victim) {
      LivingEntity resolved = victim;

      while (resolved instanceof TamableAnimal) {
         TamableAnimal tamable = (TamableAnimal)resolved;
         if (!tamable.isTame()) {
            break;
         }

         resolved = tamable.getOwner();
         if (resolved == null) {
            return false;
         }
      }

      if (resolved == owner) {
         return true;
      } else if (owner.isAlliedTo(resolved)) {
         return true;
      } else {
         ISorcererData ownerCap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         ISorcererData victimCap = (ISorcererData)resolved.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         return ownerCap != null && victimCap != null
            ? ownerCap.hasPact(resolved.getUUID(), Pact.FRIENDS) && victimCap.hasPact(owner.getUUID(), Pact.FRIENDS)
            : false;
      }
   }

   public boolean isAffected(LivingEntity victim, boolean hurtSimple) {
      LivingEntity owner = this.getOwner();
      if (owner != null && victim != owner) {
         if (victim instanceof TamableAnimal tamable && tamable.isTame() && tamable.getOwner() == owner) {
            return false;
         } else if (this.sureHitAllies && this.isAlly(owner, victim)) {
            return false;
         } else if (IncompleteDomainEntity.isShelteredFromSureHit(this.level(), victim.blockPosition())) {
            return false;
         } else if (!owner.canAttack(victim)) {
            return false;
         } else {
            if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
               ITenShadowsData victimTenShadowsCap = (ITenShadowsData)victim.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElse(null);
               ISorcererData victimSorcererCap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               if (victimTenShadowsCap != null && victim instanceof MahoragaEntity && victimTenShadowsCap.isAdaptedTo(this.ability)) {
                  return false;
               }

               if (victimSorcererCap.hasToggled((Ability)JJKAbilities.HOLLOW_WICKER_BASKET.get())) {
                  return false;
               }

               if (victimSorcererCap.hasToggled((Ability)JJKAbilities.SIMPLE_DOMAIN.get())) {
                  SimpleDomainEntity simple = victimSorcererCap.getSummonByClass(SimpleDomainEntity.class);
                  if (simple != null) {
                     if (!hurtSimple) {
                        return false;
                     }

                     ISorcererData ownerSorcererCap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                     simple.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, this.ability), ownerSorcererCap.getAbilityPower() * 10.0F);
                     return false;
                  }
               }

               for (SimpleDomainEntity simple : this.level().getEntitiesOfClass(SimpleDomainEntity.class, AABB.ofSize(victim.position(), 10.5, 10.5, 10.5))) {
                  if (simple.isInsideBarrier(victim.blockPosition())) {
                     return false;
                  }
               }
            }

            return this.isAffected(victim.blockPosition());
         }
      } else {
         return false;
      }
   }

   public boolean isAffected(LivingEntity victim) {
      return this.isAffected(victim, true);
   }

   protected boolean isReadyToCollapse() {
      return true;
   }

   public boolean shouldCollapse(float strength) {
      return this.isReadyToCollapse() && strength / this.getStrength() > 2.5F;
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      double d0 = this.getBoundingBox().getSize() * 10.0;
      if (Double.isNaN(d0)) {
         d0 = 1.0;
      }

      d0 *= 64.0 * getViewScale();
      return pDistance < d0 * d0;
   }

   public void remove(@NotNull RemovalReason pReason) {
      super.remove(pReason);
      if (!this.level().isClientSide) {
         int burnout = Math.max(300, this.getTime());
         if (burnout > 900) {
            burnout = 900;
         }

         int realburnout = burnout;
         LivingEntity owner = this.getOwner();
         if (owner != null) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
               if (cap.getType() == JujutsuType.CURSE && !cap.hasTrait(Trait.DEATH_PAINTING)) {
                  cap.setBurnout((int)Math.ceil(realburnout - 3.9 * Math.log(realburnout + 1)));
               } else {
                  cap.setBurnout(realburnout);
               }

               cap.resetSpeedStacks();
               Ability toggled = pReason.shouldDestroy() ? this.getToggledAbility() : null;
               if (toggled != null && cap.hasToggled(toggled)) {
                  cap.toggle(toggled);
               }

               if (owner instanceof ServerPlayer player) {
                  PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
               }
            });
         }
      }
   }

   @NotNull
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      LivingEntity entity = this.getOwner();
      return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
   }

   public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
      super.recreateFromPacket(pPacket);
      LivingEntity owner = (LivingEntity)this.level().getEntity(pPacket.getData());
      if (owner != null) {
         this.setOwner(owner);
      }
   }
}
