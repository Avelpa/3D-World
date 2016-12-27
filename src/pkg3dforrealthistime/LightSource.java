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
public class LightSource {
    
    private MyVector position;
    private double radius;
    private Color color;
    
    public LightSource(MyVector position, double radius, Color color) {
        this.position = position;
        this.radius = radius;
        this.color = color;
    }
    
    public Color getProjectedColor(MyVector point, MyVector surfaceNormal) {
        MyVector ray = point.sub(this.position);
        if (ray.dot(surfaceNormal) > 0) { // the light is shining from behind the surface
            return Color.BLACK;
        }
        double colorRatio = (MyVector.angleBetween(ray, surfaceNormal) - 90) / 90;
        if (ray.length() < radius) {
            colorRatio *= Math.pow(1 - ray.length() / radius, 1);
        }
        
        Color projColor = new Color((int)(this.color.getRed() * colorRatio), (int)(this.color.getGreen() * colorRatio), (int)(this.color.getBlue() * colorRatio));
        return projColor;
    }
    
    
    public MyVector getPos() {
        return this.position;
    }
    
}
