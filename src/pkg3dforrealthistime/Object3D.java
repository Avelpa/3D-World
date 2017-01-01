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
 * @author caius
 */
public class Object3D {
    
    private HashSet<Surface> surfaces = new HashSet();
    
    public Object3D (HashSet<Surface> surfaces){
        this.surfaces = surfaces;
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
