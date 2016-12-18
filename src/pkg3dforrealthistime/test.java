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
        
        /*
        MyVector normal = new MyVector(-1, 0, 0);
        MyVector x2D = new MyVector(0, 1, 0);
        MyVector y2D = new MyVector(0, 0, 1);
        
        normal = MyMatrix.rotate(normal, x2D, MyVector.ZERO,  1);
        System.out.println(normal);
        
        MyVector normal2 = null;
        for (int i = 0; i < 10000; i ++) {
            normal2 = MyMatrix.rotate(normal, y2D, MyVector.ZERO, 1);
        }
        System.out.println(normal.sub(normal2));
        
        Camera cm = new Camera(1, 90);
        cm.rotateVertically(1);
        System.out.println(cm);
        for (int i = 0; i < 13; i ++) {
            cm.rotateHorizontally(1);
        }
        System.out.println(cm);
        System.out.println(MyVector.angleBetween(cm.getNormal(), cm.getX2D()));*/
        
        MyVector point = new MyVector(1, 1, 1);
        MyVector axis = new MyVector(-0.7071067811865478,-0.707106781186547,1.414213562373095);
        System.out.println(MyVector.angleBetween(axis, point));
        point = MyMatrix.rotate(point, axis, MyVector.ZERO, 23);
        System.out.println(MyVector.angleBetween(axis, point));
    }
}
