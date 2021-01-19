//import edu.princeton.cs.algs4.In;
//import edu.princeton.cs.algs4.Point2D;
//import edu.princeton.cs.algs4.RectHV;
//import edu.princeton.cs.algs4.StdDraw;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//import java.util.stream.StreamSupport;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//public class KdTreeTest {
//    Point2D point;
//    KdTree kdTree;
//    int numPoints;
//
//    @Before
//    public void setup() {
//        // initialize the data structures from file
//        String filename = "circle10.txt";
//
//        In in = new In(filename);
//        kdTree = new KdTree();
//        numPoints = 0;
//        while (!in.isEmpty()) {
//            double x = in.readDouble();
//            double y = in.readDouble();
//            point = new Point2D(x, y);
//            kdTree.insert(point);
//            numPoints++;
//        }
//
//    }
//
//    @Test
//    public void testDraw() {
//        kdTree.draw();
//        StdDraw.pause(20);
//    }
//
//    @Test
//    public void kdTree_containsAllPoints() {
//        // check all points return true;
//        final List<KdTree.KdNode> list = new ArrayList();
//        // run operation
//        kdTree.forEachKdNode(kdTree.root, list::add);
//
//        assertEquals(numPoints, kdTree.size());
//
//        list.stream().map(node -> node.point).forEach(System.out::println);
//        final long numPointsContainedInKdTree = list.stream()
//                .map(kdNode -> kdNode.point)
//                .filter(kdNodePoint -> kdTree.contains(kdNodePoint))
//                .count();
//        assertEquals(numPoints, numPointsContainedInKdTree);
//    }
//
//    @Test
//    public void kdTree_rangeRect_bisectedHorizontal_intoQuarters() {
//        final int expectedNumPointsTopQuarter = 3;
//        final RectHV topQuarter = new RectHV(0, 0.75, 1, 1.0);
//        Collection<Point2D> topQuarterPoints = toCollection(kdTree.range(topQuarter));
//        assertEquals(topQuarterPoints.size(), expectedNumPointsTopQuarter);
//        assertEquals(topQuarterPoints.stream().filter(p -> p.y() < 0.75  || p.y() > 1.0).count(), 0);
//
//        final int expectedNumPointsUpperMiddle = 2;
//        final RectHV upperMiddle = new RectHV(0, 0.50, 1, 0.75);
//        Collection<Point2D> upperMiddlePoints = toCollection(kdTree.range(upperMiddle));
//        assertEquals(upperMiddlePoints.size(), expectedNumPointsUpperMiddle);
//        assertEquals(upperMiddlePoints.stream().filter(p -> p.y() < 0.50  || p.y() > 0.75).count(), 0);
//
//        final int expectedNumPointsBottomMiddle = 2;
//        final RectHV bottomMiddle = new RectHV(0, 0.25, 1, 0.50);
//        Collection<Point2D> bottomMiddlePoints = toCollection(kdTree.range(bottomMiddle));
//        assertEquals(bottomMiddlePoints.size(), expectedNumPointsBottomMiddle);
//        assertEquals(bottomMiddlePoints.stream().filter(p -> p.y() < 0.25  || p.y() > 0.50).count(), 0);
//
//        final int expectedNumPointsBottomQuarter = 3;
//        final RectHV bottomQuarter = new RectHV(0, 0.0, 1, 0.25);
//        Collection<Point2D> bottomQuarterPoints = toCollection(kdTree.range(bottomQuarter));
//        assertEquals(bottomQuarterPoints.size(), expectedNumPointsBottomQuarter);
//        assertEquals(bottomQuarterPoints.stream().filter(p -> p.y() < 0.0 || p.y() > 0.25).count(), 0);
//    }
//
//    private Collection<Point2D> toCollection(Iterable<Point2D> iterable) {
//        List<Point2D> listPoints = new ArrayList<Point2D>();
//        iterable.forEach(listPoints::add);
//        return listPoints;
//    }
//
//
//}
