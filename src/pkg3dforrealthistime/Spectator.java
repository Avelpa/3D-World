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
import java.util.HashSet;

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
    
    private boolean flying = true;
    private boolean slowingDown = false;
    private MyVector decceleration = MyVector.ZERO;
    
    private final MyVector ABSOLUTE_VERTICAL;
    private MyVector FORWARD = null;
    private MyVector RIGHT = null;
    
    private HashSet<MyVector> hitbox = new HashSet();
    
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
        
        this.generateHitbox();
    }
    
    private void generateHitbox() {
        this.hitbox.add(this.RIGHT.mult(0.25));
        this.hitbox.add(this.RIGHT.mult(-0.25));
        this.hitbox.add(this.FORWARD.mult(0.25));
        this.hitbox.add(this.FORWARD.mult(-0.25));
        this.hitbox.add(this.ABSOLUTE_VERTICAL.mult(0.25));
        this.hitbox.add(this.ABSOLUTE_VERTICAL.mult(-0.75));
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
        if (!flying) {
            this.acceleration = this.acceleration.add(gravity);
        }
        this.velocity = this.velocity.add(this.acceleration.mult(time));
        
        MyVector horizontalVelocity = this.velocity.projectOntoPlane(this.ABSOLUTE_VERTICAL, MyVector.ZERO);
        MyVector verticalVelocity = this.velocity.vectorProject(this.ABSOLUTE_VERTICAL);
        
        if (!slowingDown) {
            if (!this.decceleration.equals(MyVector.ZERO))
                this.decceleration = MyVector.ZERO;
            
            boolean exceededVelocity = false;
            if (horizontalVelocity.length() > this.velLimit.x) {
                horizontalVelocity = horizontalVelocity.unit().mult(this.velLimit.x);
                exceededVelocity = true;
            }
            if (verticalVelocity.length() > this.velLimit.y) {
                verticalVelocity = verticalVelocity.unit().mult(this.velLimit.y);
                exceededVelocity = true;
            }
            if (exceededVelocity) {
                this.velocity = verticalVelocity.add(horizontalVelocity);
            }
        } else {
            if (!flying && horizontalVelocity.length() < 1e-6) {
                horizontalVelocity = MyVector.ZERO;
                this.velocity = horizontalVelocity.add(verticalVelocity);
                this.decceleration = MyVector.ZERO;
            } else if (flying && this.velocity.length() < 1e-6) {
                this.velocity = MyVector.ZERO;  
                this.decceleration = MyVector.ZERO;
            } else {
                if (this.decceleration.equals(MyVector.ZERO)) {
                    MyVector verticalDecceleration = MyVector.ZERO;
                    MyVector horizontalDecceleration = horizontalVelocity.mult(-1);
                    if (flying) {
                        verticalDecceleration = verticalVelocity.mult(-1);
                    }
                    this.decceleration = horizontalDecceleration.add(verticalDecceleration);
                }
                this.velocity = this.velocity.add(this.decceleration.mult(time));
            }
        }
        
        
        this.camera.moveBy(this.velocity.mult(time));
        this.acceleration = MyVector.ZERO;
        
        slowingDown = true;
    }
    
    public void moveForward() {
//        this.acceleration = this.acceleration.add(this.camera.getNormal().unit().mult(this.accel));
        this.acceleration = this.acceleration.add(this.FORWARD.mult(this.accelLimit.x));
        slowingDown = false;
    }
    public void moveBackward() {
//        this.acceleration = this.acceleration.add(this.camera.getNormal().unit().mult(-this.accel));
        this.acceleration = this.acceleration.add(this.FORWARD.mult(-this.accelLimit.x));
        slowingDown = false;
    }
    public void moveLeft() {
//        this.acceleration = this.acceleration.add(this.camera.getX2D().unit().mult(-this.accel));
        this.acceleration = this.acceleration.add(this.RIGHT.mult(-this.accelLimit.x));
        slowingDown = false;
    }
    public void moveRight() {
//        this.acceleration = this.acceleration.add(this.camera.getX2D().unit().mult(this.accel));
        this.acceleration = this.acceleration.add(this.RIGHT.mult(this.accelLimit.x));
        slowingDown = false;
    }
    public void moveDown() {
//        this.acceleration = this.acceleration.add(ABSOLUTE_VERTICAL.mult(-this.accel));
        if (flying)
            this.acceleration = this.acceleration.add(this.ABSOLUTE_VERTICAL.mult(-this.accelLimit.y));
        slowingDown = false;
    }
    public void moveUp() {
//        this.acceleration = this.acceleration.add(ABSOLUTE_VERTICAL.mult(this.accel));
        if (flying)
            this.acceleration = this.acceleration.add(this.ABSOLUTE_VERTICAL.mult(this.accelLimit.y));
        else
            this.velocity = this.ABSOLUTE_VERTICAL.mult(this.jumpSpeed);
        slowingDown = false;
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
    
    public boolean collideWithObject(Object3D object) {
        MyVector penetration = null;
        MyVector shortestPenetration = null;
        MyVector shortestPenetrationHitpoint = null;
        for (MyVector hitpoint: this.hitbox) {
            penetration = object.getPenetration(hitpoint.add(this.camera.getPos()));
            if (penetration != null && (shortestPenetration == null || penetration.length() < shortestPenetration.length())) {
                shortestPenetration = penetration;
                shortestPenetrationHitpoint = hitpoint;
            }
        }
        if (shortestPenetrationHitpoint != null) {
            this.collide(shortestPenetration, shortestPenetrationHitpoint);
            return true;
        }
        return false;
    }
    
    private void collide(MyVector penetration, MyVector hitpoint) {
        if (flying)
            return;
        this.camera.moveBy(penetration.mult(-1));
//        this.velocity = this.velocity.sub(this.velocity.vectorProject(penetration.mult(-1)).mult(2));
//        this.velocity = this.velocity.vectorProject(penetration);
//        if (hitpoint.equals(this.ABSOLUTE_VERTICAL.mult(-0.75))) {
////            this.velocity = MyVector.ZERO;
//        }
//        else {
//            this.velocity = this.velocity.projectOntoPlane(penetration, MyVector.ZERO);
//        }
//        System.out.println("init vel: " + this.velocity);
        this.velocity = this.velocity.projectOntoPlane(penetration, MyVector.ZERO);
//        System.out.println("after vel: " + this.velocity);
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
    
    public HashSet<MyVector> getHitbox() {
        return this.hitbox;
    }
}
