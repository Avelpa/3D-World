/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyVector;
import java.awt.Color;

/**
 *
 * @author caius
 */
public class Light {
    
    private MyVector position;
    private double radius;
    private Color color;

    
    public Light(MyVector position, double radius, Color color) {
        this.position = position;
        this.radius = radius;
        this.color = color;
    }
    
    

    
}
