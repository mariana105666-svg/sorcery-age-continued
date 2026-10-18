package radon.jujutsu_kaisen.item.cursed_tool;

import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedEnergyNature;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.client.render.item.NyoiStaffRenderer;
import radon.jujutsu_kaisen.entity.NyoiStaffEntity;
import radon.jujutsu_kaisen.item.base.CursedToolItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class NyoiStaffItem extends CursedToolItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public NyoiStaffItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
      super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
   }

   @Override
   public SorcererGrade getGrade() {
      return SorcererGrade.SPECIAL_GRADE;
   }

   @NotNull
   public InteractionResult useOn(@NotNull UseOnContext pContext) {
      BlockPlaceContext ctx = new BlockPlaceContext(pContext);
      Player player = ctx.getPlayer();
      if (ctx.canPlace() && ctx.getClickedFace() == Direction.UP && player != null) {
         ItemStack stack = ctx.getItemInHand();
         NyoiStaffEntity staff = new NyoiStaffEntity(player, stack, Vec3.atLowerCornerWithOffset(ctx.getClickedPos(), 0.5, 0.0, 0.5));
         ISorcererData cap = (ISorcererData)player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (cap.getNature() == CursedEnergyNature.LIGHTNING) {
            float cost = ((Ability)JJKAbilities.LIGHTNING.get()).getRealCost(player) * 0.75F;
            cap.delayTickEvent(() -> {
               if (staff != null) {
                  if (!player.getAbilities().instabuild) {
                     if (cap.getEnergy() >= cost) {
                        cap.useEnergy(cost);
                        staff.setCharged(true);
                     }
                  } else {
                     staff.setCharged(true);
                  }
               }
            }, 20);
         }

         stack.shrink(1);
         ctx.getLevel().addFreshEntity(staff);
         return InteractionResult.sidedSuccess(ctx.getLevel().isClientSide);
      } else {
         return InteractionResult.FAIL;
      }
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(new IClientItemExtensions() {
         private NyoiStaffRenderer renderer;

         public NyoiStaffRenderer getCustomRenderer() {
            if (this.renderer == null) {
               this.renderer = new NyoiStaffRenderer();
            }

            return this.renderer;
         }
      });
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
