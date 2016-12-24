/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *
 */
package pkg3dforrealthistime;

import MyVector.MyVector;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * @author Dmitry
 */
public class Main extends JComponent implements KeyListener, MouseListener, MouseMotionListener {

    private JFrame frame;

    private final int WIDTH = 1366, HEIGHT = 720;
//    private final double WIDTH = 3300, HEIGHT = 890;
    private final int PPM = 100;

    static HashMap<Point3D, Projection> points = new HashMap();
    HashSet<Surface> surfaces = new HashSet();

    Spectator player = null;
    Spectator observer = null;
    Spectator curPlayer = null;

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
    int tessellationInterval = 2;

    public Main() {

        frame = new JFrame("3D");
        this.setPreferredSize(new Dimension((int)WIDTH, (int)HEIGHT));
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

        player = new Spectator(MyVector.X.mult(20), MyVector.ZERO, new Camera(0.017, 60, WIDTH, HEIGHT, PPM));
        player.setAccel(0.0015);
        player.setMaxVel(0.04);
        player.setLookDegrees(0.12);
        
//        observer = new Spectator(new MyVector(30, -20, 5), new MyVector(-60, -5, 0), new Camera(0.017, 61, WIDTH / 2, HEIGHT, PPM));
//        observer.setAccel(0.0015);
//        observer.setMaxVel(0.08);
//        observer.setLookDegrees(0.12);

        curPlayer = player;
        
//        for (int i = -WIDTH / 2 / PPM; i <= WIDTH / 2 / PPM; i ++) {
//            for (int j = -HEIGHT / 2 / PPM; j <= HEIGHT / 2 / PPM; j ++) {
//                for (int h = -2; h <= 2; h ++) {
//                    points.add(new MyVector(h, i, j));
//                }
//            }
//        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    final int OVAL_SIZE = 40;
    final Font surfaceFont = new Font("comic sans", 11, 20);

    @Override
    public void paintComponent(Graphics g) {
        int linesDrawn = 0;
        g.clearRect(0, 0, (int)WIDTH, (int)HEIGHT);

//        if (observer != null) {
//            Camera observerCam = observer.getCamera();
//            // second camera
//            for (Point3D point: points.keySet()) {
//                Projection pointProj = observerCam.getProjection(point);
//                if (pointProj.inRange) {
//                    if (!points.get(point).inFront)
//                        g.setColor(Color.BLACK);
//                    else
//                        g.setColor(Color.DARK_GRAY);
//                    g.fillOval((int) (pointProj.screenCoords.x - OVAL_SIZE / 2 + WIDTH / 2), (int) (pointProj.screenCoords.y - OVAL_SIZE / 2), (int) OVAL_SIZE, (int) OVAL_SIZE);
//                }
//            }
//            g.setColor(Color.MAGENTA);
//            Projection camPos = observerCam.getProjection(player.getCamera().getPos());
//            if (camPos.inRange) {
//                g.fillOval((int) (camPos.screenCoords.x - OVAL_SIZE / 4 + WIDTH / 2), (int) (camPos.screenCoords.y - OVAL_SIZE / 4), (int) OVAL_SIZE / 2, (int) OVAL_SIZE / 2);
//            
//                g.setColor(Color.BLUE);
//                Projection line = observerCam.getProjection(player.getCamera().getNormal().mult(200).add(player.getCamera().getPos()));
//                if (line.inRange) {
//                    g.drawLine((int)line.screenCoords.x + (int)WIDTH / 2, (int)line.screenCoords.y, (int)camPos.screenCoords.x + (int)WIDTH / 2, (int)camPos.screenCoords.y);
//                }
//                g.setColor(Color.RED);
//                line = observerCam.getProjection(player.getCamera().getX2D().mult(10).add(player.getCamera().getPos()));
//                if (line.inRange) {
//                    g.drawLine((int)line.screenCoords.x + (int)WIDTH / 2, (int)line.screenCoords.y, (int)camPos.screenCoords.x + (int)WIDTH / 2, (int)camPos.screenCoords.y);
//                }
//                g.setColor(Color.GREEN);
//                line = observerCam.getProjection(player.getCamera().getY2D().mult(10).add(player.getCamera().getPos()));
//                if (line.inRange) {
//                    g.drawLine((int)line.screenCoords.x + (int)WIDTH / 2, (int)line.screenCoords.y, (int)camPos.screenCoords.x + (int)WIDTH / 2, (int)camPos.screenCoords.y);
//                }
//                
//                g.setColor(Color.ORANGE);
//                line = observerCam.getProjection(MyMatrix.rotate(player.getCamera().getNormal().mult(400), player.getCamera().getY2D(), MyVector.ZERO, -player.getCamera().getHorizontalFov() / 2).add(player.getCamera().getPos()));
//                g.drawLine((int)line.screenCoords.x + (int)WIDTH / 2, (int)line.screenCoords.y, (int)camPos.screenCoords.x + (int)WIDTH / 2, (int)camPos.screenCoords.y);
//                line = observerCam.getProjection(MyMatrix.rotate(player.getCamera().getNormal().mult(400), player.getCamera().getY2D(), MyVector.ZERO, player.getCamera().getHorizontalFov()/ 2).add(player.getCamera().getPos()));
//                g.drawLine((int)line.screenCoords.x + (int)WIDTH / 2, (int)line.screenCoords.y, (int)camPos.screenCoords.x + (int)WIDTH / 2, (int)camPos.screenCoords.y);
//                line = observerCam.getProjection(MyMatrix.rotate(player.getCamera().getNormal().mult(400), player.getCamera().getX2D(), MyVector.ZERO, player.getCamera().getVerticalFov() / 2).add(player.getCamera().getPos()));
//                g.drawLine((int)line.screenCoords.x + (int)WIDTH / 2, (int)line.screenCoords.y, (int)camPos.screenCoords.x + (int)WIDTH / 2, (int)camPos.screenCoords.y);
//                line = observerCam.getProjection(MyMatrix.rotate(player.getCamera().getNormal().mult(400), player.getCamera().getX2D(), MyVector.ZERO, -player.getCamera().getVerticalFov()/ 2).add(player.getCamera().getPos()));
//                g.drawLine((int)line.screenCoords.x + (int)WIDTH / 2, (int)line.screenCoords.y, (int)camPos.screenCoords.x + (int)WIDTH / 2, (int)camPos.screenCoords.y);
//                
//                g.setColor(new Color(0.2f, 0, 0.1f, 0.5f));
//                Projection right = observerCam.getProjection(player.getCamera().getX2D().mult(10).add(player.getCamera().getPos()));
//                Projection left = observerCam.getProjection(player.getCamera().getX2D().mult(-10).add(player.getCamera().getPos()));
//                Projection top = observerCam.getProjection(player.getCamera().getY2D().mult(10).add(player.getCamera().getPos()));
//                Projection bottom = observerCam.getProjection(player.getCamera().getY2D().mult(-10).add(player.getCamera().getPos()));
//                g.fillPolygon(new int[] {(int)(left.screenCoords.x + (int)WIDTH / 2), (int)top.screenCoords.x + (int)WIDTH / 2, (int)right.screenCoords.x + (int)WIDTH / 2, (int)bottom.screenCoords.x + (int)WIDTH / 2}, 
//                        new int[] {(int)(left.screenCoords.y), (int)top.screenCoords.y, (int)right.screenCoords.y, (int)bottom.screenCoords.y}, 4);
//            }
//        }
//        // end second camera

        HashSet<Point3D> visitedNeigbhors = new HashSet();
        Projection pointProj = null;
        for (Point3D point : points.keySet()) {

            for (Surface surface : surfaces) {
                g.setColor(Color.BLACK);
                /*g.setColor(Color.CYAN);
                if (surface.getProjList().getFirst().inRange) {
                Polygon polygon = new Polygon(surface.getArrayX(), surface.getArrayY(), surface.getProjList().size());
                g.fillPolygon(polygon);
                }*/

                surface.getTriangles(surface.getProjList(points), tessellationInterval).forEach((triangle) -> {
                   // System.out.println("----BEGIN");
                    //System.out.println(triangle.getBounds().x);
                    //System.out.println("----END");
                    g.setColor(Color.CYAN);
                    g.fillPolygon(triangle);
                    g.setColor(Color.BLACK);
                    g.drawPolygon(triangle.xpoints, triangle.ypoints, triangle.npoints);
                });

                /*ProjRectangle box = surface.getBoundsProj();
                int counter2 = 0;
                if (box.getX() < 0) {
                box.setX(0);
                }
                if (box.getY() < 0) {
                box.sety(0);
                }
                if (box.getX() + box.getWidth() > WIDTH) {
                box.setWidth(box.getWidth() - (box.getX() + box.getWidth() - WIDTH));
                }
                if (box.getY() + box.getHeight() > HEIGHT) {
                box.setHeight(box.getHeight() - (box.getY() + box.getHeight() - HEIGHT));
                }
                g.setColor(Color.ORANGE);
                for (int i = box.getX(); i < box.getX() + box.getWidth(); i += 40) {
                for (int j = box.getY(); j < box.getY() + box.getHeight(); j += 40) {
                Point3D projPoint = new Point3D(i, j, 0);
                if (surface.contains(projPoint)) {
                g.fillOval(i, j, 6,6);
                // counter2++;
                //System.out.println(counter2);
                }
                }
                }*/
            }

            pointProj = points.get(point);

            if (pointProj.inRange) {
                if (point.equals(selected)) {
                    g.setColor(Color.ORANGE);
                } else if ((end == null && point.equals(start)) || point.equals(end)){
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.DARK_GRAY);
                }
                g.fillOval((int) (pointProj.screenCoords.x - OVAL_SIZE / 2), (int) (pointProj.screenCoords.y - OVAL_SIZE / 2), (int) OVAL_SIZE, (int) OVAL_SIZE);
            }

            g.setColor(Color.DARK_GRAY);
            Projection neighborProj = null;
            for (Point3D neighbor : point.getNeighbours()) {
                if (visitedNeigbhors.contains(neighbor))
                    continue;
                neighborProj = points.get(neighbor);
                
                if (player.getCamera().lineIsInFov(neighborProj, pointProj, neighbor, point)) {
                    linesDrawn ++;
                    g.drawLine((int) (pointProj.screenCoords.x), (int) (pointProj.screenCoords.y), (int) (neighborProj.screenCoords.x), (int) (neighborProj.screenCoords.y));
                }
            }
            visitedNeigbhors.add(point);
        }

        if (!playerActive) {
            g.setColor(Color.RED);
            g.drawRect(0, 0, (int)WIDTH - 1, (int)HEIGHT - 1);
        } else {
            g.setColor(Color.BLACK);
            g.drawOval((int) (WIDTH / 2 - 20), (int) (HEIGHT / 2 - 20), 40, 40);
        }

        g.setFont(surfaceFont);
        g.drawString(String.valueOf(surfaces.size()), 10, 30);
        
        g.setFont(surfaceFont);
        g.drawString(String.valueOf(linesDrawn), 30, 30);
    }

