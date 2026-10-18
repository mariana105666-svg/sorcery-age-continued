package radon.jujutsu_kaisen.ability.disaster_plants;

import java.util.List;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.effect.WoodShieldEntity;

public class WoodShield extends Summon<WoodShieldEntity> {
   public WoodShield() {
      super(WoodShieldEntity.class);
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target != null && !target.isDeadOrDying() && owner.getHealth() / owner.getMaxHealth() < 0.25F;
   }

   @Override
   public List<EntityType<?>> getTypes() {
      return List.of((EntityType<?>)JJKEntities.WOOD_SHIELD_SEGMENT.get());
   }

   @Override
   public boolean isTenShadows() {
      return false;
   }

   protected WoodShieldEntity summon(LivingEntity owner) {
      return new WoodShieldEntity(owner);
   }

   @Override
   protected boolean shouldRemove() {
      return false;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 1.0F;
   }

   @Override
   public int getCooldown() {
      return 100;
   }

   @Override
   public boolean display() {
      return false;
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class WoodShieldForgeEvents {
      @SubscribeEvent
      public static void onLivingAttack(LivingAttackEvent event) {
         LivingEntity victim = event.getEntity();
         if (!victim.level().isClientSide && JJKAbilities.hasToggled(victim, (Ability)JJKAbilities.WOOD_SHIELD.get())) {
            if (victim.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
               ISorcererData cap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               WoodShieldEntity shield = cap.getSummonByClass(WoodShieldEntity.class);
               DamageSource source = event.getSource();
               if (shield != null && !source.is(JJKDamageSources.SOUL) && !source.is(JJKDamageSources.SPLIT_SOUL_KATANA)) {
                  shield.hurt(source, event.getAmount());
                  event.setCanceled(true);
               }
            }
         }
      }
   }
}
