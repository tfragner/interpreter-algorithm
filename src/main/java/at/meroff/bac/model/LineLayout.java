package at.meroff.bac.model;

import at.meroff.bac.helper.Cosine;
import at.meroff.bac.model.enumeration.LayoutType;
import com.sun.javafx.geom.Vec2d;
import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LineLayout extends Layout {

    private HashSet<Subject> unusedSubjects = new HashSet<>();
    private HashSet<Task> unusedTasks = new HashSet<>();

    public LineLayout(Set<Subject> subjects, Set<Task> tasks, Set<Exchange> exchanges) {
        super(subjects, tasks, exchanges);
        this.unusedSubjects.addAll(subjects);
        this.unusedTasks.addAll(tasks);
        this.layoutType = LayoutType.LINE;
        calculateRelations();
    }

    @Override
    public Set<Pair<Subject, List<Task>>> calculateRelations() {

        initialAssignment();

        reassignTasksToEmptySubjects();

        boolean cont;

        do { cont = refineAssinements();} while (cont);

        return relations;

    }

    private boolean refineAssinements() {

        AtomicBoolean ret = new AtomicBoolean(false);

        subjects.forEach(subject -> {
                    List<Task> task = relations.stream()
                            .filter(subjectListPair -> subjectListPair.getKey().equals(subject))
                            .flatMap(subjectListPair -> subjectListPair.getValue().stream())
                            .collect(Collectors.toList());
                    Pair<Subject, Task> closestSubjectTask = new Pair<>(subject, task.get(task.size()-1));

                    Optional<Task> followUp = getFollowUp(closestSubjectTask, false);

                    if (followUp.isPresent()) {
                        Task contested = followUp.get();
                        Subject currentSubject = relations
                                .stream()
                                .filter(subjectListPair -> subjectListPair.getValue().contains(contested))
                                .map(Pair::getKey)
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("Kein Subjekt gefunden"));

                        // current number of assigned tasks
                        List<Task> currentSubjectTasks = relations.stream()
                                .filter(subjectListPair -> subjectListPair.getKey().equals(currentSubject))
                                .flatMap(o -> o.getValue().stream())
                                .collect(Collectors.toList());

                        if (currentSubjectTasks.size() != 1) {
                            if (currentSubjectTasks.indexOf(contested) == currentSubjectTasks.size()-1) {
                                double distCurrent = Card.getDistance(currentSubject, contested);
                                double distContender = Card.getDistance(subject, contested);

                                if (distCurrent >= distContender) {
                                    this.relations.stream()
                                            .filter(subjectListPair -> subjectListPair.getValue().contains(contested))
                                            .forEach(subjectListPair -> subjectListPair.getValue().remove(contested));

                                    addRelation(new Pair<>(subject, contested));
                                    ret.set(true);
                                }
                            }
                        }


                    }

                });

        return ret.get();

    }

    private void initialAssignment() {
        while (!unusedSubjects.isEmpty() && !unusedTasks.isEmpty()) {
            Pair<Subject, Task> closestSubjectTask = getClosestSubjectTaskPair(true);

            while (closestSubjectTask != null) {
                // follow-up tasks are all task not used
                // in the next step this tasks are getting filtered
                Optional<Task> follow = getFollowUp(closestSubjectTask, true);

                // add relation
                addRelation(closestSubjectTask);

                // remove current Task from unused list
                unusedTasks.remove(closestSubjectTask.getValue());


                if (follow.isPresent()) {
                    closestSubjectTask = new Pair<>(closestSubjectTask.getKey(), follow.get());
                } else {
                    unusedSubjects.remove(closestSubjectTask.getKey());
                    //unusedTasks.remove(follow);
                    closestSubjectTask = null;
                }
            }
        }
    }

    private void reassignTasksToEmptySubjects() {
        for (Subject unusedSubject : unusedSubjects) {
            Task nearestTask = getNearestTask(unusedSubject);

            this.relations.stream()
                    .filter(subjectListPair -> subjectListPair.getValue().contains(nearestTask))
                    .forEach(subjectListPair -> subjectListPair.getValue().remove(nearestTask));

            addRelation(new Pair<>(unusedSubject, nearestTask));
        }
    }

    private void addRelation(Pair<Subject, Task> closestSubjectTask) {
        long count = relations.stream()
                .filter(subjectSetPair -> subjectSetPair.getKey().equals(closestSubjectTask.getKey()))
                .peek(subjectSetPair -> subjectSetPair.getValue().add(closestSubjectTask.getValue()))
                .count();

        if (count == 0) {
            List<Task> addTask = new LinkedList<>();
            addTask.add(closestSubjectTask.getValue());
            relations.add(new Pair<>(closestSubjectTask.getKey(), addTask));
        }
    }

    private Optional<Task> getFollowUp(Pair<Subject, Task> finalClosestSubjectTask, boolean onlyUnused) {

        Stream<Task> pairStream = tasks.stream();

        if(onlyUnused) {
            pairStream = pairStream.filter(task -> unusedTasks.contains(task));
        }

        return pairStream
                            .filter(task -> !task.equals(finalClosestSubjectTask.getValue()))
                            .filter(task -> checkSimilarity(finalClosestSubjectTask, task))
                            .filter(task -> checkDistance(finalClosestSubjectTask, task))
                            .filter(task -> checkSideSimilarity(finalClosestSubjectTask, task))
                            .filter(task -> filterSubjectIntersection(finalClosestSubjectTask, task))
                            .min(Comparator.comparingDouble(task -> Card.getDistance(finalClosestSubjectTask.getKey(), task)));
    }

    //TODO hier weitermachen!
    private boolean filterSubjectIntersection(Pair<Subject, Task> closestSubjectTask, Task task) {

        Vec2d vector = Card.getVector(closestSubjectTask.getKey(), task);

        long count = subjects.stream()
                .filter(subject -> !subject.equals(closestSubjectTask.getKey()))
                .filter(subject -> checkIntersection(vector, subject))
                .count();

        return count != 0;

    }

    private boolean checkIntersection(Vec2d vector, Subject subject) {

        Point2D.Double p1 = new Point2D.Double(subject.getCenter().x, subject.getCenter().y);
        Point2D.Double p2 = new Point2D.Double(vector.x, vector.y);
        return ConvexPolygon2D.GetIntersectionPoints(p1 , p2, subject.getHull()).length > 0;
    }

    private boolean checkSideSimilarity(Pair<Subject, Task> closestSubjectTask, Task task) {
        Vec2d vSubjectSource = Card.getVector(closestSubjectTask.getKey(), closestSubjectTask.getValue());
        Vec2d vSourceTarget = Card.getVector(closestSubjectTask.getValue(), task);
        return Cosine.similarity(vSubjectSource, vSourceTarget) > 0.5;
    }

    private boolean checkDistance(Pair<Subject, Task> closestSubjectTask, Task task) {
        return Card.getDistance(closestSubjectTask.getKey(), task) - Card.getDistance(closestSubjectTask.getKey(), closestSubjectTask.getValue()) > 0;
    }

    private boolean checkSimilarity(Pair<Subject, Task> closestSubjectTask, Task task) {
        Vec2d vSubjectSource = Card.getVector(closestSubjectTask.getKey(), closestSubjectTask.getValue());
        Vec2d vSubjectTarget = Card.getVector(closestSubjectTask.getKey(), task);
        double similarity = Cosine.similarity(vSubjectSource, vSubjectTarget);
        double maxSimilarity = Card.getMaxCosineSimilarity(closestSubjectTask.getKey(), closestSubjectTask.getValue());

        return maxSimilarity < similarity;
    }

    private Pair<Subject, Task> getClosestSubjectTaskPair(boolean onlyUnused) {

        Stream<Pair<Task, Subject>> pairStream = getNearestTaskSubjectCombinations().stream();

        if(onlyUnused) {
            pairStream = pairStream.filter(taskSubjectPair -> unusedSubjects.contains(taskSubjectPair.getValue()) && unusedTasks.contains(taskSubjectPair.getKey()));
        }

        return pairStream
                    .filter(taskSubjectPair -> unusedSubjects.contains(taskSubjectPair.getValue()) && unusedTasks.contains(taskSubjectPair.getKey()))
                    .map(taskSubjectPair -> new Pair<>(taskSubjectPair, Card.getDistance(taskSubjectPair.getValue(), taskSubjectPair.getKey())))
                    .min(Comparator.comparingDouble(Pair::getValue))
                    .map(Pair::getKey)
                    .map(taskSubjectPair -> new Pair<>(taskSubjectPair.getValue(), taskSubjectPair.getKey()))
                    .orElseThrow(() -> new IllegalStateException("couldn't find minmal Subject/Task combination"));
    }

}
