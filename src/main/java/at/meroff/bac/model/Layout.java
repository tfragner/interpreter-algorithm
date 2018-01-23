package at.meroff.bac.model;

import at.meroff.bac.model.enumeration.LayoutType;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Layout {

    LayoutType layoutType;
    Set<Subject> subjects;
    Set<Task> tasks;
    Set<Exchange> exchanges;
    protected Set<Pair<Subject, List<Task>>> relations;

    public Layout(Set<Subject> subjects, Set<Task> tasks, Set<Exchange> exchanges) {
        if (Objects.isNull(subjects) || subjects.size() == 0) throw new IllegalArgumentException("missing subjects");
        if (Objects.isNull(tasks) || tasks.size() == 0) throw new IllegalArgumentException("missing tasks");

        this.subjects = subjects;
        this.tasks = tasks;
        this.relations =  new HashSet<>();


    }

    protected Set<Pair<Task, Subject>> getNearestTaskSubjectCombinations() {
        return this.tasks.stream()
                .map(task -> new Pair<>(task, getNearestSubject(task)))
                .collect(Collectors.toSet());
    }

    protected Subject getNearestSubject(Task task) {
        return this.subjects.stream()
                .map(subject -> new Pair<>(subject, Card.getDistance(task, subject)))
                .min(Comparator.comparingDouble(Pair::getValue))
                .map(Pair::getKey)
                .orElseThrow(() -> new IllegalStateException("Something went wrong"));
    }

    protected Task getNearestTask(Subject subject) {
        return this.tasks.stream()
                .map(task -> new Pair<>(task, Card.getDistance(subject,task)))
                .min(Comparator.comparingDouble(Pair::getValue))
                .map(Pair::getKey)
                .orElseThrow(() -> new IllegalStateException("Something went wrong"));
    }

    public abstract Set<Pair<Subject, List<Task>>> calculateRelations();

    public String getRelations() {

        StringBuilder ret = new StringBuilder();
        for (Pair<Subject, List<Task>> relation : relations) {
            ret.append(relation.getKey().getCardId());
            for (Task task : relation.getValue()) {
                ret.append(" --> ").append(task.getCardId());
            }
            ret.append("\n");
        }

        return ret.toString();
    }
}
