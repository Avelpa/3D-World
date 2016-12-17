/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyMatrix;
import MyVector.MyVector;

/**
 *
 * @author Dmitry
 */
public class Camera {
    private MyVector normal;
    private MyVector x2D;
    private MyVector y2D;
    private MyVector position;

    private double fov;
    
    private double zRot = 0.0;
    private double yRot = 0.0;
    private final MyVector ORIGINAL_POSITION;
    
    
    public Camera(double focalLength, double fov) {
        this.position = new MyVector(0, 0, 0);
        this.normal = new MyVector(-1, 0, 0).mult(focalLength);
        this.x2D = new MyVector(0, 1, 0);
        this.y2D = new MyVector(0, 0, 1);
        
        ORIGINAL_POSITION = this.position;
       
        this.fov = fov;
    }
    
    public void reset() {
        this.moveTo(this.ORIGINAL_POSITION);
        this.rotateHorizontally(-this.zRot);
        this.rotateVertically(-this.yRot);
    }
    
    public void moveTo(int x, int y, int z) {
        moveTo(new MyVector(x, y, z));
    }
    public void moveTo(MyVector newPos) {
        moveBy(newPos.sub(this.position));
    }
    
    public void moveBy(double x, double y, double z) {
        moveBy(new MyVector(x, y, z));
    }
    public void moveBy(MyVector distance) {
        this.position = this.position.add(distance);
    }
    
    public void moveHorizontally(double distance) {
        this.moveBy(this.x2D.mult(distance));
    }
    public void moveVertically(double distance) {
        this.moveBy(this.y2D.mult(distance));
    }
    public void moveDepthwise(double distance) {
        this.moveBy(this.normal.unit().mult(distance));
    }
    
    public MyVector getProjection(MyVector subject, int scrHalfLength, int scrHalfHeight) {
        MyVector scaledUnit = subject.sub(this.position).unit().mult(this.normal.length());
        
        MyVector scaledUnitProjNY = scaledUnit.projectOntoPlane(this.y2D, MyVector.ZERO);
        double nyAngle = MyVector.angleBetween(scaledUnitProjNY, this.normal);
        nyAngle *= Math.signum(scaledUnitProjNY.scalarProject(this.x2D));
        
        MyVector scaledUnitProjNZ = scaledUnit.projectOntoPlane(this.x2D, MyVector.ZERO);
        double nzAngle = MyVector.angleBetween(scaledUnitProjNZ, this.normal);
        nzAngle *= Math.signum(scaledUnitProjNZ.scalarProject(this.y2D));
        
        MyVector relativeX = new MyVector(0, nyAngle / (this.fov / 2), 0);
        MyVector relativeY = new MyVector(0, 0, nzAngle / (this.fov / 2));
        
        relativeX = relativeX.mult(scrHalfLength);
        relativeY = relativeY.mult(scrHalfHeight);

        
        return relativeX.add(relativeY);
    }
    
    // left = pos, right = neg
    public void rotateHorizontally(double degrees) {
        this.x2D = MyMatrix.rotate(this.x2D, this.y2D, MyVector.ZERO, degrees);
        this.normal = MyMatrix.rotate(this.normal, this.y2D, MyVector.ZERO, degrees);
        
        zRot += degrees;
        zRot %= 360;
    }
    // left = pos, right = neg
    public void rotateVertically(double degrees) {
        this.y2D = MyMatrix.rotate(this.y2D, this.x2D, MyVector.ZERO, degrees);
        this.normal = MyMatrix.rotate(this.normal, this.x2D, MyVector.ZERO, degrees);
        
        yRot += degrees;
        yRot %= 360;
    }
    
    public MyVector getNormal() {
        return this.normal;
    }
    public MyVector getX2D() {
        return this.x2D;
    }
    public MyVector getY2D() {
        return this.y2D;
    }
    public MyVector getPos() {
        return this.position;
    }
    public double getFov() {
        return this.fov;
    }
    
    @Override
    public String toString() {
        return "pos: " + position + " x2D " + x2D + " y2D " + y2D + " normal: " + normal + " yRot: " + yRot + "zRot: " + zRot;
    }
}
