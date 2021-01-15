import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import org.junit.Before;
import org.junit.Test;

public class PointSETTest {
    PointSET brute;
    @Before
    public void setup() {
        // initialize the data structures from file
        String filename = "circle100.txt";
        In in = new In(filename);
        brute = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.insert(p);
        }
    }

    @Test
    public void testRange() {
        final RectHV rect = new RectHV(0.0, 0.0, 1.0, 1.0);
        brute.range(rect);
    }
}
