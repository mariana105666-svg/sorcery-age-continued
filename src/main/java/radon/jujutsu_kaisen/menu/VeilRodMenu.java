package radon.jujutsu_kaisen.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.block.entity.VeilRodBlockEntity;
import radon.jujutsu_kaisen.config.ConfigHolder;

public class VeilRodMenu extends AbstractContainerMenu {
   private final ContainerLevelAccess access;
   private int size;

   public VeilRodMenu(int pContainerId, ContainerLevelAccess pAccess) {
      super((MenuType)JJKMenus.VEIL_ROD.get(), pContainerId);
      this.access = pAccess;
      this.size = (Integer)this.access
         .evaluate((level, pos) -> level.getBlockEntity(pos) instanceof VeilRodBlockEntity be ? be.getSize() : (Integer)ConfigHolder.SERVER.minimumVeilSize.get())
         .orElse((Integer)ConfigHolder.SERVER.minimumVeilSize.get());
   }

   public VeilRodMenu(int pContainerId) {
      this(pContainerId, ContainerLevelAccess.NULL);
   }

   @NotNull
   public ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
      return ItemStack.EMPTY;
   }

   public int getSize() {
      return this.size;
   }

   public void setSize(int size) {
      this.size = size;
      this.access.evaluate((level, pos) -> {
         if (level.getBlockEntity(pos) instanceof VeilRodBlockEntity be) {
            be.setSize(size);
            return true;
         } else {
            return false;
         }
      });
   }

   public boolean stillValid(@NotNull Player pPlayer) {
      return (Boolean)this.access.evaluate((level, pos) -> pPlayer.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0, true);
   }
}
