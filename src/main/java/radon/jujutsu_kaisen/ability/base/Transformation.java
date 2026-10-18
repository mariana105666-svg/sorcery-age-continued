package radon.jujutsu_kaisen.ability.base;

import java.util.ArrayList;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

public abstract class Transformation extends Ability implements Ability.IToggled, ITransformation {
   @Override
   public Ability.Status isTriggerable(LivingEntity owner) {
      if ((this.getBodyPart() != ITransformation.Part.RIGHT_ARM || owner.getMainHandItem().isEmpty())
         && (this.getBodyPart() != ITransformation.Part.LEFT_ARM || owner.getOffhandItem().isEmpty())) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

         for (Ability ability : new ArrayList<>(cap.getToggled())) {
            if (ability instanceof ITransformation transformation && ability != this && transformation.getBodyPart() == this.getBodyPart()) {
               cap.toggle(ability);
            }
         }

         return super.isTriggerable(owner);
      } else {
         return Ability.Status.FAILURE;
      }
   }

   @Override
   public Ability.Status isStillUsable(LivingEntity owner) {
      return (this.getBodyPart() != ITransformation.Part.RIGHT_ARM || owner.getMainHandItem().isEmpty())
            && (this.getBodyPart() != ITransformation.Part.LEFT_ARM || owner.getOffhandItem().isEmpty())
         ? super.isStillUsable(owner)
         : Ability.Status.FAILURE;
   }
}
