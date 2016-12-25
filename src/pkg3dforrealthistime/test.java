/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import MyVector.MyMatrix;
import MyVector.MyVector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

/**
 *
 * @author Dmitry
 */
public class test {
    public static void main(String[] args) {
        ArrayList<Point3D> points = new ArrayList();
        points.add(new Point3D(1, 1, 1));
        points.add(new Point3D(0, -1, 2));
        points.add(new Point3D(0.5, 3, -7));
        Surface surface = new Surface(points, new MyVector(0, 1, 0));
        System.out.println(surface.getList().size());
//        for (Triangle triangle: surface.getTriangles())
//            System.out.println("hi");
    }
    
    
    public test() {
        Node a = new Node('a');
        Node b = new Node('b');
        Node c = new Node('c');
        Node d = new Node('d');
        
        Node.link(a, b);
        Node.link(b, c);
        Node.link(c, a);
        
        HashSet<ArrayList<Node>> surfaces = new HashSet();
        
        ArrayList<ArrayList<Node>> results = traverse(a);
        consolidateSurfaces(surfaces, results);
        
        Node.link(d, c);
        results = traverse(a);
        consolidateSurfaces(surfaces, results);
        
        Node.link(d, a);
        results = traverse(a);
        consolidateSurfaces(surfaces, results);
        
        for (ArrayList<Node> surface: surfaces) {
            for (Node node: surface) {
                System.out.println(node.ID);
            }
            System.out.println();
        }
    }
    
    public void consolidateSurfaces(HashSet<ArrayList<Node>> surfaces, ArrayList<ArrayList<Node>> results) {
        if (results.isEmpty())
            return;
        
        int curSize = results.get(0).size();
            
        for (int i = 0; i < results.size(); i ++) {
            boolean filtered = false;
            for (ArrayList<Node> surface: surfaces) {
                if (surface.containsAll(results.get(i)) && results.get(i).containsAll(surface)) {
                    filtered = true;
                    break;
                }
            }
            if (!filtered) {
                surfaces.add(results.get(i));
                break;
            }
        }
    }
    
    public ArrayList<ArrayList<Node>> traverse(Node a) {
        
        ArrayList<ArrayList<Node>> results = new ArrayList();
        
        ArrayList<ArrayList<Node>> progress = new ArrayList();
        progress.add(new ArrayList());
        int progressIndex = progress.size() - 1;
        
        Node cur;
        Stack<Node> stack = new Stack();
        stack.add(a);
        
        while (!stack.isEmpty()) {
            cur = stack.pop();
            
            int curIndex = progress.get(progressIndex).indexOf(cur);
            if (curIndex == -1) {
                
                progress.get(progressIndex).add(cur);
                
                int curProgressSize = progress.get(progressIndex).size();
                Node prev = null;
                if (curProgressSize >= 2) {
                    prev = progress.get(progressIndex).get(curProgressSize - 2);
                }
                boolean needToDupe = false;
                for (Node neighbor: cur.neighbors) {
                    if (prev != neighbor) {
                        if (needToDupe) {
                            progress.add(new ArrayList(progress.get(progressIndex)));
                            progressIndex ++;
                        } else {
                            needToDupe = true;
                        }
                        stack.add(neighbor);
                    }
                }
                if (!needToDupe && cur.neighbors.size() <= 1) {
                    progress.remove(progressIndex);
                    progressIndex --;
                }
            } else {
                if (curIndex == 0) {
                    insertIntoListbySize(results, progress.get(progressIndex));
                }
                progress.remove(progressIndex);
                progressIndex --;
            }
        }
        return results;
    }
    
    // @params:
    // lists should be sorted inascending list size
    private void insertIntoListbySize(ArrayList<ArrayList<Node>> lists, ArrayList<Node> newList) {
        int insIndex = 0;
        for (; insIndex < lists.size(); insIndex ++) {
            if (newList.size() >= lists.get(insIndex).size()) {
                insIndex ++;
                break;
            }
        }
        lists.add(insIndex, newList);
    }
}


class Node {
    public HashSet<Node> neighbors;
    char ID;
    boolean visited = false;
    
    public Node(char ID) {
        neighbors = new HashSet();
        this.ID = ID;
    }
    
    public void linkTo(Node other) {
        this.neighbors.add(other);
    }
    
    public static void link(Node a, Node b) {
        a.linkTo(b);
        b.linkTo(a);
    }
}
