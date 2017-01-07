/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyVector;
import java.awt.Color;
import java.util.HashSet;

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

    public static Color getProjectedColor(MyVector point, MyVector surfaceNormal, Color surfaceColor, HashSet<LightSource> lights) {

        boolean inRange = false;
        for (LightSource light : lights) {
            MyVector ray = point.sub(light.position);
            
            if (ray.dot(surfaceNormal) < 0 && ray.length() <= light.radius) { // the surface is within range AND the light is shining on the front of the surface
                inRange = true;

                double colorRatio = (MyVector.angleBetween(ray, surfaceNormal) - 90) / 90;
                colorRatio *= Math.pow(1 - ray.length() / light.radius, 1);
                surfaceColor = LightSource.mergeColors(light.color, surfaceColor, colorRatio);
            }
        }

        if (!inRange) {
            return Color.BLACK;
        }

        return surfaceColor;
    }

    private static Color mergeColors(Color a, Color b, double colorRatio) {
        float[] aRGB = {a.getRed(), a.getGreen(), a.getBlue()};
        float[] bRGB = {b.getRed(), b.getGreen(), b.getBlue()};

        float[] combo = new float[3];
        for (int i = 0; i < 3; i++) {
            combo[i] = (aRGB[i] + bRGB[i]) / 2;
            combo[i] *= colorRatio;
        }
        return new Color((int) combo[0], (int) combo[1], (int) combo[2]);
    }

    public Color getColor() {
        return this.color;
    }

    public MyVector getPos() {
        return this.position;
    }

}
