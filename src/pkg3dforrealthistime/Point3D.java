/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyVector;
import java.util.HashSet;

/**
 *
 * @author Dmitry
 */
public class Point3D extends MyVector{
    HashSet<Point3D> neighbors;
    
    public Point3D(double x, double y, double z) {
        super(x, y, z);
        neighbors = new HashSet();
    }
    public Point3D(MyVector point) {
        this (point.x, point.y, point.z);
    }
    
    public void linkTo(Point3D other) {
        if (other == null) {
            System.err.println("Linking null point");
            return;
        }
        
        this.neighbors.add(other);
    }
    
    public HashSet<Point3D> getNeighbors() {
        return this.neighbors;
    }

}
