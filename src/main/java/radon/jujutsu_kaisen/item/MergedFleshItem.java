package radon.jujutsu_kaisen.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;

public class MergedFleshItem extends CursedEnergyFleshItem {
   public MergedFleshItem(Properties pProperties) {
      super(pProperties);
   }

   @NotNull
   public ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pLivingEntity) {
      if (pLivingEntity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         ISorcererData cap = (ISorcererData)pLivingEntity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (cap.getType() == JujutsuType.CURSE) {
            cap.addExtraEnergy((getGrade(pStack).ordinal() + 1) * ((Double)ConfigHolder.SERVER.cursedObjectEnergyForGrade.get()).floatValue() * 2.0F);
         }
      }

      return super.finishUsingItem(pStack, pLevel, pLivingEntity);
   }
}
