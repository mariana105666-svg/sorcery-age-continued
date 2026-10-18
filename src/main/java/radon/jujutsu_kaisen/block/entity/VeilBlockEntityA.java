package radon.jujutsu_kaisen.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;
import radon.jujutsu_kaisen.item.veil.modifier.PlayerModifier;

public class VeilBlockEntityA extends BlockEntity {
   private int counter;
   @Nullable
   private BlockPos parent;
   private boolean initialized;
   @Nullable
   private BlockState original;
   private CompoundTag deferred;
   @Nullable
   private CompoundTag saved;
   private int size;

   public VeilBlockEntityA(BlockPos pPos, BlockState pBlockState) {
      super((BlockEntityType)JJKBlockEntities.VEIL.get(), pPos, pBlockState);
   }

   public void destroy() {
      if (this.level != null) {
         BlockPos pos = this.getBlockPos();
         BlockState original = this.getOriginal();
         if (original != null && !original.isAir() && original.getBlock() != JJKBlocks.DOMAIN_AIR.get()) {
            this.level.setBlockAndUpdate(pos, original);
            if (this.saved != null) {
               BlockEntity restored = this.level.getBlockEntity(pos);
               if (restored != null && !(restored instanceof VeilBlockEntity) && this.saved != null) {
                  restored.load(this.saved);
               }
            }
         } else {
            this.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
         }
      }
   }

   @Nullable
   public CompoundTag getSaved() {
      return this.saved;
   }

   public static boolean isWhitelisted(@Nullable BlockPos parent, Entity entity) {
      if (!(entity != null && parent != null && entity.level().getBlockEntity(parent) instanceof VeilRodBlockEntity be)) {
         return false;
      } else if (be.modifiers == null) {
         return false;
      } else {
         if (entity instanceof Player player) {
            for (Modifier modifier : be.modifiers) {
               if (modifier.getAction() == Modifier.Action.ALLOW && modifier.getType() == Modifier.Type.OWNER_BYPASS && entity.getUUID().equals(be.ownerUUID)) {
                  return true;
               }

               if (modifier.getAction() == Modifier.Action.ALLOW
                  && modifier.getType() == Modifier.Type.PLAYER
                  && ((PlayerModifier)modifier).getName().equals(player.getDisplayName().getString())) {
                  return modifier.getAction() == Modifier.Action.ALLOW;
               }
            }
         }

         for (Modifier modifier : be.modifiers) {
            if (modifier.getAction() == Modifier.Action.ALLOW && (modifier.getType() == Modifier.Type.CURSE || modifier.getType() == Modifier.Type.SORCERER)) {
               if (!entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                  return false;
               } else {
                  ISorcererData cap = (ISorcererData)entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                  return cap.getType() == JujutsuType.CURSE && modifier.getType() == Modifier.Type.CURSE
                     || cap.getType() != JujutsuType.CURSE && modifier.getType() == Modifier.Type.SORCERER;
               }
            }
         }

         return false;
      }
   }

   @Nullable
   public BlockState getOriginal() {
      if (this.level == null) {
         return this.original;
      } else {
         if (this.original == null && this.deferred != null) {
            this.original = NbtUtils.readBlockState(this.level.holderLookup(Registries.BLOCK), this.deferred);
            this.deferred = null;
         }

         return this.original;
      }
   }

   public void create(BlockPos parent, int size, BlockState original, CompoundTag saved) {
      this.parent = parent;
      this.size = size;
      if (original.getBlock() == JJKBlocks.DOMAIN_AIR.get()) {
         original = Blocks.AIR.defaultBlockState();
      }

      this.original = original;
      this.saved = saved;
      this.initialized = true;
      this.setChanged();
   }

   @Nullable
   public BlockPos getParent() {
      return this.parent;
   }

   public static boolean isAllowed(BlockPos pos, Entity entity) {
      return isWhitelisted(pos, entity);
   }

   public void sendUpdates() {
      if (this.level != null) {
      }
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   protected void saveAdditional(@NotNull CompoundTag pTag) {
      super.saveAdditional(pTag);
      System.out.println("savingload");
      pTag.putBoolean("initialized", this.initialized);
      if (this.initialized) {
         if (this.original != null) {
            pTag.put("original", NbtUtils.writeBlockState(this.original));
         } else {
            pTag.put("original", this.deferred);
         }

         if (this.saved != null) {
            pTag.put("saved", this.saved);
         }

         System.out.println("okay1");
         if (this.parent != null) {
            pTag.put("parent", NbtUtils.writeBlockPos(this.parent));
         }

         pTag.putInt("size", this.size);
      }
   }

   private void markUpdated() {
      this.setChanged();
      if (this.level != null) {
         this.level.setBlock(this.worldPosition, this.getBlockState(), 50);
      }
   }

   public void load(@NotNull CompoundTag pTag) {
      super.load(pTag);
      this.initialized = pTag.getBoolean("initialized");
      if (this.initialized) {
         this.deferred = pTag.getCompound("original");
         if (pTag.contains("saved")) {
            this.saved = pTag.getCompound("saved");
         }

         if (pTag.contains("parent")) {
            this.parent = NbtUtils.readBlockPos(pTag.getCompound("parent"));
         }

         this.size = pTag.getInt("size");
      }

      if (this.level != null) {
         this.markUpdated();
      }
   }
}
