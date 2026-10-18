package radon.jujutsu_kaisen.item.cursed_tool;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.client.render.item.KamutokeDaggerRenderer;
import radon.jujutsu_kaisen.entity.JujutsuLightningEntity;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SetOverlayMessageS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class KamutokeDaggerItem extends CursedToolItem implements GeoItem {
   public static final double RANGE = 60.0;
   private static final int COUNT = 20;
   public static final float MELEE_COST = 20.0F;
   private static final float RANGE_COST = 500.0F;
   public static final float MELEE_DAMAGE = 5.5F;
   private static final float RANGE_DAMAGE = 25.0F;
   public static final int STUN = 5;
   private static final int DURATION = 60;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public KamutokeDaggerItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
      super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
   }

   @Override
   public SorcererGrade getGrade() {
      return SorcererGrade.SPECIAL_GRADE;
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(new IClientItemExtensions() {
         private KamutokeDaggerRenderer renderer;

         public KamutokeDaggerRenderer getCustomRenderer() {
            if (this.renderer == null) {
               this.renderer = new KamutokeDaggerRenderer();
            }

            return this.renderer;
         }
      });
   }

   @NotNull
   public UseAnim getUseAnimation(@NotNull ItemStack pStack) {
      return UseAnim.SPEAR;
   }

   public int getUseDuration(@NotNull ItemStack pStack) {
      return 72000;
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      pPlayer.startUsingItem(pHand);
      return InteractionResultHolder.consume(itemstack);
   }

   private float getPowerForTime(int pUseTime) {
      float f = pUseTime / 60.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   @Nullable
   private BlockHitResult getBlockHit(LivingEntity owner) {
      Vec3 start = owner.getEyePosition();
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      Vec3 end = start.add(look.scale(60.0));
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

   public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration) {
      super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
      int i = this.getUseDuration(pStack) - pRemainingUseDuration;
      float f = this.getPowerForTime(i);
      if (pLivingEntity instanceof ServerPlayer player) {
         PacketHandler.sendToClient(
            new SetOverlayMessageS2CPacket(Component.translatable(String.format("chat.%s.cost", "jujutsu_kaisen"), new Object[]{500.0F * f, 500.0F}), false),
            player
         );
      }
   }

   public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
      super.onStopUsing(stack, entity, count);
      if (entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         BlockHitResult hit = this.getBlockHit(entity);
         if (hit != null) {
            int i = this.getUseDuration(stack) - count;
            float f = this.getPowerForTime(i);
            float cost = 500.0F * f;
            ISorcererData cap = (ISorcererData)entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if (!(entity instanceof Player player && player.getAbilities().instabuild)) {
               if (cap.getEnergy() < cost) {
                  return;
               }

               cap.useEnergy(cost);
            }

            Vec3 pos = hit.getBlockPos().getCenter();

            for (int j = 0; j < 20.0F * f; j++) {
               JujutsuLightningEntity lightning = new JujutsuLightningEntity(entity, 25.0F * f);
               lightning.setPos(pos.add((HelperMethods.RANDOM.nextDouble() - 0.5) * 5.0, 0.0, (HelperMethods.RANDOM.nextDouble() - 0.5) * 5.0));
               entity.level().addFreshEntity(lightning);
            }
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
