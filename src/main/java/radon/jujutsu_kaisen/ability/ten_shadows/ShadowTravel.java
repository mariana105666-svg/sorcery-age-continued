package radon.jujutsu_kaisen.ability.ten_shadows;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class ShadowTravel extends Ability {
   private static final double RANGE = 75.0;

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target != null
         && !target.isDeadOrDying()
         && owner.hasLineOfSight(target)
         && this.getTarget(owner) instanceof EntityHitResult hit
         && hit.getEntity() == target;
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Nullable
   private HitResult getTarget(LivingEntity owner) {
      HitResult hit = RotationUtil.getLookAtHit(owner, 75.0);
      if (hit.getType() == Type.MISS) {
         return null;
      } else if (hit.getType() == Type.BLOCK && ((BlockHitResult)hit).getDirection() == Direction.DOWN) {
         return null;
      } else {
         long time = owner.level().getLevelData().getDayTime();
         boolean night = time >= 13000L && time < 24000L;
         return !(hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof TenShadowsSummon)
               && (
                  !night && owner.level().getBrightness(LightLayer.SKY, BlockPos.containing(hit.getLocation())) >= 15
                     || owner.level().getBrightness(LightLayer.BLOCK, BlockPos.containing(hit.getLocation())) != 0
               )
            ? null
            : hit;
      }
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      if (owner.level() instanceof ServerLevel level) {
         HitResult target = this.getTarget(owner);
         if (target != null) {
            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.FISHING_BOBBER_SPLASH, SoundSource.MASTER, 1.0F, 1.0F);
            JJKEffectUtil.addEffect(owner, new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, false, false));

            for (int i = 0; i < 16; i++) {
               for (int j = 0; j < owner.getBbHeight() * owner.getBbHeight(); j++) {
                  level.sendParticles(
                     ParticleTypes.SMOKE,
                     owner.getX() + owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F,
                     owner.getY(),
                     owner.getZ() + owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F,
                     0,
                     HelperMethods.RANDOM.nextGaussian() * 0.075,
                     HelperMethods.RANDOM.nextGaussian() * 0.25,
                     HelperMethods.RANDOM.nextGaussian() * 0.075,
                     1.0
                  );
                  level.sendParticles(
                     ParticleTypes.LARGE_SMOKE,
                     owner.getX() + owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F,
                     owner.getY(),
                     owner.getZ() + owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F,
                     0,
                     HelperMethods.RANDOM.nextGaussian() * 0.075,
                     HelperMethods.RANDOM.nextGaussian() * 0.25,
                     HelperMethods.RANDOM.nextGaussian() * 0.075,
                     1.0
                  );
               }
            }

            Vec3 pos = target.getLocation();
            owner.teleportTo(pos.x, pos.y, pos.z);
            owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.FISHING_BOBBER_SPLASH, SoundSource.MASTER, 1.0F, 1.0F);

            for (int i = 0; i < 16; i++) {
               for (int j = 0; j < owner.getBbHeight() * owner.getBbHeight(); j++) {
                  level.sendParticles(
                     ParticleTypes.SMOKE,
                     owner.getX() + owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F,
                     owner.getY(),
                     owner.getZ() + owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F,
                     0,
                     HelperMethods.RANDOM.nextGaussian() * 0.075,
                     HelperMethods.RANDOM.nextGaussian() * 0.25,
                     HelperMethods.RANDOM.nextGaussian() * 0.075,
                     1.0
                  );
                  level.sendParticles(
                     ParticleTypes.LARGE_SMOKE,
                     owner.getX() + owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F,
                     owner.getY(),
                     owner.getZ() + owner.getBbWidth() * HelperMethods.RANDOM.nextGaussian() * 0.1F,
                     0,
                     HelperMethods.RANDOM.nextGaussian() * 0.075,
                     HelperMethods.RANDOM.nextGaussian() * 0.25,
                     HelperMethods.RANDOM.nextGaussian() * 0.075,
                     1.0
                  );
               }
            }
         }
      }
   }

   @Override
   public float getCost(LivingEntity owner) {
      ItemStack stack = owner.getItemInHand(InteractionHand.MAIN_HAND);
      List<Item> stacks = new ArrayList<>();
      stacks.add(stack.getItem());
      stacks.addAll(CuriosUtil.findSlots(owner, owner.getMainArm() == HumanoidArm.RIGHT ? "right_hand" : "left_hand").stream().map(ItemStack::getItem).toList());
      return stacks.contains(JJKItems.JET_BLACK_SHADOW_SWORD.get()) ? 20.0F : 40.0F;
   }

   @Override
   public int getCooldown() {
      return 20;
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      HitResult target = this.getTarget(owner);
      return target == null ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
