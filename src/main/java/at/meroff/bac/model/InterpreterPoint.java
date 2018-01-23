package at.meroff.bac.model;

import java.awt.geom.Point2D;
import java.util.Comparator;

public class InterpreterPoint extends Point2D.Double implements Comparable<InterpreterPoint> {


    /**
     * Compares two points by x-coordinate.
     */
    public static final Comparator<InterpreterPoint> X_ORDER = new XOrder();

    /**
     * Compares two points by y-coordinate.
     */
    public static final Comparator<InterpreterPoint> Y_ORDER = new YOrder();

    /**
     * Compares two points by polar radius.
     */
    public static final Comparator<InterpreterPoint> R_ORDER = new ROrder();

    public InterpreterPoint(Point2D p) {
        this(p.getX(), p.getY());
    }

    public InterpreterPoint(double x, double y) {
        if (java.lang.Double.isInfinite(x) || java.lang.Double.isInfinite(y))
            throw new IllegalArgumentException("Coordinates must be finite");
        if (java.lang.Double.isNaN(x) || java.lang.Double.isNaN(y))
            throw new IllegalArgumentException("Coordinates cannot be NaN");
        if (x == 0.0) this.x = 0.0;  // convert -0.0 to +0.0
        else          this.x = x;

        if (y == 0.0) this.y = 0.0;  // convert -0.0 to +0.0
        else          this.y = y;
    }

    /**
     * Returns the polar radius of this point.
     * @return the polar radius of this point in polar coordiantes: sqrt(x*x + y*y)
     */
    public double getR() {
        return Math.sqrt(x*x + y*y);
    }

    /**
     * Returns the angle of this point in polar coordinates.
     * @return the angle (in radians) of this point in polar coordiantes (between –&pi; and &pi;)
     */
    public double getTheta() {
        return Math.atan2(y, x);
    }

    /**
     * Returns the angle between this point and that point.
     * @return the angle in radians (between –&pi; and &pi;) between this point and that point (0 if equal)
     */
    private double getAngleTo(InterpreterPoint that) {
        double dx = that.getX() - this.x;
        double dy = that.getY() - this.y;
        return Math.atan2(dy, dx);
    }

    /**
     * Returns true if a→b→c is a counterclockwise turn.
     * @param a first point
     * @param b second point
     * @param c third point
     * @return { -1, 0, +1 } if a→b→c is a { clockwise, collinear; counterclocwise } turn.
     */
    public static int ccw(InterpreterPoint a, InterpreterPoint b, InterpreterPoint c) {
        double area2 = (b.getX()-a.getX())*(c.getY()-a.getY()) - (b.getY()-a.getY())*(c.getX()-a.getX());
        if      (area2 < 0) return -1;
        else if (area2 > 0) return +1;
        else                return  0;
    }

    /**
     * Returns twice the signed area of the triangle a-b-c.
     * @param a first point
     * @param b second point
     * @param c third point
     * @return twice the signed area of the triangle a-b-c
     */
    public static double area2(InterpreterPoint a, InterpreterPoint b, InterpreterPoint c) {
        return (b.getX()-a.getX())*(c.getY()-a.getY()) - (b.getY()-a.getY())*(c.getX()-a.getX());
    }

