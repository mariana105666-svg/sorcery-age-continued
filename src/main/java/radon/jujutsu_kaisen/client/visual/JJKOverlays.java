package radon.jujutsu_kaisen.client.visual;

import java.util.ArrayList;
import java.util.List;
import radon.jujutsu_kaisen.client.visual.base.IOverlay;
import radon.jujutsu_kaisen.client.visual.overlay.BrainTransplantOverlay;
import radon.jujutsu_kaisen.client.visual.overlay.CursedSpeechOverlay;
import radon.jujutsu_kaisen.client.visual.overlay.PerfectBodyOverlay;
import radon.jujutsu_kaisen.client.visual.overlay.SimurianOverlay;
import radon.jujutsu_kaisen.client.visual.overlay.SixEyesOverlay;

public class JJKOverlays {
   public static List<IOverlay> OVERLAYS = new ArrayList<>();

   static {
      OVERLAYS.add(new SixEyesOverlay());
      OVERLAYS.add(new CursedSpeechOverlay());
      OVERLAYS.add(new BrainTransplantOverlay());
      OVERLAYS.add(new PerfectBodyOverlay());
      OVERLAYS.add(new SimurianOverlay());
   }
}
