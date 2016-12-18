/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyMatrix;
import MyVector.MyVector;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
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
    
    boolean mouseDown = false;
    double zVel = 0.0, yVel = 0.0;
    
    Camera camera;
    Camera camera2;
    
    int prevMouseX = -1;
    int prevMouseY = -1;
    
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
        
        camera = new Camera(0.017, 90);
        camera.moveTo(10, 0, 0);
        
        
        camera2 = new Camera(0.017, 90);
//        camera2.moveTo(10, -20, 2);
//        camera2.rotateHorizontally(-70);
        camera2.moveTo(10, -10, 0);
        camera2.rotateHorizontally(-90);
        
//        points.add(new MyVector(0, -2, 0));
//        points.add(new MyVector(0, 2, 0));
//        points.add(new MyVector(0, 0, 2));
//        points.add(new MyVector(0, 0, -2));
        

        for (int i = -WIDTH / 8 / PPM; i <= WIDTH / 8 / PPM; i ++) {
            for (int j = -HEIGHT / 8 / PPM; j <= HEIGHT / 8 / PPM; j ++) {
                for (int h = -2; h <= 2; h ++) {
                    points.add(new MyVector(h, i, j));
                }
            }
        }

//        for (float i = 0; i < 4; i += 0.25) {
//            points.add(new MyVector(0, 0, i));
//        }
//        for (float i = 0; i > -4; i -= 0.25) {
//            points.add(new MyVector(0, i, 0));
//        }
        
    }
    
    private MyVector toScreenCoords(MyVector vector) {
        
        int x = WIDTH/2 + (int)(vector.y * PPM);
        int y = HEIGHT/2 - (int)(vector.z * PPM);
        
        return new MyVector(x, y, 0);
    }
    private double[] mouseToCartesian(int y, int z) {
        double[] res = new double[3];
        
        res[0] = 0.0;
        res[1] = (y - WIDTH/2) / (double)PPM;
        res[2] = (HEIGHT/2 - z) / (double)PPM;
        
        return res;
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
        
//        g.clearRect(0, 0, WIDTH, HEIGHT);
//        g.setColor(Color.DARK_GRAY);
//        for (int i = 0; i < points.size(); i ++) {
//            MyVector cameraCoords = camera.getProjection(points.get(i), (WIDTH / PPM), (HEIGHT / PPM));
////            MyVector cameraCoords = camera.getProjection(points.get(i), (WIDTH / PPM / 2), (HEIGHT / PPM / 2));
//            MyVector scrCoords = toScreenCoords(cameraCoords);
//            g.fillOval((int)(scrCoords.x-5), (int)(scrCoords.y-5), 11, 11);
//        }
        
        
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, WIDTH/2, HEIGHT);
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < points.size(); i ++) {
//            MyVector cameraCoords = camera.getProjection(points.get(i), (WIDTH / PPM), (HEIGHT / PPM));
            MyVector cameraCoords = camera.getProjection(points.get(i), (WIDTH / PPM / 2), (HEIGHT / PPM / 2));
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
            MyVector scrCoords = toScreenCoords(cameraCoords);
            g.fillOval((int)(scrCoords.x-5) + WIDTH/4, (int)(scrCoords.y-5), 11, 11);
        }
        
        // camera center
        g.setColor(Color.MAGENTA);
        MyVector cameraPos = camera2.getProjection(camera.getPos(), (WIDTH / PPM / 2), (HEIGHT / PPM / 2));
        cameraPos = toScreenCoords(cameraPos);
        g.fillOval((int)(cameraPos.x-5) + WIDTH/4, (int)(cameraPos.y-5), 11, 11);
        // top fov
        MyVector fovLine = camera2.getProjection(MyMatrix.rotateY(camera.getNormal().mult(1000), camera.getFov() / 2).add(camera.getPos()), (WIDTH / PPM/2), (HEIGHT / PPM / 2));
        fovLine = toScreenCoords(fovLine);
        g.drawLine((int)(cameraPos.x + WIDTH/4), (int)(cameraPos.y), (int)(fovLine.x) + WIDTH/4, (int)(fovLine.y));
        // bottom fov
        fovLine = camera2.getProjection(MyMatrix.rotateY(camera.getNormal().mult(1000), -camera.getFov() / 2).add(camera.getPos()), (WIDTH / PPM/2), (HEIGHT / PPM / 2));
        fovLine = toScreenCoords(fovLine);
        g.drawLine((int)(cameraPos.x + WIDTH/4), (int)(cameraPos.y), (int)(fovLine.x) + WIDTH/4, (int)(fovLine.y));
        // left fov
        fovLine = camera2.getProjection(MyMatrix.rotateZ(camera.getNormal().mult(1000), camera.getFov() / 2).add(camera.getPos()), (WIDTH / PPM/2), (HEIGHT / PPM / 2));
        fovLine = toScreenCoords(fovLine);
        g.drawLine((int)(cameraPos.x + WIDTH/4), (int)(cameraPos.y), (int)(fovLine.x) + WIDTH/4, (int)(fovLine.y));
        // right fov
        fovLine = camera2.getProjection(MyMatrix.rotateZ(camera.getNormal().mult(1000), -camera.getFov() / 2).add(camera.getPos()), (WIDTH / PPM/2), (HEIGHT / PPM / 2));
        fovLine = toScreenCoords(fovLine);
        g.drawLine((int)(cameraPos.x + WIDTH/4), (int)(cameraPos.y), (int)(fovLine.x) + WIDTH/4, (int)(fovLine.y));
        // normal
        g.setColor(Color.BLUE);
        fovLine = camera2.getProjection(camera.getNormal().mult(200).add(camera.getPos()), (WIDTH / PPM/2), (HEIGHT / PPM / 2));
        fovLine = toScreenCoords(fovLine);
        g.drawLine((int)(cameraPos.x + WIDTH/4), (int)(cameraPos.y), (int)(fovLine.x) + WIDTH/4, (int)(fovLine.y));
        // x2D
        g.setColor(Color.GREEN);
        fovLine = camera2.getProjection(camera.getX2D().mult(200).add(camera.getPos()), (WIDTH / PPM/2), (HEIGHT / PPM / 2));
        fovLine = toScreenCoords(fovLine);
        g.drawLine((int)(cameraPos.x + WIDTH/4), (int)(cameraPos.y), (int)(fovLine.x) + WIDTH/4, (int)(fovLine.y));
        // y2D
        g.setColor(Color.RED);
        fovLine = camera2.getProjection(camera.getY2D().mult(200).add(camera.getPos()), (WIDTH / PPM/2), (HEIGHT / PPM / 2));
        fovLine = toScreenCoords(fovLine);
        g.drawLine((int)(cameraPos.x + WIDTH/4), (int)(cameraPos.y), (int)(fovLine.x) + WIDTH/4, (int)(fovLine.y));
    }
    
    int counter = 0;
    int interval = 5 * 60;
    public void run() {
        
        while (true) {
            
            if (mouseDown && this.getMousePosition() != null) {
                points.add(new MyVector(mouseToCartesian((int)this.getMousePosition().getX(), (int)this.getMousePosition().getY())));
            }
            
            if (yVel != 0 || zVel != 0) {
                for (int i = 0; i < points.size(); i ++) {
                    points.set(i, MyMatrix.rotateY(points.get(i), yVel));
                    points.set(i, MyMatrix.rotateZ(points.get(i), zVel));
                }
            }
            
            
            /*
            
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

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            counter /= interval;
            counter *= interval;
            counter += interval - 1;
            return;
        }
        
        int incr = 5;
        
        double rotAmount = 1;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                camera.rotateVertically(rotAmount);
                break;
            case KeyEvent.VK_DOWN:
                camera.rotateVertically(-rotAmount);
                break;
            case KeyEvent.VK_LEFT:
                camera.rotateHorizontally(rotAmount);
                break;
            case KeyEvent.VK_RIGHT:
                camera.rotateHorizontally(-rotAmount);
                break;
        }
        System.out.printf("%s %s\n", 
            MyVector.angleBetween(camera.getNormal(), camera.getY2D()),
            MyVector.angleBetween(camera.getNormal(), camera.getX2D())
        );
        System.out.println(camera);
        
        double accel = 0.5;
        switch (e.getKeyChar()) {
            case 'W':
                yVel -= accel;
                break;
            case 'S':
                yVel += accel;
                break;
            case 'A':
                zVel -= accel;
                break;
            case 'D':
                zVel += accel;
                break;
        }
        
        double moveDist = 0.1;
        switch (e.getKeyChar()) {
            case 'w':
                camera.moveDepthwise(moveDist);
//                camera.moveBy(-moveDist, 0, 0);
                break;
            case 's':
                camera.moveDepthwise(-moveDist);
//                camera.moveBy(moveDist, 0, 0);
                break;
            case 'a':
                camera.moveHorizontally(-moveDist);
//                camera.moveBy(0, -moveDist, 0);
                break;
            case 'd':
                camera.moveHorizontally(moveDist);
//                camera.moveBy(0, moveDist, 0);
                break;
            case 'j':
                camera.moveVertically(-moveDist);
//                camera.moveBy(0, 0, -moveDist);
                break;
            case 'k':
                camera.moveVertically(moveDist);
//                camera.moveBy(0, 0, moveDist);
                break;
            case 'r':
                yVel = 0;
                zVel = 0;
                points.clear();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {/*
        yVel = 0;
        zVel = 0;*/
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
        prevMouseX = e.getX();
        prevMouseY = e.getY();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        prevMouseX = -1;
        prevMouseY = -1;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseDown = true;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (prevMouseX != -1) {
//            camera.rotateHorizontally(-0.5 * (e.getX() - prevMouseX));
        }
        prevMouseX = e.getX();
        
        if (prevMouseY != -1) {
//            camera.rotateVertically(0.5 * (e.getY() - prevMouseY));
        }
        prevMouseY = e.getY();
    }
    
    
}