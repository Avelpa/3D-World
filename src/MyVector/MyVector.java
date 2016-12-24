/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyVector;

/**
 *
 * @author Dmitry
 */
public class MyVector {
    
    public double x, y, z;
    
    public static final MyVector ZERO = new MyVector(0, 0, 0);
    public static final MyVector X = new MyVector(1, 0, 0);
    public static final MyVector Y = new MyVector(0, 1, 0);
    public static final MyVector Z = new MyVector(0, 0, 1);
    
    public MyVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public MyVector mult(double factor) {
        return new MyVector(this.x*factor, this.y*factor, this.z*factor);
    }
    
    public MyVector add(double x, double y, double z) {
        return this.add(new MyVector(x, y, z));
    }
    public MyVector add(MyVector other) {
        return new MyVector(this.x + other.x, this.y + other.y, this.z + other.z);
    }
    public MyVector sub(double x, double y, double z) {
        return this.sub(new MyVector(x, y, z));
    }
    public MyVector sub(MyVector other) {
        return new MyVector(this.x - other.x, this.y - other.y, this.z - other.z);
    }
    
    public double scalarProject(MyVector other) {
        return this.dot(other) / other.length();
    }
    public MyVector vectorProject(MyVector other) {
        return other.unit().mult(scalarProject(other));
    }
    public MyVector projectOntoPlane(MyVector normal, MyVector point) {
        return this.add(point.sub(this).vectorProject(normal));
    }
    
    public double dot(MyVector other) {
        return other.x*this.x + other.y*this.y + other.z*this.z;
    }
    public MyVector cross(MyVector other) {
        return new MyVector(this.y * other.z - this.z * other.y, -(this.x * other.z - this.z * other.x), this.x * other.y - this.y * other.x);
    }
    
    public static MyVector extendUntilPlane(MyVector normal, MyVector planePoint, MyVector ray, MyVector rayStart) {
        double rightSide =  normal.dot(planePoint.sub(rayStart));
        double leftSide = normal.dot(ray);
        
        if (leftSide == 0.0) {
            if (rightSide == 0.0)
                return ray;
            return null;
        }
        
        return ray.mult(rightSide / leftSide).add(rayStart);
    }
    
    public boolean onPlane(MyVector normal, MyVector planePoint) {
        double result = normal.dot(planePoint.sub(this));
        if (result > 1e-14) {
            System.out.println(result);
        }
        return result <= 1e-14;
    }

//    // (x, y, z)
//    public boolean segmentsIntersect3D(MyVector line1A, MyVector line1B, MyVector line2A, MyVector line2B) {
//        System.err.println("segmentsIntersect3D() not implemented yet.");
//        return false;
//    }
    // (x, y, 0)
    public static boolean segmentsIntersect2D(MyVector line1A, MyVector line1B, MyVector line2A, MyVector line2B) {
        MyVector perpSlopeA = getPerpSlope2D(line1B.sub(line1A));
        MyVector perpSlopeB = null;
        
        // if both lines are single points:
        if ((line1A.x == line1B.x && line1A.y == line1B.y) && (line2A.x == line2B.x && line2A.y == line2B.y)) {
            System.out.println("early");
            System.out.println(line1A + " " + line1B + " | " + line2A + " " + line2B);
            return line1A.x == line2A.x && line1A.y == line2A.y;
	}
        
        char baseLine = '\0';
        
        // if one of the lines is a point, should slope of line as othe rperp
        if ((line1A.x == line1B.x && line1A.y == line1B.y) || (line2A.x == line2B.x && line2A.y == line2B.y)) {
            if (line1A.x == line1B.x && line1A.y == line1B.y) {
                baseLine = 'B';
            } else {
                baseLine = 'A';
            }
        } else if (line2B.sub(line2A).scalarProject(perpSlopeA) <= 1e-14) { // if lines are collinear, use one of the lines as a perpendicular slope
            baseLine = 'A';
        }
        
        if (baseLine == 'A') {
            perpSlopeB = line1B.sub(line1A);
        } else {
            perpSlopeA = line2B.sub(line2A);
            perpSlopeB = getPerpSlope2D(line2B.sub(line2A));
        }
        
        System.out.println(baseLine);
        
        double proj1A = line1A.scalarProject(perpSlopeA);
        double proj1B = line1B.scalarProject(perpSlopeA);
        double proj2A = line2A.scalarProject(perpSlopeA);
        double proj2B = line2B.scalarProject(perpSlopeA);
        
        if (!segmentsIntersect1D(proj1A, proj1B, proj2A, proj2B))
            return false;
        
        proj1A = line1A.scalarProject(perpSlopeB);
        proj1B = line1B.scalarProject(perpSlopeB);
        proj2A = line2A.scalarProject(perpSlopeB);
        proj2B = line2B.scalarProject(perpSlopeB);
        
        if (!segmentsIntersect1D(proj1A, proj1B, proj2A, proj2B))
            return false;
        
        return true;
    }
    public static boolean segmentsIntersect1D(double a1, double a2, double b1, double b2) {
        return Math.min(b1, b2) <= Math.max(a1, a2) && Math.max(b1, b2) >= Math.min(a1, a2);
    }
    
    // (x, y, 0)
    private static MyVector getPerpSlope2D(MyVector line) {
        return new MyVector(line.y, -line.x, 0);
    }
    
    
    // angle range: [0, 180]
    public static double angleBetween(MyVector a, MyVector b) {
        double angle = Math.toDegrees(Math.acos(a.dot(b) / (a.length() * b.length())));
        if (Double.isNaN(angle))
            return 0.0;
        return angle;
    }
    
    public MyVector unit() {
        if (this.length() == 0.0)
            return MyVector.ZERO;
        return this.mult(1/this.length());
    }
    public double length() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }
    
    public boolean eq(MyVector other) {
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }
    
    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z +")";
    }
    
    @Override 
    public boolean equals (Object object) {
        if (object == null || object.getClass() != this.getClass()) {
            return false;
        }
        MyVector vector = (MyVector) object;
        if (this.x == vector.x && this.y == vector.y && this.z == vector.z) {
            return true;
        }
        return false;
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }
}
