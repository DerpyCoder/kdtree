import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

    private int size;

    private Node root;

    public KdTree() {
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        nullCheck(p);
        root = insert(root, p, true, 0, 0, 1, 1);
    }

    private Node insert(Node node, Point2D p, boolean vert, double x1,
                        double y1, double x2, double y2) {
        if (node == null) {
            size++; // if it's already there it won't add it
            Node x = new Node(p);
            x.axis = new RectHV(x1, y1, x2, y2);
            return x;
        }

        if (node.p.equals(p)) return node; // if it's already there, it will break;

        // Case 1:
        // if it's vertical -> horizontal

        RectHV r = node.axis;

        if (vert) {
            if (node.p.x() > p.x()) {
                node.left = insert(node.left, p, false, r.xmin(), r.ymin(), node.p.x(), r.ymax());
            }
            else {
                node.right = insert(node.right, p, false, node.p.x(), r.ymin(), r.xmax(), r.ymax());
            }
        }

        // Case 2:
        // if it's horizontal -> vertical

        else {
            if (node.p.y() > p.y()) {
                node.left = insert(node.left, p, true, r.xmin(), r.ymin(), r.xmax(), node.p.y());
            }
            else {
                node.right = insert(node.right, p, true, r.xmin(), node.p.y(), r.xmax(), r.ymax());
            }
        }

        return node;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        nullCheck(p);

        Node x = root;

        boolean isVert = true;

        while (x != null) {

            if (p.compareTo(x.p) == 0) return true;

            if (isVert) {
                if (x.p.x() > p.x()) {
                    if (x.left == null) {
                        return false; // improves speed if not there without iterating through all
                    }
                    x = x.left;
                }
                else {
                    if (x.right == null) {
                        return false;
                    }
                    x = x.right;
                }
            }
            else {
                if (x.p.y() > p.y()) {
                    if (x.left == null) {
                        return false;
                    }
                    x = x.left;
                }
                else {
                    if (x.right == null) {
                        return false;
                    }
                    x = x.right;
                }
            }
            isVert = !isVert;
        }
        return false;

    }

    // Cases
    // if none, add new node
    // if less go left, if more go right
    // break ties by y;

    private void nullCheck(Object uwu) {
        if (uwu == null) throw new IllegalArgumentException("Can not be null");
    }

    // draw all points to standard draw
    public void draw() {
        // intial check if root is null
        if (root == null) return;
        draw(root, true);
    }

    private void draw(Node node, boolean vert) {

        RectHV r = node.axis;

        StdDraw.setPenRadius();
        if (vert) {
            // color red
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.p.x(), r.ymin(), node.p.x(), r.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(r.xmin(), node.p.y(), r.xmax(), node.p.y());
        }

        // Draw the point
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.point(node.p.x(), node.p.y());

        // Check if there's a left and right node
        if (node.left != null) {
            draw(node.left, !vert);
        }
        if (node.right != null) {
            draw(node.right, !vert);
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        nullCheck(rect);
        Queue<Point2D> q = new Queue<>();
        if (root == null) return null;
        search(q, root, rect, true);
        return q;
    }

    /*
        private boolean intersects(RectHV rect, Point2D p, boolean isVert) {
            if (isVert) {
                return rect.xmin() <= p.x() && rect.xmax() >= p.x();
            }
            else {
                return rect.ymin() <= p.y() && rect.ymax() >= p.y();
            }
        }

      */

    private double center(double min, double max) {
        return (max - min) / 2 + min;
    }

    private void search(Queue<Point2D> q, Node node, RectHV rect, boolean isVert) {
        if (node == null) return;

        // Cases: if left is shorter, no need for right, and vice versa

        Point2D p = node.p;

        if (rect.contains(p)) {
            q.enqueue(p);
        }

        double px = p.x();
        double py = p.y();

        if ((rect.intersects(new RectHV(px, node.axis.ymin(), px, node.axis.ymax()))
                && isVert) || (
                rect.intersects(new RectHV(node.axis.xmin(), py, node.axis.xmax(), py))
                        && !isVert)) {
            search(q, node.left, rect, !isVert);
            search(q, node.right, rect, !isVert);
        }
        else {
            if ((isVert && (px > center(rect.xmin(), rect.xmax()))) || (!isVert && (py
                    > center(rect.ymin(), rect.ymax())))) {
                search(q, node.left, rect, !isVert);
            }
            else {
                search(q, node.right, rect, !isVert);
            }
        }

    }







    /*

    private Iterable<Point2D> rangg(RectHV rect) {
        Queue<Point2D> q = new Queue<>();
        Node x = root;
        boolean isVert = true;
        while (x != null) {
            // if it doesn't intersect, go closer to query point
            if (isVert) {
                if (rect.intersects(new RectHV(x.p.x(), x.axis.ymin(), x.p.x(), x.axis.ymax()))) {
                    // check if intersects the axis
                    // search down trees
                    search(q, x, rect);
                    break;
                }
                else {
                    if (x.p.x() > center(rect.xmin(), rect.xmax())) {
                        x = x.left;
                    }
                    else {
                        x = x.right;
                    }
                }
            }
            else {
                if (rect.intersects(new RectHV(x.p.x(), x.axis.ymin(), x.p.x(), x.axis.ymax()))) {
                    // check if intersects the axis
                    // search down trees
                    search(q, x, rect);
                    break;
                }
                else {
                    if ((x.p.y() > center(rect.ymin(), rect.ymax()))) {
                        x = x.left;
                    }
                    else {
                        x = x.right;
                    }
                }
            }
            isVert = !isVert;
        }
        return q;
    }
    */

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        nullCheck(p);

        if (root == null) return null;
        return near(root, p, root.p, true);
    }

    private Point2D near(Node node, Point2D p, Point2D min, boolean isVert) {

        Point2D c = min;

        if (node == null) return c;


        if (node.p.distanceSquaredTo(p) < c.distanceSquaredTo(p)) {
            c = node.p;
        }

        if (node.axis.distanceSquaredTo(p) < c.distanceSquaredTo(p)) {
            Node closeS;
            Node farS;

            if ((isVert && (node.p.x() > p.x())) || (!isVert && (node.p.y() > p.y()))) {
                closeS = node.left;
                farS = node.right;
            }
            else {
                closeS = node.right;
                farS = node.left;
            }
            c = near(closeS, p, c, !isVert);
            c = near(farS, p, c, !isVert);
        }
        return c;
    }

        /*
        Point2D distt = near(near, p, min, mindist, isVert);

        if (distt.distanceSquaredTo(p) < mindist) {
            min = distt;

        }
        else {
            Point2D point2D = near(far, p, min, mindist, isVert);
            if (point2D.distanceSquaredTo(p) < mindist) {
                min = point2D;
                // mindist = distt.distanceSquaredTo(p);
            }
        }
        */


    /*
    // Yuhh pythag a^2 + b^2 = c^2
    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    */

    private class Node {
        private Point2D p;
        private Node left;
        private Node right;
        private RectHV axis; // the search area


        public Node(Point2D p) {
            this.p = p;
        }
    }


/*
    // unit testing of the methods (optional)
    public static void main(String[] args) {

    }
    */    /*

     */

}

