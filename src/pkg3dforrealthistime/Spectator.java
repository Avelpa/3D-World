/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyVector;

/**
 *
 * @author Dmitry
 */
public class Spectator {
    private Camera camera;
    
    private double speed = 0.0;
    private double lookDegrees = 0.0;
    
    public Spectator(MyVector position, MyVector YawPitchRoll, Camera camera) {
        this.camera = camera;
        
        this.camera.moveTo(position);
        this.camera.rotateHorizontally(YawPitchRoll.x);
        this.camera.rotateVertically(YawPitchRoll.y);
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public void setLookDegrees(double degrees) {
        this.lookDegrees = degrees;
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
