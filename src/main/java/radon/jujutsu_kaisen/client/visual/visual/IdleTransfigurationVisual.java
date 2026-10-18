package radon.jujutsu_kaisen.client.visual.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.client.particle.TravelParticle;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.client.visual.base.IVisual;
import radon.jujutsu_kaisen.util.HelperMethods;

public class IdleTransfigurationVisual implements IVisual {
   private static final float RADIUS = 1.5F;
   private static final float PARTICLE_SIZE = 0.3F;

   private static Vec3 rotateRoll(Vec3 pos, float roll) {
      float f = Mth.cos(roll);
      float f1 = Mth.sin(roll);
      double d0 = pos.x * f - pos.y * f1;
      double d1 = pos.y * f + pos.x * f1;
      double d2 = pos.z;
      return new Vec3(d0, d1, d2);
   }

   private static Vec3 transform3rdPerson(Vec3 pos, Vec3 angles, LivingEntity entity, HumanoidArm arm, float partialTicks) {
      return rotateRoll(pos, (float)angles.z)
         .xRot((float)(-angles.x))
         .yRot((float)(-angles.y))
         .add(0.0586F * (arm == HumanoidArm.RIGHT ? -6.0F : 6.0F), 1.3F - (entity.isShiftKeyDown() ? 0.3F : 0.0F), -0.05F)
         .yRot(-Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot) * (float) (Math.PI / 180.0))
         .add(
            Mth.lerp(partialTicks, entity.xOld, entity.getX()),
            Mth.lerp(partialTicks, entity.yOld, entity.getY()),
            Mth.lerp(partialTicks, entity.zOld, entity.getZ())
         );
   }

   @Override
   public boolean isValid(LivingEntity entity, ClientVisualHandler.ClientData data) {
      return data.toggled.contains(JJKAbilities.IDLE_TRANSFIGURATION.get());
   }

   @Override
   public void tick(LivingEntity entity, ClientVisualHandler.ClientData data) {
      Minecraft mc = Minecraft.getInstance();
      EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
      if (dispatcher.getRenderer(entity) instanceof LivingEntityRenderer<?, ?> living && living.getModel() instanceof HumanoidModel<?> humanoid) {
         Vec3 right = transform3rdPerson(
            new Vec3(0.0, -0.5499999940395355, 0.0),
            new Vec3(humanoid.rightArm.xRot, humanoid.rightArm.yRot, humanoid.rightArm.zRot),
            entity,
            HumanoidArm.RIGHT,
            mc.getPartialTick()
         );
         spawn(entity.level(), right, ParticleColors.getCursedEnergyColor(entity));
         Vec3 left = transform3rdPerson(
            new Vec3(0.0, -0.5499999940395355, 0.0),
            new Vec3(humanoid.leftArm.xRot, humanoid.leftArm.yRot, humanoid.leftArm.zRot),
            entity,
            HumanoidArm.LEFT,
            mc.getPartialTick()
         );
         spawn(entity.level(), left, ParticleColors.getCursedEnergyColor(entity));
      }
   }

   private static void spawn(Level level, Vec3 pos, Vector3f color) {
      int count = 9;

      for (int i = 0; i < count; i++) {
         double theta = HelperMethods.RANDOM.nextDouble() * Math.PI * 2.0;
         double phi = HelperMethods.RANDOM.nextDouble() * Math.PI;
         double xOffset = 1.5 * Math.sin(phi) * Math.cos(theta);
         double yOffset = 1.5 * Math.sin(phi) * Math.sin(theta);
         double zOffset = 1.5 * Math.cos(phi);
         double x = pos.x + xOffset * 0.15F;
         double y = pos.y + yOffset * 0.15F;
         double z = pos.z + zOffset * 0.15F;
         level.addParticle(new TravelParticle.TravelParticleOptions(pos.toVector3f(), color, 0.22500001F, 0.2F, true, true, 20), x, y, z, 0.0, 1.0, 0.0);
      }
   }
}
