/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyVector;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 *
 * @author caius
 */
public class Surface {

    private Point3D head;
    private MyVector normal;

    public Surface(Point3D head, MyVector normal) {
        this.head = head;
        this.normal = normal;
    }

    public boolean intersects(Point3D point, Point3D start, Point3D end) {
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

    public boolean contains(Point3D point) {
        ArrayList<Projection> pointList = this.getProjList();
        pointList.add(Main.points.get(this.head));
        int count = 0;

        for (int i = 0; i < pointList.size() - 1; i++) {
            if (intersects(point, new Point3D(pointList.get(i).coords),
                    new Point3D(pointList.get(i + 1).coords))) {
                count++;
            }
        }

        return count % 2 != 0;
    }

    public ArrayList<Projection> getProjList() {
        ArrayList<Projection> list = new ArrayList();
        list.add(Main.points.get(this.head));
        Point3D current = this.head;
        ArrayList<Point3D> visited = new ArrayList();

        while (!visited.contains(current)) {
            visited.add(current);
            for (Point3D point : current.getNeighbours()) {
                if (!visited.contains(point)) {
                    if (point.partOfSurface(this)) {
                        list.add(Main.points.get(point));
                        current = point;
                        break;
                    }
                }
            }
        }
        return list;
    }

    public ProjRectangle getBoundsProj() {
        ArrayList<Projection> pointList = this.getProjList();

        Projection xMax = Main.points.get(this.head);
        Projection yMax = Main.points.get(this.head);
        Projection xMin = Main.points.get(this.head);
        Projection yMin = Main.points.get(this.head);

        for (Projection point : pointList) {
            if (point.coords.x > xMax.coords.x) {
                xMax = point;
            } else if (point.coords.x < xMin.coords.x) {
                xMin = point;
            }
            if (point.coords.y > yMax.coords.y) {
                yMax = point;
            } else if (point.coords.y < yMin.coords.y) {
                yMin = point;
            }
        }

        double width = xMax.coords.x - xMin.coords.x;
        double height = yMax.coords.y - yMin.coords.y;

        return new ProjRectangle(xMin, yMin, (int) width, (int) height);
    }

    public ArrayList<Point3D> getList() {
        ArrayList<Point3D> list = new ArrayList();
        list.add(new Point3D(this.head));
        Point3D current = this.head;
        HashSet<Point3D> visited = new HashSet();

        while (!visited.contains(current)) {
            visited.add(current);
            for (Point3D point : current.getNeighbours()) {
                if (!visited.contains(point)) {
                    if (point.partOfSurface(this)) {
                        list.add(point);
                        current = point;
                        break;
                    }
                }
            }
        }
        return list;
    }

    public Rectangle getBounds() {
        ArrayList<Point3D> pointList = this.getList();

        double xMax = 0;
        double yMax = 0;
        double xMin = 0;
        double yMin = 0;

        for (Point3D point : pointList) {
            if (point.x > xMax) {
                xMax = point.x;
            } else if (point.x < xMin) {
                xMin = point.x;
            }
            if (point.y > yMax) {
                yMax = point.y;
            }
            if (point.y < yMin) {
                yMax = point.y;
            }
        }

        double width = xMax - xMin;
        double height = yMax - yMin;

        return new Rectangle((int) xMin, (int) yMin, (int) width, (int) height);
    }
    
    public MyVector getNormal() {
        return this.normal;
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

    public int[] getArrayX() {
        ArrayList<Projection> projList = this.getProjList();
        int[] xArray = new int[projList.size()];

        for (int i = 0; i < projList.size(); i++) {
            xArray[i] = (int) projList.get(i).coords.x;
        }

        return xArray;
    }

    public int[] getArrayY() {
        ArrayList<Projection> projList = this.getProjList();
        int[] yArray = new int[projList.size()];
        for (int i = 0; i < projList.size(); i++) {
            yArray[i] = (int) projList.get(i).coords.y;
        }

        return yArray;
    }

    public HashSet<Triangle> getTriangles(ArrayList<Projection> surfaceProj, int interval) {
        ArrayList<MyVector> surfacePoints = new ArrayList();

        for (Projection proj : surfaceProj) {
            surfacePoints.add(proj.coords);
        }

        HashSet<Triangle> triangles = new HashSet(interval * interval);

        MyVector top = surfacePoints.get(0);

        for (MyVector point : surfacePoints) {
            if (point.y < top.y) {
                top = point;
            }
        }

        surfacePoints.remove(top);

        MyVector vRight = surfacePoints.get(0);
        MyVector vLeft = surfacePoints.get(1);

        for (MyVector point : surfacePoints) {
            if (point.x > vRight.x) {
                vRight = point;
            } else if (point.x < vLeft.x) {
                vLeft = point;
            }
        }

        MyVector vertex = top;

        MyVector xShift = (vRight.sub(vLeft)).mult((double) 1 / interval);
        MyVector yShift = (vLeft.sub(vertex)).mult((double) 1 / interval);

        vRight = vertex.add((vRight.sub(vertex)).mult((double) 1 / interval));
        vLeft = vertex.add(yShift);

        MyVector referenceVertex = vertex;
        MyVector referenceVRight = vRight;
        MyVector referenceVLeft = vLeft;

        int pattern = 1;

        for (int i = 0; i < interval; i++) {
            vertex = referenceVertex;
            vRight = referenceVRight;
            vLeft = referenceVLeft;

            for (int j = 1; j <= pattern; j++) {
                if ((j % 2) == 0) {
                    triangles.add(Triangle.makeTriangle(vertex, vertex.add(xShift), vRight));
                    vertex = vertex.add(xShift);
                    vRight = vRight.add(xShift);
                    vLeft = vLeft.add(xShift);
                } else {
                    triangles.add(Triangle.makeTriangle(vertex, vRight, vLeft));
                }
            }
            pattern += 2;
            referenceVertex = referenceVertex.add(yShift);
            referenceVLeft = referenceVLeft.add(yShift);
            referenceVRight = referenceVRight.add(yShift);
        }

        return triangles;
    }
}
