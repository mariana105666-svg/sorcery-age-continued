package radon.jujutsu_kaisen.ability.shrine;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.effect.SpiderwebEntity;
import radon.jujutsu_kaisen.util.RotationUtil;

public class Spiderweb extends Ability implements Ability.ICharged {
   public static final int DELAY = 20;
   public static final float EXPLOSIVE_POWER = 3.0F;
   public static final float MAX_EXPLOSIVE_POWER = 20.0F;
   public static final int MAX_CHARGE = 15;
   private static final int RANGE = 3;

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      if (target != null && !target.isDeadOrDying()) {
         return ((BooleanValue)owner.level().getGameRules().getRule(GameRules.RULE_MOBGRIEFING)).get() ? owner.getNavigation().isStuck() : false;
      } else {
         return false;
      }
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.CHANNELED;
   }

   @Nullable
   private BlockHitResult getBlockHit(LivingEntity owner) {
      Vec3 start = owner.getEyePosition();
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      Vec3 end = start.add(look.scale(3.0));
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
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      JJKEffectUtil.addEffect(owner, new MobEffectInstance((MobEffect)JJKEffects.STUN.get(), 2, 1, false, false, false));
      if (owner instanceof Player && owner.level().isClientSide) {
         ClientWrapper.setOverlayMessage(
            Component.translatable(
               String.format("chat.%s.charge", "jujutsu_kaisen"), new Object[]{Math.round(Math.min(15, this.getCharge(owner)) / 15.0F * 100.0F)}
            ),
            false
         );
      }
   }

   @Override
   public void onStart(LivingEntity owner) {
      BlockHitResult hit = this.getBlockHit(owner);
      if (hit != null) {
         SpiderwebEntity spiderweb = new SpiderwebEntity(owner, this.getPower(owner), hit.getBlockPos(), hit.getDirection());
         owner.level().addFreshEntity(spiderweb);
      }
   }

   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      BlockHitResult hit = this.getBlockHit(owner);
      return hit == null ? Ability.Status.FAILURE : super.isTriggerable(owner);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 50.0F * (Math.min(15, this.getCharge(owner)) / 15.0F);
   }

   @Override
   public int getCooldown() {
      return 100;
   }

   @Override
   public Ability.Classification getClassification() {
      return Ability.Classification.SLASHING;
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.J2TSU;
   }
}
