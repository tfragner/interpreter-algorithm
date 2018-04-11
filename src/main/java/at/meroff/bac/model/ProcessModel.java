package at.meroff.bac.model;

import javafx.util.Pair;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ProcessModel {

    private Set<Subject> subjects = new HashSet<Subject>();
    private Set<Task> tasks = new HashSet<Task>();
    private Set<Exchange> exchanges = new HashSet<Exchange>();
    private Layout layout;

    public ProcessModel(Set<Subject> subjects, Set<Task> tasks, Set<Exchange> exchanges) {
        if (Objects.nonNull(subjects)) this.subjects = subjects;
        if (Objects.nonNull(tasks)) this.tasks = tasks;
        if (Objects.nonNull(exchanges)) this.exchanges = exchanges;

        calculateRelations();

    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void addSubject(Subject subject) {
        this.subjects.add(subject);
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public Set<Exchange> getExchanges() {
        return exchanges;
    }

    public void addExchange(Exchange exchange) {
        this.exchanges.add(exchange);
    }

    public void calculateRelations() {

        // Create the star layout for the given cards
        StarLayout starLayout = new StarLayout(subjects, tasks, exchanges);

        // Set the Layout according to the previous result
        if (starLayout.checkLayoutPossibility()) {
            this.layout = starLayout;
        } else {
            this.layout = new LineLayout(subjects, tasks, exchanges);
        }

        this.layout.assignSubjectsAndTasks();
    }

    public Layout getLayout() {
        return layout;
    }
}
