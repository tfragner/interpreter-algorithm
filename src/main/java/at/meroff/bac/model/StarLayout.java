package at.meroff.bac.model;

import at.meroff.bac.helper.Statistics;
import at.meroff.bac.model.enumeration.LayoutType;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StarLayout extends Layout {

    public StarLayout(Set<Subject> subjects, Set<Task> tasks, Set<Exchange> exchanges) {
        super(subjects, tasks, exchanges);
        this.layoutType = LayoutType.STAR;
        calculateRelations();
    }

    @Override
    public Set<Pair<Subject, List<Task>>> calculateRelations() {
        Set<Pair<Task, Subject>> nearestTaskSubjectCombinations = this.getNearestTaskSubjectCombinations();

        relations = createRelations(nearestTaskSubjectCombinations);

        return relations;

    }

    public boolean checkLayoutPossibility() {
        double v = relations.stream()
                .filter(subjectSetPair -> subjectSetPair.getValue().size() > 1)
                .map(subjectSetPair -> {
                    double[] doubles = subjectSetPair.getValue().stream()
                            .mapToDouble(value -> Card.getDistance(subjectSetPair.getKey(), value)).toArray();
                    Statistics statistics = new Statistics(doubles);
                    return statistics.variationCoefficient();
                }).mapToDouble(value -> value)
                .average()
                .orElse(0.0);

        return v < 0.3;
    }

    private Set<Pair<Subject, List<Task>>> createRelations(Set<Pair<Task, Subject>> nearestTaskSubjectCombinations) {
        return subjects.stream()
                .map(subject -> {

                    List<Task> tasks = nearestTaskSubjectCombinations.stream()
                            .filter(taskSubjectPair -> taskSubjectPair.getValue().equals(subject))
                            .sorted(Comparator.comparingDouble(o -> Card.getDistance(o.getKey(), o.getValue())))
                            .map(Pair::getKey)
                            .collect(Collectors.toList());

                    return new Pair<>(subject, tasks);
                })
                .collect(Collectors.toSet());
    }
}
