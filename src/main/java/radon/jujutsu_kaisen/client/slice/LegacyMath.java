package radon.jujutsu_kaisen.client.slice;

import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Matrix3f;

public class LegacyMath {
   public static Vec3 normalize(Vec3 vec) {
      double d0 = (float)Math.sqrt(vec.x * vec.x + vec.y * vec.y + vec.z * vec.z);
      return d0 < 1.0E-4 ? Vec3.ZERO : new Vec3(vec.x / d0, vec.y / d0, vec.z / d0);
   }

   public static void invert(Matrix3f mat) {
      double[] luMatrix = new double[9];
      double[] inverseMatrix = new double[9];
      int[] pivotIndices = new int[3];
      luMatrix[0] = mat.m00;
      luMatrix[1] = mat.m01;
      luMatrix[2] = mat.m02;
      luMatrix[3] = mat.m10;
      luMatrix[4] = mat.m11;
      luMatrix[5] = mat.m12;
      luMatrix[6] = mat.m20;
      luMatrix[7] = mat.m21;
      luMatrix[8] = mat.m22;
      if (!luDecomposition(luMatrix, pivotIndices)) {
         throw new RuntimeException("Cannot invert matrix!");
      } else {
         for (int i = 0; i < 9; i++) {
            inverseMatrix[i] = 0.0;
         }

         inverseMatrix[0] = 1.0;
         inverseMatrix[4] = 1.0;
         inverseMatrix[8] = 1.0;
         luBacksubstitution(luMatrix, pivotIndices, inverseMatrix);
         mat.m00 = (float)inverseMatrix[0];
         mat.m01 = (float)inverseMatrix[1];
         mat.m02 = (float)inverseMatrix[2];
         mat.m10 = (float)inverseMatrix[3];
         mat.m11 = (float)inverseMatrix[4];
         mat.m12 = (float)inverseMatrix[5];
         mat.m20 = (float)inverseMatrix[6];
         mat.m21 = (float)inverseMatrix[7];
         mat.m22 = (float)inverseMatrix[8];
      }
   }

   private static boolean luDecomposition(double[] matrix, int[] pivotIndices) {
      double[] scalingFactors = new double[3];
      int matrixIndex = 0;
      int scalingIndex = 0;
      int row = 3;

      while (row-- != 0) {
         double largestElement = 0.0;
         int column = 3;

         while (column-- != 0) {
            double element = matrix[matrixIndex++];
            element = Math.abs(element);
            if (element > largestElement) {
               largestElement = element;
            }
         }

         if (largestElement == 0.0) {
            return false;
         }

         scalingFactors[scalingIndex++] = 1.0 / largestElement;
      }

      byte matrixBaseIndex = 0;

      for (int var23 = 0; var23 < 3; var23++) {
         for (int var19 = 0; var19 < var23; var19++) {
            int rowIndex = matrixBaseIndex + 3 * var19 + var23;
            double sum = matrix[rowIndex];
            int iMax = var19;
            int subMatrixIndex = matrixBaseIndex + 3 * var19;

            for (int columnIndex = matrixBaseIndex + var23; iMax-- != 0; columnIndex += 3) {
               sum -= matrix[subMatrixIndex] * matrix[columnIndex];
               subMatrixIndex++;
            }

            matrix[rowIndex] = sum;
         }

         double largestScalingFactor = 0.0;
         scalingIndex = -1;

         for (int var20 = var23; var20 < 3; var20++) {
            int rowIndex = matrixBaseIndex + 3 * var20 + var23;
            double sum = matrix[rowIndex];
            int iMax = var23;
            int subMatrixIndex = matrixBaseIndex + 3 * var20;

            for (int columnIndex = matrixBaseIndex + var23; iMax-- != 0; columnIndex += 3) {
               sum -= matrix[subMatrixIndex] * matrix[columnIndex];
               subMatrixIndex++;
            }

            matrix[rowIndex] = sum;
            double scalingFactor;
            if ((scalingFactor = scalingFactors[var20] * Math.abs(sum)) >= largestScalingFactor) {
               largestScalingFactor = scalingFactor;
               scalingIndex = var20;
            }
         }

         if (scalingIndex < 0) {
            throw new RuntimeException("Logic error: imax < 0");
         }

         if (var23 != scalingIndex) {
            int iMax = 3;
            int subMatrixIndex = matrixBaseIndex + 3 * scalingIndex;
            int columnIndex = matrixBaseIndex + 3 * var23;

            while (iMax-- != 0) {
               double scalingFactor = matrix[subMatrixIndex];
               matrix[subMatrixIndex++] = matrix[columnIndex];
               matrix[columnIndex++] = scalingFactor;
            }

            scalingFactors[scalingIndex] = scalingFactors[var23];
         }

         pivotIndices[var23] = scalingIndex;
         if (matrix[matrixBaseIndex + 3 * var23 + var23] == 0.0) {
            return false;
         }

         if (var23 != 2) {
            double scalingFactor = 1.0 / matrix[matrixBaseIndex + 3 * var23 + var23];
            int rowIndex = matrixBaseIndex + 3 * (var23 + 1) + var23;

            for (int var21 = 2 - var23; var21-- != 0; rowIndex += 3) {
               matrix[rowIndex] *= scalingFactor;
            }
         }
      }

      return true;
   }

