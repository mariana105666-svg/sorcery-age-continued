package radon.jujutsu_kaisen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;

public class DomainSkyBlockEntity extends DomainBlockEntity {
   @Nullable
   private ResourceLocation domain;

   public DomainSkyBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super((BlockEntityType<?>)JJKBlockEntities.DOMAIN_SKY.get(), pPos, pBlockState);
   }

   public void setDomain(@Nullable ResourceLocation domain) {
      this.domain = domain;
      this.markUpdated();
   }

   @Nullable
   public ResourceLocation getDomain() {
      return this.domain;
   }

   public static void tick(Level pLevel, BlockPos pPos, BlockState pState, DomainSkyBlockEntity pBlockEntity) {
      if (pBlockEntity.domain == null) {
         if (!(((ServerLevel)pLevel).getEntity(pBlockEntity.pidentifier) instanceof DomainExpansionEntity domain)) {
            return;
         }

         pBlockEntity.setDomain(JJKAbilities.getKey(domain.getAbility()));
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

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
      if (this.domain != null) {
         pTag.putString("domain", this.domain.toString());
      }
   }

   @Override
   public void load(@NotNull CompoundTag pTag) {
      super.load(pTag);
      if (pTag.contains("domain")) {
         this.domain = new ResourceLocation(pTag.getString("domain"));
      }

      if (this.level != null) {
         this.markUpdated();
      }
   }
}
