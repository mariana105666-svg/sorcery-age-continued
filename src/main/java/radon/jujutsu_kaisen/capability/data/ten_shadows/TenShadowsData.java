package radon.jujutsu_kaisen.capability.data.ten_shadows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import radon.jujutsu_kaisen.ability.IAdditionalAdaptation;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.ten_shadows.WheelEntity;

public class TenShadowsData implements ITenShadowsData {
   private final Set<ResourceLocation> tamed;
   private final Set<ResourceLocation> dead;
   private final List<ItemStack> shadowInventory;
   private final Map<Adaptation, Integer> adapted;
   private final Map<Adaptation, Integer> adapting;
   private final Map<Adaptation, Integer> adaptationCD = new HashMap<>();
   private TenShadowsMode mode = TenShadowsMode.SUMMON;
   private LivingEntity owner;

   public TenShadowsData() {
      this.tamed = new HashSet<>();
      this.dead = new HashSet<>();
      this.adapted = new HashMap<>();
      this.adapting = new HashMap<>();
      this.shadowInventory = new ArrayList<>();
   }

   @Override
   public void resetAdaptations() {
      this.adapted.clear();
   }

   private void updateAdaptation() {
      ISorcererData cap = (ISorcererData)this.owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (cap.hasToggled((Ability)JJKAbilities.WHEEL.get())
         && (
            !cap.hasToggled((Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get())
               || !(cap.getExperience() < ((Double)ConfigHolder.SERVER.requiredExperienceForExperienced.get()).floatValue())
         )) {
         if (!cap.hasToggled((Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get())
            || !(cap.getExperience() >= ((Double)ConfigHolder.SERVER.requiredExperienceForExperienced.get()).floatValue())) {
            Iterator<Entry<Adaptation, Integer>> iter = this.adapting.entrySet().iterator();

            while (iter.hasNext()) {
               Entry<Adaptation, Integer> entry = iter.next();
               int timer = entry.getValue();
               int newtimer = timer + 1;
               entry.setValue(newtimer);
               if (++timer >= 1800) {
                  iter.remove();
                  this.owner
                     .level()
                     .playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(), SoundEvents.ANVIL_PLACE, SoundSource.MASTER, 3.0F, 1.0F);
                  this.adapted.put(entry.getKey(), this.adapted.getOrDefault(entry.getKey(), 0) + 1);
                  WheelEntity wheel = cap.getSummonByClass(WheelEntity.class);
                  if (wheel != null) {
                     wheel.spin();
                  }
               } else {
                  entry.setValue(timer);
               }
            }

            Iterator<Entry<Adaptation, Integer>> cdIter = this.adaptationCD.entrySet().iterator();

            while (cdIter.hasNext()) {
               Entry<Adaptation, Integer> cdEntry = cdIter.next();
               int cd = cdEntry.getValue() - 1;
               if (cd <= 0) {
                  cdIter.remove();
               } else {
                  cdEntry.setValue(cd);
               }
            }
         }
      } else {
         this.adapting.clear();
      }
   }

   @Override
   public void tick(LivingEntity owner) {
      if (this.owner == null) {
         this.owner = owner;
      }

      this.updateAdaptation();
   }

   @Override
   public void init(LivingEntity owner) {
      this.owner = owner;
   }

   @Override
   public boolean hasTamed(Registry<EntityType<?>> registry, EntityType<?> entity) {
      return this.tamed.contains(registry.getKey(entity));
   }

   @Override
   public void tame(Registry<EntityType<?>> registry, EntityType<?> entity) {
      this.tamed.add(registry.getKey(entity));
   }

   @Override
   public void setTamed(Set<ResourceLocation> tamed) {
      this.tamed.clear();
      this.tamed.addAll(tamed);
   }

   @Override
   public Set<ResourceLocation> getTamed() {
      return this.tamed;
   }

   @Override
   public boolean isDead(Registry<EntityType<?>> registry, EntityType<?> entity) {
      return this.dead.contains(registry.getKey(entity));
   }

   @Override
   public Set<ResourceLocation> getDead() {
      return this.dead;
   }

   @Override
   public void setDead(Set<ResourceLocation> dead) {
      this.dead.clear();
      this.dead.addAll(dead);
   }

   @Override
   public void kill(Registry<EntityType<?>> registry, EntityType<?> entity) {
      this.dead.add(registry.getKey(entity));
   }

   @Override
   public void revive(boolean full) {
      this.dead.clear();
      this.adapting.clear();
      this.adapted.clear();
      if (full) {
         this.tamed.clear();
      }
   }

   @Override
   public Map<Adaptation, Integer> getAdapted() {
      return this.adapted;
   }

   @Override
   public void addAdapted(Map<Adaptation, Integer> adaptations) {
      this.adapted.putAll(adaptations);
   }

   @Override
   public Map<Adaptation, Integer> getAdapting() {
      return this.adapting;
   }

   @Override
   public void addAdapting(Map<Adaptation, Integer> adapting) {
      this.adapting.putAll(adapting);
   }

   @Override
   public void addShadowInventory(ItemStack stack) {
      this.shadowInventory.add(stack);
   }

   @Override
   public ItemStack getShadowInventory(int index) {
      return this.shadowInventory.get(index);
   }

   @Override
   public List<ItemStack> getShadowInventory() {
      return this.shadowInventory;
   }

   @Override
   public void removeShadowInventory(int index) {
      this.shadowInventory.remove(index);
   }

   @Override
   public int getAdaptation(Ability ability) {
      for (Entry<Adaptation, Integer> entry : this.adapted.entrySet()) {
         Adaptation adapted = entry.getKey();
         Ability current = adapted.getAbility();
         if (current != null) {
            if (current == ability) {
               return entry.getValue();
            }

            Ability.Classification first = current.getClassification();
            Ability.Classification second = ability.getClassification();
            if (first != Ability.Classification.NONE && second != Ability.Classification.NONE && first == second) {
               return entry.getValue();
            }
         }
      }

      return 0;
   }

   @Override
   public float getAdaptationProgress(DamageSource source) {
      return this.getAdaptationProgress(this.getAdaptation(source));
   }

   @Override
   public float getAdaptationProgress(Adaptation adaptation) {
      return this.adapted.containsKey(adaptation) ? 1.0F : this.adapting.getOrDefault(adaptation, 0).intValue() / 1800.0F;
   }

   @Override
   public Adaptation.Type getAdaptationType(DamageSource source) {
      Adaptation adaptation = this.getAdaptation(source);
      return this.getAdaptationType(adaptation);
   }

   @Override
   public Adaptation.Type getAdaptationType(Adaptation adaptation) {
      RegistryAccess registry = this.owner.level().registryAccess();
      Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
      DamageType type = (DamageType)types.get(adaptation.getKey());
      return type != types.get(DamageTypes.MOB_ATTACK) && type != types.get(DamageTypes.PLAYER_ATTACK) ? Adaptation.Type.DAMAGE : Adaptation.Type.COUNTER;
   }

   private Adaptation getAdaptation(DamageSource source) {
      RegistryAccess registry = this.owner.level().registryAccess();
      Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
      return new Adaptation(types.getKey(source.type()), source instanceof JJKDamageSources.JujutsuDamageSource cap ? cap.getAbility() : null);
   }

   @Override
   public boolean isAdaptedTo(DamageSource source) {
      if (source instanceof JJKDamageSources.JujutsuDamageSource jujutsu) {
         Ability ability = jujutsu.getAbility();
         if (ability != null) {
            return this.isAdaptedTo(ability);
         }
      }

      Adaptation adaptation = this.getAdaptation(source);
      return this.adapted.containsKey(adaptation);
   }

   @Override
   public boolean isAdaptedTo(Ability ability) {
      for (Adaptation adapted : this.adapted.keySet()) {
         Ability current = adapted.getAbility();
         if (current != null) {
            if (current == ability) {
               return true;
            }

            Ability.Classification first = current.getClassification();
            Ability.Classification second = ability.getClassification();
            if (first != Ability.Classification.NONE && second != Ability.Classification.NONE && first == second) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean isAdaptedTo(CursedTechnique technique) {
      for (Ability ability : technique.getAbilities()) {
         if (this.isAdaptedTo(ability)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void tryAdapt(DamageSource source) {
      RegistryAccess registry = this.owner.level().registryAccess();
      Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
      ISorcererData cap = (ISorcererData)this.owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (!cap.hasToggled((Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
         ResourceLocation key = types.getKey(source.type());
         Ability ability = source instanceof JJKDamageSources.JujutsuDamageSource jujutsu ? jujutsu.getAbility() : null;
         if (key != null || ability != null) {
            Adaptation adaptation = new Adaptation(key != null ? key : JJKDamageSources.JUJUTSU.location(), ability);
            if (!this.adaptationCD.containsKey(adaptation)) {
               if (!this.adapting.containsKey(adaptation)) {
                  this.adapting.put(adaptation, 0);
               } else {
                  int timer = this.adapting.get(adaptation);
                  timer += 300;
                  this.adapting.put(adaptation, timer);
               }

               this.adaptationCD.put(adaptation, 60);
            }
         }
      }
   }

   @Override
   public void tryAdapt(Ability ability) {
      Adaptation adaptation = new Adaptation(JJKDamageSources.JUJUTSU.location(), ability);
      ISorcererData cap = (ISorcererData)this.owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (!cap.hasToggled((Ability)JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
         if (!this.isAdaptedTo(ability)
            || ability instanceof IAdditionalAdaptation additional
               && (this.adapted == null || this.adapted.get(adaptation) == null || this.adapted.get(adaptation) < additional.getAdditional() + 1)) {
            if (!this.adaptationCD.containsKey(adaptation)) {
               if (!this.adapting.containsKey(adaptation)) {
                  this.adapting.put(adaptation, 0);
               } else {
                  int timer = this.adapting.get(adaptation);
                  timer += 300;
                  this.adapting.put(adaptation, timer);
               }

               this.adaptationCD.put(adaptation, 60);
            }
         }
      }
   }

   @Override
   public TenShadowsMode getMode() {
      return this.mode;
   }

   @Override
   public void setMode(TenShadowsMode mode) {
      this.mode = mode;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putInt("mode", this.mode.ordinal());
      ListTag tamedTag = new ListTag();

      for (ResourceLocation key : this.tamed) {
         tamedTag.add(StringTag.valueOf(key.toString()));
      }

      nbt.put("tamed", tamedTag);
      ListTag deadTag = new ListTag();

      for (ResourceLocation key : this.dead) {
         deadTag.add(StringTag.valueOf(key.toString()));
      }

      nbt.put("dead", deadTag);
      ListTag adaptedTag = new ListTag();

      for (Entry<Adaptation, Integer> adaptation : this.adapted.entrySet()) {
         CompoundTag data = new CompoundTag();
         Adaptation key = adaptation.getKey();
         if (key != null && key.getAbility() != null && key.getKey() != null) {
            data.put("adaptation", key.serializeNBT());
            data.putInt("stage", adaptation.getValue());
            adaptedTag.add(data);
         }
      }

      nbt.put("adapted", adaptedTag);
      ListTag shadowInventoryTag = new ListTag();

      for (ItemStack stack : this.shadowInventory) {
         shadowInventoryTag.add(stack.save(new CompoundTag()));
      }

      nbt.put("shadow_inventory", shadowInventoryTag);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      this.mode = TenShadowsMode.values()[nbt.getInt("mode")];
      this.tamed.clear();

      for (Tag key : nbt.getList("tamed", 8)) {
         this.tamed.add(new ResourceLocation(key.getAsString()));
      }

      this.dead.clear();

      for (Tag key : nbt.getList("dead", 8)) {
         this.dead.add(new ResourceLocation(key.getAsString()));
      }

      this.adapted.clear();

      for (Tag key : nbt.getList("adapted", 10)) {
         CompoundTag DATA = (CompoundTag)key;
         this.adapted.put(new Adaptation(DATA.getCompound("adaptation")), DATA.getInt("stage"));
      }

      this.adapted.entrySet().removeIf(entry -> entry.getKey() == null || entry.getKey().getAbility() == null && entry.getKey().getKey() == null);
      this.shadowInventory.clear();

      for (Tag key : nbt.getList("shadow_inventory", 10)) {
         this.shadowInventory.add(ItemStack.of((CompoundTag)key));
      }
   }
}
