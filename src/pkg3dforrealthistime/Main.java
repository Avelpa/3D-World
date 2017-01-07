/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *
 */
package pkg3dforrealthistime;

import MyVector.MyMatrix;
import MyVector.MyVector;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * @author Dmitry
 */
public class Main extends JComponent implements KeyListener, MouseListener, MouseMotionListener {

    private JFrame frame;

    private final int WIDTH = 1600, HEIGHT = 720;
//    private final int WIDTH = 3300, HEIGHT = 890;
    private final int PPM = 100;

    static HashMap<Point3D, Projection> points = new HashMap();
    ArrayList<Point3D> potentialPoints = new ArrayList();
    HashSet<Surface> surfaces = new HashSet();
    HashSet<Object3D> objects = new HashSet();
    Surface selectedSurface = null;
    HashSet<LightSource> lights = new HashSet();

    Spectator player = null;
    Spectator observer = null;
    Spectator curPlayer = null;

    final MyVector GRAVITY = new MyVector(0, 0, -9.81);

    MyVector cursorPoint = null;
    Point3D selected = null;
    Point3D start = null;
    Point3D end = null;
    MyVector currentPlaneNormal = null;

    boolean mouseDown = false;
    int mouseButton = -1;
    HashMap<Integer, Boolean> keys;

    int prevMouseX = -1;
    int prevMouseY = -1;
    private Robot robot;
    boolean centeringCursor = false;
    private Cursor invisibleCursor;

    boolean playerActive = false;
    boolean objectSelection = false;
    enum ObjectType {
        NONE, CUBE, PYRAMID
    };
    int objectTypeIndex = 2;
    
    HashSet<MyVector> stars = new HashSet();

