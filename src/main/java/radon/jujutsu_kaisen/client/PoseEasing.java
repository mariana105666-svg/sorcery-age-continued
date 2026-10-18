package radon.jujutsu_kaisen.client;

public enum PoseEasing {
   LINEAR {
      @Override
      public float apply(float t) {
         return t;
      }
   },
   EASE_IN {
      @Override
      public float apply(float t) {
         return t * t;
      }
   },
   EASE_OUT {
      @Override
      public float apply(float t) {
         return 1.0F - (1.0F - t) * (1.0F - t);
      }
   },
   EASE_IN_OUT {
      @Override
      public float apply(float t) {
         return t < 0.5F ? 2.0F * t * t : 1.0F - (float)Math.pow(-2.0F * t + 2.0F, 2.0) / 2.0F;
      }
   };

   public abstract float apply(float var1);
}
