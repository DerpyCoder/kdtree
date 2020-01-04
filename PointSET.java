import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Iterator;
import java.util.TreeSet;

public class PointSET {

    private TreeSet<Point2D> bst;

    // construct an empty set of points
    public PointSET() {
        bst = new TreeSet<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return bst.isEmpty();
    }

    // number of points in the set
    public int size() {
        return bst.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        nullCheck(p);

        bst.add(p);
    }

    private void nullCheck(Object uwu) {
        if (uwu == null) throw new IllegalArgumentException("Can not be null");
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        nullCheck(p);

        return bst.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : bst) {
            StdDraw.point(p.x(), p.y());
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        return rangg(rect);
    }


    // Hides the variables
    private Iterable<Point2D> rangg(RectHV rect) {
        nullCheck(rect);

        double rectx = rect.xmin();
        double rectX = rect.xmax();
        double recty = rect.ymin();
        double rectY = rect.ymax();

        Stack<Point2D> st = new Stack<>();

        for (Point2D p : bst) {
            double px = p.x();
            double py = p.y();
            if (px >= rectx && px <= rectX && py >= recty && py <= rectY) {
                st.push(p);
            }
        }

        return st;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        nullCheck(p);

        return near(p);
    }

    private Point2D near(Point2D p) {

        Iterator<Point2D> it = bst.iterator();

        Point2D p2 = it.next();
        Point2D min = p2;
        double dist = distance(p2.x(), p2.y(), p.x(), p.y());

        while (it.hasNext()) {
            p2 = it.next();
            double distc = distance(p2.x(), p2.y(), p.x(), p.y());
            if (distc <= dist) {
                min = p2;
                dist = distc;
            }
        }

        return min;

    }

    // Yuhh pythag a^2 + b^2 = c^2
    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /*
    // unit testing of the methods (optional)
    public static void main(String[] args) {

    }
    */

}
