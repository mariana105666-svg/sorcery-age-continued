package radon.jujutsu_kaisen.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.CameraShakeS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MalevolentShrineEntity extends OpenDomainExpansionEntity implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public MalevolentShrineEntity(EntityType<?> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public MalevolentShrineEntity(LivingEntity owner, DomainExpansion ability, int radius, int height) {
      super((EntityType<?>)JJKEntities.MALEVOLENT_SHRINE.get(), owner, ability, radius, height);
   }

   @Override
   public void onAddedToWorld() {
      super.onAddedToWorld();
   }

   @Override
   public DomainExpansionEntity checkSureHitEffect() {
      return this.getTime() < 34 ? null : super.checkSureHitEffect();
   }

   @Override
   public void doSureHitEffect() {
      super.doSureHitEffect();
      LivingEntity owner = this.getOwner();
      BlockPos center = this.blockPosition();
      int width = this.getRadius();
      int height = this.getHeight();
      if (this.first) {
         for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
               int delay = i * 4;
               int horizontal = i;
               int vertical = j;
               ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               cap.delayTickEvent(
                  () -> {
                     if (!this.isRemoved()) {
                        for (int x = -horizontal; x <= horizontal; x++) {
                           for (int z = -horizontal; z <= horizontal; z++) {
                              double distance = Math.sqrt(x * x + vertical * vertical + z * z);
                              if (distance < horizontal && distance >= horizontal - 1) {
                                 BlockPos posx = center.offset(x, vertical, z);
                                 if (this.isAffected(posx)) {
                                    owner.level()
                                       .playSound(
                                          null,
                                          posx.getX(),
                                          posx.getY(),
                                          posx.getZ(),
                                          SoundEvents.GENERIC_EXPLODE,
                                          SoundSource.MASTER,
                                          1.0F,
                                          (1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F) * 0.5F
                                       );
                                    if (HelperMethods.isDestroyable(this.level(), owner, posx)) {
                                       BlockState state = owner.level().getBlockState(posx);
                                       if (state.getFluidState().isEmpty()) {
                                          owner.level().setBlock(posx, Blocks.AIR.defaultBlockState(), 50);
                                       } else {
                                          this.level().setBlockAndUpdate(posx, Blocks.AIR.defaultBlockState());
                                       }

                                       if (this.random.nextInt(10) == 0) {
                                          ((ServerLevel)owner.level())
                                             .sendParticles(ParticleTypes.EXPLOSION, posx.getX(), posx.getY(), posx.getZ(), 0, 0.0, 0.0, 0.0, 0.0);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  },
                  delay
               );
            }
         }

         this.first = false;
      }

      int size = width * height / 4;
      AABB bounds = this.getBounds();
      int blockcount = 0;

      for (BlockPos pos : BlockPos.randomBetweenClosed(
         this.random,
         size,
         (int)bounds.minX,
         (int)bounds.minY,
         (int)bounds.minZ,
         (int)bounds.maxX,
         (int)bounds.maxY,
         (int)bounds.maxZ
      )) {
         if (this.isAffected(pos)) {
            if (++blockcount % 2 == 0) {
               this.ability.onHitBlock(this, owner, pos);
            }
         }
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getTime() == 1) {
         for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBounds())) {
            if (entity instanceof ServerPlayer player) {
               JJKEffectUtil.addEffect(player, new MobEffectInstance(MobEffects.BLINDNESS, 34, 0, false, false));
               player.level()
                  .playSound(
                     null,
                     player.getX(),
                     player.getY(),
                     player.getZ(),
                     (SoundEvent)JJKSounds.MALEVOLENT_SHRINE.get(),
                     SoundSource.MASTER,
                     1.0F,
                     1.0F
                  );
            }
         }
      }

      if (this.getTime() >= 34 && this.getTime() % 10 == 0) {
         for (LivingEntity entityx : this.level().getEntitiesOfClass(LivingEntity.class, this.getBounds())) {
            if (entityx instanceof ServerPlayer player) {
               PacketHandler.sendToClient(new CameraShakeS2CPacket(0.2F, 3.0F, 20), player);
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
