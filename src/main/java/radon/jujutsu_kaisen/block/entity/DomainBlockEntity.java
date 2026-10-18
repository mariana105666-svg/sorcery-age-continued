package radon.jujutsu_kaisen.block.entity;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.base.TemporaryBlockEntity;

public class DomainBlockEntity extends TemporaryBlockEntity {
   private boolean initialized;
   protected UUID identifier;
   protected UUID pidentifier;
   @Nullable
   private BlockState original;
   private CompoundTag deferred;
   @Nullable
   private CompoundTag saved;

   public DomainBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super((BlockEntityType<?>)JJKBlockEntities.DOMAIN.get(), pPos, pBlockState);
   }

   public DomainBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
      super(pType, pPos, pBlockState);
   }

   @Override
   public void destroy() {
      if (this.level != null) {
         BlockPos pos = this.getBlockPos();
         BlockState original = this.getOriginal();
         if (original != null && !original.isAir()) {
            this.level.setBlock(pos, original, 50);
            if (this.saved != null) {
               BlockEntity restored = this.level.getBlockEntity(pos);
               if (restored != null) {
                  restored.load(this.saved);
               }
            }
         } else {
            this.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 50);
         }
      }
   }

   @Nullable
   public UUID getIdentifier() {
      return this.identifier;
   }

   @Nullable
   public UUID getPidentifier() {
      return this.pidentifier;
   }

   @Nullable
   public void setPidentifier(UUID here) {
      this.pidentifier = here;
   }

   @Nullable
   @Override
   public BlockState getOriginal() {
      if (this.level == null) {
         return this.original;
      } else {
         if (this.original == null && this.deferred != null) {
            this.original = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), this.deferred);
            this.deferred = null;
            this.setChanged();
         }

         return this.original;
      }
   }

   @Nullable
   @Override
   public CompoundTag getSaved() {
      return this.saved;
   }

   public void create(UUID identifier, int delay, BlockState state, CompoundTag saved, UUID pidentifier) {
      this.initialized = true;
      this.identifier = identifier;
      this.original = state;
      this.saved = saved;
      this.pidentifier = pidentifier;
      this.setChanged();
   }

   @Override
   public void saveAdditional(@NotNull CompoundTag pTag) {
      super.saveAdditional(pTag);
      pTag.putBoolean("initialized", this.initialized);
      if (this.initialized) {
         pTag.putUUID("identifier", this.identifier);
         if (this.original != null) {
            pTag.put("original", NbtUtils.writeBlockState(this.original));
         } else if (this.deferred != null) {
            pTag.put("original", this.deferred);
         }

         if (this.saved != null) {
            pTag.put("saved", this.saved);
         }

         pTag.putUUID("pidentifier", this.pidentifier);
      }
   }

   @Override
   public void load(@NotNull CompoundTag pTag) {
      super.load(pTag);
      this.initialized = pTag.getBoolean("initialized");
      if (this.initialized) {
         this.identifier = pTag.getUUID("identifier");
         this.deferred = pTag.getCompound("original");
         if (pTag.contains("saved")) {
            this.saved = pTag.getCompound("saved");
         }

         this.pidentifier = pTag.getUUID("pidentifier");
      }
   }
}
