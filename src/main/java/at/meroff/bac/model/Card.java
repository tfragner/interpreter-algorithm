package at.meroff.bac.model;

import at.meroff.bac.helper.GrahamScan;
import at.meroff.bac.model.enumeration.CardType;
import com.sun.javafx.geom.Vec2d;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Objects;

/**
 * This class represents cards for aprocesses model.
 */
public abstract class Card {

    private static final double EQUITY_TOLERANCE = 0.000000001d;

    /**
     * unique card id
     */
    private int cardId;

    /**
     * type of card
     */
    private CardType cardType;

    /**
     * Description for the card (eg. subject name)
     */
    private String description;

    /**
     * Shape of the card as a 2D polygon
     */
    private ConvexPolygon2D hull;

    /**
     * Constructor for a card
     * @param cardId unique card id
     * @param cardType type of card
     * @param description description for the card
     * @param points points defining the card (4 points for a rectangle)
     */
    public Card(int cardId, CardType cardType, String description, InterpreterPoint[] points) {
        this.cardId = cardId;
        if (Objects.nonNull(cardType)) {
            this.cardType = cardType;
        } else {
            throw new IllegalArgumentException("A card type is mandatory");
        }

        if (Objects.nonNull(description)) {
            this.description = description;
        } else {
            this.description = "";
        }

        if (points != null && points.length == 4) {
            createHull(points);
        } else {
            throw new IllegalArgumentException("4 points are necessary");
        }
    }

    /**
     * Constructor for a card
     * @param cardId unique card id
     * @param cardType type of card
     * @param description description for the card
     * @param x1 point 1 - x coordinate
     * @param y1 point 1 - y coordinate
     * @param x2 point 2 - x coordinate
     * @param y2 point 2 - y coordinate
     * @param x3 point 3 - x coordinate
     * @param y3 point 3 - y coordinate
     * @param x4 point 4 - x coordinate
     * @param y4 point 4 - y coordinate
     */
    public Card(int cardId, CardType cardType, String description, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        this(cardId, cardType,description,
                new InterpreterPoint[]{new InterpreterPoint(x1,y1),
                        new InterpreterPoint(x2,y2),
                        new InterpreterPoint(x3,y3),
                        new InterpreterPoint(x4,y4)});
    }

    /**
     * Constructor for a card
     * @param cardId unique card id
     * @param cardType type of card
     * @param x1 point 1 - x coordinate
     * @param y1 point 1 - y coordinate
     * @param x2 point 2 - x coordinate
     * @param y2 point 2 - y coordinate
     * @param x3 point 3 - x coordinate
     * @param y3 point 3 - y coordinate
     * @param x4 point 4 - x coordinate
     * @param y4 point 4 - y coordinate
     */
    public Card(int cardId, CardType cardType, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        this(cardId, cardType, "", x1, y1, x2, y2, x3, y3, x4, y4);
    }

    private void createHull(InterpreterPoint[] points) {
        InterpreterPoint[] sortedPoints = new InterpreterPoint[points.length];

        GrahamScan scan = new GrahamScan(points);

        int i = 0;

        for (InterpreterPoint interpreterPoint : scan.hull()) {
            sortedPoints[i] = interpreterPoint;
            i++;
        }

        hull = new ConvexPolygon2D(sortedPoints);
    }

    public Integer getCardId() {
        return cardId;
    }

    public CardType getCardType() {
        return cardType;
    }

    public String getDescription() {
        return description;
    }

    public ConvexPolygon2D getHull() {
        return hull;
    }

    /**
     * Calculates the Distance between two cards (measured center to center)
     * @param source source card for calculation
     * @param target target card for calculation
     * @return distance between the cards
     */
    public static double getDistance(Card source, Card target) {
        return Math.sqrt(
                Math.pow(target.getCenter().x - source.getCenter().x,2) +
                        Math.pow(target.getCenter().y - source.getCenter().y,2)
        );
    }

    /**
     * Method returns the center of the card
     * @return center point
     */
    public InterpreterPoint getCenter() {

        Line2D line2D1 = new Line2D.Double(hull.Corners[0], hull.Corners[2]);
        Line2D line2D2 = new Line2D.Double(hull.Corners[1], hull.Corners[3]);

        return new InterpreterPoint(GetIntersectionPoint(line2D1, line2D2));

    }

