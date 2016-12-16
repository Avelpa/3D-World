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
        
        MyVector point = new MyVector(1, 1, -1);
        
        MyVector axis = new MyVector(1, -1, 0);
        MyVector pointOnAxis = new MyVector(0.5, 0.5, -0.5);
        double degrees = 180;
        
        System.out.println(point);
        MyVector rot = MyMatrix.rotate(point, axis, pointOnAxis, degrees);
        
        System.out.println(rot);
    }
}