    public Main() {

        frame = new JFrame("3D");
        this.setPreferredSize(new Dimension((int) WIDTH, (int) HEIGHT));
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        try {
            robot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                Toolkit.getDefaultToolkit().getImage(""),
                new Point(0, 0),
                "invisible");
        keys = new HashMap();
        keys.put(KeyEvent.VK_W, false);
        keys.put(KeyEvent.VK_A, false);
        keys.put(KeyEvent.VK_S, false);
        keys.put(KeyEvent.VK_D, false);
        keys.put(KeyEvent.VK_CAPS_LOCK, false);
        keys.put(KeyEvent.VK_SPACE, false);
        keys.put(KeyEvent.VK_UP, false);
        keys.put(KeyEvent.VK_DOWN, false);
        keys.put(KeyEvent.VK_L, false);
        keys.put(KeyEvent.VK_CONTROL, false);
        keys.put(KeyEvent.VK_SHIFT, false);
        keys.put(KeyEvent.VK_LEFT, false);
        keys.put(KeyEvent.VK_RIGHT, false);
        keys.put(KeyEvent.VK_ENTER, false);

        player = new Spectator(MyVector.Z.mult(3).add(MyVector.Y.mult(0)).add(MyVector.X.mult(0)),
                MyVector.ZERO,
                new Camera(0.017, 60,
                        WIDTH, HEIGHT, PPM),
                new Point(3, 1),
                new Point(3, 3),
                new Point(3, 10),
                new Point(5, 5),
                0.12, 2
        );
        
        observer = new Spectator(MyVector.Z.mult(3).add(MyVector.Y.mult(-5)).add(MyVector.X.mult(5)),
                new MyVector(-45, 0, 0),
                new Camera(0.017, 60,
                        WIDTH, HEIGHT, PPM),
                new Point(3, 1),
                new Point(3, 3),
                new Point(9, 100),
                new Point(10, 10),
                0.12, 2
        );
        curPlayer = player;

//        lights.add(new LightSource(new MyVector(10000, 10000, 10000), 100000, Color.WHITE));
        lights.add(new LightSource(new MyVector(100, 100, 100), 800, Color.WHITE));
        lights.add(new LightSource(new MyVector(-100, -100, 100), 800, Color.WHITE));
//        lights.add(new LightSource(new MyVector(0, 0, -10000), 10010, Color.LIGHT_GRAY));
//        lights.add(new LightSource(new MyVector(0, -10000, 0), 10010, Color.LIGHT_GRAY));
//        lights.add(new LightSource(new MyVector(0, 10000, 0), 10010, Color.LIGHT_GRAY));

        ArrayList<ArrayList<Point3D>> surfaceCreationArr = new ArrayList();
        surfaceCreationArr.add(new ArrayList());
        surfaceCreationArr.get(0).add(new Point3D(200, -200, 0));
        surfaceCreationArr.get(0).add(new Point3D(200, 200, 0));
        surfaceCreationArr.get(0).add(new Point3D(-200, 200, 0));
        surfaceCreationArr.get(0).add(new Point3D(-200, -200, 0));
//        HashSet<Surface> added = addNewSurface(surfaces, surfaceCreationArr, false);
        
        spawnCube(new MyVector(100, -100, -10), new MyVector(200, 200, 10));
        
        spawnCube(new MyVector(100, -100, 1000), new MyVector(200, 2, 1000));
        spawnCube(new MyVector(100, -98, 1000), new MyVector(2, 196, 1000));
        spawnCube(new MyVector(-100, -100, 1000), new MyVector(2, 200, 1000));
        spawnCube(new MyVector(100, 98, 1000), new MyVector(196, 2, 1000));
//        Surface newS = null;
//        for (Surface sur: added) {
//            newS = sur;
//            break;
//        }
//        extendSurface(newS, MyVector.Z);
            

//        for (int i = -WIDTH / 2 / PPM; i <= WIDTH / 2 / PPM; i ++) {
//            for (int j = -HEIGHT / 2 / PPM; j <= HEIGHT / 2 / PPM; j ++) {
//                for (int h = -2; h <= 2; h ++) {
//                    points.add(new MyVector(h, i, j));
//                }
//            }
//        }

        double starDistance = 100000;
        for (int i = 0; i < 300; i ++) {
            MyVector newStar = null;
            do {
                newStar = new MyVector(starDistance, 0, 0);
                newStar = MyMatrix.rotateZ(newStar, Math.random() * 360 - 180);
                newStar = MyMatrix.rotateY(newStar, Math.random() * 360 - 180);
            } while (stars.contains(newStar));
            stars.add(newStar);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    final int OVAL_SIZE = 16;
    final Font surfaceFont = new Font("", 11, 20);
    final Font objectSelectionFont = new Font("", 11, 50);

    @Override
    public void paintComponent(Graphics g) {
        if (objectSelection) {
            drawObjectSelection(g);
        } else {
            drawGame(g);
        }
    }
    
    private void drawObjectSelection(Graphics g) {
        g.setColor(new Color(0, 0, 100));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        g.setColor(Color.ORANGE);
        g.setFont(objectSelectionFont);
        g.drawString(ObjectType.values()[objectTypeIndex].name(), WIDTH / 2, HEIGHT / 2);
    }
    private void drawGame(Graphics g) {
        int linesDrawn = 0;
        int trianglesDrawn = 0;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, (int) WIDTH, (int) HEIGHT);
        
        g.setColor(Color.WHITE);
        for (MyVector star: stars) {
            Projection starProj = player.lookAt(star);
            if (starProj.inRange) {
                g.fillOval((int)(starProj.screenCoords.x), (int)(starProj.screenCoords.y), 5, 5);
            }
        }

        HashSet<Point3D> drawnPoints = new HashSet();
        Projection pointProj = null;
        
        
        TreeMap<Double, List<Surface>> surfaceDistances = new TreeMap(Collections.reverseOrder());
        for (Surface surface: surfaces) {
            double closestDistance = Double.MAX_VALUE;
            for (Point3D point: surface.getList()) {
                double distance = player.getCamera().getPos().sub(point).length();
                if (distance < closestDistance) {
                    closestDistance = distance;
                }
            }
            if (!surfaceDistances.containsKey(closestDistance))
                surfaceDistances.put(closestDistance, new ArrayList());
            surfaceDistances.get(closestDistance).add(surface);
        }
        
        for (List<Surface> cluster : surfaceDistances.values()) {
            for (Surface surface: cluster) {
                ArrayList<Point3D> surfacePoints = surface.getList();
//                boolean inRange = false;
//                for (int i = 0; i < surfacePoints.size(); i ++) {
//                    if (player.getCamera().lineIsInFov(points.get(surfacePoints.get(i)), points.get(surfacePoints.get(i == surfacePoints.size() - 1 ? 0 : i + 1)), surfacePoints.get(i), surfacePoints.get(i == surfacePoints.size() - 1 ? 0 : i + 1))) {
//                        inRange = true;
//                        break;
//                    }
//                }
                for (Triangle triangle : surface.getTriangles()) {
                    boolean inRange = false;
                    Projection[] projectedCorners = triangle.getProjectedCorners(player.getCamera());
                    for (int i = 0; i < 3; i++) {
                        if (player.getCamera().lineIsInFov(projectedCorners[i], projectedCorners[i == 2 ? 0 : i + 1], triangle.getCorners()[i], triangle.getCorners()[i == 2 ? 0 : i + 1])) {
                            inRange = true;
                            break;
                        }
                    }
                    if (inRange) {
                        trianglesDrawn++;

                        Polygon projectedTriangle = triangle.getProjection(player.getCamera());
                        g.setColor(LightSource.getProjectedColor(triangle.getCenter(), surface.getNormal(), surface.getColor(), lights));
                        if (selectedSurface == surface) {
                            g.setColor(Color.CYAN);
                        }
                        g.fillPolygon(projectedTriangle);
    //                        g.setColor(Color.RED);
    //                        g.drawPolygon(projectedTriangle);

    //                            g.setColor(Color.RED);
    //                            Projection centerProj = player.lookAt(triangle.getCenter());
    //                            Projection normalProj = player.lookAt(surface.getNormal().add(triangle.getCenter()));
    //                            g.drawLine((int)(normalProj.screenCoords.x), (int)(normalProj.screenCoords.y), (int)centerProj.screenCoords.x, (int)centerProj.screenCoords.y);
                    }
                }
//                }

                for (Point3D point: surface.getList()) {
                    if (!drawnPoints.contains(point)) {
                        pointProj = points.get(point);

                        if (pointProj.inRange) {
                            if (point.equals(selected)) {
                                g.setColor(Color.ORANGE);
                            } else if ((end == null && point.equals(start)) || point.equals(end)) {
                                g.setColor(Color.GREEN);
                            } else {
                                g.setColor(Color.DARK_GRAY);
                            }
                            g.fillOval((int) (pointProj.screenCoords.x - OVAL_SIZE / 2), (int) (pointProj.screenCoords.y - OVAL_SIZE / 2), (int) OVAL_SIZE, (int) OVAL_SIZE);
                        }

                        drawnPoints.add(point);
                    }
                }

                if (selectedSurface == surface) {
                    Projection surfaceNormal = player.lookAt(Surface.getPolygonCenter((ArrayList<MyVector>)(ArrayList<? extends MyVector>)surface.getList()).add(surface.getNormal()));
                    Projection midpointProj = player.lookAt(Surface.getPolygonCenter((ArrayList<MyVector>)(ArrayList<? extends MyVector>)surface.getList()));

                    g.setColor(Color.RED);
                    g.drawLine((int) midpointProj.screenCoords.x, (int) midpointProj.screenCoords.y, (int) surfaceNormal.screenCoords.x, (int) surfaceNormal.screenCoords.y);
                }
            }
        }
        
        for (int i = 0; i < potentialPoints.size(); i ++) {
            pointProj = points.get(potentialPoints.get(i));
            
            if (pointProj.inRange) {
                if (potentialPoints.get(i).equals(selected)) {
                    g.setColor(Color.ORANGE);
                } else if ((end == null && potentialPoints.get(i).equals(start)) || potentialPoints.get(i).equals(end)) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.DARK_GRAY);
                }
                g.fillOval((int) (pointProj.screenCoords.x - OVAL_SIZE / 2), (int) (pointProj.screenCoords.y - OVAL_SIZE / 2), (int) OVAL_SIZE, (int) OVAL_SIZE);
            }
            
            if (i != potentialPoints.size() - 1) {
                g.setColor(Color.DARK_GRAY);
                g.drawLine((int) (pointProj.screenCoords.x), (int) (pointProj.screenCoords.y), (int) (points.get(potentialPoints.get(i + 1))).screenCoords.x, (int) (points.get(potentialPoints.get(i + 1))).screenCoords.y);
            }
        }
        
