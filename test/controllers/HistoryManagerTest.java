package controllers;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryManagerTest {
    TaskManager TaskManagerTest;
    ArrayList<Task> requestedTask;
    int taskId, epicId, subTaskId1, subTaskId2;
    Task task;
    Epic epic1;
    SubTask subTask1, subTask2;

    @BeforeEach
    void setupClass() {
        TaskManagerTest = Managers.getDefault();
        requestedTask = new ArrayList<>();

        task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        taskId = TaskManagerTest.addTask(task);

        epic1 = new Epic("Test NewEpic1", "Test NewEpic1 description");
        epicId = TaskManagerTest.addEpic(epic1);

        subTask1 = new SubTask("Test addNewSubTask1", "Test addNewTask description1", NEW, epicId);
        subTaskId1 = TaskManagerTest.addSubTask(subTask1);

        subTask2 = new SubTask("Test addNewSubTask2", "Test addNewTask description2", NEW, epicId);
        subTaskId2 = TaskManagerTest.addSubTask(subTask2);
    }

    @Test
    void getHistoryTest() {
        TaskManagerTest.getTaskById(taskId);
        requestedTask.add(task);
        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Задачи не добавляются.");
        TaskManagerTest.getEpicById(epicId);
        requestedTask.add(epic1);
        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Эпики не добавляются.");
        TaskManagerTest.getSubTaskById(subTaskId1);
        requestedTask.add(subTask1);

        TaskManagerTest.getSubTaskById(subTaskId2);
        requestedTask.add(subTask2);

        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Сабтаски не добавляются");
    }

    @Test
    void historyCountTest() {
        for (int i = 0; i < 15; i++) {
            TaskManagerTest.getEpicById(epicId);
            requestedTask.add(epic1);
        }
        assertEquals(requestedTask.subList(0, 10).size(), TaskManagerTest.getHistory().size(), "История просмотра задач работает некорректно." +
                "Длина списка отличается.");
    }

    @Test
    void checkOnlyExistTaskCanToAppended() {
        TaskManagerTest.getEpicById(-100);
        TaskManagerTest.getEpicById(-100);
        TaskManagerTest.getSubTaskById(-100);
        assertEquals(0, TaskManagerTest.getHistory().size(), "История просмотра задач работает некорректно." +
                "Добавлен несуществующий (null) объект");
    }
}