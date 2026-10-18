package radon.jujutsu_kaisen.ability.idle_transfiguration;

import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

public class Shrink extends Ability implements Ability.IToggled {
   private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("e6b4c6a4-9171-41b0-a4e2-7b3df7b6c602");

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return JJKAbilities.hasToggled(owner, this)
         ? target != null && !target.isDeadOrDying() && HelperMethods.RANDOM.nextInt(20) != 0
         : target != null && !target.isDeadOrDying() && HelperMethods.RANDOM.nextInt(5) == 0;
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
      return 0.2F;
   }

   @Override
   public void applyModifiers(LivingEntity owner) {
      EntityUtil.applyModifier(owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", -0.5, Operation.ADDITION);
   }

   @Override
   public void removeModifiers(LivingEntity owner) {
      EntityUtil.removeModifier(owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
   }

   @Override
   public void onEnabled(LivingEntity owner) {
   }

   @Override
   public void onDisabled(LivingEntity owner) {
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      return !JJKAbilities.hasToggled(owner, (Ability)JJKAbilities.INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING.get()) && super.isValid(owner);
   }
}
