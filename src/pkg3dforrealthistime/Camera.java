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

    private final double FOV;
    private final int PPM;
    
    public Camera(double focalLength, double FOV, int PPM) {
        this.position = new MyVector(0, 0, 0);
        this.normal = new MyVector(-1, 0, 0).mult(focalLength);
        this.x2D = new MyVector(0, 1, 0);
        this.y2D = new MyVector(0, 0, 1);
        
        this.FOV = FOV;
        this.PPM = PPM;
    }

    public void moveTo(double x, double y, double z) {
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
    
    public void moveRight(double distance) {
        this.moveBy(this.x2D.mult(distance));
    }
    public void moveLeft(double distance) {
        this.moveBy(this.x2D.mult(-distance));
    }
    public void moveForward(double distance) {
        this.moveBy(this.normal.unit().mult(distance));
    }
    public void moveBackward(double distance) {
        this.moveBy(this.normal.unit().mult(-distance));
    }
    
    public Projection getProjection(MyVector subject, int SCR_WIDTH, int SCR_HEIGHT) {
        
        Projection ret = new Projection();
        ret.inRange = true;
        ret.inFront = true;
        
        MyVector scaledUnit = subject.sub(this.position).unit().mult(this.normal.length());
        
        double aspectRatio = (double)SCR_WIDTH / SCR_HEIGHT;
        double horFovRad = Math.toRadians(this.FOV);
        double vertFovRad = horFovRad / aspectRatio;
        
        MyVector relSubj = subject.sub(this.position);
        
        MyVector extendedNormal = relSubj.vectorProject(this.normal);
        double horRange = extendedNormal.length() * Math.tan(horFovRad / 2);
        double vertRange = extendedNormal.length() * Math.tan(vertFovRad / 2);
        
        MyVector projNY = relSubj.projectOntoPlane(this.y2D, MyVector.ZERO);
        
        if (projNY.scalarProject(this.normal) <= 0)
            ret.inFront = false;
        
        double nyAngle = MyVector.angleBetween(projNY, this.normal);
        if (nyAngle > this.FOV / 2)
            ret.inRange = false;
        
        MyVector horVec = projNY.sub(extendedNormal);
        double horRatio = horVec.length() / horRange;
        double horRatioSign = Math.signum(horVec.scalarProject(this.x2D));
        horRatio *= horRatioSign;
        
        MyVector projNZ = relSubj.projectOntoPlane(this.x2D, MyVector.ZERO);
        
        double nzAngle = MyVector.angleBetween(projNZ, this.normal);
        if (nzAngle > this.FOV / aspectRatio / 2)
            ret.inRange = false;
        
        MyVector vertVec = projNZ.sub(extendedNormal);
        double vertRatio = vertVec.length() / vertRange;
        double vertRatioSign = Math.signum(vertVec.scalarProject(this.y2D));
        vertRatio *= vertRatioSign;
        
        ret.coords = cartesianToScreen(new MyVector(0, horRatio * SCR_WIDTH / this.PPM / 2, vertRatio * SCR_HEIGHT / this.PPM / 2), SCR_WIDTH, SCR_HEIGHT);
        
        return ret;
    }
    /* -- old projection that used angle ratio instead of distance ratios
    public MyVector getProjection(MyVector subject, double scrWidth, double scrHeight) {
        MyVector scaledUnit = subject.sub(this.position).unit().mult(this.normal.length());
        
        MyVector scaledUnitProjNY = scaledUnit.projectOntoPlane(this.y2D, MyVector.ZERO);
        double nyAngle = MyVector.angleBetween(scaledUnitProjNY, this.normal);
        nyAngle *= Math.signum(scaledUnitProjNY.scalarProject(this.x2D));
        
        MyVector scaledUnitProjNZ = scaledUnit.projectOntoPlane(this.x2D, MyVector.ZERO);
        double nzAngle = MyVector.angleBetween(scaledUnitProjNZ, this.normal);
        nzAngle *= Math.signum(scaledUnitProjNZ.scalarProject(this.y2D));
        
        MyVector relativeX = new MyVector(0, nyAngle / (this.fov / 2), 0);
        MyVector relativeY = new MyVector(0, 0, nzAngle / (this.fov / 2));
        
        relativeX = relativeX.mult(scrWidth / 2);
        relativeY = relativeY.mult(scrHeight / 2);

        
        return relativeX.add(relativeY);
    }*/
    
    
    
    // left = pos, right = neg
    public void rotateHorizontally(double degrees) {
        this.x2D = MyMatrix.rotate(this.x2D, this.y2D, MyVector.ZERO, degrees);
        this.normal = MyMatrix.rotate(this.normal, this.y2D, MyVector.ZERO, degrees);
    }
    // up = pos, down = neg
    public void rotateVertically(double degrees) {
        this.y2D = MyMatrix.rotate(this.y2D, this.x2D, MyVector.ZERO, degrees);
        this.normal = MyMatrix.rotate(this.normal, this.x2D, MyVector.ZERO, degrees);
    }
    public void rotateAroundRelativeAxis(MyVector axis, double degrees) {
        this.x2D = MyMatrix.rotate(this.x2D, axis, MyVector.ZERO, degrees);
        this.y2D = MyMatrix.rotate(this.y2D, axis, MyVector.ZERO, degrees);
        this.normal = MyMatrix.rotate(this.normal, axis, MyVector.ZERO, degrees);
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
        return this.FOV;
    }
    
    /*
        @params:
        vector: x = 0, y = desired x, z = desired y
    */
    private MyVector cartesianToScreen(MyVector vector, int SCR_WIDTH, int SCR_HEIGHT) {
        
        double x = SCR_WIDTH / 2 + vector.y * this.PPM;
        double y = SCR_HEIGHT / 2 - vector.z * this.PPM;
        
        return new MyVector(x, y, 0);
    }
    
    public double getPerspective(MyVector subject, double size){
        double distance = subject.sub(this.position).length();
        
        return size*(15/distance);
    }
    
    @Override
    public String toString() {
        return "pos: " + position + " x2D " + x2D + " y2D " + y2D + " normal: " + normal;
    }
}
