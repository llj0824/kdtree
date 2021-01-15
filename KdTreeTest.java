import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KdTreeTest {
    Point2D point;
    KdTree kdTree;
    int numPoints;

    @Before
    public void setup() {
        // initialize the data structures from file
        String filename = "circle10.txt";

        In in = new In(filename);
        kdTree = new KdTree();
        numPoints = 0;
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            point = new Point2D(x, y);
            kdTree.insert(point);
            numPoints++;
        }

    }

    @Test
    public void testDraw() {
        kdTree.draw();
        StdDraw.pause(20);
    }

    @Test
    public void kdTree_containsAllPoints() {
        // check all points return true;
        final List<KdTree.KdNode> list = new ArrayList();
        // run operation
        kdTree.forEachKdNode(kdTree.root, list::add);

        assertEquals(numPoints, kdTree.size());

        list.stream().map(node -> node.point).forEach(System.out::println);
        final long numPointsContainedInKdTree = list.stream()
                .map(kdNode -> kdNode.point)
                .filter(kdNodePoint -> kdTree.contains(kdNodePoint))
                .count();
        assertEquals(numPoints, numPointsContainedInKdTree);
    }

}
