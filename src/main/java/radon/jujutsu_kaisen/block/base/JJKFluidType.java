package radon.jujutsu_kaisen.block.base;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidType.Properties;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class JJKFluidType extends FluidType {
   private final int tintColor;

   public JJKFluidType(int tintColor, Properties properties) {
      super(properties);
      this.tintColor = tintColor;
   }

   public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
      consumer.accept(
         new IClientFluidTypeExtensions() {
            private static final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png");
            private static final ResourceLocation WATER_STILL = new ResourceLocation("block/water_still");
            private static final ResourceLocation WATER_FLOW = new ResourceLocation("block/water_flow");
            private static final ResourceLocation WATER_OVERLAY = new ResourceLocation("block/water_overlay");

            public ResourceLocation getStillTexture() {
               return WATER_STILL;
            }

            public ResourceLocation getFlowingTexture() {
               return WATER_FLOW;
            }

            @NotNull
            public ResourceLocation getOverlayTexture() {
               return WATER_OVERLAY;
            }

            public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
               return UNDERWATER_LOCATION;
            }

            public int getTintColor() {
               return JJKFluidType.this.tintColor;
            }

            @NotNull
            public Vector3f modifyFogColor(
               Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor
            ) {
               return Vec3.fromRGB24(JJKFluidType.this.tintColor).toVector3f();
            }

            public void modifyFogRender(
               Camera camera, FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape
            ) {
               RenderSystem.setShaderFogStart(-8.0F);
               RenderSystem.setShaderFogEnd(96.0F);
               RenderSystem.setShaderFogShape(FogShape.CYLINDER);
            }
         }
      );
   }
}
