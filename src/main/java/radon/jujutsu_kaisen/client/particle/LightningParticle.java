package radon.jujutsu_kaisen.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class LightningParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected LightningParticle(ClientLevel pLevel, double pX, double pY, double pZ, LightningParticle.LightningParticleOptions options, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ);
      Vector3f color = options.color();
      this.rCol = color.x;
      this.gCol = color.y;
      this.bCol = color.z;
      this.quadSize = Math.max(options.scalar(), (this.random.nextFloat() - 0.5F) * options.scalar());
      this.lifetime = options.lifetime();
      this.sprites = pSprites;
      this.setSprite(this.sprites.get(this.random));
   }

   public void tick() {
      super.tick();
      this.setSprite(this.sprites.get(this.random));
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return JJKParticleRenderTypes.ADDITIVE;
   }

   protected int getLightColor(float pPartialTick) {
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

   public record LightningParticleOptions(Vector3f color, float scalar, int lifetime) implements ParticleOptions {
      public static Deserializer<LightningParticle.LightningParticleOptions> DESERIALIZER = new Deserializer<LightningParticle.LightningParticleOptions>() {
         @NotNull
         public LightningParticle.LightningParticleOptions fromCommand(
            @NotNull ParticleType<LightningParticle.LightningParticleOptions> type, @NotNull StringReader reader
         ) throws CommandSyntaxException {
            Vector3f color = LightningParticle.LightningParticleOptions.readColorVector3f(reader);
            reader.expect(' ');
            return new LightningParticle.LightningParticleOptions(color, reader.readFloat(), reader.readInt());
         }

         @NotNull
         public LightningParticle.LightningParticleOptions fromNetwork(
            @NotNull ParticleType<LightningParticle.LightningParticleOptions> type, @NotNull FriendlyByteBuf buf
         ) {
            return new LightningParticle.LightningParticleOptions(
               LightningParticle.LightningParticleOptions.readColorFromNetwork(buf), buf.readFloat(), buf.readInt()
            );
         }
      };

      @NotNull
      public ParticleType<?> getType() {
         return (ParticleType<?>)JJKParticles.LIGHTNING.get();
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
         buf.writeInt(this.lifetime);
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
            this.scalar,
            this.lifetime
         );
      }
   }

   public static class Provider implements ParticleProvider<LightningParticle.LightningParticleOptions> {
      private final SpriteSet sprites;

      public Provider(SpriteSet pSpriteSet) {
         this.sprites = pSpriteSet;
      }

      public LightningParticle createParticle(
         @NotNull LightningParticle.LightningParticleOptions options,
         @NotNull ClientLevel level,
         double x,
         double y,
         double z,
         double xSpeed,
         double ySpeed,
         double zSpeed
      ) {
         return new LightningParticle(level, x, y, z, options, this.sprites);
      }
   }
}
