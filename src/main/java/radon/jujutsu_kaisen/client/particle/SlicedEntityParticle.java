package radon.jujutsu_kaisen.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.client.FakeEntityRenderer;
import radon.jujutsu_kaisen.client.slice.CutModelUtil;
import radon.jujutsu_kaisen.client.slice.GJK;
import radon.jujutsu_kaisen.client.slice.RigidBody;

public class SlicedEntityParticle extends TextureSheetParticle {
   private static final List<SlicedEntityParticle> ALL = new ArrayList<>();
   private final int entityId;
   private final Vector3f plane;
   private final float distance;
   @Nullable
   private LivingEntity entity;
   @Nullable
   private FakeEntityRenderer renderer;
   private final List<RigidBody> parts = new ArrayList<>();

   protected SlicedEntityParticle(ClientLevel pLevel, double pX, double pY, double pZ, SlicedEntityParticle.SliceParticleOptions options) {
      super(pLevel, pX, pY, pZ);
      this.entityId = options.entityId;
      this.plane = options.plane;
      this.distance = options.distance;
      this.lifetime = 1200;
      ALL.add(this);
   }

   public void tick() {
      super.tick();
      if (this.entity == null && this.level.getEntity(this.entityId) instanceof LivingEntity living) {
         this.entity = living;
         this.renderer = new FakeEntityRenderer(this.entity);
      }

      if (!this.isAlive()) {
         ALL.remove(this);
      }

      for (RigidBody part : this.parts) {
         part.tick();
      }
   }

   public static List<SlicedEntityParticle> getAll() {
      return ALL;
   }

   private static void generateChunks(List<List<RigidBody.CutModelData>> chunks, List<RigidBody.CutModelData> toSort) {
      GJK.margin = 0.01F;
      List<RigidBody.CutModelData> chunk = new ArrayList<>();

      while (!toSort.isEmpty()) {
         boolean removed = false;
         List<RigidBody.CutModelData> toAdd = new ArrayList<>();

         for (RigidBody.CutModelData a : chunk) {
            Iterator<RigidBody.CutModelData> iter = toSort.iterator();

            while (iter.hasNext()) {
               RigidBody.CutModelData b = iter.next();
               if (b.collider.localBox.inflate(0.01F).intersects(a.collider.localBox) && GJK.collidesAny(null, null, a.collider, b.collider)) {
                  removed = true;
                  toAdd.add(b);
                  iter.remove();
               }
            }
         }

         chunk.addAll(toAdd);
         if (!removed) {
            if (!chunk.isEmpty()) {
               chunks.add(chunk);
               chunk = new ArrayList<>();
            }

            chunk.add(toSort.remove(0));
         }
      }

      if (!chunk.isEmpty()) {
         chunks.add(chunk);
      }

      GJK.margin = 0.0F;
   }

