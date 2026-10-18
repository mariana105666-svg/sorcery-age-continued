package radon.jujutsu_kaisen.ability.barrier;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Configuration;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;

public class ShellBalanceConfig extends Configuration {
   @Override
   public boolean isDisplayed(LivingEntity owner) {
      return this.isParentUnlocked(owner);
   }

   @Override
   public int getPointsCost() {
      return 100;
   }

   @Nullable
   @Override
   public Ability getParent(LivingEntity owner) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      CursedTechnique technique = cap.getTechnique();
      return technique != null && technique.getDomain() != null ? technique.getDomain() : null;
   }

   @Override
   public Vec2 getDisplayCoordinates() {
      return new Vec2(4.0F, -1.0F);
   }

   @Override
   public MenuType getMenuType() {
      return MenuType.NONE;
   }
}
