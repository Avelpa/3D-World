/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyMatrix;
import MyVector.MyVector;
import java.awt.Point;
import java.util.HashMap;

/**
 *
 * @author Dmitry
 */
public class Spectator {
    private Camera camera;

    private Point movingAccelLimit = null;
    private Point flyingAccelLimit = null;
    private Point movingVelLimit = null;
    private Point flyingVelLimit = null;
    private Point velLimit = null;
    private Point accelLimit = null;
    private double jumpSpeed = 0;
    
    private MyVector acceleration = MyVector.ZERO;
    private MyVector velocity = MyVector.ZERO;
    private double lookDegrees = 0.0;
    
//    private boolean moving = false;
    
    private boolean flying = true;
    
    private final MyVector ABSOLUTE_VERTICAL;
    private MyVector FORWARD = null;
    private MyVector RIGHT = null;
    
    public Spectator(MyVector position, MyVector YawPitchRoll, Camera camera, Point movingAccelLimit, Point flyingAccelLimit, Point movingVelLimit, Point flyingVelLimit, double lookDegrees, double jumpSpeed) {
        this.camera = camera;
        
        this.camera.moveTo(position);
        this.camera.rotateHorizontally(YawPitchRoll.x);
        this.camera.rotateVertically(YawPitchRoll.y);
        
        this.ABSOLUTE_VERTICAL = this.camera.getY2D();
        this.FORWARD = this.camera.getNormal().unit();
        this.RIGHT = this.camera.getX2D();
        
        this.movingAccelLimit = movingAccelLimit;
        this.flyingAccelLimit = flyingAccelLimit;
        this.movingVelLimit = movingVelLimit;
        this.flyingVelLimit = flyingVelLimit;
        this.lookDegrees = lookDegrees;
        this.jumpSpeed = jumpSpeed;
        
        this.velLimit = this.flyingVelLimit;
        this.accelLimit = this.flyingAccelLimit;
    }
    
    public void lookAt(HashMap<Point3D, Projection> points) {
        for (Point3D point: points.keySet()) {
            points.put(point, this.camera.getProjection(point));
        }
    }
    public Projection lookAt(MyVector point) {
        return this.camera.getProjection(point);
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
//        {
//            // if moving in multiple directions at once, cap the acceleration
//            if (this.acceleration.length() != this.accel) {
//                this.acceleration = this.acceleration.unit().mult(this.accel);
//            }
//        }

        

        if (!flying) {
            this.acceleration = this.acceleration.add(gravity);
        }
        
        this.velocity = this.velocity.add(this.acceleration.mult(time));
        MyVector horizontalComponent = this.velocity.projectOntoPlane(this.ABSOLUTE_VERTICAL, MyVector.ZERO);
        MyVector verticalComponent = this.velocity.projectOntoPlane(this.FORWARD, MyVector.ZERO);
        boolean exceededVelocity = false;
        if (horizontalComponent.length() > this.velLimit.x) {
            horizontalComponent = horizontalComponent.unit().mult(this.velLimit.x);
            exceededVelocity = true;
        }
        if (verticalComponent.length() > this.velLimit.y) {
            verticalComponent = verticalComponent.unit().mult(this.velLimit.y);
            exceededVelocity = true;
        }
        if (exceededVelocity)
            this.velocity = verticalComponent.add(horizontalComponent);
        
        this.camera.moveBy(this.velocity.mult(time));
//        moving = false;
        this.acceleration = MyVector.ZERO;
    }
    
    public void moveForward() {
//        this.acceleration = this.acceleration.add(this.camera.getNormal().unit().mult(this.accel));
        this.acceleration = this.acceleration.add(this.FORWARD.mult(this.accelLimit.x));
//        moving = true;
    }
    public void moveBackward() {
//        this.acceleration = this.acceleration.add(this.camera.getNormal().unit().mult(-this.accel));
        this.acceleration = this.acceleration.add(this.FORWARD.mult(-this.accelLimit.x));
//        moving = true;
    }
    public void moveLeft() {
//        this.acceleration = this.acceleration.add(this.camera.getX2D().unit().mult(-this.accel));
        this.acceleration = this.acceleration.add(this.RIGHT.mult(-this.accelLimit.x));
//        moving = true;
    }
    public void moveRight() {
//        this.acceleration = this.acceleration.add(this.camera.getX2D().unit().mult(this.accel));
        this.acceleration = this.acceleration.add(this.RIGHT.mult(this.accelLimit.x));
//        moving = true;
    }
    public void moveDown() {
//        this.acceleration = this.acceleration.add(ABSOLUTE_VERTICAL.mult(-this.accel));
        if (flying)
            this.acceleration = this.acceleration.add(this.ABSOLUTE_VERTICAL.mult(-this.accelLimit.y));
//        moving = true;
    }
    public void moveUp() {
//        this.acceleration = this.acceleration.add(ABSOLUTE_VERTICAL.mult(this.accel));
        if (flying)
            this.acceleration = this.acceleration.add(this.ABSOLUTE_VERTICAL.mult(this.accelLimit.y));
        else
            this.velocity = this.ABSOLUTE_VERTICAL.mult(this.jumpSpeed);
//        moving = true;
    }
    
    public void toggleFlying() {
        flying = !flying;
        if (flying) {
            this.velocity = MyVector.ZERO;
            this.velLimit = this.flyingVelLimit;
            this.accelLimit = this.flyingAccelLimit;
        } else {
            this.velLimit = this.movingVelLimit;
            this.accelLimit = this.movingAccelLimit;
        }
    }
    
    public void collide(Surface surface, MyVector penetration) {
        if (flying)
            return;
        this.camera.moveBy(penetration.mult(-1));
        this.velocity = this.velocity.sub(this.velocity.vectorProject(surface.getNormal()).mult(2));
    }
    
    
    public void lookLeft(int pixels) {
        this.camera.rotateAroundRelativeAxis(ABSOLUTE_VERTICAL, pixels * this.lookDegrees);
        this.FORWARD = MyMatrix.rotate(this.FORWARD, ABSOLUTE_VERTICAL, MyVector.ZERO, pixels * this.lookDegrees);
        this.RIGHT = MyMatrix.rotate(this.RIGHT, ABSOLUTE_VERTICAL, MyVector.ZERO, pixels * this.lookDegrees);
    }
    public void lookRight(int pixels) {
        this.camera.rotateAroundRelativeAxis(ABSOLUTE_VERTICAL, pixels * (-this.lookDegrees));
        this.FORWARD = MyMatrix.rotate(this.FORWARD, ABSOLUTE_VERTICAL, MyVector.ZERO, pixels * (-this.lookDegrees));
        this.RIGHT = MyMatrix.rotate(this.RIGHT, ABSOLUTE_VERTICAL, MyVector.ZERO, pixels * (-this.lookDegrees));
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
