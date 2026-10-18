package radon.jujutsu_kaisen.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.HugeExplosionParticle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class CursedSpeechParticle extends HugeExplosionParticle {
   protected CursedSpeechParticle(ClientLevel level, double x, double y, double z, CursedSpeechParticle.CursedSpeechParticleOptions options, SpriteSet sprites) {
      super(level, x, y, z, 1.0, sprites);
      this.lifetime = 16;
      this.quadSize = options.scalar();
      Vector3f color = options.color();
      this.rCol = color.x;
      this.gCol = color.y;
      this.bCol = color.z;
      this.setSpriteFromAge(sprites);
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return JJKParticleRenderTypes.ADDITIVE;
   }

   public int getLightColor(float pPartialTick) {
      float f = (this.age + pPartialTick) / this.lifetime;
      f = Mth.clamp(f, 0.0F, 1.0F);
      int i = super.getLightColor(pPartialTick);
      int j = i & 0xFF;
      int k = i >> 16 & 0xFF;
      j += (int)(f * 15.0F * 16.0F);
      if (j > 240) {
         j = 240;
      }

      return j | k << 16;
   }

   public record CursedSpeechParticleOptions(Vector3f color, float scalar) implements ParticleOptions {
      public static final Deserializer<CursedSpeechParticle.CursedSpeechParticleOptions> DESERIALIZER = new Deserializer<CursedSpeechParticle.CursedSpeechParticleOptions>() {
         @NotNull
         public CursedSpeechParticle.CursedSpeechParticleOptions fromCommand(
            @NotNull ParticleType<CursedSpeechParticle.CursedSpeechParticleOptions> type, @NotNull StringReader reader
         ) throws CommandSyntaxException {
            Vector3f color = CursedSpeechParticle.CursedSpeechParticleOptions.readColorVector3f(reader);
            reader.expect(' ');
            return new CursedSpeechParticle.CursedSpeechParticleOptions(color, reader.readFloat());
         }

         @NotNull
         public CursedSpeechParticle.CursedSpeechParticleOptions fromNetwork(
            @NotNull ParticleType<CursedSpeechParticle.CursedSpeechParticleOptions> type, @NotNull FriendlyByteBuf buf
         ) {
            return new CursedSpeechParticle.CursedSpeechParticleOptions(
               CursedSpeechParticle.CursedSpeechParticleOptions.readColorFromNetwork(buf), buf.readFloat()
            );
         }
      };

      @NotNull
      public ParticleType<?> getType() {
         return (ParticleType<?>)JJKParticles.CURSED_SPEECH.get();
      }

      public static Vector3f readColorVector3f(StringReader reader) throws CommandSyntaxException {
         reader.expect(' ');
         float f0 = reader.readFloat();
         reader.expect(' ');
         float f1 = reader.readFloat();
         reader.expect(' ');
         float f2 = reader.readFloat();
         return new Vector3f(f0, f1, f2);
      }

      public static Vector3f readColorFromNetwork(FriendlyByteBuf buf) {
         return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
      }

      public void writeToNetwork(FriendlyByteBuf buf) {
         buf.writeFloat(this.color.x);
         buf.writeFloat(this.color.y);
         buf.writeFloat(this.color.z);
         buf.writeFloat(this.scalar);
      }

      @NotNull
      public String writeToString() {
         return String.format(
            Locale.ROOT,
            "%s %.2f %.2f %.2f %.2f %d",
            BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
            this.color.x,
            this.color.y,
            this.color.z,
            this.scalar
         );
      }
   }

   public static class Provider implements ParticleProvider<CursedSpeechParticle.CursedSpeechParticleOptions> {
      private final SpriteSet sprites;

      public Provider(SpriteSet pSprites) {
         this.sprites = pSprites;
      }

      public CursedSpeechParticle createParticle(
         @NotNull CursedSpeechParticle.CursedSpeechParticleOptions options,
         @NotNull ClientLevel level,
         double x,
         double y,
         double z,
         double xSpeed,
         double ySpeed,
         double zSpeed
      ) {
         return new CursedSpeechParticle(level, x, y, z, options, this.sprites);
      }
   }
}
