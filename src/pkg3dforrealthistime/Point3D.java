/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyVector;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Dmitry
 */
public class Point3D extends MyVector{
    private HashSet<Point3D> neighbours;
    private ArrayList<Surface> surfaces;
    
    public Point3D(double x, double y, double z) {
        super(x, y, z);
        neighbours = new HashSet();
        surfaces = new ArrayList();
    }
    public Point3D(MyVector point) {
        this (point.x, point.y, point.z);
    }

    public void linkTo(Point3D other) {
        if (other == null) {
            System.err.println("Linking null point");
            return;
        }

        this.neighbours.add(other);
    }
    public void unlinkFrom(Point3D other) {
        this.neighbours.remove(other);
    }
    public static void link(Point3D a, Point3D b) {
        a.linkTo(b);
        b.linkTo(a);
    }
    public static void unlink(Point3D a, Point3D b) {
        a.unlinkFrom(b);
        b.unlinkFrom(a);
    }
    
    public HashSet<Point3D> getNeighbours() {
        return this.neighbours;
    }
    public boolean partOfSurface(Surface surface) {
        return this.surfaces.contains(surface);
    }
    public void addSurface(Surface surface) {
        this.surfaces.add(surface);
    }
    public void remSurface(Surface surface) {
        this.surfaces.remove(surface);
    }
    public boolean hasSurface() {
        return !this.surfaces.isEmpty();
    }
    
    public ArrayList<Surface> getSurfaces() {
        return this.surfaces;
    }
}
