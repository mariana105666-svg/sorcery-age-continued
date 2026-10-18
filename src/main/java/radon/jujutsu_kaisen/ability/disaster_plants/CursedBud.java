package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.projectile.CursedBudProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class CursedBud extends Ability {
   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return HelperMethods.RANDOM.nextInt(2) == 0 && target != null && owner.hasLineOfSight(target);
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return Ability.ActivationType.INSTANT;
   }

   @Override
   public void run(LivingEntity owner) {
      owner.swing(InteractionHand.MAIN_HAND);
      CursedBudProjectile bud = new CursedBudProjectile(owner, this.getPower(owner));
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - bud.getBbHeight() / 2.0F, owner.getZ()).add(look);
      bud.moveTo(spawn.x, spawn.y, spawn.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));
      owner.level().addFreshEntity(bud);
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 50.0F;
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE
   )
   public static class CursedBudForgeEvents {
      @SubscribeEvent
      public static void onLivingDamage(LivingDamageEvent event) {
         DamageSource source = event.getSource();
         if (source.getEntity() instanceof LivingEntity attacker) {
            if (!attacker.level().isClientSide) {
               LivingEntity victim = event.getEntity();
               if (HelperMethods.isMelee(source)) {
                  if (attacker.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                     ISorcererData cap = (ISorcererData)attacker.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                     if (cap.hasTechnique(CursedTechnique.DISASTER_PLANTS)) {
                        for (CursedBudProjectile bud : victim.level().getEntitiesOfClass(CursedBudProjectile.class, AABB.ofSize(victim.position(), 8.0, 8.0, 8.0))) {
                           if (bud.getOwner() == attacker) {
                              bud.implant(victim);
                              return;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
