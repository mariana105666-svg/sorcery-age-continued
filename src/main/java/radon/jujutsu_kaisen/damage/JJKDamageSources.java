package radon.jujutsu_kaisen.damage;

import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.base.Ability;

public class JJKDamageSources {
   public static final ResourceKey<DamageType> JUJUTSU = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("jujutsu_kaisen", "jujutsu"));
   public static final ResourceKey<DamageType> WORLD_SLASH = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("jujutsu_kaisen", "world_slash"));
   public static final ResourceKey<DamageType> SOUL = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("jujutsu_kaisen", "soul"));
   public static final ResourceKey<DamageType> SELF = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("jujutsu_kaisen", "self"));
   public static final ResourceKey<DamageType> SPLIT_SOUL_KATANA = ResourceKey.create(
      Registries.DAMAGE_TYPE, new ResourceLocation("jujutsu_kaisen", "split_soul_katana")
   );

   public static JJKDamageSources.JujutsuDamageSource jujutsuAttack(LivingEntity source, @Nullable Ability ability) {
      RegistryAccess registry = source.level().registryAccess();
      Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
      return new JJKDamageSources.JujutsuDamageSource(types.getHolderOrThrow(JUJUTSU), source, ability);
   }

   public static JJKDamageSources.JujutsuDamageSource indirectJujutsuAttack(Entity source, @Nullable LivingEntity indirect, @Nullable Ability ability) {
      RegistryAccess registry = source.level().registryAccess();
      Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
      return new JJKDamageSources.JujutsuDamageSource(types.getHolderOrThrow(JUJUTSU), source, indirect, ability);
   }

   public static DamageSource worldSlash(Entity source, @Nullable LivingEntity indirect) {
      RegistryAccess registry = source.level().registryAccess();
      Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
      return new DamageSource(types.getHolderOrThrow(WORLD_SLASH), source, indirect);
   }

   public static DamageSource soulAttack(LivingEntity source) {
      RegistryAccess registry = source.level().registryAccess();
      Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
      return new DamageSource(types.getHolderOrThrow(SOUL), source);
   }

   public static DamageSource self(LivingEntity source) {
      RegistryAccess registry = source.level().registryAccess();
      Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
      return new DamageSource(types.getHolderOrThrow(SELF), source);
   }

   public static DamageSource splitSoulKatanaAttack(LivingEntity source) {
      RegistryAccess registry = source.level().registryAccess();
      Registry<DamageType> types = registry.registryOrThrow(Registries.DAMAGE_TYPE);
      return new DamageSource(types.getHolderOrThrow(SPLIT_SOUL_KATANA), source);
   }

   public static class JujutsuDamageSource extends DamageSource {
      @Nullable
      private final Ability ability;

      public JujutsuDamageSource(Reference<DamageType> holderOrThrow, Entity source, @Nullable LivingEntity indirect, @Nullable Ability ability) {
         super(holderOrThrow, source, indirect);
         this.ability = ability;
      }

      public JujutsuDamageSource(Reference<DamageType> holderOrThrow, @Nullable LivingEntity mob, @Nullable Ability ability) {
         super(holderOrThrow, mob);
         this.ability = ability;
      }

      @Nullable
      public Ability getAbility() {
         return this.ability;
      }
   }
}
