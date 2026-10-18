package radon.jujutsu_kaisen.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class BetterSmokeParticle extends SmokeParticle {
   protected BetterSmokeParticle(
      ClientLevel pLevel,
      double pX,
      double pY,
      double pZ,
      double pXSpeed,
      double pYSpeed,
      double pZSpeed,
      BetterSmokeParticle.BetterSmokeParticleOptions options,
      SpriteSet pSprites
   ) {
      super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0, options.scalar, pSprites);
      this.gravity = 0.0F;
      this.friction = 0.98F;
      this.quadSize = Math.max(options.scalar(), (this.random.nextFloat() - 0.5F) * options.scalar());
      this.lifetime = options.lifetime();
      this.xd = pXSpeed;
      this.yd = pYSpeed;
      this.zd = pZSpeed;
      this.hasPhysics = false;
      this.speedUpWhenYMotionIsBlocked = false;
   }

   public record BetterSmokeParticleOptions(float scalar, int lifetime) implements ParticleOptions {
      public static Deserializer<BetterSmokeParticle.BetterSmokeParticleOptions> DESERIALIZER = new Deserializer<BetterSmokeParticle.BetterSmokeParticleOptions>() {
         @NotNull
         public BetterSmokeParticle.BetterSmokeParticleOptions fromCommand(
            @NotNull ParticleType<BetterSmokeParticle.BetterSmokeParticleOptions> type, @NotNull StringReader reader
         ) throws CommandSyntaxException {
            return new BetterSmokeParticle.BetterSmokeParticleOptions(reader.readFloat(), reader.readInt());
         }

         @NotNull
         public BetterSmokeParticle.BetterSmokeParticleOptions fromNetwork(
            @NotNull ParticleType<BetterSmokeParticle.BetterSmokeParticleOptions> type, @NotNull FriendlyByteBuf buf
         ) {
            return new BetterSmokeParticle.BetterSmokeParticleOptions(buf.readFloat(), buf.readInt());
         }
      };

      @NotNull
      public ParticleType<?> getType() {
         return (ParticleType<?>)JJKParticles.SMOKE.get();
      }

      public void writeToNetwork(FriendlyByteBuf buf) {
         buf.writeFloat(this.scalar);
         buf.writeInt(this.lifetime);
      }

      @NotNull
      public String writeToString() {
         return String.format(Locale.ROOT, "%s %.2f %d", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.scalar, this.lifetime);
      }
   }

   public static class Provider implements ParticleProvider<BetterSmokeParticle.BetterSmokeParticleOptions> {
      private final SpriteSet sprites;

      public Provider(SpriteSet pSpriteSet) {
         this.sprites = pSpriteSet;
      }

      public BetterSmokeParticle createParticle(
         @NotNull BetterSmokeParticle.BetterSmokeParticleOptions options,
         @NotNull ClientLevel level,
         double x,
         double y,
         double z,
         double xSpeed,
         double ySpeed,
         double zSpeed
      ) {
         return new BetterSmokeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, options, this.sprites);
      }
   }
}
