package radon.jujutsu_kaisen.client.visual;

import java.util.ArrayList;
import java.util.List;
import radon.jujutsu_kaisen.client.visual.base.IVisual;
import radon.jujutsu_kaisen.client.visual.visual.BlueFistsVisual;
import radon.jujutsu_kaisen.client.visual.visual.CursedEnergyVisual;
import radon.jujutsu_kaisen.client.visual.visual.IdleTransfigurationVisual;
import radon.jujutsu_kaisen.client.visual.visual.MythicalBeastAmberVisual;
import radon.jujutsu_kaisen.client.visual.visual.PerfectBodyVisual;
import radon.jujutsu_kaisen.client.visual.visual.TransfiguredSoulVisual;

public class JJKVisuals {
   public static List<IVisual> VISUALS = new ArrayList<>();

   static {
      VISUALS.add(new CursedEnergyVisual());
      VISUALS.add(new BlueFistsVisual());
      VISUALS.add(new MythicalBeastAmberVisual());
      VISUALS.add(new IdleTransfigurationVisual());
      VISUALS.add(new TransfiguredSoulVisual());
      VISUALS.add(new PerfectBodyVisual());
   }
}