    public void run() {
        while (true) {

            if (playerActive) {

                curPlayer.move();

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
                            case KeyEvent.VK_UP:
                                tessellationInterval++;
                                break;
                            case KeyEvent.VK_DOWN:
                                tessellationInterval--;
                                break;
                        }
                    }
                }
                cursorPoint = spawnVector();
                Projection cursorProj = player.lookAt(cursorPoint);

                player.lookAt(points);

                selected = null;
                double closestDist = Float.MAX_VALUE;
                for (Point3D point : points.keySet()) {
                    if (points.get(point).inRange && point != start) {
                        double dist = points.get(point).screenCoords.sub(cursorProj.screenCoords).length();
                        if (dist <= OVAL_SIZE) {
                            closestDist = dist;
                            selected = point;
                        }
                    }
                }

                if (mouseDown) {
                    if (mouseButton == MouseEvent.BUTTON1) {

                        boolean validPoint = true;
                        Point3D newPoint = null;

                        if (currentPlaneNormal != null) {
                            if (selected == null) {
                                newPoint = new Point3D(MyVector.extendUntilPlane(currentPlaneNormal, start, player.getCamera().getNormal(), player.getCamera().getPos()));
                            } else {
                                if (!selected.onPlane(currentPlaneNormal, start)) {
                                    validPoint = false;
                                } else {
                                    newPoint = selected;
                                }
                            }
                        } else if (validPoint) {
                            if (selected == null) {
                                newPoint = new Point3D(spawnVector());
                            } else {
                                newPoint = selected;
                            }
                            if (start == null && selected != null && !selected.getSurfaces().isEmpty()) {
                                currentPlaneNormal = selected.getSurfaces().get(0).getNormal();
                            } else if (start != null && end != null) {
                                currentPlaneNormal = newPoint.sub(end).cross(newPoint.sub(start));
                            }
                        }
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
                                    addNewSurface(surfaces, findLoops(start, end));
                                }
                            }
                        }
                    } else if (mouseButton == MouseEvent.BUTTON3) {
                        start = null;
                        end = null;
                        currentPlaneNormal = null;
                    }
                    mouseDown = false;
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
    public void addNewSurface(HashSet<Surface> surfaces, ArrayList<ArrayList<Point3D>> results) {
        if (results.isEmpty()) {
            return;
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
            Surface newSurface = new Surface(curResult.get(0), curResult.get(0).sub(curResult.get(1)).cross(curResult.get(0).sub(curResult.get(2))));
            for (Point3D point : curResult) {
                point.addSurface(newSurface);
            }
            surfaces.add(newSurface);

            minimumSize = curResult.size();
            numAddedSurfacesOfMinimumSize++;
        }
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
        return player.getCamera().getPos().add(player.getCamera().getNormal().unit().mult(4));
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
            robot.mouseMove((int) this.getLocationOnScreen().getX() + (int)WIDTH / 2, (int) this.getLocationOnScreen().getY() + (int)HEIGHT / 2);
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
            playerActive = !playerActive;
            if (playerActive) {
                robot.mouseMove((int) this.getLocationOnScreen().getX() + (int) WIDTH / 2, (int) this.getLocationOnScreen().getY() + (int) HEIGHT / 2);
                centeringCursor = true;
                frame.setCursor(invisibleCursor);
            } else {
                frame.setCursor(Cursor.getDefaultCursor());
            }
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
//        mouseDown = true;
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

}
