package radon.jujutsu_kaisen.client.slice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;

public class GJK {
   public static final int gjkMaxIterations = 64;
   public static final int epaMaxIterations = 128;
   public static float margin = 0.0F;
   public static GJK.Simplex csoSimplex = new GJK.Simplex();
   private static final List<GJK.Mkv[]> faces = new ArrayList<>();
   private static final List<GJK.Mkv[]> edges = new ArrayList<>();
   private static final Vec3[][] features = new Vec3[2][3];

   public static GJK.GJKInfo colliding(RigidBody bodyA, RigidBody bodyB, Collider a, Collider b) {
      return colliding(bodyA, bodyB, a, b, true);
   }

   public static boolean collidesAny(RigidBody bodyA, RigidBody bodyB, Collider a, Collider b) {
      return colliding(bodyA, bodyB, a, b, false) != null;
   }

   public static GJK.GJKInfo colliding(@Nullable RigidBody bodyA, @Nullable RigidBody bodyB, Collider a, Collider b, boolean epa) {
      GJK.GJKInfo result = new GJK.GJKInfo();
      csoSimplex.reset();
      Vec3 direction = new Vec3(0.0, 0.0, 1.0);
      Vec3 supportCSO = doCSOSupport(bodyA, bodyB, a, b, direction).v;
      direction = supportCSO.reverse();

      for (int iter = 0; iter < 64; iter++) {
         supportCSO = doCSOSupport(bodyA, bodyB, a, b, direction).v;
         if (supportCSO.dot(direction) < 0.0) {
            result.result = GJK.Result.SEPARATED;
            if (!epa) {
               return null;
            }

            return result;
         }

         switch (csoSimplex.size) {
            case 0:
            case 1:
            default:
               break;
            case 2: {
               Vec3 ab = csoSimplex.points[1].v.subtract(csoSimplex.points[0].v);
               Vec3 ao = csoSimplex.points[0].v.reverse();
               if (ab.dot(ao) > 0.0) {
                  direction = ab.cross(ao).cross(ab);
               } else {
                  csoSimplex.points[1] = null;
                  csoSimplex.size--;
                  direction = csoSimplex.points[0].v.scale(-1.0);
               }
               break;
            }
            case 3: {
               Vec3 ab = csoSimplex.points[1].v.subtract(csoSimplex.points[0].v);
               Vec3 ac = csoSimplex.points[2].v.subtract(csoSimplex.points[0].v);
               Vec3 abc = ab.cross(ac);
               Vec3 ao = csoSimplex.points[0].v.reverse();
               direction = triangleCase(ab, ac, abc, ao);
               break;
            }
            case 4: {
               Vec3 ab = csoSimplex.points[1].v.subtract(csoSimplex.points[0].v);
               Vec3 ac = csoSimplex.points[2].v.subtract(csoSimplex.points[0].v);
               Vec3 ad = csoSimplex.points[3].v.subtract(csoSimplex.points[0].v);
               Vec3 ao = csoSimplex.points[0].v.reverse();
               Vec3 dir = tetraCase(ab, ac, ad, ao);
               if (dir == null) {
                  if (epa) {
                     EPA(bodyA, bodyB, a, b, result);
                  }

                  return result;
               }

               direction = dir;
            }
         }
      }

      result.result = GJK.Result.GJK_FAILED;
      return result;
   }

   public static Vec3 triangleCase(Vec3 ab, Vec3 ac, Vec3 abc, Vec3 ao) {
      if (abc.cross(ac).dot(ao) > 0.0) {
         if (ac.dot(ao) > 0.0) {
            csoSimplex.points[1] = csoSimplex.points[2];
            csoSimplex.points[2] = null;
            csoSimplex.size--;
            return ac.cross(ao).cross(ac);
         } else if (ab.dot(ao) > 0.0) {
            csoSimplex.points[2] = null;
            csoSimplex.size--;
            return ab.cross(ao).cross(ab);
         } else {
            csoSimplex.points[1] = null;
            csoSimplex.points[2] = null;
            csoSimplex.size -= 2;
            return ao;
         }
      } else if (ab.cross(abc).dot(ao) > 0.0) {
         if (ab.dot(ao) > 0.0) {
            csoSimplex.points[2] = null;
            csoSimplex.size--;
            return ab.cross(ao).cross(ab);
         } else {
            csoSimplex.points[1] = null;
            csoSimplex.points[2] = null;
            csoSimplex.size -= 2;
            return ao;
         }
      } else if (abc.dot(ao) > 0.0) {
         return abc;
      } else {
         GJK.Mkv tmp = csoSimplex.points[2];
         csoSimplex.points[2] = csoSimplex.points[1];
         csoSimplex.points[1] = tmp;
         return abc.reverse();
      }
   }

