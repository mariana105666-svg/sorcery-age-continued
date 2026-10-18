package radon.jujutsu_kaisen.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
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
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class CursedEnergyParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected CursedEnergyParticle(
      ClientLevel pLevel,
      double pX,
      double pY,
      double pZ,
      double pXSpeed,
      double pYSpeed,
      double pZSpeed,
      CursedEnergyParticle.CursedEnergyParticleOptions options,
      SpriteSet pSprites
   ) {
      super(pLevel, pX, pY, pZ);
      this.quadSize = Math.max(options.scalar(), (this.random.nextFloat() - 0.5F) * options.scalar());
      this.lifetime = options.lifetime();
      this.xd = pXSpeed;
      this.yd = pYSpeed;
      this.zd = pZSpeed;
      Vector3f color = options.color();
      this.rCol = color.x;
      this.gCol = color.y;
      this.bCol = color.z;
      this.alpha = options.opacity();
      this.hasPhysics = false;
      this.sprites = pSprites;
      this.setSpriteFromAge(this.sprites);
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return JJKParticleRenderTypes.GLOW;
   }

   public record CursedEnergyParticleOptions(Vector3f color, float scalar, float opacity, int lifetime) implements ParticleOptions {
      public static Deserializer<CursedEnergyParticle.CursedEnergyParticleOptions> DESERIALIZER = new Deserializer<CursedEnergyParticle.CursedEnergyParticleOptions>() {
         @NotNull
         public CursedEnergyParticle.CursedEnergyParticleOptions fromCommand(
            @NotNull ParticleType<CursedEnergyParticle.CursedEnergyParticleOptions> type, @NotNull StringReader reader
         ) throws CommandSyntaxException {
            Vector3f color = CursedEnergyParticle.CursedEnergyParticleOptions.readColorVector3f(reader);
            reader.expect(' ');
            return new CursedEnergyParticle.CursedEnergyParticleOptions(color, reader.readFloat(), reader.readFloat(), reader.readInt());
         }

         @NotNull
         public CursedEnergyParticle.CursedEnergyParticleOptions fromNetwork(
            @NotNull ParticleType<CursedEnergyParticle.CursedEnergyParticleOptions> type, @NotNull FriendlyByteBuf buf
         ) {
            return new CursedEnergyParticle.CursedEnergyParticleOptions(
               CursedEnergyParticle.CursedEnergyParticleOptions.readColorFromNetwork(buf), buf.readFloat(), buf.readFloat(), buf.readInt()
            );
         }
      };

      @NotNull
      public ParticleType<?> getType() {
         return (ParticleType<?>)JJKParticles.CURSED_ENERGY.get();
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
         buf.writeFloat(this.opacity);
         buf.writeInt(this.lifetime);
      }

      @NotNull
      public String writeToString() {
         return String.format(
            Locale.ROOT,
            "%s %.2f %.2f %.2f %.2f %.2f %d",
            BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()),
            this.color.x,
            this.color.y,
            this.color.z,
            this.scalar,
            this.opacity,
            this.lifetime
         );
      }
   }

   public static class Provider implements ParticleProvider<CursedEnergyParticle.CursedEnergyParticleOptions> {
      private final SpriteSet sprites;

      public Provider(SpriteSet pSprites) {
         this.sprites = pSprites;
      }

      public Particle createParticle(
         @NotNull CursedEnergyParticle.CursedEnergyParticleOptions pType,
         @NotNull ClientLevel pLevel,
         double pX,
         double pY,
         double pZ,
         double pXSpeed,
         double pYSpeed,
         double pZSpeed
      ) {
         return new CursedEnergyParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType, this.sprites);
      }
   }
}
