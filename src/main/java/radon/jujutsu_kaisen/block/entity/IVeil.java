package radon.jujutsu_kaisen.block.entity;

import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.entity.VeilEntity;
import radon.jujutsu_kaisen.item.veil.modifier.Modifier;

public interface IVeil extends IBarrier {
   List<Modifier> getModifiers();

   VeilEntity getVeil();

   int getRadius();

   LivingEntity getOwner();
}
