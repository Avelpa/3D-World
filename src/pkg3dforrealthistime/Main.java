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
import java.util.ArrayList;
import java.util.HashMap;
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
    
    ArrayList<MyVector> points = new ArrayList();
    
    Spectator player;
    
    boolean mouseDown = false;
    HashMap<Character, Boolean> WASD;
    
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
        WASD = new HashMap();
        WASD.put('w', false);
        WASD.put('a', false);
        WASD.put('s', false);
        WASD.put('d', false);
        WASD.put('1', false);
        
        player = new Spectator(MyVector.X.mult(20), MyVector.ZERO, new Camera(0.017, 60, PPM));
        player.setSpeed(0.1);
        player.setLookDegrees(0.2);
        
        for (int i = -WIDTH / 2 / PPM; i <= WIDTH / 2 / PPM; i ++) {
           for (int j = -HEIGHT / 2 / PPM; j <= HEIGHT / 2 / PPM; j ++) {
               for (int h = -2; h <= 2; h ++) {
                   points.add(new MyVector(h, i, j));
               }
           }
       }
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
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < points.size(); i ++) {
            MyVector camProj = player.getCamera().getProjection(points.get(i), WIDTH, HEIGHT);
            if (camProj == null)
                continue;
            g.fillOval((int)(camProj.x-5), (int)(camProj.y-5), 11, 11);
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
    int interval = 5 * 60;
    public void run() {
        while (true) {
            
            if (mouseDown && this.getMousePosition() != null) {
                points.add(player.getCamera().getPos().add(player.getCamera().getNormal().unit()));
            }
            
            if (playerActive) {
                for (Character key: WASD.keySet()) {
                    if (WASD.get(key) == true) {
                        switch (key) {
                            case 'w':
                                player.moveForward();
                                break;
                            case 's':
                                player.moveBackward();
                                break;
                            case 'a':
                                player.moveLeft();
                                break;
                            case 'd':
                                player.moveRight();
                                break;
                            case '1':
                                player.moveDown();
                                break;
                        }
                    }
                }
            }
            
            /* demo cycle
            
            counter ++;

            if (counter % (interval) == 0) {
                camera.reset();
                camera.moveTo(10, 0, 0);
            }
            switch (counter / (interval)) {
                case 0:
                    camera.moveBy(-0.01, 0, 0);
                    break;
                case 1:
                    camera.moveBy(0.01, 0, 0);
                    break;
                case 2:
                    camera.moveBy(0, -0.01, 0);
                    break;
                case 3:
                    camera.moveBy(0, 0.01, 0);
                    break;
                case 4:
                    camera.moveBy(0, 0, -0.01);
                    break;
                case 5:
                    camera.moveBy(0, 0, 0.01);
                    break;
                case 6:
                    camera.rotateHorizontally(0.04);
                    break;
                case 7:
                    camera.rotateHorizontally(-0.04);
                    break;
                case 8:
                    camera.rotateVertically(0.04);
                    break;
                case 9:
                    camera.rotateVertically(-0.04);
                    break;
            }
            */
//            System.out.println(camera);
            
            repaint();
            try {
                Thread.sleep(1000/60);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
        
        /* -- for demo cycle
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            counter /= interval;
            counter *= interval;
            counter += interval - 1;
            return;
        }*/
        
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
        
        char keyChar = Character.toLowerCase(e.getKeyChar());
        if (WASD.containsKey(keyChar)) {
            WASD.put(keyChar, true);
        } else {
            if (e.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
                WASD.put('1', true);
            }
        }
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        char keyChar = Character.toLowerCase(e.getKeyChar());
        if (WASD.containsKey(keyChar)) {
            WASD.put(keyChar, false);
        } else {
            if (e.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
                WASD.put('1', false);
            }
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