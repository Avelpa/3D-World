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
    private final double SCR_WIDTH, SCR_HEIGHT;
    
    public Camera(double focalLength, double FOV, double SCR_WIDTH, double SCR_HEIGHT, int PPM) {
        this.position = new MyVector(0, 0, 0);
        this.normal = new MyVector(-1, 0, 0).mult(focalLength);
        this.x2D = new MyVector(0, 1, 0);
        this.y2D = new MyVector(0, 0, 1);
        
        this.SCR_WIDTH = SCR_WIDTH;
        this.SCR_HEIGHT = SCR_HEIGHT;
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
    
    public Projection getProjection(MyVector subject) {
        Projection ret = new Projection();
        ret.inRange = true;
        ret.inFront = true;
        
        double horFovRad = Math.toRadians(this.getHorizontalFov());
        double vertFovRad = Math.toRadians(this.getVerticalFov());
        
        MyVector relSubj = subject.sub(this.position);
        
        MyVector extendedNormal = relSubj.vectorProject(this.normal);
        double horRange = extendedNormal.length() * Math.tan(horFovRad / 2);
        double vertRange = extendedNormal.length() * Math.tan(vertFovRad / 2);
        
        ret.horHalfWidth = horRange;
        ret.vertHalfHeight = vertRange;
        
        MyVector projNY = relSubj.projectOntoPlane(this.y2D, MyVector.ZERO);
        
        if (projNY.scalarProject(this.normal) <= 0)
            ret.inFront = false;
        
        double nyAngle = MyVector.angleBetween(projNY, this.normal);
        if (nyAngle > this.getHorizontalFov() / 2)
            ret.inRange = false;
        
        MyVector horVec = projNY.sub(extendedNormal);
        double horRatio = horVec.length() / horRange;
        double horRatioSign = Math.signum(horVec.scalarProject(this.x2D));
        horRatio *= horRatioSign;
        
        MyVector projNZ = relSubj.projectOntoPlane(this.x2D, MyVector.ZERO);
        
        double nzAngle = MyVector.angleBetween(projNZ, this.normal);
        if (nzAngle > this.getVerticalFov() / 2)
            ret.inRange = false;
        
        MyVector vertVec = projNZ.sub(extendedNormal);
        double vertRatio = vertVec.length() / vertRange;
        double vertRatioSign = Math.signum(vertVec.scalarProject(this.y2D));
        vertRatio *= vertRatioSign;
        
        ret.cartesianCoords = new MyVector(horRatio * SCR_WIDTH / this.PPM / 2, vertRatio * SCR_HEIGHT / this.PPM / 2, 0);
        ret.screenCoords = cartesianToScreen(ret.cartesianCoords, SCR_WIDTH, SCR_HEIGHT);
        
        return ret;
    }
    
    public boolean lineIsInFov(Projection aProj, Projection bProj, MyVector a, MyVector b) {
        if (aProj.inRange || bProj.inRange)
            return true;
        if (!aProj.inFront && !bProj.inFront)
            return false;
        
        MyVector fovTopLeft = MyMatrix.rotate(MyMatrix.rotate(this.getNormal(), this.getY2D(), MyVector.ZERO, this.getHorizontalFov() / 2), this.getX2D(), MyVector.ZERO, this.getVerticalFov() / 2);
        MyVector fovTopRight = MyMatrix.rotate(MyMatrix.rotate(this.getNormal(), this.getY2D(), MyVector.ZERO, -this.getHorizontalFov() / 2), this.getX2D(), MyVector.ZERO, this.getVerticalFov() / 2);
        MyVector fovBottomLeft = MyMatrix.rotate(MyMatrix.rotate(this.getNormal(), this.getY2D(), MyVector.ZERO, this.getHorizontalFov() / 2), this.getX2D(), MyVector.ZERO, -this.getVerticalFov() / 2);
        MyVector fovBottomRight = MyMatrix.rotate(MyMatrix.rotate(this.getNormal(), this.getY2D(), MyVector.ZERO, -this.getHorizontalFov() / 2), this.getX2D(), MyVector.ZERO, -this.getVerticalFov() / 2);

        if (lineIntersectsFovSegment(a, b, fovTopLeft, fovTopRight) || 
            lineIntersectsFovSegment(a, b, fovTopLeft, fovBottomLeft) || 
            lineIntersectsFovSegment(a, b, fovBottomLeft, fovBottomRight) || 
            lineIntersectsFovSegment(a, b, fovTopRight, fovBottomRight))
            return true;
        return false;
    }
    
    private boolean lineIntersectsFovSegment(MyVector lineA, MyVector lineB, MyVector ray1, MyVector ray2) {
        // line is in between rays
        MyVector line = lineB.sub(lineA);
        MyVector ray1Proj = ray1.projectOntoPlane(line, MyVector.ZERO);
        MyVector ray2Proj = ray2.projectOntoPlane(line, MyVector.ZERO);
        MyVector lineProj = lineA.projectOntoPlane(line, this.getPos()).sub(this.getPos());
        
        double rayAngle = MyVector.angleBetween(ray1Proj, ray2Proj);
        double angle = MyVector.angleBetween(lineProj, ray1Proj);
        if (angle > rayAngle)
            return false;
        angle = MyVector.angleBetween(lineProj, ray2Proj);
        if (angle > rayAngle)
            return false;
        
        // line is in ray plane
        MyVector planeNormal = ray1.cross(ray2);
        double fromTop = lineA.sub(this.getPos()).scalarProject(planeNormal);
        if (fromTop == 0)
            return true;
        double fromBottom = lineB.sub(this.getPos()).scalarProject(planeNormal.mult(-1));
        if (fromBottom == 0)
            return true;
        
        return fromBottom > 0 == fromTop > 0;
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
    public double getHorizontalFov() {
        return this.FOV;
    }
    public double getVerticalFov() {
        return this.SCR_HEIGHT / this.SCR_WIDTH * this.FOV;
    }
    
    private MyVector cartesianToScreen(MyVector vector, double SCR_WIDTH, double SCR_HEIGHT) {
        
        double x = SCR_WIDTH / 2 + vector.x * this.PPM;
        double y = SCR_HEIGHT / 2 - vector.y * this.PPM;
        
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
