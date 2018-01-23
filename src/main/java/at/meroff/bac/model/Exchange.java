package at.meroff.bac.model;

import at.meroff.bac.model.enumeration.CardType;

public class Exchange extends Card {

    public Exchange(Integer cardId, String description, InterpreterPoint[] points) {
        super(cardId, CardType.EXCHANGE, description, points);
    }

    public Exchange(int cardId, String description, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        super(cardId, CardType.EXCHANGE, description, x1, y1, x2, y2, x3, y3, x4, y4);
    }

    public Exchange(int cardId, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        super(cardId, CardType.EXCHANGE, x1, y1, x2, y2, x3, y3, x4, y4);
    }

}
