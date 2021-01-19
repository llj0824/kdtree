import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PointSET {
    //Rule of thumb: √N-by-√N grid -> use rectangles
    private static final int GRID_DIMENSION = 10; // grid = 10 by 10
    private static final double SQUARE_GRID_SIZE = 1.0/GRID_DIMENSION; // total grid size = 1.
    private final List<RectWrapper> grid;
    private int count;

    // construct an empty set of points
    public PointSET() {
        grid = initGrid();
        count = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return count == 0;
    }

    // number of points in the set
    public int size() {
        return count;
    }

    // add the point to the set (if it is not already in the set) -> o(logn)
    public void insert(Point2D p) {
        assertNotNull(p);
        final RectWrapper rect = findRect(p.x(), p.y());
        boolean isNewPointAdded = rect.addPoint(p);

        // increment count.
        if (isNewPointAdded) {
            count++;
        }
    }

    // does the set contain point p? -> o(logn)
    public boolean contains(Point2D p) {
        assertNotNull(p);
        final RectWrapper rect = findRect(p.x(), p.y());
        return rect.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        grid.stream().flatMap(rect -> rect.getPoints().stream())
                .forEach(Point2D::draw);
    }

    // all points that are inside the rectangle (or on the boundary) -> o(n)
    public Iterable<Point2D> range(RectHV rect) {
        assertNotNull(rect);
        final List<Point2D> intersectingPoints = grid.stream()
                .filter(smallSqGrid -> rect.intersects(smallSqGrid.rect))
                .flatMap(intersectingSmallSqGrid -> intersectingSmallSqGrid.getPoints().stream())
                .filter(smallSqGridPts -> rect.contains(smallSqGridPts))
                .collect(Collectors.toList());

        return intersectingPoints;
    }

    // a nearest neighbor in the set to point p; null if the set is empty -> o(n)
    public Point2D nearest(Point2D p) {
        assertNotNull(p);
        final Comparator<Point2D> closestPointComparator = Comparator.comparingDouble(pt -> pt.distanceTo(p));
        return grid.stream()
                .flatMap(sq -> sq.getPoints().stream())
                .min(closestPointComparator)
                .orElse(null);
    }

    private RectWrapper findRect(final double x, final double y) {
        final int highestCol = 9;
        final int highestRow = 9;

        final int col = Math.min((int) (x/ SQUARE_GRID_SIZE), highestCol);
        final int row = Math.min((int) (y/ SQUARE_GRID_SIZE), highestRow);
        int index = getIndex(row, col);

        return grid.get(index);
    }

    private int getIndex(final int row, final int col) {
        return (row * GRID_DIMENSION) + col;
    }

    private List<RectWrapper> initGrid() {
        final List<RectWrapper> arr2d = new ArrayList<>();

        for (int row = 0; row < GRID_DIMENSION; row++) {
            for (int col = 0; col < GRID_DIMENSION; col++) {
                final double xmin = col * SQUARE_GRID_SIZE;
                final double xmax = xmin + SQUARE_GRID_SIZE;
                final double ymin = row * SQUARE_GRID_SIZE;
                final double ymax = ymin + SQUARE_GRID_SIZE;
                final RectHV rect = new RectHV(xmin, ymin, xmax, ymax);
                arr2d.add(getIndex(row, col), new RectWrapper(rect));
            }
        }
        return arr2d;
    }

    private void assertNotNull(Object o ) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
    }

    private class RectWrapper {
        private RectHV rect;
        private Set<Point2D> points;

        public RectWrapper(final RectHV rect) {
            this.rect = rect;
            this.points = new TreeSet<>();
        }

        public boolean contains(final Point2D p) {
            return points.contains(p);
        }

        public boolean addPoint(final Point2D p) {
            // returns true if point did not already exist in set.
            return points.add(p);
        }

        public Set<Point2D> getPoints() {
            return points;
        }
    }
}
