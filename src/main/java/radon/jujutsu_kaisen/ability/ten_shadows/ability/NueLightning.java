package radon.jujutsu_kaisen.ability.ten_shadows.ability;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsMode;
import radon.jujutsu_kaisen.client.particle.LightningParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

public class NueLightning extends Ability implements Ability.IToggled, Ability.IAttack {
   private static final float DAMAGE = 3.5F;
   private static final int STUN = 7;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target != null && !target.isDeadOrDying() && owner.distanceTo(target) < 20.0;
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      if (!super.isValid(owner)) {
         return false;
      } else {
         ITenShadowsData cap = (ITenShadowsData)owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
         return !(
            owner instanceof Player player
               && (
                  JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.NUE.get())
                     || !cap.hasTamed(owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE), (EntityType<?>)JJKEntities.NUE.get())
                     || cap.getMode() != TenShadowsMode.ABILITY
               )
         );
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.TOGGLED;
   }

   @Override
   public void run(LivingEntity owner) {
   }

   @Override
   public float getCost(LivingEntity owner) {
      ItemStack stack = owner.getItemInHand(InteractionHand.MAIN_HAND);
      List<Item> stacks = new ArrayList<>();
      stacks.add(stack.getItem());
      stacks.addAll(CuriosUtil.findSlots(owner, owner.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand").stream().map(ItemStack::getItem).toList());
      return stacks.contains(JJKItems.JET_BLACK_SHADOW_SWORD.get()) ? 10.0F : 15.0F;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.LIGHTNING;
   }

   @Override
   public void onEnabled(LivingEntity owner) {
   }

   @Override
   public void onDisabled(LivingEntity owner) {
   }

   @Override
   public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
      if (owner.level().isClientSide) {
         return false;
      } else if (!HelperMethods.isMelee(source)) {
         return false;
      } else if (!target.hurt(
         JJKDamageSources.jujutsuAttack(owner, (Ability)JJKAbilities.NUE_LIGHTNING.get()),
         3.5F * Ability.getPower((Ability)JJKAbilities.NUE_LIGHTNING.get(), owner)
      )) {
         return false;
      } else {
         JJKEffectUtil.addEffect(target, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), 7, 0, false, false, false));
         owner.level()
            .playSound(
               null,
               target.getX(),
               target.getY(),
               target.getZ(),
               SoundEvents.LIGHTNING_BOLT_IMPACT,
               SoundSource.MASTER,
               1.0F,
               0.5F + HelperMethods.RANDOM.nextFloat() * 0.2F
            );

         for (int i = 0; i < 32; i++) {
            double offsetX = HelperMethods.RANDOM.nextGaussian() * 1.5;
            double offsetY = HelperMethods.RANDOM.nextGaussian() * 1.5;
            double offsetZ = HelperMethods.RANDOM.nextGaussian() * 1.5;
            ((ServerLevel)owner.level())
               .sendParticles(
                  new LightningParticle.LightningParticleOptions(ParticleColors.PURPLE_LIGHTNING, 0.5F, 1),
                  target.getX() + offsetX,
                  target.getY() + offsetY,
                  target.getZ() + offsetZ,
                  0,
                  0.0,
                  0.0,
                  0.0,
                  0.0
               );
         }

         return true;
      }
   }
}
