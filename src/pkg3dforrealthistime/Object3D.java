/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyVector;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author caius
 */
public class Object3D {
    
    private HashSet<Surface> surfaces = new HashSet();
    
    public Object3D (HashSet<Surface> surfaces, Color color){
        this.surfaces = surfaces;
        for (Surface surface: this.surfaces) {
            surface.setColor(color);
        }
        
        this.fixNormals();
    }
    
    private void fixNormals() {
        MyVector center = this.getCenter();
        for (Surface surface: this.surfaces) {
            if (center.sub(surface.getPoint()).scalarProject(surface.getNormal()) > 0) {
                surface.flipNormal();
            }
        }
    }

    public MyVector getCenter() {
        MyVector center = MyVector.ZERO;
        for (Surface surface: this.surfaces) {
            center = center.add(Surface.getPolygonCenter((ArrayList<MyVector>)(ArrayList<? extends MyVector>)surface.getList()));
        }
        center = center.mult(1d / this.surfaces.size());
        
        return center;
    }
    
    public MyVector getPenetration(MyVector point) {
        MyVector shortestPenetration = null;
        MyVector penetration;
        for (Surface surface : this.surfaces) {
            if (point.sub(surface.getPoint()).dot(surface.getNormal()) < 0) {
                penetration = point.sub(surface.getPoint()).vectorProject(surface.getNormal());
                if (shortestPenetration == null || penetration.length() < shortestPenetration.length())
                    shortestPenetration = penetration;
            } else {
                return null;
            }
        }
        return shortestPenetration;
    }
}
