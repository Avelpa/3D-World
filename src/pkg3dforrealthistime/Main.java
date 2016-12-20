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

    HashSet<Point3D> points = new HashSet();

    Spectator player = null;

    MyVector cursorPoint = null;
    Point3D start = null;
    Point3D end = null;
    boolean mouseDown = false;
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

        HashMap<MyVector, MyVector> pointProjPairs = new HashMap();
        MyVector pointProj = null;
        g.setColor(Color.DARK_GRAY);
        for (Point3D point : points) { // need to change null return val
            if (!pointProjPairs.containsKey(point)) {
                pointProjPairs.put(point, player.getCamera().getProjection(point, WIDTH, HEIGHT));
            }

            pointProj = pointProjPairs.get(point);
            if (pointProj == null) {
                continue;
            }

            double ovalSize = player.getCamera().getPerspective(point, 41);

            g.fillOval((int) (pointProj.x - ovalSize / 2), (int) (pointProj.y - ovalSize / 2), (int) ovalSize, (int) ovalSize);

            MyVector neighborProj = null;
            for (Point3D neighbor : point.getNeighbours()) {
                if (!pointProjPairs.containsKey(neighbor)) {
                    pointProjPairs.put(neighbor, player.getCamera().getProjection(neighbor, WIDTH, HEIGHT));
                }
                neighborProj = pointProjPairs.get(neighbor);

                if (pointProjPairs.get(neighbor) != null) {
                    g.drawLine((int) (pointProj.x), (int) (pointProj.y), (int) (neighborProj.x), (int) (neighborProj.y));
                }
            }

        }

        

        g.setColor(Color.GREEN);
        if (start != null) {
            double ovalSize = player.getCamera().getPerspective(start, 41);
            MyVector proj = player.getCamera().getProjection(start, WIDTH, HEIGHT);
            if (proj != null) {
                g.fillOval((int) (proj.x - ovalSize / 2), (int) (proj.y - ovalSize / 2), (int) ovalSize, (int) ovalSize);
            }
        }
        if (end != null) {
            MyVector proj = player.getCamera().getProjection(end, WIDTH, HEIGHT);
            if (proj != null) {
                double ovalSize = player.getCamera().getPerspective(end, 41);
                g.fillOval((int) (proj.x - ovalSize / 2), (int) (proj.y - ovalSize / 2), (int) ovalSize, (int) ovalSize);
            }
        }

        if (!playerActive) {
            g.setColor(Color.RED);
            g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
        } else {
            g.setColor(Color.BLACK);
            g.drawOval((int) (WIDTH / 2 - 20), (int) (HEIGHT / 2 - 20), 40, 40);
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

                cursorPoint = spawnVector();

                if (mouseDown) {
                    if (start == null) {
                        start = new Point3D(spawnVector());
                    } else {
                        end = new Point3D(spawnVector());
                        start.linkTo(end);
                        end.linkTo(start);

                        points.add(start);
                        points.add(end);

                        start = end;
                        end = null;
                    }
                    mouseDown = false;
                }

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
            }

            repaint();
            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public MyVector spawnVector() {
        return player.getCamera().getPos().add(player.getCamera().getNormal().unit().mult(4));
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
            robot.mouseMove((int) this.getLocationOnScreen().getX() + WIDTH / 2, (int) this.getLocationOnScreen().getY() + HEIGHT / 2);
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
