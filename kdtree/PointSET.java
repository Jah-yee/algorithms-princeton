import java.util.Iterator;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdRandom;

public class PointSET {
  private SET<Point2D> pointSet;

  public PointSET() {
    pointSet = new SET<>();
  }

  public boolean isEmpty() {
    return pointSet.isEmpty();
  }

  public int size() {
    return pointSet.size();
  }

  public void insert(Point2D p) {
    validateNotNull(p);
    pointSet.add(p);
  }

  public boolean contains(Point2D p) {
    validateNotNull(p);
    return pointSet.contains(p);
  }

  public void draw() {
    Iterator<Point2D> keys = pointSet.iterator();
    while (keys.hasNext()) {
      keys.next().draw();
    }
  }

  public Iterable<Point2D> range(RectHV rect) {
    validateNotNull(rect);

    Queue<Point2D> pointsInRange = new Queue<>();
    Iterator<Point2D> points = pointSet.iterator();

    while (points.hasNext()) {
      Point2D currentPoint = points.next();
      if (rect.contains(currentPoint))
        pointsInRange.enqueue(currentPoint);
    }

    return pointsInRange;
  }

  public Point2D nearest(Point2D p) {
    validateNotNull(p);
    if (isEmpty())
      return null;

    Iterator<Point2D> points = pointSet.iterator();
    double minDistance = Double.POSITIVE_INFINITY;
    Point2D nearest = pointSet.max();

    while (points.hasNext()) {
      Point2D currentPoint = points.next();
      double currentDistance = currentPoint.distanceSquaredTo(p); // slightly faster than distanceTo()
      if (currentDistance < minDistance) {
        nearest = currentPoint;
        minDistance = currentDistance;
      }
    }

    return nearest;
  }

  private void validateNotNull(Object toCheck) {
    if (toCheck == null)
      throw new IllegalArgumentException();
  }

  public static void main(String[] args) {
    PointSET s = new PointSET();

    assert s.isEmpty();
    assert s.size() == 0;

    Point2D firstPoint = new Point2D(0.5, 0.5);

    s.nearest(firstPoint); // shouldn't throw error

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

    s = new PointSET();
    int numPointsToDraw = 1000;
    for (int i = 0; i < numPointsToDraw; i++) {
      s.insert(new Point2D(StdRandom.uniformDouble(), StdRandom.uniformDouble()));
    }
    s.draw();

    System.out.println("All tests passed");
  }
}
