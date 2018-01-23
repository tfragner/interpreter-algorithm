package at.meroff.bac.model;

import at.meroff.bac.model.enumeration.CardType;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class SubjectTest {

    Subject subject;
    Subject subjectNoRectangle;

    @Before
    public void setUp() throws Exception {
        subject = new Subject(1, "Subject 1", 1,1,1,2,2,2,2,1);
        subjectNoRectangle = new Subject(2,"Subject 2",1,1,3,1,2,2,4,2);
    }

    @Test
    public void createSubjects() {
        Subject subject1 = new Subject(1, "Subject 1", 1, 1, 1, 2, 2, 2, 2, 1);
        Subject subject2 = new Subject(1, 1, 1, 1, 2, 2, 2, 2, 1);
        Subject subject3= new Subject(1,"test", new InterpreterPoint[]{new InterpreterPoint(1,1),new InterpreterPoint(2,1),new InterpreterPoint(1,3),new InterpreterPoint(2,2)});
    }

    @Test
    public void getTasks() {
        Task task1 = new Task(2,1,1,1,2,2,1,2,2);
        Task task2 = new Task(2,1,1,1,2,2,1,2,2);
        subject.addTask(task1);
        subject.addTask(task2);

        assertTrue(subject.getTasks().size() == 2);
    }

    @Test
    public void setTasks() {
        Task task1 = new Task(2,1,1,1,2,2,1,2,2);
        Task task2 = new Task(2,1,1,1,2,2,1,2,2);
        List<Task> collection = new LinkedList<>();
        collection.add(task1);
        collection.add(task2);
        subject.setTasks(collection);

        assertTrue(subject.getTasks().size() == 2);
    }

    @Test
    public void addTask() {
        Task task1 = new Task(2,1,1,1,2,2,1,2,2);
        subject.addTask(task1);

        assertTrue(subject.getTasks().size() == 1);
    }

    @Test
    public void getCardId() {
        assertTrue(subject.getCardId() == 1);
    }

    @Test
    public void getCardType() {
        assertTrue(subject.getCardType().equals(CardType.SUBJECT));
    }

    @Test
    public void getDescription() {
        assertTrue(subject.getDescription().equals("Subject 1"));
    }

    @Test
    public void getHull() {
        ConvexPolygon2D hull = subject.getHull();

        InterpreterPoint[] check = new InterpreterPoint[]{new InterpreterPoint(1,1), new InterpreterPoint(2,1), new InterpreterPoint(2,2), new InterpreterPoint(1,2)};

        InterpreterPoint[] corners = hull.Corners;

        assertArrayEquals(check, corners);
    }

    @Test
    public void getCenter() {
        assertTrue(subject.getCenter().equals(new InterpreterPoint(1.5,1.5)));
        assertTrue(subjectNoRectangle.getCenter().equals(new InterpreterPoint(2.5,1.5)));
    }

    @Test
    public void getIntersectionPoint() {
    }

    @Test
    public void getIntersectionPoint1() {
    }
}