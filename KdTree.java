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
        return rangg(rect);
    }

    private boolean intersects(RectHV rect, Point2D p, boolean isVert) {
        if (isVert) {
            return rect.xmin() <= p.x() && rect.xmax() >= p.x();
        }
        else {
            return rect.ymin() <= p.y() && rect.ymax() >= p.y();
        }
    }

    private void search(Queue<Point2D> q, Node node, RectHV rect) {
        if (node == null) return;

        search(q, node.left, rect);
        search(q, node.right, rect);

        Point2D p = node.p;

        if (p.x() >= rect.xmin() && p.x() <= rect.xmax() && p.y() >= rect.ymin() && p.y() <= rect
                .ymax()) {
            q.enqueue(p);
        }


    }

    private double center(double min, double max) {
        return (max - min) / 2 + min;
    }

    // Hides the variables
    private Iterable<Point2D> rangg(RectHV rect) {
        Queue<Point2D> q = new Queue<>();
        Node x = root;
        boolean isVert = true;
        while (x != null) {
            // check if intersects the axis
            if (intersects(rect, x.p, isVert)) {
                // search down trees
                search(q, x, rect);
                break;
            }
            else {
                // if it doesn't intersect, go closer to query point
                if (isVert) {
                    if (x.p.x() > center(rect.xmin(), rect.xmax())) {
                        x = x.left;
                    }
                    else {
                        x = x.right;
                    }
                }
                else {
                    if ((x.p.y() > center(rect.ymin(), rect.ymax()))) {
                        x = x.left;
                    }
                    else {
                        x = x.right;
                    }
                }
                isVert = !isVert;
            }
        }
        return q;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        nullCheck(p);

        return near(root, p, root.p, true);
    }

    private Point2D near(Node node, Point2D p, Point2D min, boolean isVert) {

        if (node == null) return min;

        if (node.p.equals(p)) return node.p;

        if (node.p.distanceSquaredTo(p) < min.distanceSquaredTo(p))
            min = node.p;


        // check which side the query point is on compared to the split

        Node near;
        Node far;
        if (isVert) {
            if (node.p.x() > p.x()) {
                near = node.left;
                far = node.right;
            }
            else {
                near = node.right;
                far = node.left;

            }
        }
        else {
            if (node.p.y() > p.y()) {
                near = node.left;
                far = node.right;
            }
            else {
                near = node.right;
                far = node.left;
            }
        }
        min = near(near, p, min, isVert);
        min = near(far, p, min, isVert);

        return min;
    }

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

