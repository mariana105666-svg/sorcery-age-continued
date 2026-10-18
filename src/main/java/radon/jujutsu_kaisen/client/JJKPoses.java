package radon.jujutsu_kaisen.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class JJKPoses {
   private static final Map<ResourceLocation, JJKPose> REGISTRY = new HashMap<>();
   public static final JJKPose HOLLOW_WICKER_BASKET = register(
      new ResourceLocation("jujutsu_kaisen", "hollow_wicker_basket"),
      define(
         new PoseKeyframe(0.0F, 1.0F)
            .rotate(PoseLimb.RIGHT_ARM, 75.0F, 40.0F, -15.0F)
            .rotate(PoseLimb.LEFT_ARM, 75.0F, -40.0F, 15.0F)
            .easing(PoseEasing.EASE_OUT)
      ),
      10,
      5,
      false
   );
   public static final JJKPose MAHORAGA_WHEEL = register(
      new ResourceLocation("jujutsu_kaisen", "mahoraga_wheel"),
      define(
         new PoseKeyframe(0.0F, 0.5F)
            .rotate(PoseLimb.RIGHT_ARM, 75.0F, 40.0F, -15.0F)
            .rotate(PoseLimb.LEFT_ARM, 75.0F, -40.0F, 15.0F)
            .easing(PoseEasing.EASE_OUT),
         new PoseKeyframe(0.5F, 1.0F)
            .rotate(PoseLimb.RIGHT_ARM, 81.59F, -22.85F, -190.36F)
            .rotate(PoseLimb.LEFT_ARM, 69.96F, -37.6F, 14.64F)
            .position(PoseLimb.RIGHT_ARM, 0.0F, -2.0F, -3.0F)
            .position(PoseLimb.LEFT_ARM, 0.0F, 0.0F, -1.0F)
            .easing(PoseEasing.EASE_OUT)
      ),
      0,
      20,
      true
   );

   public static JJKPose register(ResourceLocation id, PoseDefinition definition, int priority, int duration, boolean autocancel) {
      JJKPose pose = new JJKPose(id, definition, priority, duration, autocancel);
      REGISTRY.put(id, pose);
      return pose;
   }

   public static JJKPose get(ResourceLocation id) {
      return REGISTRY.get(id);
   }

   public static Collection<JJKPose> values() {
      return REGISTRY.values();
   }

   private static PoseDefinition define(PoseKeyframe... frames) {
      return new PoseDefinition(List.of(frames));
   }

   public static float applyEasing(PoseEasing easing, float t) {
      if (easing == null) {
         easing = PoseEasing.LINEAR;
      }

      return Mth.clamp(easing.apply(Mth.clamp(t, 0.0F, 1.0F)), 0.0F, 1.0F);
   }

   private JJKPoses() {
   }
}
