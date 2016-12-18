/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyVector;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dmitry
 */
public class MyMatrix {
    private List<MyVector> rows;
    private int numRows, numCols;
    public MyMatrix() {
        rows = new ArrayList();
    }
    
    /*
    *   The index is zero based.
    */
    public void insertRow(int index, MyVector vector) {
        rows.add(index, vector);
        numRows ++;
    }
    public void appendRow(MyVector vector) {
        insertRow(numRows, vector);
    }
    
    /*
    * At the moment can only multiply by one vector
    */
    public MyVector mult(MyVector a) {
        return new MyVector(a.dot(rows.get(0)), a.dot(rows.get(1)), a.dot(rows.get(2)));
    }
    public static MyVector rotateX(MyVector a, double degrees) {
        double radians = Math.toRadians(degrees);
        MyMatrix rotX = new MyMatrix();
        rotX.appendRow(new MyVector(1, 0, 0));
        rotX.appendRow(new MyVector(0, Math.cos(radians), -Math.sin(radians)));
        rotX.appendRow(new MyVector(0, Math.sin(radians), Math.cos(radians)));
        
        return rotX.mult(a);
    }
    public static MyVector rotateY(MyVector a, double degrees) {
        double radians = Math.toRadians(degrees);
        MyMatrix rotY = new MyMatrix();
        rotY.appendRow(new MyVector(Math.cos(radians), 0, Math.sin(radians)));
        rotY.appendRow(new MyVector(0, 1, 0));
        rotY.appendRow(new MyVector(-Math.sin(radians), 0, Math.cos(radians)));
        
        return rotY.mult(a);
    }
    public static MyVector rotateZ(MyVector a, double degrees) {
        double radians = Math.toRadians(degrees);
        MyMatrix rotZ = new MyMatrix();
        rotZ.appendRow(new MyVector(Math.cos(radians), -Math.sin(radians), 0));
        rotZ.appendRow(new MyVector(Math.sin(radians), Math.cos(radians), 0));
        rotZ.appendRow(new MyVector(0, 0, 1));
        
        return rotZ.mult(a);
    }
    /*
    public static MyVector rotateXonAxis(MyVector a, MyVector axisPoint, double degrees) {
        MyVector aRelOrigin = a.sub(axisPoint);
        aRelOrigin = MyMatrix.rotateX(aRelOrigin, degrees);
        return aRelOrigin.add(axisPoint);
    }
    public static MyVector rotateYonAxis(MyVector a, MyVector axisPoint, double degrees) {
        MyVector aRelOrigin = a.sub(axisPoint);
        aRelOrigin = MyMatrix.rotateY(aRelOrigin, degrees);
        return aRelOrigin.add(axisPoint);
    }
    public static MyVector rotateZonAxis(MyVector a, MyVector axisPoint, double degrees) {
        MyVector aRelOrigin = a.sub(axisPoint);
        aRelOrigin = MyMatrix.rotateZ(aRelOrigin, degrees);
        return aRelOrigin.add(axisPoint);
    }*/
    
    
    public static MyVector rotate(MyVector vector, MyVector axisDirVector, MyVector axisPoint, double degrees) {
        vector = vector.sub(axisPoint);
        
        MyVector XYproj = axisDirVector.projectOntoPlane(MyVector.Z, MyVector.ZERO);
        double XYangle = MyVector.angleBetween(XYproj, MyVector.X);
        double xySign = Math.signum(XYproj.scalarProject(MyVector.Y));
        
        vector = MyMatrix.rotateZ(vector, -XYangle * xySign);
        axisDirVector = MyMatrix.rotateZ(axisDirVector, -XYangle * xySign);
        
        MyVector XZproj = axisDirVector.projectOntoPlane(MyVector.Y, MyVector.ZERO);
        double XZangle = MyVector.angleBetween(XZproj, MyVector.X);
        double xzSign = Math.signum(XZproj.scalarProject(MyVector.Z.mult(-1)));
        
        vector = MyMatrix.rotateY(vector, -XZangle * xzSign);
        
        vector = MyMatrix.rotateX(vector, degrees);
        
        vector = MyMatrix.rotateY(vector, XZangle * xzSign);
        vector = MyMatrix.rotateZ(vector, XYangle * xySign);
        
        vector  = vector.add(axisPoint);
        
        return vector;
    }
}