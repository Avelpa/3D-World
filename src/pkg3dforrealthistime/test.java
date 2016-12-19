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
public class test {
    public static void main(String[] args) {
        
//        Camera camera = new Camera(0.017, 90);
        
        /*
        MyVector offset = new MyVector(10, 0, 0);
        
        MyVector orthog1 = MyMatrix.rotate(new MyVector(-1, 0, 0), MyVector.Y, MyVector.ZERO, 12);
        MyVector orthog2 = MyMatrix.rotateY(orthog1, 90);
        
        MyVector orthog1Proj = camera.getProjection(orthog1.mult(200).add(offset), 4, 4);
        MyVector orthog2Proj = camera.getProjection(orthog2.mult(200).add(offset), 4, 4);
        
        System.out.println(orthog1Proj);
        System.out.println(orthog2Proj);
        System.out.println(MyVector.angleBetween(orthog1Proj, orthog2Proj));*/
        
        final int SCR_WIDTH = 4;
        final int SCR_HEIGHT = 4;
        
//        camera.moveTo(0, 0, 0);
        
//        System.out.println(camera.getProjection(MyVector.Z, SCR_WIDTH, SCR_HEIGHT));
        /*
        MyVector center = MyVector.ZERO;
        MyVector offset = new MyVector(-10, 0, 0);
        
        MyVector point = new MyVector(0, 0, 1).add(offset); 
        
        MyVector oldProjPoint = camera.getProjection(point, SCR_WIDTH, SCR_HEIGHT);
        System.out.println("point: " + point + "\n--------------");
        
        System.out.println("real before: " + point.sub(offset));
        
        
        MyVector oldPoint = point;
        point = MyMatrix.rotate(point, MyVector.X, center, -10);
        System.out.println("real point: " + point.sub(offset));
        System.out.println("real point len: " + point.sub(offset).length());
        System.out.println("real angle: " + MyVector.angleBetween(point.sub(offset), MyVector.Z));
        
        MyVector projPoint = camera.getProjection(point, SCR_WIDTH, SCR_HEIGHT);
        
        System.out.println("--------\nproj before: " + oldProjPoint);
        MyVector referenceProj = camera.getProjection(MyVector.Z.sub(MyVector.X), SCR_WIDTH, SCR_HEIGHT);
        System.out.println("proj point: " + projPoint);
        System.out.println("proj point len: " + projPoint.length());
        System.out.println("proj angle: " + MyVector.angleBetween(projPoint, referenceProj));
*/
        
        System.out.println(0.5 % 1);
    }
}
