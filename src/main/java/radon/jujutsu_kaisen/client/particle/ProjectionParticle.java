package radon.jujutsu_kaisen.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.client.MixinData;

public class ProjectionParticle<T extends ProjectionParticle.ProjectionParticleOptions> extends TextureSheetParticle {
   private final int entityId;
   private final float yaw;
   @Nullable
   private LivingEntity entity;
   private float position;
   private float speed;

   protected ProjectionParticle(ClientLevel pLevel, double pX, double pY, double pZ, T options) {
      super(pLevel, pX, pY, pZ);
      this.alpha = 0.5F;
      this.entityId = options.entityId();
      this.yaw = options.yaw();
   }

   public void tick() {
      if (this.entity == null) {
         if (!(this.level.getEntity(this.entityId) instanceof LivingEntity living)) {
            return;
         }

         this.entity = living;
         this.position = living.walkAnimation.position();
         this.speed = living.walkAnimation.speed();
      } else if (!JJKAbilities.isChanneling(this.entity, (Ability)JJKAbilities.PROJECTION_SORCERY.get())) {
         this.remove();
      }
   }

   @NotNull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.CUSTOM;
   }

   public void render(@NotNull VertexConsumer pBuffer, @NotNull Camera pRenderInfo, float pPartialTicks) {
      if (this.entity != null) {
         PoseStack stack = new PoseStack();
         float yRot = this.entity.getYRot();
         float yRotO = this.entity.yRotO;
         float yHeadRot = this.entity.yHeadRot;
         float yHeadRotO = this.entity.yHeadRotO;
         float yBodyRot = this.entity.yBodyRot;
         float yBodyRotO = this.entity.yBodyRotO;
         boolean invisible = this.entity.isInvisible();
         this.entity.setInvisible(false);
         MixinData.isCustomWalkAnimation = true;
         MixinData.walkAnimationPosition = this.position;
         MixinData.walkAnimationSpeed = this.speed;
         this.entity.setYRot(this.yaw);
         this.entity.yRotO = this.yaw;
         this.entity.yHeadRot = this.yaw;
         this.entity.yHeadRotO = this.yaw;
         this.entity.yBodyRot = this.yaw;
         this.entity.yBodyRotO = this.yaw;
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
         EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
         EntityRenderer<? super Entity> renderer = manager.getRenderer(this.entity);
         BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
         Vec3 offset = renderer.getRenderOffset(this.entity, pPartialTicks);
         stack.translate(
            this.x - pRenderInfo.getPosition().x + offset.x,
            this.y - pRenderInfo.getPosition().y + offset.y,
            this.z - pRenderInfo.getPosition().z + offset.z
         );
         renderer.render(this.entity, 0.0F, pPartialTicks, stack, buffer, manager.getPackedLightCoords(this.entity, pPartialTicks));
         buffer.getBuffer(RenderType.translucent());
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         this.entity.yBodyRotO = yBodyRotO;
         this.entity.yBodyRot = yBodyRot;
         this.entity.yHeadRotO = yHeadRotO;
         this.entity.yHeadRot = yHeadRot;
         this.entity.yRotO = yRotO;
         this.entity.setYRot(yRot);
         this.entity.setInvisible(invisible);
         MixinData.isCustomWalkAnimation = false;
         buffer.endBatch();
      }
   }

   public record ProjectionParticleOptions(int entityId, float yaw) implements ParticleOptions {
      public static Deserializer<ProjectionParticle.ProjectionParticleOptions> DESERIALIZER = new Deserializer<ProjectionParticle.ProjectionParticleOptions>() {
         @NotNull
         public ProjectionParticle.ProjectionParticleOptions fromCommand(
            @NotNull ParticleType<ProjectionParticle.ProjectionParticleOptions> type, @NotNull StringReader reader
         ) throws CommandSyntaxException {
            return new ProjectionParticle.ProjectionParticleOptions(reader.readInt(), reader.readFloat());
         }

         @NotNull
         public ProjectionParticle.ProjectionParticleOptions fromNetwork(
            @NotNull ParticleType<ProjectionParticle.ProjectionParticleOptions> type, @NotNull FriendlyByteBuf buf
         ) {
            return new ProjectionParticle.ProjectionParticleOptions(buf.readInt(), buf.readFloat());
         }
      };

      @NotNull
      public ParticleType<?> getType() {
         return (ParticleType<?>)JJKParticles.PROJECTION.get();
      }

      public void writeToNetwork(FriendlyByteBuf buf) {
         buf.writeInt(this.entityId);
         buf.writeFloat(this.yaw);
      }

      @NotNull
      public String writeToString() {
         return String.format(Locale.ROOT, "%s %d %f %f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.entityId, this.yaw);
      }
   }

   public static class Provider implements ParticleProvider<ProjectionParticle.ProjectionParticleOptions> {
      public Provider(SpriteSet ignored) {
      }

      public Particle createParticle(
         @NotNull ProjectionParticle.ProjectionParticleOptions pType,
         @NotNull ClientLevel pLevel,
         double pX,
         double pY,
         double pZ,
         double pXSpeed,
         double pYSpeed,
         double pZSpeed
      ) {
         return new ProjectionParticle<ProjectionParticle.ProjectionParticleOptions>(pLevel, pX, pY, pZ, pType);
      }
   }
}
