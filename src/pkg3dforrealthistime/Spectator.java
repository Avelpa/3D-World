/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyMatrix;
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
    
    private final MyVector ABSOLUTE_VERTICAL;
    
    public Spectator(MyVector position, MyVector YawPitchRoll, Camera camera) {
        this.camera = camera;
        
        this.camera.moveTo(position);
        this.camera.rotateHorizontally(YawPitchRoll.x);
        this.camera.rotateVertically(YawPitchRoll.y);
        
        this.ABSOLUTE_VERTICAL = MyMatrix.rotate(MyVector.Z, this.camera.getX2D(), MyVector.ZERO, YawPitchRoll.y);
    }
    
    public void lookAt(HashMap<Point3D, Projection> points) {
        for (Point3D point: points.keySet()) {
            points.put(point, this.camera.getProjection(point));
        }
    }
    public Projection lookAt(MyVector point) {
        return this.camera.getProjection(point);
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
    
    public void move(MyVector gravity, double time) {
        // decelerate the player when not actively moving
//        if (!moving) {
//            if (this.velocity.length() < this.accel) { // accel is the smallest unit of velocity
//                this.velocity = MyVector.ZERO;
//                return;
//            }
//            this.acceleration = this.velocity.unit().mult(-this.accel);
//        } 
//        else 
        {
            // if moving in multiple directions at once, cap the acceleration
            if (this.acceleration.length() != this.accel) {
                this.acceleration = this.acceleration.unit().mult(this.accel);
            }
        }
        
        this.acceleration = this.acceleration.add(gravity);
        
        this.velocity = this.velocity.add(this.acceleration.mult(time));
        if (this.velocity.length() > this.maxVel) {
            this.velocity = this.velocity.unit().mult(this.maxVel);
        }
        
        this.camera.moveBy(this.velocity.mult(time));
        moving = false;
        this.acceleration = MyVector.ZERO;
    }
    
    public void moveForward() {
        this.acceleration = this.acceleration.add(this.camera.getNormal().unit().mult(this.accel));
        moving = true;
    }
    public void moveBackward() {
        this.acceleration = this.acceleration.add(this.camera.getNormal().unit().mult(-this.accel));
        moving = true;
    }
    public void moveLeft() {
        this.acceleration = this.acceleration.add(this.camera.getX2D().unit().mult(-this.accel));
        moving = true;
    }
    public void moveRight() {
        this.acceleration = this.acceleration.add(this.camera.getX2D().unit().mult(this.accel));
        moving = true;
    }
    public void moveDown() {
        this.acceleration = this.acceleration.add(ABSOLUTE_VERTICAL.mult(-this.accel));
        moving = true;
    }
    public void moveUp() {
        this.acceleration = this.acceleration.add(ABSOLUTE_VERTICAL.mult(this.accel));
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
        this.camera.rotateAroundRelativeAxis(ABSOLUTE_VERTICAL, pixels * this.lookDegrees);
    }
    public void lookRight(int pixels) {
        this.camera.rotateAroundRelativeAxis(ABSOLUTE_VERTICAL, pixels * (-this.lookDegrees));
    }
    public void lookDown(int pixels) {
        this.camera.rotateVertically(pixels * (-this.lookDegrees));
        
        double angleFromVertical = MyVector.angleBetween(this.camera.getY2D(), ABSOLUTE_VERTICAL);
        if (angleFromVertical > 90) {
            this.camera.rotateVertically(angleFromVertical - 90);
        }
    }
    public void lookUp(int pixels) {
        this.camera.rotateVertically(pixels * this.lookDegrees);
        
        double angleFromVertical = MyVector.angleBetween(this.camera.getY2D(), ABSOLUTE_VERTICAL);
        if (angleFromVertical > 90) {
            this.camera.rotateVertically(90 - angleFromVertical);
        }
    }
    
    public MyVector getVelocity() {
        return this.velocity;
    }
    
    public Camera getCamera() {
        return this.camera;
    }
    
    
    
    
    
}
