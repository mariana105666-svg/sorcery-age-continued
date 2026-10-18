package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.particle.MirageParticle;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class QuickDash extends Dash {
   public static final double RANGE = 80.0;
   private static final float DASH = 2.0F;
   private static final float MAX_DASH = 3.0F;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target == null) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.hasTrait(Trait.HEAVENLY_RESTRICTION) ? HelperMethods.RANDOM.nextInt(1) == 0 : HelperMethods.RANDOM.nextInt(10) == 0;
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      return cap.isCooldownDone((Ability)JJKAbilities.DASH.get()) ? super.isTriggerable(owner) : Ability.Status.FAILURE;
   }

   private static double getDistanceGround(LivingEntity entity) {
      Vec3 pos = entity.position();
      Vec3 down = pos.add(0.0, -256.0, 0.0);
      BlockHitResult result = entity.level().clip(new ClipContext(pos, down, Block.COLLIDER, Fluid.ANY, entity));
      return result.getType() == Type.BLOCK ? pos.y - result.getLocation().y : Double.MAX_VALUE;
   }

   private static float getRange(LivingEntity owner) {
      return (float)(80.0 * (JJKAbilities.hasTrait(owner, Trait.HEAVENLY_RESTRICTION) ? 1.5F : 1.0F));
   }

   private static boolean canDash(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (!owner.hasEffect((MobEffect)JJKEffects.DOMAINSTUN.get())
         && !owner.hasEffect((MobEffect)JJKEffects.STUN.get())
         && (!cap.hasToggled((Ability)JJKAbilities.ANGEL_WINGS.get()) || !(getDistanceGround(owner) > 4.0))) {
         boolean collision = false;
         AABB bounds = owner.getBoundingBox();
         int bound = 4;
         Cursor3D cursor = new Cursor3D(
            Mth.floor(bounds.minX - 1.0E-7) - bound,
            Mth.floor(bounds.minY - 1.0E-7) - bound,
            Mth.floor(bounds.minZ - 1.0E-7) - bound,
            Mth.floor(bounds.maxX + 1.0E-7) + bound,
            Mth.floor(bounds.maxY + 1.0E-7) + bound,
            Mth.floor(bounds.maxZ + 1.0E-7) + bound
         );

         while (cursor.advance()) {
            int i = cursor.nextX();
            int j = cursor.nextY();
            int k = cursor.nextZ();
            int l = cursor.getNextType();
            if (l != 3) {
               BlockState state = owner.level().getBlockState(new BlockPos(i, j, k));
               if (!state.isAir()) {
                  collision = true;
                  break;
               }
            }
         }

         return collision || owner.getXRot() >= 1.0F;
      } else {
         return false;
      }
   }

   @Override
   public void run(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         if (canDash(owner)) {
            ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            cap.addDash();
            cap.delayTickEvent(cap::subDash, 10);
            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.BUNDLE_INSERT, SoundSource.MASTER, 2.0F, 1.25F);
            if (cap.getSpeedStacks() > 0 || cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
               owner.level()
                  .playSound(null, owner.getX(), owner.getY(), owner.getZ(), (SoundEvent)JJKSounds.DASH.get(), SoundSource.MASTER, 0.2F, 1.5F);
               JJKEffectUtil.addEffect(owner, new MobEffectInstance((MobEffect)JJKEffects.INVISIBILITY.get(), 4, 0, false, false, false));
               level.sendParticles(
                  new MirageParticle.MirageParticleOptions(owner.getId()), owner.getX(), owner.getY(), owner.getZ(), 0, 0.0, 0.0, 0.0, 1.0
               );
            }

            Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
            HitResult hit = RotationUtil.getLookAtHit(owner, getRange(owner));
            float power = Math.min(3.0F, 2.0F * (1.0F + this.getPower(owner) * 0.1F));
            if (!owner.isShiftKeyDown()) {
               power *= 0.55F;
            }

            if (owner.onGround() && look.y < 0.0) {
               look = owner.getLookAngle().multiply(1.0, 0.0, 1.0);
            }

            Vec3 velocity = look.normalize().scale(power);
            velocity = velocity.multiply(new Vec3(1.5, 1.0, 1.5));
            if (velocity.y > 0.0) {
               velocity = velocity.multiply(new Vec3(1.2, 0.6, 1.2));
            } else {
               velocity = velocity.multiply(new Vec3(1.2, 1.3, 1.2));
            }

            if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
               velocity = velocity.multiply(new Vec3(1.2, 1.0, 1.2));
               if (owner.isShiftKeyDown()) {
                  JJKEffectUtil.addEffect(owner, new MobEffectInstance((MobEffect)JJKEffects.INVISIBILITY.get(), 8, 0, false, false, false));
               } else {
                  JJKEffectUtil.addEffect(owner, new MobEffectInstance((MobEffect)JJKEffects.INVISIBILITY.get(), 4, 0, false, false, false));
                  velocity = velocity.multiply(new Vec3(1.1, 1.0, 1.1));
               }
            }

            if (owner.onGround() && velocity.y < 0.0) {
               velocity.multiply(1.0, 0.0, 1.0);
            }

            velocity = velocity.add(new Vec3(0.0, 0.6, 0.0));
            if (!owner.onGround() && owner.level().getBlockState(owner.blockPosition()).getFluidState().isEmpty()) {
               velocity = velocity.add(new Vec3(0.0, -0.75, 0.0));
            }

            owner.setDeltaMovement(velocity);
            owner.hurtMarked = true;
            Vec3 pos = owner.position();

            for (int i = 0; i < 32; i++) {
               double xPos = owner.getX()
                  + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.25F)
                  - owner.getLookAngle().scale(0.35).x;
               double yPos = owner.getY() + HelperMethods.RANDOM.nextDouble() * owner.getBbHeight();
               double zPos = owner.getZ()
                  + (HelperMethods.RANDOM.nextDouble() - 0.5) * (owner.getBbWidth() * 1.25F)
                  - owner.getLookAngle().scale(0.35).z;
               double theta = HelperMethods.RANDOM.nextDouble() * 2.0 * Math.PI;
               double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
               double r = HelperMethods.RANDOM.nextDouble() * 0.2;
               double x = r * Math.sin(phi) * Math.cos(theta);
               double y = r * Math.sin(phi) * Math.sin(theta);
               double z = r * Math.cos(phi);
               Vec3 speed = look.add(x, y, z).reverse();
               level.sendParticles(ParticleTypes.CLOUD, xPos, yPos, zPos, 0, speed.x, speed.y, speed.z, 1.0);
            }

            for (int i = 0; i < 4; i++) {
               cap.delayTickEvent(
                  () -> {
                     for (int a = 0; a < 4; a++) {
                        Vec3 pos2 = owner.getEyePosition();
                        level.sendParticles(
                           ParticleTypes.POOF,
                           pos2.x + (HelperMethods.RANDOM.nextDouble() - 0.5) * 0.8,
                           pos2.y + (HelperMethods.RANDOM.nextDouble() - 0.5) * 0.8,
                           pos2.z + (HelperMethods.RANDOM.nextDouble() - 0.5) * 0.8,
                           0,
                           0.0,
                           0.0,
                           0.0,
                           1.0
                        );
                     }
                  },
                  i
               );
            }
         }
      }
   }

   @Override
   public int getRealCooldown(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      if (cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
         return owner.isShiftKeyDown() ? 16 : 8;
      } else {
         return owner.isShiftKeyDown() ? 25 : super.getSuperRealCooldown(owner);
      }
   }
}
