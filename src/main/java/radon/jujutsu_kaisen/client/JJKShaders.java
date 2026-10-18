package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "jujutsu_kaisen",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class JJKShaders {
   private static ShaderInstance domainShader;
   private static ShaderInstance skyShader;
   private static ShaderInstance fakeSkyShader;

   @SubscribeEvent
   public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
      event.registerShader(
         new ShaderInstance(event.getResourceProvider(), new ResourceLocation("jujutsu_kaisen", "domain"), DefaultVertexFormat.POSITION_TEX),
         shader -> domainShader = shader
      );
      event.registerShader(
         new ShaderInstance(event.getResourceProvider(), new ResourceLocation("jujutsu_kaisen", "sky"), DefaultVertexFormat.POSITION_TEX),
         shader -> skyShader = shader
      );
      event.registerShader(
         new ShaderInstance(event.getResourceProvider(), new ResourceLocation("jujutsu_kaisen", "fake_sky"), DefaultVertexFormat.POSITION_TEX),
         shader -> fakeSkyShader = shader
      );
   }

   public static ShaderInstance getDomainShader() {
      return domainShader;
   }

   public static ShaderInstance getSkyShader() {
      return skyShader;
   }

   public static ShaderInstance getFakeSkyShader() {
      return fakeSkyShader;
   }
}
