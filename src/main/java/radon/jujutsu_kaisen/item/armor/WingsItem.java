package radon.jujutsu_kaisen.item.armor;

import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.client.render.item.armor.WingsRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WingsItem extends ArmorItem implements GeoItem {
   private static final RawAnimation FLY_VERTICAL = RawAnimation.begin().thenLoop("move.fly_vertical");
   private static final RawAnimation FLY_HORIZONTAL = RawAnimation.begin().thenLoop("move.fly_horizontal");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public WingsItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
      super(pMaterial, pType, pProperties);
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(new IClientItemExtensions() {
         private WingsRenderer renderer;

         @NotNull
         public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
            if (this.renderer == null) {
               this.renderer = new WingsRenderer();
            }

            this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
            return this.renderer;
         }
      });
   }

   private static double getDistanceGround(LivingEntity entity) {
      Vec3 pos = entity.position();
      Vec3 down = pos.add(0.0, -256.0, 0.0);
      BlockHitResult result = entity.level().clip(new ClipContext(pos, down, Block.COLLIDER, Fluid.ANY, entity));
      return result.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK ? pos.y - result.getLocation().y : Double.MAX_VALUE;
   }

   public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
      double dist = getDistanceGround(entity);
      return dist > 4.0 && !entity.onGround() && new Vec3(entity.xxa, 0.0, entity.zza).length() > 1.0E-7;
   }

   public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
      return true;
   }

   private PlayState flyPredicate(AnimationState<WingsItem> animationState) {
      if (animationState.getData(DataTickets.ENTITY) instanceof LivingEntity entity) {
         double dist = getDistanceGround(entity);
         if (dist > 4.0 && !entity.onGround()) {
            return animationState.setAndContinue(new Vec3(entity.xxa, 0.0, entity.zza).lengthSqr() > 1.0E-7 ? FLY_HORIZONTAL : FLY_VERTICAL);
         }
      }

      return PlayState.STOP;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController[]{new AnimationController(this, 20, this::flyPredicate)});
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
