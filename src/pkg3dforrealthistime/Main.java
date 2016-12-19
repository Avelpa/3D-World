/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * @author Dmitry
 */
public class Main extends JComponent implements KeyListener, MouseListener, MouseMotionListener{
    
    private JFrame frame;
    
    private final int WIDTH = 800, HEIGHT = 800;
    private final int PPM = 100;
    
    private HashMap<MyVector, Color> grid = new HashMap();
    double gridDist = 2; // maybe make final
    MyVector potentialPoint = null;
    
    Spectator player;
    
    boolean mouseDown = false;
    HashMap<Integer, Boolean> keys;
    
    int prevMouseX = -1;
    int prevMouseY = -1;
    private Robot robot;
    boolean centeringCursor = false;
    private Cursor invisibleCursor;
    
    boolean playerActive = false;
    boolean running = false;
    
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
        player.setAccel(0.0005);
        player.setMaxVel(0.4);
        player.setLookDegrees(0.2);
        
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
    
    @Override
    public void paintComponent(Graphics g) {
        
        g.clearRect(0, 0, WIDTH, HEIGHT);
//        g.setColor(new Color(0f, 0.9f, 0f, 0.2f));
        for (MyVector point: grid.keySet()) {
            g.setColor(grid.get(point));
            MyVector camProj = player.getCamera().getProjection(point, WIDTH, HEIGHT);
            if (camProj == null)
                continue;
            g.fillOval((int)(camProj.x-20), (int)(camProj.y-20), 41, 41);
        }
        
        if (potentialPoint != null && !running) {
            g.setColor(Color.BLACK);
            MyVector potScrn = player.getCamera().getProjection(potentialPoint, WIDTH, HEIGHT);
            if (potScrn != null) {
                g.drawOval((int)(potScrn.x - 5), (int)(potScrn.y - 5), 10, 10);
            }
        }
        
        if (!running) {
            g.setColor(Color.RED);
            g.drawRect(0, 0, WIDTH-1, HEIGHT-1);
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
                
                if (!running) {
                    MyVector newPoint = player.getCamera().getPos().add(player.getCamera().getNormal().unit().mult(3));
                    double newX = newPoint.x % gridDist >= (gridDist / 2) ? Math.ceil(newPoint.x / gridDist) * gridDist : Math.floor(newPoint.x / gridDist) * gridDist;
                    double newY = newPoint.y % gridDist >= (gridDist / 2) ? Math.ceil(newPoint.y / gridDist) * gridDist : Math.floor(newPoint.y / gridDist) * gridDist;
                    double newZ = newPoint.z % gridDist >= (gridDist / 2) ? Math.ceil(newPoint.z / gridDist) * gridDist : Math.floor(newPoint.z / gridDist) * gridDist;
                    potentialPoint = new MyVector(newX, newY, newZ);

                    if (mouseDown && this.getMousePosition() != null) {
                        if (!grid.containsKey(potentialPoint)) {
                            grid.put(potentialPoint, genRandColor());
                        }
                    }
                } else {
                    counter ++;
                    if (counter % 60 == 0)
                        runLife();
                }
                
                for (Integer key: keys.keySet()) {
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
            } else {
                for (Integer key: keys.keySet()) {
                    if (keys.get(key) == true) {
                        switch (key) {
                            case KeyEvent.VK_S:
                                running = !running;
                                keys.put(KeyEvent.VK_S, false);
                                break;
                        }
                    }
                }
            }
            
            repaint();
            try {
                Thread.sleep(1000/60);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    HashSet<MyVector> curEmptyNeighbors = new HashSet();
    HashSet<MyVector> checkedEmptyNeighbors = new HashSet();
    void runLife() {
        HashMap<MyVector, Color> toAdd = new HashMap();
        HashSet<MyVector> toDelete = new HashSet();
        
        for (MyVector point: grid.keySet()) {
            int numNeighbors = getNumNeighbors(point, false);
            if (numNeighbors < 2 || numNeighbors > 3) {
                toDelete.add(point);
            }
            for (MyVector empty : curEmptyNeighbors) {
                
                numNeighbors = getNumNeighbors(empty, true);
                if (numNeighbors == 3)
                    toAdd.put(empty, genRandColor());
                
                checkedEmptyNeighbors.add(empty);
            }
            curEmptyNeighbors.clear();
        }
        for (MyVector del: toDelete) {
            grid.remove(del);
        }
        grid.putAll(toAdd);
        checkedEmptyNeighbors.clear();
    }
    int getNumNeighbors(MyVector point, boolean emptyCheck) {
        int numNeighbors = 0;
        for (double x = point.x + gridDist; x >= point.x - gridDist; x -= gridDist) {
            for (double z = point.z + gridDist; z >= point.z - gridDist; z -= gridDist) {
                for (double y = point.y - gridDist; y <= point.y + gridDist; y += gridDist) {
                    if (x == point.x && y == point.y && z == point.z)
                        continue;
                    
                    MyVector testPoint = new MyVector(x, y, z);
                    if (grid.containsKey(testPoint))
                        numNeighbors ++;
                    else if (!emptyCheck && !checkedEmptyNeighbors.contains(testPoint))
                        curEmptyNeighbors.add(testPoint);
                }
            }
        }
        return numNeighbors;
    }
    
    Color genRandColor() {
        int r = (int) (Math.random() * 200);
        int g = (int) (Math.random() * 200);
        int b = (int) (Math.random() * 200);
        return new Color(r, g, b);
    }
    
    private void pan(MouseEvent e) {
        if (!centeringCursor) {
            if (prevMouseX != -1) {
                int pixels = e.getX() - prevMouseX;
                if (pixels < 0) {
                    player.lookLeft(-pixels);
                } else {
                    player.lookRight(pixels);
                }
            }
            if (prevMouseY != -1) {
                int pixels = e.getY() - prevMouseY;
                if (pixels < 0) {
                    player.lookUp(-pixels);
                } else {
                    player.lookDown(pixels);
                }
            }
            robot.mouseMove((int)this.getLocationOnScreen().getX() + WIDTH/2, (int)this.getLocationOnScreen().getY() + HEIGHT/2);
            centeringCursor = true;
        } else {
            centeringCursor = false;
        }

        prevMouseX = e.getX();
        prevMouseY = e.getY();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            playerActive = !playerActive;
            if (playerActive) {
                robot.mouseMove((int)this.getLocationOnScreen().getX() + WIDTH/2, (int)this.getLocationOnScreen().getY() + HEIGHT/2);
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
        char keyChar = Character.toLowerCase(e.getKeyChar());
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
        mouseDown = true;
        if (playerActive) {
            pan(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (playerActive) {
            pan(e);
        }
    }
    
    
}