   public static Vec3 tetraCase(Vec3 ab, Vec3 ac, Vec3 ad, Vec3 ao) {
      if (ab.cross(ac).dot(ao) > 0.0) {
         csoSimplex.points[3] = null;
         csoSimplex.size--;
         return triangleCase(ab, ac, ab.cross(ac), ao);
      } else if (ac.cross(ad).dot(ao) > 0.0) {
         csoSimplex.points[1] = csoSimplex.points[2];
         csoSimplex.points[2] = csoSimplex.points[3];
         csoSimplex.points[3] = null;
         csoSimplex.size--;
         return triangleCase(ac, ad, ac.cross(ad), ao);
      } else if (ad.cross(ab).dot(ao) > 0.0) {
         csoSimplex.points[2] = csoSimplex.points[1];
         csoSimplex.points[1] = csoSimplex.points[3];
         csoSimplex.points[3] = null;
         csoSimplex.size--;
         return triangleCase(ad, ab, ad.cross(ab), ao);
      } else {
         return null;
      }
   }

   public static GJK.Mkv doCSOSupport(RigidBody bodyA, RigidBody bodyB, Collider a, Collider b, Vec3 direction) {
      Vec3 supportCSO = csoSupport(bodyA, bodyB, a, b, direction);
      GJK.Mkv vert = new GJK.Mkv(supportCSO, direction);
      csoSimplex.push(vert);
      return vert;
   }

   public static Vec3 csoSupport(RigidBody bodyA, RigidBody bodyB, Collider a, Collider b, Vec3 dir) {
      return localSupport(bodyA, a, dir).subtract(localSupport(bodyB, b, dir.reverse()));
   }

   public static Vec3 localSupport(RigidBody body, Collider collider, Vec3 worldDir) {
      if (body != null) {
         Vec3 localDir = body.globalToLocalVec(worldDir);
         if (margin != 0.0F) {
            localDir = localDir.normalize();
            return body.localToGlobalPos(collider.support(localDir).add(localDir.scale(margin)));
         } else {
            return body.localToGlobalPos(collider.support(localDir));
         }
      } else if (margin != 0.0F) {
         worldDir = worldDir.normalize();
         return collider.support(worldDir).add(worldDir.scale(margin));
      } else {
         return collider.support(worldDir);
      }
   }

   public static void EPA(RigidBody bodyA, RigidBody bodyB, Collider a, Collider b, GJK.GJKInfo info) {
      faces.add(buildFace(csoSimplex.points[0], csoSimplex.points[1], csoSimplex.points[2]));
      faces.add(buildFace(csoSimplex.points[0], csoSimplex.points[2], csoSimplex.points[3]));
      faces.add(buildFace(csoSimplex.points[0], csoSimplex.points[3], csoSimplex.points[1]));
      faces.add(buildFace(csoSimplex.points[1], csoSimplex.points[2], csoSimplex.points[3]));

      for (int i = 0; i < 128; i++) {
         GJK.Mkv[] closestFace = null;
         double smallestDist = Double.MAX_VALUE;

         for (GJK.Mkv[] face : faces) {
            double lenSq = originDistToPlaneSq(face);
            if (lenSq < smallestDist) {
               smallestDist = lenSq;
               closestFace = face;
            }
         }

         GJK.Mkv support = doCSOSupport(bodyA, bodyB, a, b, closestFace[3].v);
         float epsilon = 1.0E-5F;
         if (distToPlaneSq(closestFace, support.v) < 1.0E-5F) {
            info.result = GJK.Result.COLLIDING;
            Vec3 separation = planeProjectOrigin(closestFace);
            info.normal = separation.normalize();
            info.depth = (float)separation.length();

            for (int j = 0; j < 3; j++) {
               features[0][j] = localSupport(bodyA, a, closestFace[j].r);
               features[1][j] = localSupport(bodyB, b, closestFace[j].r.reverse());
            }

            Vec3 bCoords = barycentricCoords(closestFace, separation);
            info.contactPointA = new Vec3(
               features[0][0].x * bCoords.x + features[0][1].x * bCoords.y + features[0][2].x * bCoords.z,
               features[0][0].y * bCoords.x + features[0][1].y * bCoords.y + features[0][2].y * bCoords.z,
               features[0][0].z * bCoords.x + features[0][1].z * bCoords.y + features[0][2].z * bCoords.z
            );
            info.contactPointB = new Vec3(
               features[1][0].x * bCoords.x + features[1][1].x * bCoords.y + features[1][2].x * bCoords.z,
               features[1][0].y * bCoords.x + features[1][1].y * bCoords.y + features[1][2].y * bCoords.z,
               features[1][0].z * bCoords.x + features[1][1].z * bCoords.y + features[1][2].z * bCoords.z
            );
            faces.clear();
            return;
         }

         Iterator<GJK.Mkv[]> iter = faces.iterator();

         while (iter.hasNext()) {
            GJK.Mkv[] facex = iter.next();
            if (facex[3].v.dot(support.v.subtract(facex[0].v)) > 0.0) {
               iter.remove();
               GJK.Mkv[] edge = new GJK.Mkv[]{facex[1], facex[0]};
               if (!removeEdge(edge)) {
                  edge[0] = facex[0];
                  edge[1] = facex[1];
                  edges.add(edge);
               }

               edge = new GJK.Mkv[]{facex[2], facex[1]};
               if (!removeEdge(edge)) {
                  edge[0] = facex[1];
                  edge[1] = facex[2];
                  edges.add(edge);
               }

               edge = new GJK.Mkv[]{facex[0], facex[2]};
               if (!removeEdge(edge)) {
                  edge[0] = facex[2];
                  edge[1] = facex[0];
                  edges.add(edge);
               }
            }
         }

         for (GJK.Mkv[] edgex : edges) {
            faces.add(buildFace(edgex[0], edgex[1], support));
         }

         edges.clear();
      }

      faces.clear();
      info.result = GJK.Result.EPA_FAILED;
   }

