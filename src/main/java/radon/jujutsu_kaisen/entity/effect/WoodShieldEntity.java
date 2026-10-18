package radon.jujutsu_kaisen.entity.effect;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;

public class WoodShieldEntity extends Mob {
   private static final float STRENGTH = 85.0F;
   @Nullable
   private Vec3 pos;
   @Nullable
   private UUID ownerUUID;
   @Nullable
   private LivingEntity cachedOwner;

   public WoodShieldEntity(EntityType<? extends Mob> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public WoodShieldEntity(LivingEntity owner) {
      super((EntityType)JJKEntities.WOOD_SHIELD.get(), owner.level());
      this.setOwner(owner);
      this.pos = owner.position();
      this.setPos(owner.position());
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      AttributeInstance attribute = this.getAttribute(Attributes.MAX_HEALTH);
      if (attribute != null) {
         attribute.setBaseValue(85.0F * cap.getAbilityPower());
         this.setHealth(this.getMaxHealth());
      }
   }

   public void onAddedToWorld() {
      super.onAddedToWorld();
      this.level().addFreshEntity(new WoodShieldSegmentEntity(this));
   }

   @NotNull
   public Vec3 getDeltaMovement() {
      return Vec3.ZERO;
   }

   public boolean isSilent() {
      return true;
   }

   @NotNull
   public EntityDimensions getDimensions(@NotNull Pose pPose) {
      LivingEntity owner = this.getOwner();
      return owner == null ? super.getDimensions(pPose) : EntityDimensions.fixed(owner.getBbWidth(), owner.getBbHeight()).scale(2.0F);
   }

   public void tick() {
      this.refreshDimensions();
      LivingEntity owner = this.getOwner();
      if (this.level().isClientSide
         || owner != null && !owner.isRemoved() && owner.isAlive() && JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.WOOD_SHIELD.get())) {
         super.tick();
         if (owner != null && this.pos != null) {
            owner.teleportTo(this.pos.x, this.pos.y, this.pos.z);
         }
      } else {
         this.discard();
      }
   }

   public boolean isPickable() {
      return false;
   }

   public void aiStep() {
   }

   public boolean isPushable() {
      return false;
   }

   protected boolean updateInWaterStateAndDoFluidPushing() {
      return false;
   }

   public void setOwner(@Nullable LivingEntity pOwner) {
      if (pOwner != null) {
         this.ownerUUID = pOwner.getUUID();
         this.cachedOwner = pOwner;
      }
   }

   @Nullable
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

   public boolean isInWall() {
      return false;
   }

   public boolean isPersistenceRequired() {
      return true;
   }

   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      if (this.pos != null) {
         pCompound.putDouble("pos_x", this.pos.x);
         pCompound.putDouble("pos_y", this.pos.y);
         pCompound.putDouble("pos_z", this.pos.z);
      }

      if (this.ownerUUID != null) {
         pCompound.putUUID("owner", this.ownerUUID);
      }
   }

   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.pos = new Vec3(pCompound.getDouble("pos_x"), pCompound.getDouble("pos_y"), pCompound.getDouble("pos_z"));
      if (pCompound.hasUUID("owner")) {
         this.ownerUUID = pCompound.getUUID("owner");
      }
   }

   @NotNull
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      Entity entity = this.getOwner();
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
