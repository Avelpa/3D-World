/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyVector;
import java.util.ArrayList;

/**
 *
 * @author caius
 */
public class Object3D {
    
    private ArrayList<Surface> surfaces = new ArrayList();
    
    public Object3D (ArrayList<Surface> surfaces){
        this.surfaces = surfaces;
    }
    
}
