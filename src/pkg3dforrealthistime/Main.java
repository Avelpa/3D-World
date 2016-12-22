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
    private final int PPM = 100;

    static HashMap<Point3D, Projection> points = new HashMap();
    HashSet<Surface> surfaces = new HashSet();

    Spectator player = null;

    MyVector cursorPoint = null;
    Point3D selected = null;
    Point3D start = null;
    Point3D end = null;
    boolean mouseDown = false;
    int mouseButton = -1;
    HashMap<Integer, Boolean> keys;

    int prevMouseX = -1;
    int prevMouseY = -1;
    private Robot robot;
    boolean centeringCursor = false;
    private Cursor invisibleCursor;

    boolean playerActive = false;

    public Main() {
        
        frame = new JFrame("3D");
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
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

        player = new Spectator(MyVector.X.mult(20), MyVector.ZERO, new Camera(0.017, 60, PPM));
        player.setAccel(0.0015);
        player.setMaxVel(0.04);
        player.setLookDegrees(0.12);

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

    @Override
    public void paintComponent(Graphics g) {
        g.clearRect(0, 0, WIDTH, HEIGHT);
//        g.setColor(new Color(0f, 0.9f, 0f, 0.2f));

        Projection pointProj = null;
        for (Point3D point : points.keySet()) { // need to change null return val

            pointProj = points.get(point);

            if (pointProj.inRange) {
                if (point.equals(selected)) {
                    g.setColor(Color.ORANGE);
                } else {
                    g.setColor(Color.DARK_GRAY);
                }
                g.fillOval((int) (pointProj.coords.x - OVAL_SIZE / 2), (int) (pointProj.coords.y - OVAL_SIZE / 2), (int) OVAL_SIZE, (int) OVAL_SIZE);
            }

            g.setColor(Color.DARK_GRAY);
            Projection neighborProj = null;
            for (Point3D neighbor : point.getNeighbours()) {
                neighborProj = points.get(neighbor);
                if (neighborProj.inFront || pointProj.inFront) {
                    g.drawLine((int) (pointProj.coords.x), (int) (pointProj.coords.y), (int) (neighborProj.coords.x), (int) (neighborProj.coords.y));
                }
            }
        }

        g.setColor(Color.GREEN);
        if (start != null) {
            Projection proj = points.get(start);
            if (proj.inRange) {
                g.fillOval((int) (proj.coords.x - OVAL_SIZE / 2), (int) (proj.coords.y - OVAL_SIZE / 2), (int) OVAL_SIZE, (int) OVAL_SIZE);
            }
        }
        if (end != null) {
            Projection proj = points.get(end);
            if (proj.inRange) {
                double ovalSize = player.getCamera().getPerspective(end, 41);
                g.fillOval((int) (proj.coords.x - OVAL_SIZE / 2), (int) (proj.coords.y - OVAL_SIZE / 2), (int) OVAL_SIZE, (int) OVAL_SIZE);
            }
        }

        if (!playerActive) {
            g.setColor(Color.RED);
            g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
        } else {
            g.setColor(Color.BLACK);
            g.drawOval((int) (WIDTH / 2 - 20), (int) (HEIGHT / 2 - 20), 40, 40);
        }

        for (Surface surface : surfaces) {
            ProjRectangle box = surface.getBoundsProj();
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

            for (int i = box.getX(); i < box.getX() + box.getWidth(); i += 40) {
                for (int j = box.getY(); j < box.getY() + box.getHeight(); j += 40) {
                    //MyVector projPoint = player.lookAt(new MyVector(i, j, 0), WIDTH, HEIGHT).coords;
                    Point3D projPoint = new Point3D(i, j, 0);
                    if (surface.contains(projPoint)) {
                        g.setColor(Color.BLUE);
                        g.drawOval(i, j, 5, 5);
                        // counter2++;
                        //System.out.println(counter2);
                    }
                }
            }
        }

        /* 2 CAMERA SET-UP
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, WIDTH/2, HEIGHT);
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < points.size(); i ++) {
//            MyVector cameraCoords = camera.getProjection(points.get(i), (WIDTH / PPM), (HEIGHT / PPM));
            MyVector cameraCoords = camera.getProjection(points.get(i), (WIDTH / PPM / 2), (HEIGHT / PPM / 2));
            if (cameraCoords == null)
                continue;
            MyVector scrCoords = toScreenCoords(cameraCoords);
            g.fillOval((int)(scrCoords.x-5) - WIDTH/4, (int)(scrCoords.y-5), 11, 11);
        }
        
        g.setColor(Color.RED);
        g.drawLine(WIDTH/2, 0, WIDTH/2, HEIGHT);

        g.setColor(Color.WHITE);
        g.fillRect(WIDTH/2, 0, WIDTH/2, HEIGHT);
        
        // points
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < points.size(); i ++) {
//            MyVector cameraCoords = camera.getProjection(points.get(i), (WIDTH / PPM), (HEIGHT / PPM));
            MyVector cameraCoords = camera2.getProjection(points.get(i), (WIDTH / PPM / 2), (HEIGHT / PPM / 2));
            if (cameraCoords == null)
                continue;
            MyVector scrCoords = toScreenCoords(cameraCoords);
            g.fillOval((int)(scrCoords.x-5) + WIDTH/4, (int)(scrCoords.y-5), 11, 11);
        }
        
        g.setColor(Color.MAGENTA);
        MyVector camPos = camera2.getProjection(camera.getPos(), WIDTH / PPM / 2, HEIGHT / PPM / 2);
        if (camPos != null) {
            camPos = toScreenCoords(camPos);
            g.fillOval((int)(camPos.x - 5) + WIDTH / 4, (int)(camPos.y - 5), 11, 11);
        }
        
        g.setColor(Color.BLUE);
        MyVector normPos = camera2.getProjection(camera.getNormal().mult(200).add(camera.getPos()), WIDTH / PPM / 2, HEIGHT / PPM / 2);
        if (normPos != null) {
            normPos = toScreenCoords(normPos);
            g.drawLine((int)(camPos.x) + WIDTH / 4, (int)(camPos.y), (int)(normPos.x) + WIDTH / 4, (int)(normPos.y));
        }
        
        g.setColor(Color.RED);
        MyVector y2dPos = camera2.getProjection(camera.getY2D().mult(4).add(camera.getPos()), WIDTH / PPM / 2, HEIGHT / PPM / 2);
        if (y2dPos != null) {
            y2dPos = toScreenCoords(y2dPos);
            g.drawLine((int)(camPos.x) + WIDTH / 4, (int)(camPos.y), (int)(y2dPos.x) + WIDTH / 4, (int)(y2dPos.y));
        }
        
        g.setColor(Color.GREEN);
        MyVector x2dPos = camera2.getProjection(camera.getX2D().mult(4).add(camera.getPos()), WIDTH / PPM / 2, HEIGHT / PPM / 2);
        if (x2dPos != null) {
            x2dPos = toScreenCoords(x2dPos);
            g.drawLine((int)(camPos.x) + WIDTH / 4, (int)(camPos.y), (int)(x2dPos.x) + WIDTH / 4, (int)(x2dPos.y));
        }*/
    }

    int counter = 0;

    public void run() {
        while (true) {

            if (playerActive) {

                player.move();

                for (Integer key : keys.keySet()) {
                    if (keys.get(key) == true) {
                        switch (key) {
                            case KeyEvent.VK_W:
                                player.moveForward();
                                break;
                            case KeyEvent.VK_S:
                                player.moveBackward();
                                break;
                            case KeyEvent.VK_A:
                                player.moveLeft();
                                break;
                            case KeyEvent.VK_D:
                                player.moveRight();
                                break;
                            case KeyEvent.VK_CAPS_LOCK:
                                player.moveDown();
                                break;
                            case KeyEvent.VK_SPACE:
                                player.moveUp();
                                break;
                        }
                    }
                }

                cursorPoint = spawnVector();
                Projection cursorProj = player.lookAt(cursorPoint, WIDTH, HEIGHT);

                /*for (Surface surface : surfaces) {
                    if (surface.contains((new Point3D(cursorProj.coords)))) {
                        System.out.println("yep it's contained all right "+ counter++);
                        
                    }
                }*/
                player.lookAt(points, WIDTH, HEIGHT);

                selected = null;
                double closestDist = Float.MAX_VALUE;
                for (Point3D point : points.keySet()) {
                    if (points.get(point).inRange && point != start) {
                        double dist = points.get(point).coords.sub(cursorProj.coords).length();
                        if (dist <= OVAL_SIZE) {
                            closestDist = dist;
                            selected = point;
                        }
                    }
                }

                if (mouseDown) {
                    if (mouseButton == MouseEvent.BUTTON1) {
                        Point3D newPoint = selected;
                        if (newPoint == null) {
                            newPoint = new Point3D(spawnVector());
                        }

                        if (start == null) {
                            start = newPoint;
                        } else {
                            end = newPoint;
                            Point3D.link(start, end);
                            
                            if (selected != null) {
                                Surface newSurface = findNewSurface(surfaces, findLoops(end));
                                if (newSurface != null) {
                                    System.out.println(surfaces.size());
                                    for (Surface surface: surfaces)
                                        System.out.println(surface);
                                    System.out.println();
                                }
                            }
                            start = end;
                            end = null;
                        }
                        points.put(start, player.lookAt(start, WIDTH, HEIGHT));
                    } 
                    else if (mouseButton == MouseEvent.BUTTON3) {
                        start = null;
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
    public ArrayList<ArrayList<Point3D>> findLoops(Point3D root) {
        
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
                    lastPotentialLoopIndex --;
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
                    for (Point3D neighbor: cur.getNeighbours()) {
                        if (prev != neighbor) {
                            if (needToDupe) {
                                potentialLoops.add(new ArrayList(potentialLoops.get(lastPotentialLoopIndex)));
                                lastPotentialLoopIndex ++;
                            } else {
                                needToDupe = true;
                            }
                            stack.add(neighbor);
                        }
                    }
                }
            } else {
                if (curIndex == 0) {
                    insertIntoListbySize(loops, potentialLoops.get(lastPotentialLoopIndex));
                }
                potentialLoops.remove(lastPotentialLoopIndex);
                lastPotentialLoopIndex --;
            }
        }
        return loops;
    }
    
    // returns the last added surface
    public Surface findNewSurface(HashSet<Surface> surfaces, ArrayList<ArrayList<Point3D>> results) {
        if (results.isEmpty())
            return null;
        
        HashMap<Surface, ArrayList<Point3D>> surfacePoints = new HashMap();
        
        int curSize = results.get(0).size();
        for (int i = 0; i < results.size(); i ++) {
            boolean filtered = false;
            for (Surface surface: surfaces) {
                if (!surfacePoints.containsKey(surface)) {
                    surfacePoints.put(surface, surface.getList());
                }
                if (surfacePoints.get(surface).containsAll(results.get(i)) && results.get(i).containsAll(surfacePoints.get(surface))) {
                    filtered = true;
                    break;
                }
            }
            if (!filtered) {
                Surface newSurface = new Surface(results.get(i).get(0));
                for (Point3D point: results.get(i)) {
                    point.addSurface(newSurface);
                }
                surfaces.add(newSurface);
                return newSurface;
            }
        }
        return null;
    }
    // @params:
    // lists should be sorted inascending list size
    void insertIntoListbySize(ArrayList<ArrayList<Point3D>> ascendingSurfaces, ArrayList<Point3D> potentialSurface) {
        int insIndex = 0;
        for (; insIndex < ascendingSurfaces.size(); insIndex ++) {
            if (potentialSurface.size() >= ascendingSurfaces.get(insIndex).size()) {
                insIndex ++;
                break;
            }
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
                    player.lookLeft(-pixels);
                } else {
                    player.lookRight(pixels);
                }
            }
            if (prevMouseY != -1) {
                int pixels = y - prevMouseY;
                if (pixels < 0) {
                    player.lookUp(-pixels);
                } else {
                    player.lookDown(pixels);
                }
            }
            robot.mouseMove((int) this.getLocationOnScreen().getX() + WIDTH / 2, (int) this.getLocationOnScreen().getY() + HEIGHT / 2);
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
                robot.mouseMove((int) this.getLocationOnScreen().getX() + WIDTH / 2, (int) this.getLocationOnScreen().getY() + HEIGHT / 2);
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

