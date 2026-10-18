package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.ability.base.Transformation;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Wings extends Transformation {
   private static final float SPEED = 0.01F;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   private static double getDistanceGround(LivingEntity entity) {
      Vec3 pos = entity.position();
      Vec3 down = pos.add(0.0, -256.0, 0.0);
      BlockHitResult result = entity.level().clip(new ClipContext(pos, down, Block.COLLIDER, Fluid.NONE, entity));
      return result.getType() == Type.BLOCK ? pos.y - result.getLocation().y : Double.MAX_VALUE;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return getDistanceGround(owner) > 4.0;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.TOGGLED;
   }

   @Override
   public void run(LivingEntity owner) {
      if (getDistanceGround(owner) > 4.0) {
         owner.resetFallDistance();
         Vec3 movement = owner.getDeltaMovement();
         Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
         owner.setDeltaMovement(movement.x, look.y, movement.z);
         float f = owner.xxa * 0.5F;
         float f1 = owner.zza;
         if (f1 <= 0.0F) {
            f1 *= 0.25F;
         }

         owner.moveRelative(0.01F, new Vec3(f, 0.0, f1));
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 1.0F;
   }

   @Override
   public boolean isReplacement() {
      return false;
   }

   @Override
   public Item getItem() {
      return (Item)JJKItems.WINGS.get();
   }

   @Override
   public ITransformation.Part getBodyPart() {
      return ITransformation.Part.BODY;
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

   @Override
   public boolean usesHands() {
      return false;
   }
}