        for (LightSource light : lights) {
            Projection lightSourceProj = player.lookAt(light.getPos());
            if (lightSourceProj.inRange) {
                g.setColor(light.getColor());
                g.fillOval((int) lightSourceProj.screenCoords.x - OVAL_SIZE / 4, (int) lightSourceProj.screenCoords.y - OVAL_SIZE / 4, OVAL_SIZE / 2, OVAL_SIZE / 2);
            }
        }
//        if (observer != null) {
//            Projection playerProj = player.lookAt(player.getCamera().getPos());
//            g.setColor(Color.PINK);
//            g.fillOval((int) (playerProj.screenCoords.x - 5), (int)(playerProj.screenCoords.y - 5), 10, 10);
//            
//            g.setColor(Color.BLUE);
//            Projection lineProj = player.lookAt(player.getCamera().getPos().add(player.getCamera().getNormal().mult(100)));
//            g.drawLine((int) (playerProj.screenCoords.x), (int) (playerProj.screenCoords.y), (int) (lineProj.screenCoords.x), (int) (lineProj.screenCoords.y));
//            
//            g.setColor(Color.GREEN);
//            lineProj = player.lookAt(player.getCamera().getPos().add(player.getCamera().getY2D().mult(2)));
//            g.drawLine((int) (playerProj.screenCoords.x), (int) (playerProj.screenCoords.y), (int) (lineProj.screenCoords.x), (int) (lineProj.screenCoords.y));
//            
//            g.setColor(Color.RED);
//            lineProj = player.lookAt(player.getCamera().getPos().add(player.getCamera().getX2D().mult(2)));
//            g.drawLine((int) (playerProj.screenCoords.x), (int) (playerProj.screenCoords.y), (int) (lineProj.screenCoords.x), (int) (lineProj.screenCoords.y));
//            
//            g.setColor(Color.ORANGE);
//            for (MyVector hitpoint: player.getHitbox()) {
//                Projection hitpointProj = player.lookAt(hitpoint.add(player.getCamera().getPos()));
//                g.fillOval((int) (hitpointProj.screenCoords.x - 3), (int)(hitpointProj.screenCoords.y - 3), 6, 6);
//            }
//        }
        
//        if (player != null) {
//            g.setColor(Color.ORANGE);
//            for (MyVector hitpoint: player.getHitbox()) {
//                Projection hitpointProj = player.lookAt(hitpoint.add(player.getCamera().getPos()));
//                g.fillOval((int) (hitpointProj.screenCoords.x - 3), (int)(hitpointProj.screenCoords.y - 3), 6, 6);
//            }
//        }
        
