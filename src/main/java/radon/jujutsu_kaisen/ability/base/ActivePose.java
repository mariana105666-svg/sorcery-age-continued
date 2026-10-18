package radon.jujutsu_kaisen.ability.base;

import net.minecraft.world.InteractionHand;
import radon.jujutsu_kaisen.client.JJKPose;

public class ActivePose {
   public final JJKPose pose;
   public final InteractionHand hand;
   public int ticksLeft;
   public int ghostTicks;

   public ActivePose(JJKPose pose, InteractionHand hand, int duration) {
      this.pose = pose;
      this.hand = hand;
      this.ticksLeft = duration;
      this.ghostTicks = -1;
   }

   public ActivePose(JJKPose pose, InteractionHand hand, int duration, int ghostTicks) {
      this.pose = pose;
      this.hand = hand;
      this.ticksLeft = duration;
      this.ghostTicks = ghostTicks;
   }

   public int getTicksLeft() {
      return this.ticksLeft;
   }

   public boolean tick() {
      if (this.ticksLeft < 0) {
         return true;
      } else {
         if (this.ticksLeft > 0) {
            this.ticksLeft--;
         } else if (this.ticksLeft == 0) {
            if (this.ghostTicks == -1 && this.pose.autocancel()) {
               this.ghostTicks = this.pose.defaultDuration();
            } else if (this.ghostTicks > 0) {
               this.ghostTicks--;
            }
         }

         return this.ticksLeft > 0 || this.ghostTicks != 0;
      }
   }
}
