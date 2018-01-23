package at.meroff.bac.model;

import at.meroff.bac.model.enumeration.CardType;

public class Task extends Card {

    private Subject subject;

    public Task(Integer cardId, String description, InterpreterPoint[] points) {
        super(cardId, CardType.TASK, description, points);
    }

    public Task(int cardId, String description, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        super(cardId, CardType.TASK, description, x1, y1, x2, y2, x3, y3, x4, y4);
    }

    public Task(int cardId, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        super(cardId, CardType.TASK, x1, y1, x2, y2, x3, y3, x4, y4);
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
