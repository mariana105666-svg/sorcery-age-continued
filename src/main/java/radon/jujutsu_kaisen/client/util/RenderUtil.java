package radon.jujutsu_kaisen.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.BufferBuilder.RenderedBuffer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class RenderUtil {
   public static void drawWithShader(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, RenderedBuffer rendered) {
      VertexBuffer vertex = rendered.drawState().format().getImmediateDrawVertexBuffer();
      vertex.bind();
      vertex.upload(rendered);
      vertex.drawWithShader(modelViewMatrix, projectionMatrix, RenderSystem.getShader());
   }

   public static void renderEntityInInventoryFollowsAngle(
      GuiGraphics graphics, int x, int y, int scale, float yOffset, float angleXComponent, float angleYComponent, LivingEntity entity
   ) {
      Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
      Quaternionf cam = new Quaternionf().rotateX(angleYComponent * 20.0F * (float) (Math.PI / 180.0));
      pose.mul(cam);
      float yBodyRot = entity.yBodyRot;
      float yRot = entity.getYRot();
      float xRot = entity.getXRot();
      float yHeadRotO = entity.yHeadRotO;
      float yHeadRot = entity.yHeadRot;
      entity.yBodyRot = 180.0F + angleXComponent * 20.0F;
      entity.setYRot(180.0F + angleXComponent * 40.0F);
      entity.setXRot(-angleYComponent * 20.0F);
      entity.yHeadRot = entity.getYRot();
      entity.yHeadRotO = entity.getYRot();
      float scalar = entity.getScale();
      InventoryScreen.renderEntityInInventory(graphics, x, y, (int)(scale / scalar), pose, cam, entity);
      entity.yBodyRot = yBodyRot;
      entity.setYRot(yRot);
      entity.setXRot(xRot);
      entity.yHeadRotO = yHeadRotO;
      entity.yHeadRot = yHeadRot;
   }
}
