package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.effect.base.JJKEffectUtil;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.RabbitEscapeEntity;

public class RabbitEscape extends Summon<RabbitEscapeEntity> {
   private boolean triggered = false;

   public RabbitEscape() {
      super(RabbitEscapeEntity.class);
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return target != null && !target.isDeadOrDying() && owner.tickCount - owner.getLastHurtByMobTimestamp() < 20;
   }

   @Override
   public List<EntityType<?>> getTypes() {
      return List.of((EntityType<?>)JJKEntities.RABBIT_ESCAPE.get());
   }

   @Override
   public void run(LivingEntity owner) {
      super.run(owner);
   }

   @Override
   public Ability.ActivationType getActivationType(LivingEntity owner) {
      return this.isTamed(owner) ? Ability.ActivationType.TOGGLED : Ability.ActivationType.INSTANT;
   }

   protected RabbitEscapeEntity summon(LivingEntity owner) {
      return new RabbitEscapeEntity(owner, this.isTamed(owner));
   }

   @Override
   public boolean canDie() {
      return true;
   }

   @Override
   public boolean isTenShadows() {
      return true;
   }

   @Override
   protected boolean canTame() {
      return true;
   }

   @Override
   public boolean canDisable() {
      return false;
   }

   @Override
   public int getCooldown() {
      return 600;
   }

   @Override
   public float getCost(LivingEntity owner) {
      float normalcost = 0.2F;
      return this.isTamed(owner) ? normalcost : 10.0F;
   }

   @Override
   public void onEnabled(LivingEntity owner) {
      if (!this.triggered) {
         this.triggered = true;
         JJKEffectUtil.addEffect(owner, new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, false, false));
      }

      super.onEnabled(owner);
   }

   @Override
   public void onDisabled(LivingEntity owner) {
      this.triggered = false;
      super.onDisabled(owner);
   }
}