   public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
   }

   public void actuallyRender(PoseStack poseStack, float partialTicks) {
      if (this.entity != null && this.renderer != null) {
         if (this.parts.isEmpty()) {
            this.renderer.setup(() -> {
               List<RigidBody.CutModelData> top = new ArrayList<>();
               List<RigidBody.CutModelData> bottom = new ArrayList<>();
               CutModelUtil.collect(this.renderer, this.plane, this.distance, partialTicks, top, bottom);
               List<List<RigidBody.CutModelData>> chunks = new ArrayList<>();
               generateChunks(chunks, top);
               generateChunks(chunks, bottom);
               double d0 = Mth.lerp(partialTicks, this.xo, this.x);
               double d1 = Mth.lerp(partialTicks, this.yo, this.y);
               double d2 = Mth.lerp(partialTicks, this.zo, this.z);

               for (List<RigidBody.CutModelData> chunk : chunks) {
                  RigidBody partxx = new RigidBody(this.level, d0, d1, d2);
                  partxx.addChunk(chunk);
                  float direction = chunk.get(0).flip ? -1.0F : 1.0F;
                  partxx.impulseVelocityDirect(new Vec3(this.plane.x * direction, this.plane.y * direction, this.plane.z * direction), partxx.globalCentroid);
                  this.parts.add(partxx);
               }

               for (RigidBody partx : this.parts) {
                  partx.addParts(this.parts);
               }
            });
         }

         if (!this.parts.isEmpty()) {
            Minecraft mc = Minecraft.getInstance();
            EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
            int packedLight = dispatcher.getPackedLightCoords(this.entity, partialTicks);

            for (RigidBody part : this.parts) {
               part.render(poseStack, packedLight, partialTicks);
            }
         }
      }
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.CUSTOM;
   }

   public static class Provider implements ParticleProvider<SlicedEntityParticle.SliceParticleOptions> {
      public Provider(SpriteSet ignored) {
      }

      public Particle createParticle(
         @NotNull SlicedEntityParticle.SliceParticleOptions pType,
         @NotNull ClientLevel pLevel,
         double pX,
         double pY,
         double pZ,
         double pXSpeed,
         double pYSpeed,
         double pZSpeed
      ) {
         return new SlicedEntityParticle(pLevel, pX, pY, pZ, pType);
      }
   }

   public record SliceParticleOptions(int entityId, Vector3f plane, float distance) implements ParticleOptions {
      public static final Deserializer<SlicedEntityParticle.SliceParticleOptions> DESERIALIZER = new Deserializer<SlicedEntityParticle.SliceParticleOptions>() {
         @NotNull
         public SlicedEntityParticle.SliceParticleOptions fromCommand(
            @NotNull ParticleType<SlicedEntityParticle.SliceParticleOptions> type, @NotNull StringReader reader
         ) throws CommandSyntaxException {
            int entityId = SlicedEntityParticle.SliceParticleOptions.readEntityId(reader);
            reader.expect(' ');
            Vector3f plane = SlicedEntityParticle.SliceParticleOptions.readVector3f(reader);
            reader.expect(' ');
            float distance = reader.readFloat();
            return new SlicedEntityParticle.SliceParticleOptions(entityId, plane, distance);
         }

         @NotNull
         public SlicedEntityParticle.SliceParticleOptions fromNetwork(
            @NotNull ParticleType<SlicedEntityParticle.SliceParticleOptions> type, @NotNull FriendlyByteBuf buf
         ) {
            int entityId = buf.readInt();
            Vector3f plane = SlicedEntityParticle.SliceParticleOptions.readVector3fFromNetwork(buf);
            float distance = buf.readFloat();
            return new SlicedEntityParticle.SliceParticleOptions(entityId, plane, distance);
         }
      };

      public static int readEntityId(StringReader reader) throws CommandSyntaxException {
         reader.expect(' ');
         return reader.readInt();
      }

      public static Vector3f readVector3f(StringReader reader) throws CommandSyntaxException {
         reader.expect(' ');
         float x = reader.readFloat();
         reader.expect(' ');
         float y = reader.readFloat();
         reader.expect(' ');
         float z = reader.readFloat();
         return new Vector3f(x, y, z);
      }

      public static Vector3f readVector3fFromNetwork(FriendlyByteBuf buf) {
         return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
      }

      public void writeToNetwork(@NotNull FriendlyByteBuf buf) {
         buf.writeInt(this.entityId);
         buf.writeFloat(this.plane.x);
         buf.writeFloat(this.plane.y);
         buf.writeFloat(this.plane.z);
         buf.writeFloat(this.distance);
      }

      @NotNull
      public String writeToString() {
         return String.format(
            Locale.ROOT,
            "%s %d %.2f %.2f %.2f %.2f",
            BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
            this.entityId,
            this.plane.x,
            this.plane.y,
            this.plane.z,
            this.distance
         );
      }

      @NotNull
      public ParticleType<?> getType() {
         return (ParticleType<?>)JJKParticles.SLICE.get();
      }
   }
}
