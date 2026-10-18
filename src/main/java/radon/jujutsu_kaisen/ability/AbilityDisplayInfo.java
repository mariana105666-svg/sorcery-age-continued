package radon.jujutsu_kaisen.ability;

import net.minecraft.resources.ResourceLocation;

public class AbilityDisplayInfo {
   private final ResourceLocation icon;
   private final float x;
   private final float y;

   public AbilityDisplayInfo(String name, float x, float y) {
      this.icon = new ResourceLocation("jujutsu_kaisen", String.format("textures/ability/%s.png", name));
      this.x = x;
      this.y = y;
   }

   public ResourceLocation getIcon() {
      return this.icon;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }
}
