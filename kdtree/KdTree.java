import java.util.function.BiFunction;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;

public class KdTree {
  private static final double pointSize = 0.01;
  private static final double lineWidth = 0.005;
  private Node root;
  private boolean logVisitedNodesNearest = false; // for testing

  private class Node {
    private Point2D point;
    private Node left, right;
    private int N;
    private boolean compareByX;
    private RectHV subtreeRect;

    public Node(Point2D point, int N, boolean compareByX, RectHV subtreeRect) {
      this.point = point;
      this.N = N;
      this.compareByX = compareByX;
      this.subtreeRect = subtreeRect;
    }

    @Override
    public String toString() {
      return point.toString() + " " + (compareByX ? "cmpX" : "cmpY");
    }
  }

  public boolean isEmpty() {
    return root == null;
  }

  public int size() {
    return size(root);
  }

  private int size(Node h) {
    if (h == null)
      return 0;
    else
      return h.N;
  }

  public void insert(Point2D p) {
    validateNotNull(p);

    root = insert(root, p, true, 0, 0, 1, 1);
  }

  private Node insert(Node h, Point2D p, boolean comparingByX, double xmin, double ymin, double xmax, double ymax) {
    if (h == null)
      return new Node(p, 1, comparingByX, new RectHV(xmin, ymin, xmax, ymax));

    if (h.point.equals(p))
      return h; // do not modify tree

    // determine based on given node whether to compare by X or Y next
    if (h.compareByX) {
      double currentX = h.point.x();

      if (currentX > p.x())
        h.left = insert(h.left, p, !comparingByX, xmin, ymin, currentX, ymax);
      else
        h.right = insert(h.right, p, !comparingByX, currentX, ymin, xmax, ymax);

    } else {
      double currentY = h.point.y();

      if (currentY > p.y())
        h.left = insert(h.left, p, !comparingByX, xmin, ymin, xmax, currentY);
      else
        h.right = insert(h.right, p, !comparingByX, xmin, currentY, xmax, ymax);
    }

    h.N = size(h.left) + size(h.right) + 1;
    return h;
  }

  public boolean contains(Point2D p) {
    validateNotNull(p);

    return contains(root, p);
  }

  private boolean contains(Node h, Point2D p) {
    if (h == null)
      return false;

    // pruning rule: don't search subtrees whose rectangles don't contain target
    if (!h.subtreeRect.contains(p))
      return false;

    if (h.point.equals(p))
      return true;

    return contains(h.left, p) || contains(h.right, p);
  }

  public void draw() {
    draw(root);
  }

  private void draw(Node h) {
    if (h == null)
      return;

    if (h.compareByX) {
      double currentX = h.point.x();

      StdDraw.setPenRadius(lineWidth);
      StdDraw.setPenColor(StdDraw.RED);
      StdDraw.line(currentX, h.subtreeRect.ymin(), currentX, h.subtreeRect.ymax());
    } else {
      double currentY = h.point.y();

      StdDraw.setPenRadius(lineWidth);
      StdDraw.setPenColor(StdDraw.BLUE);
      StdDraw.line(h.subtreeRect.xmin(), currentY, h.subtreeRect.xmax(), currentY);
    }

    StdDraw.setPenRadius(pointSize);
    StdDraw.setPenColor(StdDraw.BLACK);
    h.point.draw();

    draw(h.left);
    draw(h.right);
  }

  public Iterable<Point2D> range(RectHV rect) {
    validateNotNull(rect);

    Queue<Point2D> pointsInRange = new Queue<>();

    range(root, rect, pointsInRange);

    return pointsInRange;
  }

  private void range(Node h, RectHV rect, Queue<Point2D> points) {
    if (h == null)
      return;

    // pruning rule: don't search subtrees that don't intersect with given rect
    if (!h.subtreeRect.intersects(rect))
      return;

    if (rect.contains(h.point))
      points.enqueue(h.point);

    range(h.left, rect, points);
    range(h.right, rect, points);
  }

  public Point2D nearest(Point2D p) {
    validateNotNull(p);

    if (isEmpty())
      return null;

    return nearest(root, root.point, p);
  }

