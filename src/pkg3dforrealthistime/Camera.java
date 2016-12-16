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
        
//        System.out.println(nyAngle);
        
        MyVector relativeX = new MyVector(0, nyAngle / (this.fov / 2), 0);
        MyVector relativeY = new MyVector(0, 0, nzAngle / (this.fov / 2));
        
        relativeX = relativeX.mult(scrHalfLength);
        relativeY = relativeY.mult(scrHalfHeight);

        
        return relativeX.add(relativeY);
    }
    /*
    public MyVector getProjection(MyVector subject, int scrHalfLength, int scrHalfHeight) {
        
        double fovRadians = Math.toRadians(this.fov);
        /*
        // get vertical angle
        MyVector verticalProjection = subject.projectOntoPlane(this.x2D.sub(this.position), this.position);
        double verticalAngle = MyVector.angleBetween(verticalProjection.sub(this.position), this.normal.sub(this.position));

        // get horizontal angle
        MyVector horizontalProjection = subject.projectOntoPlane(this.y2D.sub(this.position), this.position);
        double horizontalAngle = MyVector.angleBetween(horizontalProjection.sub(this.position), this.normal.sub(this.position));
        
        System.out.printf("vertical angle: %g, horizontal angle: %g\n", verticalAngle, horizontalAngle);
        
        // scaled vertical proj
        MyVector verticalUnit = verticalProjection.unit().mult(this.normal.length()).vectorProject(this.normal);
        *
        
//        System.out.println("subject: "  + subject);
        
        // scaled to radius
        MyVector scaledUnit = subject.sub(this.position).unit().mult(this.normal.length());
        
//        System.out.println("pos: " + this.position);
//        System.out.println("sub sub pos: "  + subject.sub(this.position));
//        System.out.println("scaled unit: " + scaledUnit.sub(this.position));
//        System.out.println("normal length: " + this.normal.sub(this.position).length());
        
        // project onto the arc extending from fov applied to the normal
        MyVector normalToScreen = this.normal.unit().mult(this.normal.length() * Math.abs(Math.cos(fovRadians / 2)));
        MyVector screenProj = scaledUnit.projectOntoPlane(this.normal, normalToScreen);
        
//        System.out.println("fuck me: " + scaledUnit.sub(this.position.add(normalToScreen)));
        
//        System.out.println("normal to screen: " + normalToScreen);
//        System.out.println("screen proj: " + screenProj);
        
        MyVector relativeX = screenProj.vectorProject(this.x2D);
        MyVector relativeY = screenProj.vectorProject(this.y2D);
        
        
//        System.out.println("relX: " + relativeX);
        
        
        relativeX = relativeX.unit().mult(relativeX.length() / (this.normal.length() * Math.abs(Math.sin(fovRadians / 2))) * scrHalfLength);
        relativeY = relativeY.unit().mult(relativeY.length() / (this.normal.length() * Math.abs(Math.sin(fovRadians / 2))) * scrHalfHeight);

//        System.out.println(relativeX);
        
        
        return relativeX.add(relativeY);
    }*/
    
    
    
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
    
    @Override
    public String toString() {
        return "pos: " + position + " x2D " + x2D + " y2D " + y2D + " normal: " + normal + " yRot: " + yRot + "zRot: " + zRot;
    }
}
