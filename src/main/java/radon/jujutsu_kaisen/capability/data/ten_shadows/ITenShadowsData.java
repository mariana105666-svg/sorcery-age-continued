package radon.jujutsu_kaisen.capability.data.ten_shadows;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;

@AutoRegisterCapability
public interface ITenShadowsData {
   void tick(LivingEntity var1);

   void init(LivingEntity var1);

   boolean hasTamed(Registry<EntityType<?>> var1, EntityType<?> var2);

   void tame(Registry<EntityType<?>> var1, EntityType<?> var2);

   void setTamed(Set<ResourceLocation> var1);

   Set<ResourceLocation> getTamed();

   boolean isDead(Registry<EntityType<?>> var1, EntityType<?> var2);

   Set<ResourceLocation> getDead();

   void setDead(Set<ResourceLocation> var1);

   void kill(Registry<EntityType<?>> var1, EntityType<?> var2);

   void revive(boolean var1);

   Map<Adaptation, Integer> getAdapted();

   void addAdapted(Map<Adaptation, Integer> var1);

   Map<Adaptation, Integer> getAdapting();

   void addAdapting(Map<Adaptation, Integer> var1);

   void addShadowInventory(ItemStack var1);

   ItemStack getShadowInventory(int var1);

   List<ItemStack> getShadowInventory();

   void removeShadowInventory(int var1);

   float getAdaptationProgress(DamageSource var1);

   float getAdaptationProgress(Adaptation var1);

   Adaptation.Type getAdaptationType(DamageSource var1);

   Adaptation.Type getAdaptationType(Adaptation var1);

   int getAdaptation(Ability var1);

   void resetAdaptations();

   boolean isAdaptedTo(DamageSource var1);

   boolean isAdaptedTo(@Nullable Ability var1);

   boolean isAdaptedTo(CursedTechnique var1);

   void tryAdapt(DamageSource var1);

   void tryAdapt(Ability var1);

   TenShadowsMode getMode();

   void setMode(TenShadowsMode var1);

   CompoundTag serializeNBT();

   void deserializeNBT(CompoundTag var1);
}
