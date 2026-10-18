package radon.jujutsu_kaisen.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class PoseState {
   public float x;
   public float y;
   public float z;
   public float xRot;
   public float yRot;
   public float zRot;
   public static final PoseState IDENTITY = new PoseState();

   public PoseState() {
   }

   public PoseState(float xRot, float yRot, float zRot, float x, float y, float z) {
      this.xRot = xRot;
      this.yRot = yRot;
      this.zRot = zRot;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public static PoseState capture(ModelPart part) {
      return new PoseState(part.xRot, part.yRot, part.zRot, part.x, part.y, part.z);
   }

   public void apply(ModelPart part) {
      part.xRot = this.xRot;
      part.yRot = this.yRot;
      part.zRot = this.zRot;
      part.x = this.x;
      part.y = this.y;
      part.z = this.z;
   }

   public static PoseState lerp(PoseState a, PoseState b, float t) {
      if (t <= 0.0F) {
         return a;
      } else {
         return t >= 1.0F
            ? b
            : new PoseState(
               Mth.lerp(t, a.xRot, b.xRot),
               Mth.lerp(t, a.yRot, b.yRot),
               Mth.lerp(t, a.zRot, b.zRot),
               Mth.lerp(t, a.x, b.x),
               Mth.lerp(t, a.y, b.y),
               Mth.lerp(t, a.z, b.z)
            );
      }
   }

   public void set(PoseState other) {
      this.xRot = other.xRot;
      this.yRot = other.yRot;
      this.zRot = other.zRot;
      this.x = other.x;
      this.y = other.y;
      this.z = other.z;
   }
}
