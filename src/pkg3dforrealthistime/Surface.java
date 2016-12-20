/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

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
        if (point.x > Math.max(start.x, end.x) || point.y < Math.min(start.y, end.y) || point.y > Math.max(start.y, end.y)) {
            return false;
        }
        return true;
    }

    public boolean contains(Point3D point) {
        LinkedList<Point3D> pointList = this.getSurfaceList();
        int count = 0;
        
        for (int i = 0; i < pointList.size(); i++) {
            if(intersects(point, pointList.get(i), pointList.get(i+1))){
                count++;
            }
        }
        if(intersects(point, pointList.getLast(), pointList.getFirst())){
                count++;
            }
        return count % 2 == 0;
    }

    public LinkedList getSurfaceList() {
        LinkedList<Point3D> list = new LinkedList();
        list.add(head);
        Point3D current = head;

        do {
            for (Point3D point : head.getNeighbours()) {
                if (point.partOfSurface(this)) {
                    list.add(point);
                    current = head;
                    break;
                }
            }
        } while (current != head);
        return list;
    }
}
