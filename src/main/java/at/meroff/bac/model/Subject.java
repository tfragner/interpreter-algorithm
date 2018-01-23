package at.meroff.bac.model;

import at.meroff.bac.model.enumeration.CardType;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Subject extends Card {

    private List<Task> tasks = new LinkedList<>();

    public Subject(Integer cardId, String description, InterpreterPoint[] points) {
        super(cardId, CardType.SUBJECT, description, points);
    }

    public Subject(int cardId, String description, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        super(cardId, CardType.SUBJECT, description, x1, y1, x2, y2, x3, y3, x4, y4);
    }

    public Subject(int cardId, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        super(cardId, CardType.SUBJECT, x1, y1, x2, y2, x3, y3, x4, y4);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void addTasks(Collection<Task> tasks) {
        this.tasks.addAll(tasks);
    }

}
