/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyVector;
import java.util.HashMap;

/**
 *
 * @author Dmitry
 */
public class Spectator {
    private Camera camera;
    
    private double accel = 0.0;
    private MyVector acceleration = MyVector.ZERO;
    
    private double maxVel = 0.0;
    private MyVector velocity = MyVector.ZERO;
    
    private double lookDegrees = 0.0;
    
    private boolean moving = false;
    
    public Spectator(MyVector position, MyVector YawPitchRoll, Camera camera) {
        this.camera = camera;
        
        this.camera.moveTo(position);
        this.camera.rotateHorizontally(YawPitchRoll.x);
        this.camera.rotateVertically(YawPitchRoll.y);
    }
    
    public void lookAt(HashMap<Point3D, Projection> points, int SCR_WIDTH, int SCR_HEIGHT) {
        for (Point3D point: points.keySet()) {
            points.put(point, this.camera.getProjection(point, SCR_WIDTH, SCR_HEIGHT));
        }
    }
    public Projection lookAt(MyVector point, int SCR_WIDTH, int SCR_HEIGHT) {
        return this.camera.getProjection(point, SCR_WIDTH, SCR_HEIGHT);
    }
    
    public void setAccel(double accel) {
        this.accel = accel;
    }
    public void setMaxVel(double maxVel) {
        this.maxVel = maxVel;
    }
    public void setLookDegrees(double degrees) {
        this.lookDegrees = degrees;
    }
    
    public void move() {
        this.velocity = this.velocity.add(this.acceleration);
        if (velocity.length() > maxVel) {
            velocity = velocity.unit().mult(maxVel);
        }
        this.camera.moveBy(this.velocity);
        
        if (!moving)
            this.acceleration = this.velocity.unit().mult(-accel);
        moving = false;
    }
    
    public void moveForward() {
        this.acceleration = this.camera.getNormal().unit().mult(this.accel);
        moving = true;
    }
    public void moveBackward() {
        this.acceleration = this.camera.getNormal().unit().mult(-this.accel);
        moving = true;
    }
    public void moveLeft() {
        this.acceleration = this.camera.getX2D().unit().mult(-this.accel);
        moving = true;
    }
    public void moveRight() {
        this.acceleration = this.camera.getX2D().unit().mult(this.accel);
        moving = true;
    }
    public void moveDown() {
        this.acceleration = this.camera.getY2D().unit().mult(-this.accel);
        moving = true;
    }
    public void moveUp() {
        this.acceleration = this.camera.getY2D().unit().mult(this.accel);
        moving = true;
    }
    
    /*
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public void moveForward() {
        this.camera.moveForward(this.speed);
    }
    public void moveBackward() {
        this.camera.moveBackward(this.speed);
    }
    public void moveLeft() {
        this.camera.moveLeft(this.speed);
    }
    public void moveRight() {
        this.camera.moveRight(this.speed);
    }
    public void moveDown() {
        this.camera.moveBy(MyVector.Z.mult(-this.speed));
    }
    public void moveUp() {
        this.camera.moveBy(MyVector.Z.mult(this.speed));
    }*/
    
    public void lookLeft(int pixels) {
        this.camera.rotateAroundRelativeAxis(MyVector.Z, pixels * this.lookDegrees);
    }
    public void lookRight(int pixels) {
        this.camera.rotateAroundRelativeAxis(MyVector.Z, pixels * (-this.lookDegrees));
    }
    public void lookDown(int pixels) {
        this.camera.rotateVertically(pixels * (-this.lookDegrees));
    }
    public void lookUp(int pixels) {
        this.camera.rotateVertically(pixels * this.lookDegrees);
    }
    
    public Camera getCamera() {
        return this.camera;
    }
    
    
    
    
    
}
