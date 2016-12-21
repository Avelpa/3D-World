/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import java.awt.Rectangle;
import java.util.ArrayList;
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

    //
    
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
        LinkedList<Point3D> pointList = this.getSurfaceList();
        int count = 0;

        for (int i = 0; i < pointList.size() - 1; i++) {
            if (intersects(point, pointList.get(i), pointList.get(i + 1))) {
                count++;
            }
        }
        return count % 2 != 0;
    }

    public LinkedList<Point3D> getSurfaceList() {
        LinkedList<Point3D> list = new LinkedList();
        list.add(new Point3D(Main.points.get(this.head).coords));
        Point3D current = this.head;
        ArrayList<Point3D> visited = new ArrayList();
        visited.add(this.head);
        
        do {
            visited.add(current);
            for (Point3D point : current.getNeighbours()) {
                if (point.partOfSurface(this)) {
                    list.add(new Point3D(Main.points.get(point).coords));
                    current = point;
                    break;
                }
            }
        } while (!visited.contains(current));
        return list;
    }

    public Rectangle getBounds() {
        LinkedList<Point3D> pointList = this.getSurfaceList();

        double xMax = 0;
        double yMax = 0;
        double xMin = 0;
        double yMin = 0;

        for (Point3D point : pointList) {
             if(point.x > xMax)
                 xMax = point.x;
             else if(point.x < xMin)
                 xMin = point.x;
             if(point.y > yMax)
                 yMax = point.y;
             if(point.y < yMin)
                 yMax = point.y;
        }
        
        double width = xMax - xMin;
        double height = yMax - yMin;
              
        return new Rectangle((int)xMin, (int)yMin, (int)width, (int)height);
    }
}
