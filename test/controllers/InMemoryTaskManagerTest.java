package controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class InMemoryTaskManagerTest extends TaskManagerTest{
    @BeforeEach
    public void setUp() throws IOException {
        taskManagerTest = new InMemoryTaskManager();
        initTasks();
    }

    void initTasks() throws IOException {
        super.initTestData();
    }

    @Test
    @Override
    void addNewTaskTest() throws IOException {
        super.addNewTaskTest();
    }

    @Test
    @Override
    void addNewEpicTest() throws IOException {
        super.addNewEpicTest();
    }

    @Test
    @Override
    void addNewSubTaskTest() throws IOException {
        super.addNewTaskTest();
    }

    @Test
    @Override
    void updateTaskTest() throws IOException {
        super.addNewTaskTest();
    }

    @Test
    @Override
    void updateEpicTest() throws IOException {
        super.addNewTaskTest();
    }

    @Test
    @Override
    void updateSubTaskTest() throws IOException {
        super.addNewTaskTest();
    }

    @Test
    @Override
    void deleteTaskTest()throws IOException {
        super.deleteTaskTest();
    }
    @Test
    @Override
    void deleteTasksTest()throws IOException {
        super.deleteTaskTest();
    }


    @Test
    @Override
    void deleteEpicTest()throws IOException {
        super.deleteEpicTest();
    }

    @Test
    @Override
    void deleteEpicsTest()throws IOException {
        super.deleteEpicsTest();
    }


    @Test
    @Override
    void deleteSubTaskById()throws IOException {
        super.deleteSubTaskById();
    }

    @Test
    @Override
    void getSubtasksFromEpicTest()throws IOException {
        super.getSubtasksFromEpicTest();
    }

}
