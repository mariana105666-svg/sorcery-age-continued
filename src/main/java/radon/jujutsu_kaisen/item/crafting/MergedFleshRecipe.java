package radon.jujutsu_kaisen.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.item.CursedEnergyFleshItem;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.item.JJKRecipeSerializers;

public class MergedFleshRecipe extends CustomRecipe {
   public MergedFleshRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
      super(pId, pCategory);
   }

   public boolean matches(CraftingContainer pContainer, @NotNull Level pLevel) {
      ItemStack stack = ItemStack.EMPTY;

      for (int i = 0; i < pContainer.getContainerSize(); i++) {
         ItemStack current = pContainer.getItem(i);
         if (!current.isEmpty() && current.getItem() instanceof CursedEnergyFleshItem) {
            if (!stack.isEmpty() && stack.getItem() != current.getItem()) {
               return CursedEnergyFleshItem.getGrade(current) == CursedEnergyFleshItem.getGrade(stack);
            }

            stack = current;
         }
      }

      return false;
   }

   @NotNull
   public ItemStack assemble(@NotNull CraftingContainer pContainer, @NotNull RegistryAccess pRegistryAccess) {
      ItemStack stack = new ItemStack((ItemLike)JJKItems.MERGED_FLESH.get());
      SorcererGrade grade = null;

      for (int i = 0; i < pContainer.getContainerSize(); i++) {
         ItemStack current = pContainer.getItem(i);
         if (!current.isEmpty() && current.getItem() instanceof CursedEnergyFleshItem) {
            grade = CursedEnergyFleshItem.getGrade(current);
            break;
         }
      }

      if (grade == null) {
         return ItemStack.EMPTY;
      } else {
         CursedEnergyFleshItem.setGrade(stack, grade);
         return stack;
      }
   }

   public boolean canCraftInDimensions(int pWidth, int pHeight) {
      return pWidth * pHeight >= 2;
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)JJKRecipeSerializers.MERGED_FLESH.get();
   }
}
