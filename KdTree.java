import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class KdTree {
    private static final Axis DEFAULT_AXIS = Axis.Vertical;
    private static final double INF = 1;
    private static final double NEG_INF = 0;
    private int count;
    private KdNode root;

    public KdTree() {
        this.count = 0;
    }

    public void insert(Point2D p) {
        assertNotNull(p);
        final boolean isPointAdded = dfsInsert(p, root);
        if (isPointAdded) {
            count++;
        }
    }

    // returns true, if node is inserted.
    // returns false, if node is rejected (duplicate)
    private boolean dfsInsert(Point2D newPoint, KdNode currentNode) {
        // special case: root node
        if (currentNode == null) {
            root = new KdNode(newPoint, null, DEFAULT_AXIS);
            return true;
        } else if (newPoint.equals(currentNode.point)) {
            // reject duplicate point.
            return false;
        }

        final Axis childComparisonAxis = isHorizontalAxis(currentNode.axis) ? Axis.Vertical : Axis.Horizontal;
        final double currVal = isHorizontalAxis(currentNode.axis) ? currentNode.point.y() : currentNode.point.x();
        final double childVal = isHorizontalAxis(currentNode.axis) ? newPoint.y() : newPoint.x();

        // choices -> insert left, insert right
        if (childVal >= currVal) {
            if (currentNode.right == null) {
                // empty right leaf -> insert here
                currentNode.right = new KdNode(newPoint, currentNode, childComparisonAxis);
                return true;
            }
            // insert into right subtree
            return dfsInsert(newPoint, currentNode.right);
        } else {
            if (currentNode.left == null) {
                // empty left leaf -> insert here
                currentNode.left = new KdNode(newPoint, currentNode, childComparisonAxis);
                return true;
            }
            // insert into left subtree
            return dfsInsert(newPoint, currentNode.left);
        }
    }

    public boolean contains(Point2D p) {
        assertNotNull(p);
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
        final double nodeComparisonValue = isHorizontalAxis(node.axis) ? node.point.y() : node.point.x();
        final double pointComparisonValue = isHorizontalAxis(node.axis) ? p.y() : p.x();
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
        };
        forEachKdNode(root, op);
    }

    private void forEachKdNode(KdNode node, Consumer<KdNode> function) {
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
        assertNotNull(rect);
        // Range search. To find all points contained in a given query rectangle, start at the root and recursively search for points in both subtrees
        // using the following pruning rule: if the query rectangle does not intersect the rectangle corresponding to a node,
        // there is no need to explore that node (or its subtrees). A subtree is searched only if it might contain a point contained in the query rectangle.

        final List<Point2D> intersectingPoints = new ArrayList<>();
        dfsRange(rect, root, intersectingPoints);
        return intersectingPoints;
    }

    private void dfsRange(RectHV queryRect, KdNode kdNode, List<Point2D> containedPoints) {
        // base case -> null. Stop Searching
        if (kdNode == null) {
            return;
        }

        // choices -> kdnode intersects queryRect. add KdNode::point if in queryRect. Keep searching subtree.
        //         -> does not intersect. stop searching subtree.
        final double xmin = kdNode.xrange[0];
        final double xmax = kdNode.xrange[1];
        final double ymin = kdNode.yrange[0];
        final double ymax = kdNode.yrange[1];
        final RectHV nodeRect = new RectHV(xmin, ymin, xmax, ymax);
        if (nodeRect.intersects(queryRect)) {
            // kdnode intersects -> add point
            if (queryRect.contains(kdNode.point)) {
                // nodeRect might be superset of queryRect.
                containedPoints.add(kdNode.point);
            }
            // add left and right subtree
            dfsRange(queryRect, kdNode.left, containedPoints);
            dfsRange(queryRect, kdNode.right, containedPoints);
        }
        // kdnode does not intersect. Not searching anymore. Return.
    }

    public Point2D nearest(Point2D p) {
        assertNotNull(p);
        // do not search subtree (rect), if it is further than closest point found so far
        // choice -> choose first the subtree on same side of spitting line
        final KdNode impossibleFarAwayNode = new KdNode(new Point2D(INF * 2.0, INF * 2), null, null);
        final KdNode closestNode = dfsNearest(p, root, impossibleFarAwayNode);
        return closestNode.point;
    }

    private KdNode dfsNearest(final Point2D queryPoint, final KdNode currNode, KdNode closestNode) {
        // base condition -> if null -> return
        if (currNode == null) {
            return null;
        }

        // process current node - check if new closePoint currNode::point is closer
        final double currPointToQueryPointDistance = currNode.point.distanceSquaredTo(queryPoint);
        final double closestPointToQueryPointDistance = closestNode.point.distanceSquaredTo(queryPoint);
        if (currPointToQueryPointDistance < closestPointToQueryPointDistance) {
            closestNode = currNode;
        }

        // choice -> left or right
        //        -> go subtree on same axis of point -> if exists: search subtree. Do not go other subtree
        //                                            -> if not exist: search other subtree
        //        -> update closestPoint if subtree returns closer minPoint
        final Axis comparisonAxis = currNode.axis;
        final double nodeComparisonValue = isHorizontalAxis(comparisonAxis) ? currNode.point.y() : currNode.point.x();
        final double queryComparisonValue = isHorizontalAxis(comparisonAxis) ? queryPoint.y() : queryPoint.x();
        final boolean isQueryOnLeftSubtree = queryComparisonValue < nodeComparisonValue;

        final KdNode sameSideSubtree = isQueryOnLeftSubtree ? currNode.left : currNode.right;
        final KdNode otherSideSubtree = isQueryOnLeftSubtree ? currNode.right : currNode.left;

        final KdNode sameSideSubtreeResult = dfsNearest(queryPoint, sameSideSubtree, closestNode);
        if (sameSideSubtreeResult != null) {
            if (sameSideSubtreeResult.point.distanceSquaredTo(queryPoint) < closestNode.point.distanceSquaredTo(queryPoint)) {
                closestNode = sameSideSubtreeResult;
            }
        }

        // if the distance from query point to border (aka dividing line) of otherside's subtree,
        // is further than distance sameside closest point to query point. Don't bother searching.
        final double distanceToDividingLineSq = currNode.getRect().distanceSquaredTo(queryPoint);
        final double distanceToClosestPointSq = closestNode.point.distanceSquaredTo(queryPoint);
        if (distanceToClosestPointSq >= distanceToDividingLineSq) {
            final KdNode otherSideSubtreeResult = dfsNearest(queryPoint, otherSideSubtree, closestNode);
            if (otherSideSubtreeResult != null &&
                    otherSideSubtreeResult.point.distanceSquaredTo(queryPoint) < closestNode.point.distanceSquaredTo(queryPoint)) {
                closestNode = otherSideSubtreeResult;
            }
        }

        return closestNode;
    }

    private void log(final String id, final KdNode closest, final KdNode updatedClosest, final Point2D queryPoint) {
        double originalDistance = closest.point.distanceSquaredTo(queryPoint);
        double newDistance = updatedClosest.point.distanceSquaredTo(queryPoint);

        String printLine = String.format("[%s] closest:(%s) -> (%s). Distance delta: %s",
                id, closest.point, updatedClosest.point, (originalDistance - newDistance));
        System.out.println(printLine);
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


    private class KdNode {
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
            // Comment out because cousera checkstyle is doesn't allow Color import lol.
//            StdDraw.setPenColor(isHorizontalAxis() ? Color.blue : Color.red);
//            if (isHorizontalAxis()) {
//                final double x0 = xrange[0];
//                final double x1 = xrange[1];
//                final double y = point.y();
//                StdDraw.line(x0, y, x1, y);
//            } else {
//                // vertical line
//                final double x0 = point.x();
//                final double y0 = yrange[0];
//                final double x1 = point.x();
//                final double y1 = yrange[1];
//                StdDraw.line(x0, y0, x1, y1);
//            }
//            StdDraw.setPenRadius(0.01D);
//            StdDraw.setPenColor(Color.black);
//            StdDraw.point(point.x(), point.y());
//            //reset to normal
//            StdDraw.setPenRadius();
        }

        public RectHV getRect() {
            return new RectHV(xrange[0], yrange[0],
                              xrange[1], yrange[1]);
        }
    }

    private enum Axis {
        Vertical,
        Horizontal
    }

    private void assertNotNull(Object o ) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
    }
}
