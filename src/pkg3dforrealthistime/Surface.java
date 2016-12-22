/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

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

    public Surface(Point3D head) {
        this.head = head;
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
        LinkedList<Projection> pointList = this.getProjList();
        int count = 0;

        for (int i = 0; i < pointList.size() - 1; i++) {
            if (intersects(point, new Point3D(pointList.get(i).coords),
                    new Point3D(pointList.get(i + 1).coords))) {
                count++;
            }
        }
        return count % 2 != 0;
    }

    public LinkedList<Projection> getProjList() {
        LinkedList<Projection> list = new LinkedList();
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
        LinkedList<Projection> pointList = this.getProjList();

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
            System.out.println(current);
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

}
