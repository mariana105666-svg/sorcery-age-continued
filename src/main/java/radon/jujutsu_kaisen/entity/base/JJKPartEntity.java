package radon.jujutsu_kaisen.entity.base;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;

public abstract class JJKPartEntity<T extends Entity> extends PartEntity<T> {
   private EntityDimensions size;
   protected int newPosRotationIncrements;
   protected double interpTargetX;
   protected double interpTargetY;
   protected double interpTargetZ;
   protected double interpTargetYaw;
   protected double interpTargetPitch;
   public float renderYawOffset;
   public float prevRenderYawOffset;

   public JJKPartEntity(T parent) {
      super(parent);
      this.setPos(parent.getX(), parent.getY(), parent.getZ());
   }

   public boolean isPickable() {
      return true;
   }

   @Nullable
   public ItemStack getPickResult() {
      return this.getParent().getPickResult();
   }

   public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
      return !this.isInvulnerableTo(pSource) && this.getParent().hurt(pSource, pAmount);
   }

   public boolean isInvulnerableTo(DamageSource pSource) {
      return pSource.getEntity() == this.getParent() || super.isInvulnerableTo(pSource);
   }

   @NotNull
   public EntityDimensions getDimensions(@NotNull Pose pPose) {
      return this.size;
   }

   public boolean shouldBeSaved() {
      return false;
   }

   protected void setSize(EntityDimensions size) {
      this.size = size;
      this.refreshDimensions();
   }

   public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
      super.recreateFromPacket(packet);
      assignPartIDs(this);
   }

   public final void updateLastPos() {
      this.moveTo(this.getX(), this.getY(), this.getZ());
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
      this.tickCount++;
   }

   public void tick() {
      this.updateLastPos();
      super.tick();
      if (this.newPosRotationIncrements > 0) {
         double d0 = this.getX() + (this.interpTargetX - this.getX()) / this.newPosRotationIncrements;
         double d2 = this.getY() + (this.interpTargetY - this.getY()) / this.newPosRotationIncrements;
         double d4 = this.getZ() + (this.interpTargetZ - this.getZ()) / this.newPosRotationIncrements;
         double d6 = Mth.wrapDegrees(this.interpTargetYaw - this.getYRot());
         this.setYRot((float)(this.getYRot() + d6 / this.newPosRotationIncrements));
         this.setXRot((float)(this.getXRot() + (this.interpTargetPitch - this.getXRot()) / this.newPosRotationIncrements));
         this.newPosRotationIncrements--;
         this.setPos(d0, d2, d4);
         this.setRot(this.getYRot(), this.getXRot());
      }

      while (this.getYRot() - this.yRotO < -180.0F) {
         this.yRotO -= 360.0F;
      }

      while (this.getYRot() - this.yRotO >= 180.0F) {
         this.yRotO += 360.0F;
      }

      while (this.renderYawOffset - this.prevRenderYawOffset < -180.0F) {
         this.prevRenderYawOffset -= 360.0F;
      }

      while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0F) {
         this.prevRenderYawOffset += 360.0F;
      }

      while (this.getXRot() - this.xRotO < -180.0F) {
         this.xRotO -= 360.0F;
      }

      while (this.getXRot() - this.xRotO >= 180.0F) {
         this.xRotO += 360.0F;
      }
   }

   public abstract ResourceLocation getRenderer();

   public void setId(int pId) {
      super.setId(pId + 1);
   }

   public boolean isCurrentlyGlowing() {
      return this.getParent().isCurrentlyGlowing();
   }

   public boolean isInvisible() {
      return this.getParent().isInvisible();
   }

   public static void assignPartIDs(Entity parent) {
      PartEntity<?>[] parts = parent.getParts();
      if (parts != null) {
         int i = 0;

         for (int length = parts.length; i < length; i++) {
            PartEntity<?> part = parts[i];
            part.setId(parent.getId() + i);
         }
      }
   }

   public void setRot(float pYRot, float pXRot) {
      super.setRot(pYRot, pXRot);
   }

   protected void defineSynchedData() {
   }

   protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
   }

   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
   }
}
