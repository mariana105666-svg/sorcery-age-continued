package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.entity.effect.ForestSpikeEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ForestSpikes extends Ability {
   private static final double RANGE = 30.0;
   private static final float SPREAD = 25.0F;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return HelperMethods.RANDOM.nextInt(3) == 0 && target != null && owner.hasLineOfSight(target);
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Nullable
   private BlockHitResult getBlockHit(LivingEntity owner) {
      Vec3 start = owner.getEyePosition();
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      Vec3 end = start.add(look.scale(30.0));
      HitResult result = RotationUtil.getHitResult(owner, start, end);
      if (result.getType() == Type.BLOCK) {
         return (BlockHitResult)result;
      } else if (result.getType() == Type.ENTITY) {
         Entity entity = ((EntityHitResult)result).getEntity();
         Vec3 offset = entity.position().subtract(0.0, 5.0, 0.0);
         return owner.level().clip(new ClipContext(entity.position(), offset, Block.COLLIDER, Fluid.NONE, null));
      } else {
         return null;
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      BlockHitResult hit = this.getBlockHit(owner);
      return hit == null ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   private void spawnSpike(LivingEntity owner, Direction dir, BlockPos pos, double offset1, double offset2) {
      ForestSpikeEntity spike = new ForestSpikeEntity(owner, this.getPower(owner));

      Vec3 spawnPos = switch (dir.getAxis()) {
         case Y -> pos.getCenter().add(offset1, 0.0, offset2);
         case X -> pos.getCenter().add(0.0, offset1, offset2);
         case Z -> pos.getCenter().add(offset1, offset2, 0.0);
         default -> pos.getCenter();
      };
      float yRot = dir.toYRot() + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 60.0F;
      float xRot = (float)(Mth.atan2(dir.getStepY(), dir.getStepX()) * 180.0 / (float) Math.PI) + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 60.0F;
      if (dir == Direction.UP || dir == Direction.DOWN) {
         xRot = -xRot;
      }

      if (dir == Direction.WEST) {
         xRot -= 180.0F;
      }

      spike.setYRot(yRot);
      spike.setXRot(xRot);
      Vec3 rayDir = new Vec3(dir.getStepX(), dir.getStepY(), dir.getStepZ()).scale(-1.0);
      Vec3 rayEnd = spawnPos.add(rayDir.scale(5.0));
      BlockHitResult hitResult = owner.level().clip(new ClipContext(spawnPos, rayEnd, Block.COLLIDER, Fluid.NONE, spike));
      if (hitResult.getType() == Type.BLOCK) {
         BlockPos blockPos = hitResult.getBlockPos();
         BlockPos airCheck = blockPos.relative(dir);
         BlockState state = owner.level().getBlockState(airCheck);
         if (!state.getCollisionShape(owner.level(), airCheck).isEmpty()) {
            spike.discard();
            return;
         }

         double blockCenterX = blockPos.getX() + 0.5;
         double blockCenterY = blockPos.getY() + 0.5;
         double blockCenterZ = blockPos.getZ() + 0.5;
         double extraOffset = -0.25;
         double pushDistance = 0.5 + spike.getBbHeight() / 2.0F + extraOffset;
         double finalX = blockCenterX + dir.getStepX() * pushDistance;
         double finalY = blockCenterY + dir.getStepY() * pushDistance;
         double finalZ = blockCenterZ + dir.getStepZ() * pushDistance;
         spike.moveTo(finalX, finalY, finalZ, yRot, xRot);
         owner.level().addFreshEntity(spike);
      } else {
         spike.discard();
      }
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      BlockHitResult hit = this.getBlockHit(owner);
      if (hit != null) {
         owner.level().playSound(null, hit.getBlockPos(), (SoundEvent)JJKSounds.FOREST_SPIKES.get(), SoundSource.MASTER, 1.0F, 1.0F);
         Direction dir = hit.getDirection();
         BlockPos pos = hit.getBlockPos();
         double offset1 = 0.0;
         double offset2 = 0.0;
         this.spawnSpike(owner, dir, pos, offset1, offset2);

         for (int i = 0; i < 63; i++) {
            offset1 = (HelperMethods.RANDOM.nextDouble() - 0.5) * 25.0;
            offset2 = (HelperMethods.RANDOM.nextDouble() - 0.5) * 25.0;
            this.spawnSpike(owner, dir, pos, offset1, offset2);
         }
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 150.0F;
   }

   @Override
   public int getCooldown() {
      return 300;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.PLANTS;
   }
}
