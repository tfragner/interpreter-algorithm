package at.meroff.bac;

import at.meroff.bac.model.*;
import at.meroff.bac.model.enumeration.CardType;

import java.util.HashSet;
import java.util.Set;

public class App {


    public static void main(String[] args) {
        Set<Subject> subjects = new HashSet<>();
        Set<Task> tasks = new HashSet<>();
        Set<Exchange> exchanges = new HashSet<>();

        subjects.add(new Subject(50,"Subject 50", 719, 580, 730, 387, 1081, 408, 1070, 600));
        subjects.add(new Subject(44,"Subject 44",1575, 583, 1575, 399, 1898, 399, 1898, 583));
        subjects.add(new Subject(41,"Subject 41",2419, 565, 2394, 365, 2729, 324, 2753, 525));

        tasks.add(new Task(18,"Task 18", 647, 942, 667, 734, 1044, 771, 1024, 979));
        tasks.add(new Task(9,"Task 9", 605, 1266, 627, 1044, 1038, 1085, 1016, 1306));
        tasks.add(new Task(33,"Task 33", 387, 2165, 412, 1890, 865, 1930, 841, 2205));
        tasks.add(new Task(15,"Task 15", 1569, 1497, 1569, 1267, 1928, 1267, 1928, 1497));
        tasks.add(new Task(51,"Task 51", 1553, 1971, 1553, 1738, 1932, 1737, 1933, 1971));
        tasks.add(new Task(3,"Task 3", 2625, 1842, 2598, 1585, 3023, 1541, 3050, 1797));

        exchanges.add(new Exchange(28,"Exchange 28", 1111, 1345, 1111, 1122, 1487, 1122, 1487, 1345));
        exchanges.add(new Exchange(1,"Exchange 1", 989, 2077, 989, 1812, 1394, 1812, 1394, 2077));
        exchanges.add(new Exchange(22,"Exchange 22", 2091, 1609, 2078, 1378, 2474, 1356, 2487, 1587));
        exchanges.add(new Exchange(16,"Exchange 16", 2102, 1998, 2084, 1735, 2507, 1706, 2525, 1968));
        ProcessModel processModel = new ProcessModel(subjects, tasks, exchanges);

        System.out.println(processModel.getLayout().getRelations());


        Layout layout = processModel.getLayout();
        System.out.println(layout);
    }


}
