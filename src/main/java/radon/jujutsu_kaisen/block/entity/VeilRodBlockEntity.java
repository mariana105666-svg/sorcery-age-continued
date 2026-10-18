package radon.jujutsu_kaisen.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.ModifierUtils;

public class VeilRodBlockEntity extends BlockEntity {
   public static final int RANGE = 128;
   public static final int INTERVAL = 2;
   private static final float COST = 0.1F;
   private int blockCursor;
   private int counter;
   private int size = (Integer)ConfigHolder.SERVER.minimumVeilSize.get();
   private float experience;
   private float storedEnergy;
   public List<Modifier> modifiers = new ArrayList<>();
   @Nullable
   public UUID ownerUUID;

   public VeilRodBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super((BlockEntityType)JJKBlockEntities.VEIL_ROD.get(), pPos, pBlockState);
      this.blockCursor = 0;
      this.storedEnergy = 0.0F;
   }

   @Nullable
   public UUID getOwnerUUID() {
      return this.ownerUUID;
   }

   public void setOwnerUUID(UUID ownerUUID) {
      this.ownerUUID = ownerUUID;
      this.setChanged();
   }

   public List<Modifier> getModifiers() {
      return this.modifiers;
   }

   public int getSize() {
      return this.size;
   }

   public void setSize(int size) {
      this.size = size;
      this.setChanged();
   }

   public void setExperience(float experience) {
      this.experience = experience;
      this.setChanged();
   }

   public float getExperience() {
      return this.experience;
   }

   public void setOwner(UUID ownerUUID) {
      this.ownerUUID = ownerUUID;
      this.setChanged();
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   protected void saveAdditional(@NotNull CompoundTag pTag) {
      super.saveAdditional(pTag);
      if (this.ownerUUID != null) {
         pTag.putUUID("owner", this.ownerUUID);
      }

      pTag.putInt("counter", this.counter);
      pTag.putInt("size", this.size);
      pTag.putFloat("experience", this.experience);
      pTag.putFloat("storedEnergy", this.storedEnergy);
      if (this.modifiers != null) {
         pTag.put("modifiers", ModifierUtils.serialize(this.modifiers));
      }
   }

   public void load(@NotNull CompoundTag pTag) {
      super.load(pTag);
      if (pTag.contains("owner")) {
         this.ownerUUID = pTag.getUUID("owner");
      }

      this.counter = pTag.getInt("counter");
      this.size = pTag.getInt("size");
      this.experience = pTag.getFloat("experience");
      this.storedEnergy = pTag.getFloat("storedEnergy");
      if (pTag.contains("modifiers")) {
         this.modifiers = ModifierUtils.deserialize(pTag.getList("modifiers", 10));
      }
   }
}
