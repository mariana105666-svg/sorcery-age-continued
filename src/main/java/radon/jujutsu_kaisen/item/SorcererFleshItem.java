package radon.jujutsu_kaisen.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;

public class SorcererFleshItem extends CursedEnergyFleshItem {
   public SorcererFleshItem(Properties pProperties) {
      super(pProperties);
   }

   @NotNull
   public ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
      ItemStack stack = super.finishUsingItem(pStack, pLevel, pEntityLiving);
      pEntityLiving.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
         if (cap.getType() == JujutsuType.CURSE) {
            cap.addExtraEnergy((getGrade(pStack).ordinal() + 1) * ((Double)ConfigHolder.SERVER.cursedObjectEnergyForGrade.get()).floatValue());
         }
      });
      return stack;
   }
}
