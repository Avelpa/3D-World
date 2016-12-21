/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

/**
 *
 * @author caius
 */
public class ProjRectangle {

    private Projection projX;

    private Projection projY;
    private int width;
    private int height;

    public ProjRectangle(Projection xMin, Projection yMin, int width, int height) {
        this.projX = xMin;
        this.projY = yMin;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return (int) projX.coords.x;
    }

    public int getY() {
        return (int) projY.coords.y;
    }

    public void setX(double x) {
        this.projX.coords.x = x;
    }

    public void sety(double y) {
        this.projY.coords.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Projection getProjX() {
        return projX;
    }

    public Projection getProjY() {
        return projY;
    }

}
