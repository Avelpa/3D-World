/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyVector;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author caius
 */
public class Surface {

    private Point3D head = null;
    private MyVector normal = null;

    private HashSet<Triangle> triangles = null;

    private static final int NUM_TRIANGLES_ALONG_SIDE = 4;

    private final Color color;

    public Surface(ArrayList<Point3D> points, MyVector normal, Color color) {
        this.head = points.get(0);
        this.normal = normal;

        for (Point3D point : points) {
            point.addSurface(this);
        }

        this.triangles = new HashSet();
        this.generateTriangles();

        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }
    
    public MyVector getPoint() {
        return this.head;
    }
    
//    public boolean intersects(MyVector point, MyVector start, MyVector end) {
//        if (start.y > end.y) {
//            intersects(point, end, start);
//        }
//
//        if (point.y <= Math.min(start.y, end.y) || point.y >= Math.max(start.y, end.y) || point.x > Math.max(start.x, end.x)) {
//            return false;
//        }
//        if (point.x < Math.min(start.x, end.x)) {
//            return true;
//        }
//
//        double slopeEnd = Math.abs((end.y - point.y) / (end.x - point.x));
//        double slopeStart = Math.abs((point.y - start.y) / (point.x - start.x));
//
//        if (end.x < start.x) {
//            if (slopeEnd < slopeStart) {
//                return false;
//            }
//        } else if (end.x > start.x) {
//            if (slopeEnd > slopeStart) {
//                return false;
//            }
//        }
//
//        return true;
//    }

    public MyVector getHeadVectorProj() {
        return Main.points.get(head).screenCoords;
    }

    public MyVector getHead() {
        return this.head;
    }

    public boolean intersects(MyVector point, MyVector start, MyVector end) {
        if (start.y > end.y) {
            intersects(point, end, start);
        }

        if (point.y <= Math.min(start.y, end.y) || point.y >= Math.max(start.y, end.y) || point.x > Math.max(start.x, end.x)) {
            return false;
        }
        if (point.x < Math.min(start.x, end.x)) {
            return true;
        }

        double slopeEnd = Math.abs((end.y - point.y) / (end.x - point.x));
        double slopeStart = Math.abs((point.y - start.y) / (point.x - start.x));

        if (end.x < start.x) {
            if (slopeEnd < slopeStart) {
                return false;
            }
        } else if (end.x > start.x) {
            if (slopeEnd > slopeStart) {
                return false;
            }
        }

        return true;
    }

    public boolean contains(MyVector point) {
        ArrayList<Projection> pointList = this.getProjList(Main.points);
        pointList.add(pointList.get(0));
        int count = 0;

        for (int i = 0; i < pointList.size() - 1; i++) {
            if (intersects(point, new Point3D(pointList.get(i).screenCoords),
                    new Point3D(pointList.get(i + 1).screenCoords))) {
                count++;
            }
        }

        return count % 2 != 0;
    }
//    public ProjRectangle getBoundsProj() {
//        ArrayList<Point3D> pointList = this.getList();
//
//        Projection xMax = Main.points.get(this.head);
//        Projection yMax = Main.points.get(this.head);
//        Projection xMin = Main.points.get(this.head);
//        Projection yMin = Main.points.get(this.head);
//
//        for (Point3D point : pointList) {
//            if (point.screenCoords.x > xMax.screenCoords.x) {
//                xMax = point;
//            } else if (point.screenCoords.x < xMin.screenCoords.x) {
//                xMin = point;
//            }
//            if (point.screenCoords.y > yMax.screenCoords.y) {
//                yMax = point;
//            } else if (point.screenCoords.y < yMin.screenCoords.y) {
//                yMin = point;
//            }
//        }
//
//        double width = xMax.screenCoords.x - xMin.screenCoords.x;
//        double height = yMax.screenCoords.y - yMin.screenCoords.y;
//
//        return new ProjRectangle(xMin, yMin, (int) width, (int) height);
//    }

    public ArrayList<Projection> getProjList(HashMap<Point3D, Projection> pointProjPairs) {
        ArrayList<Projection> projList = new ArrayList();
        ArrayList<Point3D> list = this.getList();
        for (Point3D point : list) {
            projList.add(pointProjPairs.get(point));
        }
        return projList;
    }

