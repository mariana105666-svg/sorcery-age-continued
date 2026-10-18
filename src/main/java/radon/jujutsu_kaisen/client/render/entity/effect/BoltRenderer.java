package radon.jujutsu_kaisen.client.render.entity.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;
import radon.jujutsu_kaisen.client.JJKRenderTypes;

public class BoltRenderer {
   private static final float REFRESH_TIME = 3.0F;
   private static final double MAX_OWNER_TRACK_TIME = 100.0;
   private BoltRenderer.Timestamp refreshTimestamp = new BoltRenderer.Timestamp();
   private final Random random = new Random();
   private final Minecraft minecraft = Minecraft.getInstance();
   private final Map<Object, BoltRenderer.BoltOwnerData> boltOwners = new Object2ObjectOpenHashMap();

   public void render(float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn) {
      VertexConsumer buffer = bufferIn.getBuffer(JJKRenderTypes.lightning());
      Matrix4f matrix = matrixStackIn.last().pose();
      BoltRenderer.Timestamp timestamp = new BoltRenderer.Timestamp(this.minecraft.level.getGameTime(), partialTicks);
      boolean refresh = timestamp.isPassed(this.refreshTimestamp, 0.33333334F);
      if (refresh) {
         this.refreshTimestamp = timestamp;
      }

      Iterator<Entry<Object, BoltRenderer.BoltOwnerData>> iter = this.boltOwners.entrySet().iterator();

      while (iter.hasNext()) {
         Entry<Object, BoltRenderer.BoltOwnerData> entry = iter.next();
         BoltRenderer.BoltOwnerData data = entry.getValue();
         if (refresh) {
            data.bolts.removeIf(bolt -> bolt.tick(timestamp));
         }

         if (data.bolts.isEmpty() && data.lastBolt != null && data.lastBolt.getSpawnFunction().isConsecutive()) {
            data.addBolt(new BoltRenderer.BoltInstance(data.lastBolt, timestamp), timestamp);
         }

         data.bolts.forEach(bolt -> bolt.render(matrix, buffer, timestamp));
         if (data.bolts.isEmpty() && timestamp.isPassed(data.lastUpdateTimestamp, 100.0)) {
            iter.remove();
         }
      }
   }

   public void update(Object owner, BoltEffect newBoltData, float partialTicks) {
      if (this.minecraft.level != null) {
         BoltRenderer.BoltOwnerData data = this.boltOwners.computeIfAbsent(owner, o -> new BoltRenderer.BoltOwnerData());
         data.lastBolt = newBoltData;
         BoltRenderer.Timestamp timestamp = new BoltRenderer.Timestamp(this.minecraft.level.getGameTime(), partialTicks);
         if ((!data.lastBolt.getSpawnFunction().isConsecutive() || data.bolts.isEmpty()) && timestamp.isPassed(data.lastBoltTimestamp, data.lastBoltDelay)) {
            data.addBolt(new BoltRenderer.BoltInstance(newBoltData, timestamp), timestamp);
         }

         data.lastUpdateTimestamp = timestamp;
      }
   }

   public class BoltInstance {
      private final BoltEffect bolt;
      private final List<BoltEffect.BoltQuads> renderQuads;
      private final BoltRenderer.Timestamp createdTimestamp;

      public BoltInstance(BoltEffect bolt, BoltRenderer.Timestamp timestamp) {
         this.bolt = bolt;
         this.renderQuads = bolt.generate();
         this.createdTimestamp = timestamp;
      }

      public void render(Matrix4f matrix, VertexConsumer buffer, BoltRenderer.Timestamp timestamp) {
         float lifeScale = timestamp.subtract(this.createdTimestamp).value() / this.bolt.getLifespan();
         Pair<Integer, Integer> bounds = this.bolt.getFadeFunction().getRenderBounds(this.renderQuads.size(), lifeScale);

         for (int i = (Integer)bounds.getLeft(); i < bounds.getRight(); i++) {
            this.renderQuads
               .get(i)
               .getVecs()
               .forEach(
                  v -> buffer.vertex(matrix, (float)v.x, (float)v.y, (float)v.z)
                     .color(this.bolt.getColor().x, this.bolt.getColor().y, this.bolt.getColor().z, this.bolt.getColor().w())
                     .endVertex()
               );
         }
      }

      public boolean tick(BoltRenderer.Timestamp timestamp) {
         return timestamp.isPassed(this.createdTimestamp, this.bolt.getLifespan());
      }
   }

   public class BoltOwnerData {
      private final Set<BoltRenderer.BoltInstance> bolts = new ObjectOpenHashSet();
      private BoltEffect lastBolt;
      private BoltRenderer.Timestamp lastBoltTimestamp = BoltRenderer.this.new Timestamp();
      private BoltRenderer.Timestamp lastUpdateTimestamp = BoltRenderer.this.new Timestamp();
      private double lastBoltDelay;

      private void addBolt(BoltRenderer.BoltInstance instance, BoltRenderer.Timestamp timestamp) {
         this.bolts.add(instance);
         this.lastBoltDelay = instance.bolt.getSpawnFunction().getSpawnDelay(BoltRenderer.this.random);
         this.lastBoltTimestamp = timestamp;
      }
   }

   public class Timestamp {
      private final long ticks;
      private final float partial;

      public Timestamp() {
         this(0L, 0.0F);
      }

      public Timestamp(long ticks, float partial) {
         this.ticks = ticks;
         this.partial = partial;
      }

      public BoltRenderer.Timestamp subtract(BoltRenderer.Timestamp other) {
         long newTicks = this.ticks - other.ticks;
         float newPartial = this.partial - other.partial;
         if (newPartial < 0.0F) {
            newPartial++;
            newTicks--;
         }

         return BoltRenderer.this.new Timestamp(newTicks, newPartial);
      }

      public float value() {
         return (float)this.ticks + this.partial;
      }

      public boolean isPassed(BoltRenderer.Timestamp prev, double duration) {
         long ticksPassed = this.ticks - prev.ticks;
         if (ticksPassed > duration) {
            return true;
         } else {
            duration -= ticksPassed;
            return duration >= 1.0 ? false : this.partial - prev.partial >= duration;
         }
      }
   }
}
