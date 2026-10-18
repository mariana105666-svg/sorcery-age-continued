package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;

public class SuguruGetoEntity extends SorcererEntity {
   public SuguruGetoEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
      super(pType, pLevel);
   }

   @Override
   protected boolean isCustom() {
      return false;
   }

   @Override
   public float getExperience() {
      return SorcererGrade.SPECIAL_GRADE.getRequiredExperience();
   }

   public static Builder createAttributes() {
      return createMobAttributes()
         .add(Attributes.ATTACK_DAMAGE)
         .add(Attributes.FOLLOW_RANGE, 140.0)
         .add(Attributes.ARMOR_TOUGHNESS, 10.0)
         .add(Attributes.ARMOR, 20.0);
   }

   @Nullable
   @Override
   public CursedTechnique getTechnique() {
      return CursedTechnique.CURSE_MANIPULATION;
   }

   @Override
   public JujutsuType getJujutsuType() {
      return JujutsuType.SORCERER;
   }

   @Nullable
   private AbsorbedCurse createCurse(EntityType<?> type) {
      if (type.create(this.level()) instanceof CursedSpirit curse) {
         ISorcererData var4 = (ISorcererData)curse.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return new AbsorbedCurse(type.getDescription(), type, var4.serializeNBT());
      } else {
         return null;
      }
   }

   private void tryAddCurse(ISorcererData data, EntityType<?> type) {
      AbsorbedCurse curse = this.createCurse(type);
      if (curse != null) {
         data.addCurse(curse);
      }
   }

   @Override
   public void init(ISorcererData data) {
      super.init(data);

      for (RegistryObject<EntityType<?>> entry : JJKEntities.ENTITIES.getEntries()) {
         EntityType<?> type = (EntityType<?>)entry.get();
         if (type != JJKEntities.ABSORBED_PLAYER.get()
            && type.create(this.level()) instanceof CursedSpirit curse
            && curse.getGrade().ordinal() < SorcererGrade.SPECIAL_GRADE.ordinal()) {
            for (int i = 0; i < SorcererGrade.values().length - curse.getGrade().ordinal(); i++) {
               this.tryAddCurse(data, type);
            }
         }
      }

      this.tryAddCurse(data, (EntityType<?>)JJKEntities.KUCHISAKE_ONNA.get());
      this.tryAddCurse(data, (EntityType<?>)JJKEntities.DINO_CURSE.get());
   }

   @Override
   public void onAddedToWorld() {
      super.onAddedToWorld();
      this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack((ItemLike)JJKItems.PLAYFUL_CLOUD.get()));
   }
}