    /**
     * Returns the vector between two cards
     * @param source source card for calculation
     * @param target target card for calculation
     * @return vector between two cards
     */
    public static Vec2d getVector(Card source, Card target) {
        return new Vec2d(target.getCenter().x - source.getCenter().x, target.getCenter().y - source.getCenter().y);
    }

    /**
     * Calculate the max cosine similarity based on two cards. The dimension of the first card is used as a reference dimension.
     * @param subject subject as base for calculation
     * @param sourceTask task to determine the cosine similarity
     * @return max cosine similarity
     */
    public static double getMaxCosineSimilarity(Card subject, Card sourceTask) {

        double x = Card.getBiggestLength(subject) / 2;
        double y = Card.getDistance(subject, sourceTask);
        double alpha = Math.asin(x/y);
        return Math.cos(alpha);

    }

    private static double getBiggestLength(Card card) {

        double maxLength = 0.0;

        if (card.hull.Corners[0].distance(card.hull.Corners[1]) > maxLength) maxLength = card.hull.Corners[0].distance(card.hull.Corners[1]);
        if (card.hull.Corners[1].distance(card.hull.Corners[2]) > maxLength) maxLength = card.hull.Corners[1].distance(card.hull.Corners[2]);
        if (card.hull.Corners[2].distance(card.hull.Corners[3]) > maxLength) maxLength = card.hull.Corners[2].distance(card.hull.Corners[3]);
        if (card.hull.Corners[3].distance(card.hull.Corners[0]) > maxLength) maxLength = card.hull.Corners[3].distance(card.hull.Corners[0]);

        return maxLength;
    }

    public static Point2D GetIntersectionPoint(Line2D line1, Line2D line2) {
        return GetIntersectionPoint(line1.getP1(), line1.getP2(), line2.getP1(), line2.getP2());
    }

    public static Point2D GetIntersectionPoint(Point2D l1p1, Point2D l1p2, Point2D l2p1, Point2D l2p2)
    {
        double A1 = l1p2.getY() - l1p1.getY();
        double B1 = l1p1.getX() - l1p2.getX();
        double C1 = A1 * l1p1.getX() + B1 * l1p1.getY();

        double A2 = l2p2.getY() - l2p1.getY();
        double B2 = l2p1.getX() - l2p2.getX();
        double C2 = A2 * l2p1.getX() + B2 * l2p1.getY();

        //lines are parallel
        double det = A1 * B2 - A2 * B1;
        if (IsEqual(det, 0d))
        {
            return null; //parallel lines
        }
        else
        {
            double x = (B2 * C1 - B1 * C2) / det;
            double y = (A1 * C2 - A2 * C1) / det;
            boolean online1 = ((Math.min(l1p1.getX(), l1p2.getX()) < x || IsEqual(Math.min(l1p1.getX(), l1p2.getX()), x))
                    && (Math.max(l1p1.getX(), l1p2.getX()) > x || IsEqual(Math.max(l1p1.getX(), l1p2.getX()), x))
                    && (Math.min(l1p1.getY(), l1p2.getY()) < y || IsEqual(Math.min(l1p1.getY(), l1p2.getY()), y))
                    && (Math.max(l1p1.getY(), l1p2.getY()) > y || IsEqual(Math.max(l1p1.getY(), l1p2.getY()), y))
            );
            boolean online2 = ((Math.min(l2p1.getX(), l2p2.getX()) < x || IsEqual(Math.min(l2p1.getX(), l2p2.getX()), x))
                    && (Math.max(l2p1.getX(), l2p2.getX()) > x || IsEqual(Math.max(l2p1.getX(), l2p2.getX()), x))
                    && (Math.min(l2p1.getY(), l2p2.getY()) < y || IsEqual(Math.min(l2p1.getY(), l2p2.getY()), y))
                    && (Math.max(l2p1.getY(), l2p2.getY()) > y || IsEqual(Math.max(l2p1.getY(), l2p2.getY()), y))
            );

            if (online1 && online2)
                return new Point2D.Double(x, y);
        }
        return null; //intersection is at out of at least one segment.
    }

    private static boolean IsEqual(double d1, double d2)
    {
        return Math.abs(d1-d2) <= EQUITY_TOLERANCE;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardId=" + cardId +
                ", cardType=" + cardType +
                ", description='" + description + '\'' +
                ", hull=" + hull +
                '}';
    }
}