   private static void luBacksubstitution(double[] luMatrix, int[] pivotIndices, double[] inverseMatrix) {
      byte matrixBaseIndex = 0;

      for (int row = 0; row < 3; row++) {
         int index = row;
         int backSubRowIndex = -1;

         for (int column = 0; column < 3; column++) {
            int pivotIndex = pivotIndices[matrixBaseIndex + column];
            double sum = inverseMatrix[index + 3 * pivotIndex];
            inverseMatrix[index + 3 * pivotIndex] = inverseMatrix[index + 3 * column];
            if (backSubRowIndex >= 0) {
               int i = column * 3;

               for (int backSubColumn = backSubRowIndex; backSubColumn <= column - 1; backSubColumn++) {
                  sum -= luMatrix[i + backSubColumn] * inverseMatrix[index + 3 * backSubColumn];
               }
            } else if (sum != 0.0) {
               backSubRowIndex = column;
            }

            inverseMatrix[index + 3 * column] = sum;
         }

         byte luMatrixBaseIndex = 6;
         inverseMatrix[index + 6] = inverseMatrix[index + 6] / luMatrix[luMatrixBaseIndex + 2];
         int i = luMatrixBaseIndex - 3;
         inverseMatrix[index + 3] = (inverseMatrix[index + 3] - luMatrix[i + 2] * inverseMatrix[index + 6]) / luMatrix[i + 1];
         i -= 3;
         inverseMatrix[index + 0] = (inverseMatrix[index + 0] - luMatrix[i + 1] * inverseMatrix[index + 3] - luMatrix[i + 2] * inverseMatrix[index + 6])
            / luMatrix[i + 0];
      }
   }

   public static void set(Matrix3f mat, AxisAngle4f axis) {
      float magnitude = (float)Math.sqrt(axis.x * axis.x + axis.y * axis.y + axis.z * axis.z);
      if (magnitude < 1.0E-8) {
         mat.m00 = 1.0F;
         mat.m01 = 0.0F;
         mat.m02 = 0.0F;
         mat.m10 = 0.0F;
         mat.m11 = 1.0F;
         mat.m12 = 0.0F;
         mat.m20 = 0.0F;
         mat.m21 = 0.0F;
         mat.m22 = 1.0F;
      } else {
         magnitude = 1.0F / magnitude;
         float x = axis.x * magnitude;
         float y = axis.y * magnitude;
         float z = axis.z * magnitude;
         float sinAngle = (float)Math.sin(axis.angle);
         float cosAngle = (float)Math.cos(axis.angle);
         float oneMinusCosAngle = 1.0F - cosAngle;
         float xz = x * z;
         float xy = x * y;
         float yz = y * z;
         mat.m00 = oneMinusCosAngle * x * x + cosAngle;
         mat.m01 = oneMinusCosAngle * xy - sinAngle * z;
         mat.m02 = oneMinusCosAngle * xz + sinAngle * y;
         mat.m10 = oneMinusCosAngle * xy + sinAngle * z;
         mat.m11 = oneMinusCosAngle * y * y + cosAngle;
         mat.m12 = oneMinusCosAngle * yz - sinAngle * x;
         mat.m20 = oneMinusCosAngle * xz - sinAngle * y;
         mat.m21 = oneMinusCosAngle * yz + sinAngle * x;
         mat.m22 = oneMinusCosAngle * z * z + cosAngle;
      }
   }

   public static void mul(Matrix3f a, Matrix3f b) {
      float m00 = a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20;
      float m01 = a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21;
      float m02 = a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22;
      float m10 = a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20;
      float m11 = a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21;
      float m12 = a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22;
      float m20 = a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20;
      float m21 = a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21;
      float m22 = a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22;
      a.m00 = m00;
      a.m01 = m01;
      a.m02 = m02;
      a.m10 = m10;
      a.m11 = m11;
      a.m12 = m12;
      a.m20 = m20;
      a.m21 = m21;
      a.m22 = m22;
   }
}
