package radon.jujutsu_kaisen.ability.mimicry;

import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

public class Rika extends Summon<RikaEntity> {
   private static final float AMOUNT = 35.0F;
   private static final int INTERVAL = 100;

   public Rika() {
      super(RikaEntity.class);
   }

   @Override
   public boolean isScalable(LivingEntity owner) {
      return false;
   }

   @Override
   public boolean canDisable() {
      return false;
   }

   @Override
   public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
      return !owner.getCapability(SorcererDataHandler.INSTANCE).isPresent() ? false : target != null;
   }

   @Override
   public void run(LivingEntity owner) {
      if (owner.level().getGameTime() % 100L == 0L) {
         ISorcererData ownerCap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         RikaEntity rika = ownerCap.getSummonByClass(RikaEntity.class);
         if (rika != null) {
            ISorcererData summonCap = (ISorcererData)rika.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            if (summonCap.getEnergy() > 35.0F && ownerCap.getEnergy() < ownerCap.getMaxEnergy()) {
               ownerCap.addEnergy(35.0F);
               summonCap.useEnergy(35.0F);
               if (owner instanceof ServerPlayer player) {
                  PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(ownerCap.serializeNBT()), player);
               }
            }
         }
      }
   }

   @Override
   public List<EntityType<?>> getTypes() {
      return List.of((EntityType<?>)JJKEntities.RIKA.get());
   }

   @Override
   public boolean isTenShadows() {
      return false;
   }

   @Override
   public boolean usesHands() {
      return false;
   }

   @Override
   public float getCost(LivingEntity owner) {
      return 0.0F;
   }

   protected RikaEntity summon(LivingEntity owner) {
      return new RikaEntity(owner);
   }

   @Override
   public int getCooldown() {
      return 1200;
   }
}
