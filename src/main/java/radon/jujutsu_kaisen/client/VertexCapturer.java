package radon.jujutsu_kaisen.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import radon.jujutsu_kaisen.client.slice.RigidBody;

public class VertexCapturer {
   public static boolean capture;
   public static List<VertexCapturer.Capture> captured = new ArrayList<>();

   public record Capture(RenderType type, List<RigidBody.Triangle[]> triangles) {
   }
}
