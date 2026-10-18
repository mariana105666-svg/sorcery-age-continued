package radon.jujutsu_kaisen.item.armor;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public record JJKArmorMaterial(
   String name,
   int durabilityMultiplier,
   int[] slotProtections,
   int enchantmentValue,
   SoundEvent sound,
   float toughness,
   float knockbackResistance,
   Supplier<Ingredient> repairIngredient
) implements ArmorMaterial {
   private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};

   public int getDurabilityForType(@NotNull Type pType) {
      return HEALTH_PER_SLOT[pType.getSlot().getIndex()] * this.durabilityMultiplier;
   }

   public int getDefenseForType(@NotNull Type pType) {
      return this.slotProtections[pType.getSlot().getIndex()];
   }

   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   @NotNull
   public SoundEvent getEquipSound() {
      return this.sound;
   }

   @NotNull
   public Ingredient getRepairIngredient() {
      return this.repairIngredient.get();
   }

   @NotNull
   public String getName() {
      return new ResourceLocation("jujutsu_kaisen", this.name).toString();
   }

   public float getToughness() {
      return this.toughness;
   }

   public float getKnockbackResistance() {
      return this.knockbackResistance;
   }
}
