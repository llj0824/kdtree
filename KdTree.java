import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

public class KdTree {
    private static final Axis DEFAULT_AXIS = Axis.Vertical;
    private static final double INF = 1;
    private static final double NEG_INF = 0;
    private int count;
    KdNode root;

    public KdTree() {
        this.count = 0;
    }

    public void insert(Point2D p) {
        dfsInsert(p, root);
        count++;
    }

    private void dfsInsert(Point2D newPoint, KdNode currentNode) {
        // special case: root node
        if (currentNode == null) {
            root = new KdNode(newPoint, null, DEFAULT_AXIS);
            return;
        }

        final Axis childComparisonAxis = isHorizontalAxis(currentNode.axis) ? Axis.Horizontal : Axis.Vertical;
        final double currVal = isHorizontalAxis(currentNode.axis) ? currentNode.point.x() : currentNode.point.y();
        final double childVal = isHorizontalAxis(currentNode.axis) ? newPoint.x() : newPoint.y();

        // choices -> insert left, insert right
        if (childVal >= currVal) {
            if (currentNode.right == null) {
                // insert right
                currentNode.right = new KdNode(newPoint, currentNode, childComparisonAxis);
                return;
            }
            // insert into right subtree
            dfsInsert(newPoint, currentNode.right);
        } else {
            if (currentNode.left == null) {
                // insert left
                currentNode.left = new KdNode(newPoint, currentNode, childComparisonAxis);
                return;
            }
            // insert into left subtree
            dfsInsert(newPoint, currentNode.left);
        }
    }

    public boolean contains(Point2D p) {
        return dfsContains(p, root);
    }

    private boolean dfsContains(final Point2D p, final KdNode node) {
        // base case -> null : false, equals: true
        if (node == null) {
            // end of path. not found.
            return false;
        } else if (node.point.equals(p)) {
            // found node.
            return true;
        }

        // check if out of range
        final double nodeComparisonValue = isHorizontalAxis(node.axis) ? node.point.x() : node.point.y();
        final double pointComparisonValue = isHorizontalAxis(node.axis) ? p.x() : p.y();
        // choices -> search left subtree, search right subtree
        if (pointComparisonValue >= nodeComparisonValue) {
            // search right subtree.
            if (dfsContains(p, node.right)) {
                // found node. stop searching.
                return true;
            }
        } else {
            // search left subtree.
            if (dfsContains(p, node.left)) {
                // found node. stop searching.
                return true;
            }
        }

        return false;
    }

    public void draw() {
        final Consumer<KdNode> op = (kdNode) -> {
            kdNode.draw();
            System.out.println(String.format("%s -> x:[%s,%s] y:[%s,%s]", kdNode.point, kdNode.xrange[0], kdNode.xrange[1], kdNode.yrange[0], kdNode.yrange[1]));
        };
        forEachKdNode(root, op);
    }

    void forEachKdNode(KdNode node, Consumer<KdNode> function) {
        if (node == null) {
            return;
        } else {
            // run function
            function.accept(node);
        }

        // transverse children
        forEachKdNode(node.left, function);
        forEachKdNode(node.right, function);
    }

    public Iterable<Point2D> range(RectHV rect) {
        // Range search. To find all points contained in a given query rectangle, start at the root and recursively search for points in both subtrees
        // using the following pruning rule: if the query rectangle does not intersect the rectangle corresponding to a node,
        // there is no need to explore that node (or its subtrees). A subtree is searched only if it might contain a point contained in the query rectangle.

        return null;
    }

    public Point2D nearest(Point2D p) {
        return null;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    private boolean isHorizontalAxis(final Axis axis) {
        return Axis.Horizontal.equals(axis);
    }


    protected class KdNode {
        double[] xrange;
        double[] yrange;
        KdNode parent;
        KdNode left;
        KdNode right;
        final Axis axis;
        final Point2D point;


        public KdNode(final Point2D point, final KdNode parent, final Axis axis) {
            this.point = point;
            this.axis = axis;
            this.parent = parent;
            setRange();
        }

        private void setRange() {
            if (parent == null) {
                this.xrange = new double[]{NEG_INF, INF};
                this.yrange = new double[]{NEG_INF, INF};
                return;
            }
            this.xrange = Arrays.copyOf(parent.xrange, 2);
            this.yrange = Arrays.copyOf(parent.yrange, 2);

            if (isHorizontalAxis()) {
                // compare x
                if (point.x() >= parent.point.x()) {
                    // min x value is parent.x
                    this.xrange[0] = parent.point.x();
                } else {
                    // parent.x less than child.x -> max child.x value is parent.x
                    this.xrange[1] = parent.point.x();
                }
            } else {
                // compare y
                if (point.y() >= parent.point.y()) {
                    // min x value is parent.x
                    this.yrange[0] = parent.point.y();
                } else {
                    // parent.x less than child.x -> max child.x value is parent.x
                    this.yrange[1] = parent.point.y();
                }
            }
        }

        private boolean isHorizontalAxis() {
            return Axis.Horizontal.equals(this.axis);
        }

        private void draw() {
            StdDraw.setPenColor(isHorizontalAxis() ? Color.blue : Color.red);

            if (isHorizontalAxis()) {
                final double x0 = xrange[0];
                final double x1 = xrange[1];
                final double y = point.y();
                StdDraw.line(x0, y, x1, y);
            } else {
                // vertical line
                final double x0 = point.x();
                final double y0 = yrange[0];
                final double x1 = point.x();
                final double y1 = yrange[1];
                StdDraw.line(x0, y0, x1, y1);
            }
            StdDraw.setPenRadius(0.01D);
            StdDraw.setPenColor(Color.black);
            StdDraw.point(point.x(), point.y());
            StdDraw.setPenRadius(); //reset to normal
        }
    }

    private enum Axis {
        Vertical,
        Horizontal
    }
}
