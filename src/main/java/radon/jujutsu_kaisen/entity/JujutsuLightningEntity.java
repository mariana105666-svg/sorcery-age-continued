package radon.jujutsu_kaisen.entity;

import java.util.List;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;

public class JujutsuLightningEntity extends LightningBolt {
   private int life;
   public long seed;
   private int flashes;
   private float damage;
   @Nullable
   private UUID ownerUUID;
   @Nullable
   private LivingEntity cachedOwner;

   public JujutsuLightningEntity(EntityType<? extends LightningBolt> pType, Level pLevel) {
      super(pType, pLevel);
      this.noCulling = true;
      this.life = 2;
      this.seed = this.random.nextLong();
      this.flashes = this.random.nextInt(3) + 1;
      this.setVisualOnly(true);
   }

   public JujutsuLightningEntity(LivingEntity owner, float damage) {
      this((EntityType<? extends LightningBolt>)JJKEntities.JUJUTSU_LIGHTNING.get(), owner.level());
      this.setOwner(owner);
      this.damage = damage;
   }

   public void setOwner(@Nullable LivingEntity pOwner) {
      if (pOwner != null) {
         this.ownerUUID = pOwner.getUUID();
         this.cachedOwner = pOwner;
      }
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
         return this.cachedOwner;
      } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
         this.cachedOwner = (LivingEntity)((ServerLevel)this.level()).getEntity(this.ownerUUID);
         return this.cachedOwner;
      } else {
         return null;
      }
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.WEATHER;
   }

   public void tick() {
      super.tick();
      if (this.life == 2 && this.level().isClientSide) {
         this.level()
            .playLocalSound(
               this.getX(),
               this.getY(),
               this.getZ(),
               SoundEvents.LIGHTNING_BOLT_THUNDER,
               SoundSource.WEATHER,
               2.0F,
               0.8F + this.random.nextFloat() * 0.2F,
               false
            );
         this.level()
            .playLocalSound(
               this.getX(),
               this.getY(),
               this.getZ(),
               SoundEvents.LIGHTNING_BOLT_IMPACT,
               SoundSource.WEATHER,
               1.0F,
               0.5F + this.random.nextFloat() * 0.2F,
               false
            );
      }

      this.life--;
      if (this.life < 0) {
         if (this.flashes == 0) {
            this.discard();
         } else if (this.life < -this.random.nextInt(10)) {
            this.flashes--;
            this.life = 1;
            this.seed = this.random.nextLong();
         }
      }

      if (this.life >= 0) {
         if (!(this.level() instanceof ServerLevel)) {
            this.level().setSkyFlashTime(2);
         } else {
            List<Entity> entities = this.level()
               .getEntities(
                  this,
                  new AABB(
                     this.getX() - 3.0,
                     this.getY() - 3.0,
                     this.getZ() - 3.0,
                     this.getX() + 3.0,
                     this.getY() + 6.0 + 3.0,
                     this.getZ() + 3.0
                  ),
                  Entity::isAlive
               );
            LivingEntity owner = this.getOwner();
            if (owner != null) {
               ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

               for (Entity entity : entities) {
                  if (entity != owner || cap.hasSelfHit()) {
                     entity.hurt(
                        JJKDamageSources.indirectJujutsuAttack(this, owner, (Ability)JJKAbilities.LIGHTNING.get()), this.damage * cap.getAbilityPower()
                     );
                  }
               }
            }
         }
      }
   }
}