        if (!playerActive) {
            g.setColor(Color.RED);
            g.drawRect(0, 0, (int) WIDTH - 1, (int) HEIGHT - 1);
        } else {
            g.setColor(Color.WHITE);
            g.drawOval((int) (WIDTH / 2 - 20), (int) (HEIGHT / 2 - 20), 40, 40);
        }

        g.setFont(surfaceFont);
        g.drawString("surfaces: " + String.valueOf(surfaces.size()), 10, 30);

        g.setFont(surfaceFont);
        g.drawString("lines: " + String.valueOf(linesDrawn), 10, 60);

        g.setFont(surfaceFont);
        g.drawString("triangles: " + String.valueOf(trianglesDrawn), 10, 90);
        g.drawString("points: " + String.valueOf(points.size()), 10, 120);
        g.drawString("objects: " + String.valueOf(objects.size()), 10, 150);

        g.setFont(surfaceFont);
        g.drawString("position: " + curPlayer.getCamera().getPos(), 300, 30);
        g.setFont(surfaceFont);
        g.drawString("velocity: " + curPlayer.getVelocity(), 300, 60);

//        for (Surface surface : selectedSurfaces) {
//            g.setColor(Color.CYAN);
//            MyVector centerPoint = Surface.getPolygonCenter((ArrayList<MyVector>) (ArrayList<? extends MyVector>) surface.getList());
//            g.fillOval((int) centerPoint.x, (int) centerPoint.y, 10, 10);
//        }
    }

    public void run() {

        while (true) {

            if (playerActive) {

                player.move(GRAVITY, 1d / 60);
                observer.move(MyVector.ZERO, 1d / 60);
                collidePlayer();

                for (Integer key : keys.keySet()) {
                    if (keys.get(key) == true) {
                        switch (key) {
                            case KeyEvent.VK_W:
                                curPlayer.moveForward();
                                break;
                            case KeyEvent.VK_S:
                                curPlayer.moveBackward();
                                break;
                            case KeyEvent.VK_A:
                                curPlayer.moveLeft();
                                break;
                            case KeyEvent.VK_D:
                                curPlayer.moveRight();
                                break;
                            case KeyEvent.VK_CAPS_LOCK:
                                curPlayer.moveDown();
                                break;
                            case KeyEvent.VK_SPACE:
                                curPlayer.moveUp();
                                break;
                            case KeyEvent.VK_L:
                                curPlayer = curPlayer == player ? observer : player;
                                keys.put(KeyEvent.VK_L, false);
                                break;
                            case KeyEvent.VK_DOWN:
                                if (selectedSurface != null) {
                                    objects.add(extendSurface(selectedSurface, cursorPoint));
                                }
                                selectedSurface = null;
                                keys.put(KeyEvent.VK_DOWN, false);
                                break;
                            case KeyEvent.VK_CONTROL:
                                curPlayer.toggleFlying();
                                keys.put(KeyEvent.VK_CONTROL, false);
                                break;
                        }
                    }
                }
                cursorPoint = spawnVector();
                Projection cursorProj = player.lookAt(cursorPoint);

                player.lookAt(points);

                selected = null;
                for (Point3D point : points.keySet()) {
                    if (points.get(point).inRange && point != start) {
                        double dist = points.get(point).screenCoords.sub(cursorProj.screenCoords).length();
                        if (dist <= OVAL_SIZE) {
                            selected = point;
                        }
                    }
                }

                if (mouseDown) {
                    if (mouseButton == MouseEvent.BUTTON1) {
                        switch (ObjectType.values()[objectTypeIndex]) {
                            case NONE:
                                spawnPoint();
                                break;
                            case CUBE:
                                spawnCube(selected != null ? selected : new Point3D(cursorPoint), new MyVector(1, 2, 3));
                                break;
                            case PYRAMID:
                                spawnPyramid();
                                break;
                        }
                    } else if (mouseButton == MouseEvent.BUTTON3) {
                        if (!keys.get(KeyEvent.VK_SHIFT)) {
                            start = null;
                            end = null;
                            currentPlaneNormal = null;
                            selectedSurface = null;
                        } else {
                            if (selectedSurface != null)
                                selectedSurface.flipNormal();
                        }
                    } else if (mouseButton == MouseEvent.BUTTON2) {
                        double distance = 0;
                        double smallestDistance = Integer.MAX_VALUE;
                        Surface closestSurface = null;
                        for (Surface surface : surfaces) {
                            distance = curPlayer.getCamera().getPos().sub(Surface.getPolygonCenter((ArrayList<MyVector>) (ArrayList<? extends MyVector>) surface.getList())).length();
                            if (surface.contains(cursorProj.screenCoords)) {
                                if (distance < smallestDistance) {
                                    smallestDistance = distance;
                                    closestSurface = surface;
                                }
                            }
                        }
                        selectedSurface = closestSurface;
                    }
                    mouseDown = false;
                }
            } else {
                for (Integer key : keys.keySet()) {
                    if (keys.get(key) == true) {
                        switch (key) {
                            case KeyEvent.VK_SPACE:
                                objectSelection = !objectSelection;
                                keys.put(KeyEvent.VK_SPACE, false);
                                break;
                        }
                    }
                }
                if (objectSelection) {
                    for (Integer key : keys.keySet()) {
                        if (keys.get(key) == true) {
                            switch (key) {
                                case KeyEvent.VK_ENTER:
                                    objectSelection = false;
                                    togglePlayerActive();
                                    break;
                                case KeyEvent.VK_LEFT:
                                    objectTypeIndex --;
                                    if (objectTypeIndex < 0) {
                                        objectTypeIndex = ObjectType.values().length - 1;
                                    }
                                    keys.put(KeyEvent.VK_LEFT, false);
                                    break;
                                case KeyEvent.VK_RIGHT:
                                    objectTypeIndex ++;
                                    if (objectTypeIndex >= ObjectType.values().length) {
                                        objectTypeIndex = 0;
                                    }
                                    keys.put(KeyEvent.VK_RIGHT, false);
                                    break;
                            }
                        }
                    }
                }
            }

            repaint();
            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void togglePlayerActive() {
        playerActive = !playerActive;
        if (playerActive) {
            robot.mouseMove((int) this.getLocationOnScreen().getX() + (int) WIDTH / 2, (int) this.getLocationOnScreen().getY() + (int) HEIGHT / 2);
            centeringCursor = true;
            frame.setCursor(invisibleCursor);
        } else {
            frame.setCursor(Cursor.getDefaultCursor());
        }
    }
    
    void spawnPoint() {
        boolean validPoint = true;
        Point3D newPoint = null;

        if (currentPlaneNormal != null) {
            if (selected == null) {
                newPoint = new Point3D(MyVector.extendUntilPlane(currentPlaneNormal, start, curPlayer.getCamera().getNormal(), curPlayer.getCamera().getPos()));
            } else {
                if (!selected.onPlane(currentPlaneNormal, start)) {
                    validPoint = false;
                } else {
                    newPoint = selected;
                }
            }
        } else if (validPoint) {
            if (selected == null) {
                newPoint = new Point3D(cursorPoint);
            } else {
                newPoint = selected;
            }
            if (start == null && selected != null && !selected.getSurfaces().isEmpty()) {
                currentPlaneNormal = selected.getSurfaces().get(0).getNormal();
            } else if (start != null && end != null) {
                currentPlaneNormal = newPoint.sub(end).cross(newPoint.sub(start));
                if (curPlayer.getCamera().getPos().sub(start).dot(currentPlaneNormal) < 0) {
                    currentPlaneNormal = currentPlaneNormal.mult(-1);
                }
            }
        }

        if (selected == null)
            potentialPoints.add(newPoint);
        if (validPoint) {
            if (start == null) {
                start = newPoint;
                points.put(start, player.lookAt(start));
            } else {
                if (end != null) {
                    start = end;
                }
                end = newPoint;
                points.put(end, player.lookAt(end));

                Point3D.link(start, end);
                if (selected != null) {
                    addNewSurface(surfaces, findLoops(start, end), true);
                }
            }
        }
    }
    void spawnCube(MyVector startPoint, MyVector dimensions) {
        ArrayList<ArrayList<Point3D>> surfaceList = new ArrayList();
        surfaceList.add(new ArrayList());
        
        surfaceList.get(0).add(new Point3D(startPoint));
        surfaceList.get(0).add(new Point3D(startPoint.sub(MyVector.X.mult(dimensions.x))));
        surfaceList.get(0).add(new Point3D(startPoint.sub(MyVector.X.mult(dimensions.x)).add(MyVector.Y.mult(dimensions.y))));
        surfaceList.get(0).add(new Point3D(startPoint.add(MyVector.Y.mult(dimensions.y))));
        
        Surface top = null;
        for (Surface surface: addNewSurface(surfaces, surfaceList, false)) {
            top = surface;
            break;
        }
        objects.add(extendSurface(top, Surface.getPolygonCenter((ArrayList<MyVector>) (ArrayList<? extends MyVector>) surfaceList.get(0)).sub(MyVector.Z.mult(dimensions.z))));
    }
    
    void spawnPyramid() {
        ArrayList<ArrayList<Point3D>> surfaceList = new ArrayList();
        surfaceList.add(new ArrayList());
        
        Point3D startPoint = selected != null ? selected : new Point3D(cursorPoint);
        
        surfaceList.get(0).add(new Point3D(startPoint));
        surfaceList.get(0).add(new Point3D(startPoint.sub(MyVector.X)));
        surfaceList.get(0).add(new Point3D(startPoint.sub(MyVector.X).add(MyVector.Y)));
        surfaceList.get(0).add(new Point3D(startPoint.add(MyVector.Y)));
        
        HashSet<Surface> pyramidSurfaces = addNewSurface(surfaces, surfaceList, false);
        pyramidSurfaces = addNewSurface(surfaces, surfaceList, false);
        
        Point3D tip = new Point3D(Surface.getPolygonCenter((ArrayList<MyVector>)(ArrayList<? extends MyVector>)surfaceList.get(0)).add(MyVector.Z));
        
        for (int i = 0; i < 4; i ++) {
            ArrayList<Point3D> side = new ArrayList();
            
            side.add(surfaceList.get(0).get(i));
            side.add(surfaceList.get(0).get(i == 3 ? 0 : i + 1));
            side.add(tip);
            
            surfaceList.add(side);
        }
        
        pyramidSurfaces.addAll(addNewSurface(surfaces, surfaceList, false));
        objects.add(new Object3D(pyramidSurfaces, genRandColor()));
    }
    
    public Color genRandColor() {
        return new Color(
            (int) (Math.random() * 255),
            (int) (Math.random() * 255),
            (int) (Math.random() * 255)
        );
    }
    
    public void collidePlayer() {
        for (Object3D object: objects) {
            if (player.collideWithObject(object))
                break;
        }
    }

    /*
        Finds all loops stemming from root, in points map
     */
    public ArrayList<ArrayList<Point3D>> findLoops(Point3D root, Point3D connection) {

        // loops to be returned
        ArrayList<ArrayList<Point3D>> loops = new ArrayList();

        ArrayList<ArrayList<Point3D>> potentialLoops = new ArrayList();
        potentialLoops.add(new ArrayList());
        int lastPotentialLoopIndex = potentialLoops.size() - 1;

        Point3D cur = null;
        Point3D prev = null;

        Stack<Point3D> stack = new Stack();
        stack.add(root);

        while (!stack.isEmpty()) {
            cur = stack.pop();

            int curIndex = potentialLoops.get(lastPotentialLoopIndex).indexOf(cur);
            if (curIndex == -1) {

                potentialLoops.get(lastPotentialLoopIndex).add(cur);

                // if the current node only has one child then this is not a loop. ABANDON LOOP!!! 
                if (cur.getNeighbours().size() == 1) {
                    potentialLoops.remove(lastPotentialLoopIndex);
                    lastPotentialLoopIndex--;
                } else {
                    // since the map is doubly-linked, points should-not be re-added to the stack if they are in the current loop's second-last 
                    // position (the last-added element is the one whose children are being re-added)
                    // needToDupe corresponds whether the loops are branching out (i.e., ABC, vs ABD -- if more than one child node is being added,
                    // needToDupe becomes true, and all successive child nodes create new branches
                    prev = null;
                    int curProgressSize = potentialLoops.get(lastPotentialLoopIndex).size();
                    if (curProgressSize >= 2) {
                        prev = potentialLoops.get(lastPotentialLoopIndex).get(curProgressSize - 2);
                    }
                    boolean needToDupe = false;
                    for (Point3D neighbor : cur.getNeighbours()) {
                        if (prev != neighbor) {
                            if (needToDupe) {
                                potentialLoops.add(new ArrayList(potentialLoops.get(lastPotentialLoopIndex)));
                                lastPotentialLoopIndex++;
                            } else {
                                needToDupe = true;
                            }
                            stack.add(neighbor);
                        }
                    }
                }
            } else {
                if (curIndex == 0 && potentialLoops.get(lastPotentialLoopIndex).contains(connection) && potentialLoops.get(lastPotentialLoopIndex).size() != -1) {
                    insertIntoListbySize(loops, potentialLoops.get(lastPotentialLoopIndex));
                }
                potentialLoops.remove(lastPotentialLoopIndex);
                lastPotentialLoopIndex--;
            }
        }
        return loops;
    }

    // results must be sorted in terms of list size
    public HashSet<Surface> addNewSurface(HashSet<Surface> surfaces, ArrayList<ArrayList<Point3D>> results, boolean playerMade) {
        
        HashSet<Surface> newSurfaces = new HashSet();
        
        if (results.isEmpty()) {
            return newSurfaces;
        }

        // to prevent calling surface.getList() every time and having it re-build its point list, simply store those lists in a map
        HashMap<Surface, ArrayList<Point3D>> surfacePoints = new HashMap();

        int numAddedSurfacesOfMinimumSize = 0;
        int minimumSize = 0;

        ArrayList<Point3D> curResult = null;
        for (int i = 0; i < results.size(); i++) {
            curResult = results.get(i);

            // the max number of surfaces has been added (based on surface size)
            if (numAddedSurfacesOfMinimumSize > 0 && curResult.size() > minimumSize) {
                break;
            }

            // the max number of surfaces has been added (based on surface size)
            if (numAddedSurfacesOfMinimumSize > 0 && curResult.size() > minimumSize) {
                break;
            }

            boolean surfaceExists = false;
            for (Surface surface : surfaces) {
                if (!surfacePoints.containsKey(surface)) {
                    surfacePoints.put(surface, surface.getList());
                }

                // since results is sorted in ascending size, can loop forward in search of the correct size
                if (surfacePoints.get(surface).size() != curResult.size()) {
                    continue;
                }

                if (surfacePoints.get(surface).containsAll(curResult) && curResult.containsAll(surfacePoints.get(surface))) {
                    surfaceExists = true;
                    break;
                }
            }
            if (!surfaceExists) {
                
                if (currentPlaneNormal == null) {
                    currentPlaneNormal = curResult.get(0).sub(curResult.get(1)).cross(curResult.get(0).sub(curResult.get(2)));
                }
                if (!playerMade) {
                    for (int j = 0; j < curResult.size(); j++) {
                        points.put(curResult.get(j), player.lookAt(curResult.get(j)));
                        Point3D.link(curResult.get(j), (curResult.get(j == curResult.size() - 1 ? 0 : j + 1)));
                    }
                }
                
                Surface newSurface = new Surface(curResult, currentPlaneNormal, genRandColor());
                surfaces.add(newSurface);
                newSurfaces.add(newSurface);

                minimumSize = curResult.size();
                numAddedSurfacesOfMinimumSize++;

                if (!playerMade) {
                    currentPlaneNormal = null;
                }
                
                for (Point3D point : curResult) {
                    if (potentialPoints.contains(point))
                        potentialPoints.remove(point);
                }
            }
        }
        
        return newSurfaces;
    }

    // Inserts a list into a list of lists by increasing size
    // @params:
    // lists should be sorted inascending list size
    void insertIntoListbySize(ArrayList<ArrayList<Point3D>> ascendingSurfaces, ArrayList<Point3D> potentialSurface) {
        int insIndex = 0;
        while (insIndex < ascendingSurfaces.size()) {
            if (ascendingSurfaces.get(insIndex).size() >= potentialSurface.size()) {
                break;
            }
            insIndex++;
        }
        ascendingSurfaces.add(insIndex, potentialSurface);
    }

    public MyVector spawnVector() {
        return curPlayer.getCamera().getPos().add(curPlayer.getCamera().getNormal().unit().mult(4));
    }

    private void pan(int x, int y) {
        if (!centeringCursor) {
            if (prevMouseX != -1) {
                int pixels = x - prevMouseX;
                if (pixels < 0) {
                    curPlayer.lookLeft(-pixels);
                } else {
                    curPlayer.lookRight(pixels);
                }
            }
            if (prevMouseY != -1) {
                int pixels = y - prevMouseY;
                if (pixels < 0) {
                    curPlayer.lookUp(-pixels);
                } else {
                    curPlayer.lookDown(pixels);
                }
            }
            robot.mouseMove((int) this.getLocationOnScreen().getX() + (int) WIDTH / 2, (int) this.getLocationOnScreen().getY() + (int) HEIGHT / 2);
            centeringCursor = true;
        } else {
            centeringCursor = false;
        }

        prevMouseX = x;
        prevMouseY = y;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            togglePlayerActive();
        }

        if (keys.containsKey(e.getKeyCode())) {
            keys.put(e.getKeyCode(), true);
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (keys.containsKey(e.getKeyCode())) {
            keys.put(e.getKeyCode(), false);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDown = true;
        mouseButton = e.getButton();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        prevMouseX = -1;
        prevMouseY = -1;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (playerActive) {
            pan(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (playerActive) {
            pan(e.getX(), e.getY());
        }

    }

    public Object3D extendSurface(Surface surface, MyVector endPos) {
        
        HashSet<Surface> objectSurfaces = new HashSet();
        objectSurfaces.add(surface);

        ArrayList<Point3D> initialPositions = surface.getList();
        ArrayList<Point3D> translatedPositions = new ArrayList<>();

        MyVector translation = endPos.sub(Surface.getPolygonCenter((ArrayList<MyVector>) (ArrayList<? extends MyVector>) initialPositions));

        for (Point3D point : initialPositions) {
            Point3D translatedPoint = new Point3D(point.add(translation));
            translatedPositions.add(translatedPoint);
        }
        ArrayList<ArrayList<Point3D>> listPoints = new ArrayList<>();
        listPoints.add(translatedPositions);

        objectSurfaces.addAll(addNewSurface(surfaces, listPoints, false));

        initialPositions.add(initialPositions.get(0));
        translatedPositions.add(translatedPositions.get(0));

        for (int i = 0; i < initialPositions.size() - 1; i++) {
            listPoints.clear();
            ArrayList<Point3D> newPoints = new ArrayList<>();
            newPoints.add(initialPositions.get(i));
            newPoints.add(initialPositions.get(i + 1));
            newPoints.add(translatedPositions.get(i + 1));
            newPoints.add(translatedPositions.get(i));
            listPoints.add(newPoints);

            objectSurfaces.addAll(addNewSurface(surfaces, listPoints, false));
        }
        
        return new Object3D(objectSurfaces, genRandColor());
    }
}
