package radon.jujutsu_kaisen.item.base;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.config.ConfigHolder;

public abstract class CursedObjectItem extends Item {
   private static final int DURATION = 600;
   private static final int AMPLIFIER = 5;

   public CursedObjectItem(Properties pProperties) {
      super(pProperties);
   }

   public abstract SorcererGrade getGrade();

   @NotNull
   public Component getName(@NotNull ItemStack pStack) {
      MutableComponent name = super.getName(pStack).copy();
      return name.withStyle(ChatFormatting.DARK_PURPLE);
   }

   public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
      pTooltipComponents.add(
         Component.translatable(
            String.format("item.%s.grade", "jujutsu_kaisen"), new Object[]{this.getGrade().getName().copy().withStyle(ChatFormatting.DARK_RED)}
         )
      );
   }

   public float getEnergy() {
      return (this.getGrade().ordinal() + 1) * ((Double)ConfigHolder.SERVER.cursedObjectEnergyForGrade.get()).floatValue();
   }

   @NotNull
   public ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pEntityLiving) {
      ItemStack stack = super.finishUsingItem(pStack, pLevel, pEntityLiving);
      pEntityLiving.getCapability(SorcererDataHandler.INSTANCE)
         .ifPresent(
            cap -> {
               if (cap.getType() == JujutsuType.CURSE) {
                  cap.addExtraEnergy(this.getEnergy());
               } else {
                  pEntityLiving.addEffect(
                     new MobEffectInstance(
                        MobEffects.WITHER,
                        Mth.floor(600.0F * ((float)(this.getGrade().ordinal() + 1) / SorcererGrade.values().length)),
                        Mth.floor(5.0F * ((float)(this.getGrade().ordinal() + 1) / SorcererGrade.values().length))
                     )
                  );
               }
            }
         );
      return stack;
   }
}
