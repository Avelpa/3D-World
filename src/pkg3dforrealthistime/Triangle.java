/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyVector;
import java.awt.Color;
import java.awt.Polygon;

/**
 *
 * @author caius
 */
public class Triangle extends Polygon {

    Color colour;
    MyVector normal;

    public Triangle(int[] xPoints, int[] yPoints, int nPoints) {
        super(xPoints, yPoints, nPoints);
    }

    public static Triangle makeTriangle(MyVector v1, MyVector v2, MyVector v3) {
        int[] xArray = {(int) v1.x, (int) v2.x, (int) v3.x};
        int[] yArray = {(int) v1.y, (int) v2.y, (int) v3.y};

        return new Triangle(xArray, yArray, xArray.length);
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public MyVector getNormal() {
        return normal;
    }

    public void setNormal(MyVector normal) {
        this.normal = normal;
    }

}