    /**
     * Returns the Euclidean distance between this point and that point.
     * @param that the other point
     * @return the Euclidean distance between this point and that point
     */
    public double distanceTo(InterpreterPoint that) {
        double dx = this.x - that.getX();
        double dy = this.y - that.getY();
        return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * Returns the square of the Euclidean distance between this point and that point.
     * @param that the other point
     * @return the square of the Euclidean distance between this point and that point
     */
    public double distanceSquaredTo(InterpreterPoint that) {
        double dx = this.x - that.getX();
        double dy = this.y - that.getY();
        return dx*dx + dy*dy;
    }

    public int compareTo(InterpreterPoint that) {
        if (this.y < that.getY()) return -1;
        if (this.y > that.getY()) return +1;
        if (this.x < that.getX()) return -1;
        if (this.x > that.getX()) return +1;
        return 0;
    }

    /**
     * Compares two points by polar angle (between 0 and 2&pi;) with respect to this point.
     *
     * @return the comparator
     */
    public Comparator<InterpreterPoint> polarOrder() {
        return new PolarOrder();
    }

    /**
     * Compares two points by atan2() angle (between –&pi; and &pi;) with respect to this point.
     *
     * @return the comparator
     */
    public Comparator<InterpreterPoint> atan2Order() {
        return new Atan2Order();
    }

    /**
     * Compares two points by distance to this point.
     *
     * @return the comparator
     */
    public Comparator<InterpreterPoint> distanceToOrder() {
        return new DistanceToOrder();
    }

    // compare points according to their x-coordinate
    private static class XOrder implements Comparator<InterpreterPoint> {
        public int compare(InterpreterPoint p, InterpreterPoint q) {
            return java.lang.Double.compare(p.getX(), q.getX());
        }
    }

    // compare points according to their y-coordinate
    private static class YOrder implements Comparator<InterpreterPoint> {
        public int compare(InterpreterPoint p, InterpreterPoint q) {
            if (p.getY() < q.getY()) return -1;
            if (p.getY() > q.getY()) return +1;
            return 0;
        }
    }

    // compare points according to their polar radius
    private static class ROrder implements Comparator<InterpreterPoint> {
        public int compare(InterpreterPoint p, InterpreterPoint q) {
            double delta = (p.getX()*p.getX() + p.getY()*p.getY()) - (q.getX()*q.getX() + q.getY()*q.getY());
            if (delta < 0) return -1;
            if (delta > 0) return +1;
            return 0;
        }
    }

    // compare other points relative to atan2 angle (bewteen -pi/2 and pi/2) they make with this Point
    private class Atan2Order implements Comparator<InterpreterPoint> {
        public int compare(InterpreterPoint q1, InterpreterPoint q2) {
            double angle1 = getAngleTo(q1);
            double angle2 = getAngleTo(q2);
            if      (angle1 < angle2) return -1;
            else if (angle1 > angle2) return +1;
            else                      return  0;
        }
    }

    // compare other points relative to polar angle (between 0 and 2pi) they make with this Point
    private class PolarOrder implements Comparator<InterpreterPoint> {
        public int compare(InterpreterPoint q1, InterpreterPoint q2) {
            double dx1 = q1.getX() - x;
            double dy1 = q1.getY() - y;
            double dx2 = q2.getX() - x;
            double dy2 = q2.getY() - y;

            if      (dy1 >= 0 && dy2 < 0) return -1;    // q1 above; q2 below
            else if (dy2 >= 0 && dy1 < 0) return +1;    // q1 below; q2 above
            else if (dy1 == 0 && dy2 == 0) {            // 3-collinear and horizontal
                if      (dx1 >= 0 && dx2 < 0) return -1;
                else if (dx2 >= 0 && dx1 < 0) return +1;
                else                          return  0;
            }
            else return -ccw(InterpreterPoint.this, q1, q2);     // both above or below

            // Note: ccw() recomputes dx1, dy1, dx2, and dy2
        }
    }

    // compare points according to their distance to this point
    private class DistanceToOrder implements Comparator<InterpreterPoint> {
        public int compare(InterpreterPoint p, InterpreterPoint q) {
            double dist1 = distanceSquaredTo(p);
            double dist2 = distanceSquaredTo(q);
            if      (dist1 < dist2) return -1;
            else if (dist1 > dist2) return +1;
            else                    return  0;
        }
    }

    /**
     * Compares this point to the specified point.
     *
     * @param  other the other point
     * @return {@code true} if this point equals {@code other};
     *         {@code false} otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != this.getClass()) return false;
        InterpreterPoint that = (InterpreterPoint) other;
        return this.x == that.getX() && this.y == that.getY();
    }

    /**
     * Return a string representation of this point.
     * @return a string representation of this point in the format (x, y)
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Returns an integer hash code for this point.
     * @return an integer hash code for this point
     */
    @Override
    public int hashCode() {
        int hashX = ((java.lang.Double) x).hashCode();
        int hashY = ((java.lang.Double) y).hashCode();
        return 31*hashX + hashY;
    }
}
