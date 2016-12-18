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
    
    public MyVector add(int x, int y, int z) {
        return add(new MyVector(x, y, z));
    }
    public MyVector add(MyVector other) {
        return new MyVector(this.x + other.x, this.y + other.y, this.z + other.z);
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
}