  private Point2D nearest(Node h, Point2D closestFound, Point2D p) {
    if (h == null)
      return closestFound;

    if (logVisitedNodesNearest)
      System.out.println(h.toString());

    double closestFoundDistance = closestFound.distanceSquaredTo(p);

    if (closestFoundDistance == 0)
      return closestFound;

    // pruning rule: don't search if closestFound is closer than current rectangle
    if (closestFoundDistance < h.subtreeRect.distanceSquaredTo(p))
      return closestFound;

    BiFunction<Node, Node, Point2D> findNearest = (Node searchFirst, Node searchSecond) -> {
      Point2D localClosest;
      double localClosestDistance;

      // determine proximity of current point
      double currentPointDistance = h.point.distanceSquaredTo(p);
      if (currentPointDistance == 0)
        return h.point;

      // update closest found point between given closestFound and current point
      if (currentPointDistance < closestFoundDistance) {
        localClosest = h.point;
        localClosestDistance = currentPointDistance;
      } else {
        localClosest = closestFound;
        localClosestDistance = closestFoundDistance;
      }

      // search the first subtree
      Point2D closestFirst = nearest(searchFirst, localClosest, p);

      // update closest found point between previous and result of first search
      double closestFirstDistance = closestFirst.distanceSquaredTo(p);
      if (closestFirstDistance == 0)
        return closestFirst;

      if (closestFirstDistance < localClosestDistance) {
        localClosest = closestFirst;
        localClosestDistance = closestFirstDistance;
      }

      // search second subtree
      Point2D closestSecond = nearest(searchSecond, localClosest, p);
      double closestSecondDistance = closestSecond.distanceSquaredTo(p);
      if (closestSecondDistance == 0)
        return closestSecond;

      // return the closest overall point within subtree
      return localClosestDistance < closestSecondDistance ? localClosest : closestSecond;
    };

    if (h.compareByX) {
      if (p.x() < h.point.x()) {
        return findNearest.apply(h.left, h.right);
      } else {
        return findNearest.apply(h.right, h.left);
      }
    } else {
      if (p.y() < h.point.y()) {
        return findNearest.apply(h.left, h.right);
      } else {
        return findNearest.apply(h.right, h.left);
      }
    }
  }

  private void validateNotNull(Object toCheck) {
    if (toCheck == null)
      throw new IllegalArgumentException();
  }

  public static void main(String[] args) {
    KdTree s = new KdTree();

    assert s.isEmpty();
    assert s.size() == 0;

    Point2D firstPoint = new Point2D(0.5, 0.5);
    s.insert(firstPoint);

    assert !s.isEmpty();
    assert s.size() == 1;
    assert s.contains(new Point2D(0.5, 0.5));

    Point2D secondPoint = new Point2D(0, 0);
    s.insert(secondPoint);
    assert s.nearest(new Point2D(0.2, 0.2)).equals(secondPoint);
    assert s.nearest(new Point2D(0.7, 0.7)).equals(firstPoint);

    int containedPoints = 0;
    RectHV shouldContainNone = new RectHV(0.6, 0.6, 1, 1);
    for (Point2D point : s.range(shouldContainNone)) {
      containedPoints++;
    }
    assert containedPoints == 0;

    containedPoints = 0;
    RectHV shouldContainOne = new RectHV(0.5, 0.5, 1, 1);
    for (Point2D point : s.range(shouldContainOne)) {
      containedPoints++;
    }
    assert containedPoints == 1;

    containedPoints = 0;
    RectHV shouldContainTwo = new RectHV(0, 0, 1, 1);
    for (Point2D point : s.range(shouldContainTwo)) {
      containedPoints++;
    }
    assert containedPoints == 2;

    // test case detected by autograder
    s = new KdTree();
    s.logVisitedNodesNearest = true;
    Point2D searchPoint = new Point2D(0.2, 0.3);
    s.insert(new Point2D(0.7, 0.2));
    s.insert(new Point2D(0.5, 0.4));
    s.insert(searchPoint);
    s.insert(new Point2D(0.4, 0.7));
    s.insert(new Point2D(0.9, 0.6));

    assert s.nearest(searchPoint) == searchPoint;
    /*
     * The autograder detected that nearest() erroneously visited the last node
     * (0.9, 0.6) as part of its search when given this sequence, but my code
     * doesn't reproduce the issue.
     */

    s.logVisitedNodesNearest = false;

    s = new KdTree();
    int numPointsToDraw = 50;
    for (int i = 0; i < numPointsToDraw; i++) {
      s.insert(new Point2D(StdRandom.uniformDouble(), StdRandom.uniformDouble()));
    }
    s.draw();

    System.out.println("All tests passed");
  }
}
