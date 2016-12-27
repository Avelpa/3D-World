/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyVector;
import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author caius
 */
public class Triangle extends Polygon {

    Color colour;
    
    private MyVector[] corners = new MyVector[3];
    private MyVector centerPoint = null;

    public Triangle(MyVector v1, MyVector v2, MyVector v3) {
        super(new int[] {(int) v1.x, (int) v2.x, (int) v3.x},
                new int[] {(int) v1.y, (int) v2.y, (int) v3.y},
                3);
        
        corners[0] = v1;
        corners[1] = v2;
        corners[2] = v3;
        
        this.centerPoint = Surface.getPolygonCenter(new ArrayList<MyVector>(Arrays.asList(corners)));
    }
    
    public Polygon getProjection(Camera camera) {
        Projection[] projPoints = new Projection[3];
        for (int i = 0; i < 3; i ++) {
            projPoints[i] = camera.getProjection(corners[i]);
        }
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        for (int i = 0; i < 3; i ++) {
            xPoints[i] = (int)projPoints[i].screenCoords.x;
            yPoints[i] = (int)projPoints[i].screenCoords.y;
        }
        
        return new Polygon(xPoints, yPoints, 3);
    }

    public MyVector[] getCorners() {
        return this.corners;
    }
    public Projection[] getProjectedCorners(Camera camera) {
        Projection[] projectedCorners = new Projection[3];
        for (int i = 0; i < 3; i ++) {
            projectedCorners[i] = camera.getProjection(corners[i]);
        }
        return projectedCorners;
    }
    
    public MyVector getCenter() {
        return this.centerPoint;
    }
    
    
    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    @Override
    public String toString() {
        String str = "(";
        for (int i = 0; i < 3; i ++) {
            str += corners[i];
            str += " ";
        }
        str += ")";
        return str;
    }

}
