package at.meroff.bac.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static at.meroff.bac.model.Card.GetIntersectionPoint;

public class ConvexPolygon2D {

    public InterpreterPoint[] Corners;

    public ConvexPolygon2D(InterpreterPoint[] corners)
    {
        Corners = corners;
    }

    public static Point2D[] GetIntersectionPoints(Point2D l1p1, Point2D l1p2, ConvexPolygon2D poly)
    {
        List<Point2D> intersectionPoints = new ArrayList<>();
        for (int i = 0; i < poly.Corners.length; i++)
        {

            int next = (i + 1 == poly.Corners.length) ? 0 : i + 1;

            Point2D ip = GetIntersectionPoint(l1p1, l1p2, poly.Corners[i], poly.Corners[next]);

            if (ip != null) intersectionPoints.add(ip);

        }

        return intersectionPoints.toArray(new Point2D[0]);
    }

}
