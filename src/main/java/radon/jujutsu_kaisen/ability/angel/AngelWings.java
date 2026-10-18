package radon.jujutsu_kaisen.ability.angel;

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
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.ability.base.Transformation;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.item.JJKItems;

public class AngelWings extends Transformation {
   private static final float SPEED = 0.006F;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean isValid(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return cap.getTechnique() == CursedTechnique.ANGEL && super.isValid(owner);
   }

   private static double getDistanceGround(LivingEntity entity) {
      Vec3 pos = entity.position();
      Vec3 down = pos.add(0.0, -256.0, 0.0);
      BlockHitResult result = entity.level().clip(new ClipContext(pos, down, Block.COLLIDER, Fluid.ANY, entity));
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
         owner.setDeltaMovement(movement.x, movement.y, movement.z);
         float f = owner.xxa * 0.5F;
         float f1 = owner.zza;
         if (f1 <= 0.0F) {
            f1 *= 0.25F;
         }

         owner.moveRelative(0.006F, new Vec3(f, 0.0, f1));
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.0F;
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
}
