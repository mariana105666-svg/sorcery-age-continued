package radon.jujutsu_kaisen.client;

import java.util.ArrayList;
import java.util.List;

public final class PoseDefinition {
   private List<PoseKeyframe> keyframes = new ArrayList<>();

   public PoseDefinition(List<PoseKeyframe> keyframes) {
      this.keyframes = keyframes;
   }

   public PoseDefinition addKeyframe(PoseKeyframe frame) {
      this.keyframes.add(frame);
      return this;
   }

   public PoseKeyframe getKeyframeForProgress(float progress) {
      for (PoseKeyframe kf : this.keyframes) {
         if (progress >= kf.startProgress && progress <= kf.endProgress) {
            return kf;
         }
      }

      return this.keyframes.isEmpty() ? null : this.keyframes.get(this.keyframes.size() - 1);
   }

   public PoseKeyframe getPrevKeyframeForProgress(float progress) {
      PoseKeyframe prev = null;
      float bestEnd = -Float.MAX_VALUE;

      for (PoseKeyframe kf : this.keyframes) {
         if (kf.endProgress < progress && kf.endProgress > bestEnd) {
            bestEnd = kf.endProgress;
            prev = kf;
         }
      }

      return prev;
   }

   public boolean affects(PoseLimb limb) {
      for (PoseKeyframe frame : this.keyframes) {
         if (frame.affects(limb)) {
            return true;
         }
      }

      return false;
   }
}
