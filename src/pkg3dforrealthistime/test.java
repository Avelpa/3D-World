/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dforrealthistime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

/**
 *
 * @author Dmitry
 */
public class test {
    public static void main(String[] args) {
        test test = new test();
    }
    
    public test() {
        Node a = new Node('a');
        Node b = new Node('b');
        Node c = new Node('c');
        Node d = new Node('d');
        
        Node.link(a, b);
        Node.link(b, c);
        Node.link(c, a);
        Node.link(c, d);
        Node.link(d, a);
        
        HashSet<ArrayList<Node>> results = traverse(a);
        for (ArrayList<Node> set: results) {
            for (Node node: set) {
                System.out.println(node.ID);
            }
            System.out.println();
        }
    }
    
    public HashSet<ArrayList<Node>> traverse(Node a) {
        
        HashSet<ArrayList<Node>> results = new HashSet();
        
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
                    results.add(progress.get(progressIndex));
                }
                progress.remove(progressIndex);
                progressIndex --;
            }
        }
        return results;
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
