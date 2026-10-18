package radon.jujutsu_kaisen.client.slice;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;

public abstract class Collider {
   public float mass;
   public Matrix3f localInertiaTensor;
   public Vec3 localCentroid;

   public abstract Vec3 support(Vec3 var1);

   public abstract Collider copy();
}
