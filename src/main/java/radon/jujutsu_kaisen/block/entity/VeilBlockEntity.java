package radon.jujutsu_kaisen.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.block.base.TemporaryBlockEntity;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.ModifierUtils;

public class VeilBlockEntity extends TemporaryBlockEntity {
   private boolean initialized;
   @Nullable
   private UUID parentUUID;
   @Nullable
   private UUID ownerUUID;
   private int size;
   private List<Modifier> modifiers = new ArrayList<>();
   private int death;

   public VeilBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super((BlockEntityType<?>)JJKBlockEntities.VEIL.get(), pPos, pBlockState);
   }

   @Nullable
   public UUID getParentUUID() {
      return this.parentUUID;
   }

   @Nullable
   public UUID getOwnerUUID() {
      return this.ownerUUID;
   }

   public List<Modifier> getModifiers() {
      return this.modifiers;
   }

   public void create(UUID parentUUID, UUID ownerUUID, int delay, int size, List<Modifier> modifiers, BlockState state, CompoundTag saved) {
      this.initialized = true;
      this.parentUUID = parentUUID;
      this.ownerUUID = ownerUUID;
      this.death = delay;
      this.size = size;
      this.modifiers = modifiers;
      this.setOriginal(state);
      this.setSaved(saved);
      this.setChanged();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   private void markUpdated() {
      this.setChanged();
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 50);
      }
   }

   @Override
   public void saveAdditional(@NotNull CompoundTag pTag) {
      super.saveAdditional(pTag);
      pTag.putBoolean("initialized", this.initialized);
      if (this.initialized) {
         pTag.putInt("death", this.death);
         if (this.parentUUID != null) {
            pTag.putUUID("parent", this.parentUUID);
         }

         if (this.ownerUUID != null) {
            pTag.putUUID("owner", this.ownerUUID);
         }

         pTag.putInt("size", this.size);
         pTag.put("modifiers", ModifierUtils.serialize(this.modifiers));
      }
   }

   @Override
   public void load(@NotNull CompoundTag pTag) {
      super.load(pTag);
      this.initialized = pTag.getBoolean("initialized");
      if (this.initialized) {
         this.death = pTag.getInt("death");
         if (pTag.contains("parent")) {
            this.parentUUID = pTag.getUUID("parent");
         }

         if (pTag.contains("owner")) {
            this.ownerUUID = pTag.getUUID("owner");
         }

         this.size = pTag.getInt("size");
         this.modifiers = ModifierUtils.deserialize(pTag.getList("modifiers", 10));
      }

      if (this.level != null) {
         this.markUpdated();
      }
   }
}