    public ArrayList<Point3D> getList() {
        ArrayList<Point3D> list = new ArrayList();
        Point3D current = this.head;
        HashSet<Point3D> visited = new HashSet();

        list.add(current);

        boolean loopedBack = false;
        while (!loopedBack) {
            visited.add(current);

            loopedBack = true;
            for (Point3D point : current.getNeighbours()) {
                if (!visited.contains(point) && point.partOfSurface(this)) {
                    list.add(point);
                    current = point;
                    loopedBack = false;
                    break;
                }
            }
        }
        return list;
    }

//    public Rectangle getBounds() {
//        ArrayList<Point3D> pointList = this.getList();
//
//        double xMax = 0;
//        double yMax = 0;
//        double xMin = 0;
//        double yMin = 0;
//
//        for (Point3D point : pointList) {
//            if (point.x > xMax) {
//                xMax = point.x;
//            } else if (point.x < xMin) {
//                xMin = point.x;
//            }
//            if (point.y > yMax) {
//                yMax = point.y;
//            }
//            if (point.y < yMin) {
//                yMax = point.y;
//            }
//        }
//
//        double width = xMax - xMin;
//        double height = yMax - yMin;
//
//        return new Rectangle((int) xMin, (int) yMin, (int) width, (int) height);
//    }
    public MyVector getNormal() {
        return this.normal;
    }
    public void flipNormal() {
        this.normal = this.normal.mult(-1);
    }
    
    @Override
    public String toString() {
        String str = "";
        ArrayList<Point3D> points = this.getList();
        for (Point3D point : points) {
            str += point + " ";
        }
        return str;
    }

//    public int[] getArrayX() {
//        ArrayList<Projection> projList = this.getProjList();
//        int[] xArray = new int[projList.size()];
//
//        for (int i = 0; i < projList.size(); i++) {
//            xArray[i] = (int) projList.get(i).screenCoords.x;
//        }
//
//        return xArray;
//    }
//    public int[] getArrayY() {
//        ArrayList<Projection> projList = this.getProjList();
//        int[] yArray = new int[projList.size()];
//        for (int i = 0; i < projList.size(); i++) {
//            yArray[i] = (int) projList.get(i).screenCoords.y;
//        }
//
//        return yArray;
//    }
    public static MyVector getPolygonCenter(ArrayList<MyVector> points) {
        MyVector centerPoint = new Point3D(0, 0, 0);

        for (MyVector point : points) {
            centerPoint = centerPoint.add(point);
        }

        centerPoint = centerPoint.mult(1d / points.size());

        return centerPoint;
    }

    private ArrayList<ArrayList<MyVector>> getMainTriangles(ArrayList<MyVector> points) {
        ArrayList<ArrayList<MyVector>> mainTriangles = new ArrayList();

        if (points.size() == 3) {
            mainTriangles.add(points);
            return mainTriangles;
        }

        MyVector centerPoint = getPolygonCenter(points);

        points.add(points.get(0));

        ArrayList<MyVector> newTriangle = new ArrayList();

        for (int i = 1; i < points.size(); i++) {
            newTriangle.clear();
            newTriangle.add(points.get(i));
            newTriangle.add(points.get(i - 1));
            newTriangle.add(centerPoint);

            mainTriangles.add(new ArrayList(newTriangle));
        }
        return mainTriangles;
    }

