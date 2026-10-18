package radon.jujutsu_kaisen.entity.sorcerer;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;

public class RyuIshigoriEntity extends SorcererEntity {
   private static final float EXPERIENCE = 15000.0F;

   public RyuIshigoriEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
      super(pType, pLevel);
   }

   @Override
   protected boolean isCustom() {
      return false;
   }

   @Override
   public float getExperience() {
      return 15000.0F;
   }

   public static Builder createAttributes() {
      return createMobAttributes()
         .add(Attributes.ATTACK_DAMAGE)
         .add(Attributes.FOLLOW_RANGE, 140.0)
         .add(Attributes.ARMOR_TOUGHNESS, 16.0)
         .add(Attributes.ARMOR, 22.0);
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return CursedTechnique.CURSED_ENERGY_DISCHARGE;
   }

   @Override
   public JujutsuType getJujutsuType() {
      return JujutsuType.SORCERER;
   }

   @Override
   public List<Ability> getUnlocked() {
      return List.of(
         (Ability)JJKAbilities.SWEET_BURNING_MATSURI.get(),
         (Ability)JJKAbilities.SIMPLE_DOMAIN.get(),
         (Ability)JJKAbilities.RCT1.get(),
         (Ability)JJKAbilities.QUICKDASH.get()
      );
   }

   @NotNull
   @Override
   public List<Trait> getTraits() {
      return List.of(Trait.INCARNATED);
   }
}
