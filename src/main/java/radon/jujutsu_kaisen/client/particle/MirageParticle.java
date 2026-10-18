package radon.jujutsu_kaisen.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.client.FakeEntityRenderer;

public class MirageParticle<T extends MirageParticle.MirageParticleOptions> extends TextureSheetParticle {
   private final int entityId;
   @Nullable
   private Entity entity;
   @Nullable
   private FakeEntityRenderer renderer;

   protected MirageParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, T options) {
      super(pLevel, pX, pY, pZ);
      this.lifetime = 10;
      this.xd = pXSpeed;
      this.yd = pYSpeed;
      this.zd = pZSpeed;
      this.entityId = options.entityId();
   }

   public void tick() {
      super.tick();
      this.alpha = 1.0F - (float)this.age / this.lifetime;
      if (this.entity == null) {
         this.entity = this.level.getEntity(this.entityId);
         if (this.entity == null) {
            return;
         }

         this.renderer = new FakeEntityRenderer(this.entity);
      }
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.CUSTOM;
   }

   public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
      if (this.renderer != null) {
         this.renderer.setAlpha(this.alpha);
         this.renderer.render(this.getPos(), pPartialTicks);
      }
   }

   public record MirageParticleOptions(int entityId) implements ParticleOptions {
      public static Deserializer<MirageParticle.MirageParticleOptions> DESERIALIZER = new Deserializer<MirageParticle.MirageParticleOptions>() {
         @NotNull
         public MirageParticle.MirageParticleOptions fromCommand(@NotNull ParticleType<MirageParticle.MirageParticleOptions> type, @NotNull StringReader reader) throws CommandSyntaxException {
            return new MirageParticle.MirageParticleOptions(reader.readInt());
         }

         @NotNull
         public MirageParticle.MirageParticleOptions fromNetwork(@NotNull ParticleType<MirageParticle.MirageParticleOptions> type, @NotNull FriendlyByteBuf buf) {
            return new MirageParticle.MirageParticleOptions(buf.readInt());
         }
      };

      @NotNull
      public ParticleType<?> getType() {
         return (ParticleType<?>)JJKParticles.MIRAGE.get();
      }

      public void writeToNetwork(FriendlyByteBuf buf) {
         buf.writeInt(this.entityId);
      }

      @NotNull
      public String writeToString() {
         return String.format(Locale.ROOT, "%s %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.entityId);
      }
   }

   public static class Provider implements ParticleProvider<MirageParticle.MirageParticleOptions> {
      public Provider(SpriteSet ignored) {
      }

      public Particle createParticle(
         @NotNull MirageParticle.MirageParticleOptions pType,
         @NotNull ClientLevel pLevel,
         double pX,
         double pY,
         double pZ,
         double pXSpeed,
         double pYSpeed,
         double pZSpeed
      ) {
         return new MirageParticle<MirageParticle.MirageParticleOptions>(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType);
      }
   }
}