    public void generateTriangles() {
        if (!this.triangles.isEmpty())
            this.triangles.clear();

        ArrayList<ArrayList<MyVector>> mainTriangles = getMainTriangles((ArrayList<MyVector>) (ArrayList<? extends MyVector>) this.getList());
        for (ArrayList<MyVector> mainTriangle : mainTriangles) {
            MyVector topVector = mainTriangle.get(1).sub(mainTriangle.get(0)).mult(1d / Surface.NUM_TRIANGLES_ALONG_SIDE);
            MyVector bottomVector = mainTriangle.get(2).sub(mainTriangle.get(0)).mult(1d / Surface.NUM_TRIANGLES_ALONG_SIDE);
            MyVector oppositeSide = mainTriangle.get(2).sub(mainTriangle.get(1)).mult(1d / Surface.NUM_TRIANGLES_ALONG_SIDE);

            MyVector startingVertex = mainTriangle.get(0);

            // Add all upward-facing triangles
            int numTrianglesOnCurrentLevel = Surface.NUM_TRIANGLES_ALONG_SIDE;
            for (int i = 0; i < Surface.NUM_TRIANGLES_ALONG_SIDE; i++) {
                MyVector rowStartingVertex = startingVertex.add(topVector.mult(i));
                for (int j = 0; j < numTrianglesOnCurrentLevel; j++) {
                    triangles.add(new Triangle(rowStartingVertex, rowStartingVertex.add(bottomVector), rowStartingVertex.add(topVector)));
                    rowStartingVertex = rowStartingVertex.add(bottomVector);
                }
                numTrianglesOnCurrentLevel--;
            }
            // Add all downward-facing triangles
            numTrianglesOnCurrentLevel = Surface.NUM_TRIANGLES_ALONG_SIDE - 1;
            for (int i = 1; i <= Surface.NUM_TRIANGLES_ALONG_SIDE - 1; i++) {
                MyVector rowStartingVertex = startingVertex.add(topVector.mult(i));
                for (int j = 0; j < numTrianglesOnCurrentLevel; j++) {
                    triangles.add(new Triangle(rowStartingVertex, rowStartingVertex.add(oppositeSide), rowStartingVertex.add(bottomVector)));
                    rowStartingVertex = rowStartingVertex.add(bottomVector);
                }
                numTrianglesOnCurrentLevel--;
            }
        }
    }
    //        ArrayList<MyVector> surfacePoints = new ArrayList();

    //        for (ArrayList<Projection> mainTriangle : mainTriangles) {
    //
    //            surfacePoints.clear();
    //
    //            for (Projection proj : mainTriangle) {
    //                surfacePoints.add(proj.screenCoords);
    //            }
    //
    //            MyVector vertex = null;
    //
    //            for (MyVector point : surfacePoints) {
    //                if (vertex == null || point.y < vertex.y) {
    //                    vertex = point;
    //                }
    //            }
    //
    //            surfacePoints.remove(vertex);
    //
    //            MyVector vRight = null;
    //            MyVector vLeft = null;
    //
    //            for (MyVector point : surfacePoints) {
    //                if (vRight == null || point.x > vRight.x) {
    //                    vRight = point;
    //                } else if (vLeft == null || point.x < vLeft.x) {
    //                    vLeft = point;
    //                }
    //            }
    //
    //            MyVector xShift = (vRight.sub(vLeft)).mult((double) 1 / interval);
    //            MyVector yShift = (vLeft.sub(vertex)).mult((double) 1 / interval);
    //
    //            vRight = vertex.add((vRight.sub(vertex)).mult((double) 1 / interval));
    //            vLeft = vertex.add(yShift);
    //
    //            MyVector referenceVertex = vertex;
    //            MyVector referenceVRight = vRight;
    //            MyVector referenceVLeft = vLeft;
    //
    //            int pattern = 1;
    //
    //            for (int i = 0; i < interval; i++) {
    //                vertex = referenceVertex;
    //                vRight = referenceVRight;
    //                vLeft = referenceVLeft;
    //
    //                for (int j = 1; j <= pattern; j++) {
    //                    if ((j % 2) == 0) {
    //                        triangles.add(Triangle.makeTriangle(vertex, vertex.add(xShift), vRight));
    //                        vertex = vertex.add(xShift);
    //                        vRight = vRight.add(xShift);
    //                        vLeft = vLeft.add(xShift);
    //                    } else {
    //                        triangles.add(Triangle.makeTriangle(vertex, vRight, vLeft));
    //                    }
    //                }
    //                pattern += 2;
    //                referenceVertex = referenceVertex.add(yShift);
    //                referenceVLeft = referenceVLeft.add(yShift);
    //                referenceVRight = referenceVRight.add(yShift);
    //            }
    //        }
    //        return triangles;
    //    }
    public HashSet<Triangle> getTriangles() {
        return this.triangles;
    }
    
//    @Override
//    public int compareTo(Object o) { // distance
//        Surface other = (Surface)o;
//        
//        distance = curPlayer.getCamera().getPos().sub(Surface.getPolygonCenter((ArrayList<MyVector>) (ArrayList<? extends MyVector>) surface.getList())).length();
//    }
}