   public static boolean removeEdge(GJK.Mkv[] edge) {
      Iterator<GJK.Mkv[]> iter = edges.iterator();

      while (iter.hasNext()) {
         GJK.Mkv[] edge2 = iter.next();
         if (edge[0] == edge2[0] && edge[1] == edge2[1]) {
            iter.remove();
            return true;
         }
      }

      return false;
   }

   public static Vec3 planeProjectOrigin(GJK.Mkv[] face) {
      Vec3 point = face[0].v.reverse();
      double dot = face[3].v.dot(point);
      return face[3].v.scale((float)dot).reverse();
   }

   public static double distToPlaneSq(GJK.Mkv[] face, Vec3 point) {
      double dot = face[3].v.dot(point.subtract(face[0].v));
      Vec3 proj = face[3].v.scale((float)dot);
      return proj.lengthSqr();
   }

   public static double originDistToPlaneSq(GJK.Mkv[] face) {
      double dot = face[0].v.dot(face[3].v);
      Vec3 proj = face[3].v.scale((float)dot);
      return proj.lengthSqr();
   }

   public static GJK.Mkv[] buildFace(GJK.Mkv a, GJK.Mkv b, GJK.Mkv c) {
      Vec3 ab = b.v.subtract(a.v);
      Vec3 ac = c.v.subtract(a.v);
      Vec3 ao = a.v.reverse();
      Vec3 normal = LegacyMath.normalize(ab.cross(ac));
      return normal.dot(ao) < 0.0 ? new GJK.Mkv[]{a, b, c, new GJK.Mkv(normal, null)} : new GJK.Mkv[]{a, c, b, new GJK.Mkv(normal.reverse(), null)};
   }

   public static Vec3 barycentricCoords(GJK.Mkv[] face, Vec3 point) {
      double u = (float)face[1].v.subtract(point).cross(face[2].v.subtract(point)).length();
      double v = (float)face[0].v.subtract(point).cross(face[2].v.subtract(point)).length();
      double w = (float)face[0].v.subtract(point).cross(face[1].v.subtract(point)).length();
      double uvw = u + v + w;
      return new Vec3(u, v, w).scale(1.0 / uvw);
   }

   public static class GJKInfo {
      public GJK.Result result;
      public Vec3 normal;
      public float depth;
      public Vec3 contactPointA;
      public Vec3 contactPointB;
   }

   public static class Mkv {
      public Vec3 v;
      public Vec3 r;

      public Mkv(Vec3 point, Vec3 direction) {
         this.v = point;
         this.r = direction;
      }

      public GJK.Mkv copy() {
         return new GJK.Mkv(this.v, this.r);
      }
   }

   public static enum Result {
      COLLIDING,
      SEPARATED,
      GJK_FAILED,
      EPA_FAILED;
   }

   public static class Simplex {
      public int size = 0;
      public GJK.Mkv[] points = new GJK.Mkv[4];

      public void push(GJK.Mkv vec) {
         for (int i = Math.min(this.size, 2); i >= 0; i--) {
            this.points[i + 1] = this.points[i];
         }

         this.points[0] = vec;
         this.size++;
         if (this.size > 4) {
            this.size = 4;
         }
      }

      public void reset() {
         this.size = 0;

         for (int i = 0; i < 4; i++) {
            this.points[i] = null;
         }
      }

      public GJK.Simplex copy() {
         GJK.Simplex simp = new GJK.Simplex();
         simp.size = this.size;

         for (int i = 0; i < 4; i++) {
            simp.points[i] = this.points[i].copy();
         }

         return simp;
      }
   }
}
