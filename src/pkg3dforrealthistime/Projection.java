/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyVector;

/**
 *
 * @author Dmitry
 */
public class Projection {
    public MyVector cartesianCoords;
    public double horHalfWidth;
    public double vertHalfHeight;
    public MyVector screenCoords;
    public boolean inRange; // these fields are used to determine whether an objerct or line should be drawn, but don't work perfectly -- need a better system
    public boolean inFront;
}
