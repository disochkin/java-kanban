package controllers;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    private TaskManager TaskManagerTest;

    @BeforeEach
    void setupClass() {
        TaskManagerTest = Managers.getDefault();
    }


    @Test
    void TaskManagerInitTest() {
        assertNotNull(TaskManagerTest, "ошибка создания TaskManager");
    }

    @Test
    void addNewTaskTest() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = TaskManagerTest.addTask(task);

        final Task savedTask = TaskManagerTest.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = TaskManagerTest.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpicTest() {
        Epic epic = new Epic("Test addNewEpic1", "Test addNewEpic1 description");

        final int epicId = TaskManagerTest.addEpic(epic);

        final Epic savedEpic = TaskManagerTest.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = TaskManagerTest.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addNewSubTaskTest() {
        Epic epic = new Epic("Test addNewEpic1", "Test addNewEpic1 description");
        final int epicId = TaskManagerTest.addEpic(epic);

        SubTask subTask = new SubTask("Test addNewTask", "Test addNewTask description", NEW, epicId);
        final int subTaskId = TaskManagerTest.addSubTask(subTask);

        final SubTask savedTask = TaskManagerTest.getSubTaskById(subTaskId);

        assertNotNull(savedTask, "Подзадача не найдена.");
        assertEquals(subTask, savedTask, "Подзадачи не совпадают.");

        final List<Task> subTasks = TaskManagerTest.getSubTasks();

        assertNotNull(subTasks, "Подзадачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void updateTaskTest() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = TaskManagerTest.addTask(task);

        Task updatedTask = new Task("Test Updated", "Updated task description", NEW);
        TaskManagerTest.updateTask(taskId, updatedTask);

        assertEquals(updatedTask, TaskManagerTest.getTaskById(taskId), "Задачи не совпадают.");
    }

    @Test
    void updateEpicTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = TaskManagerTest.addEpic(epic);

        Epic updatedEpic = new Epic("Epic Updated", "Updated epic description");
        TaskManagerTest.updateEpic(epicId, updatedEpic);

        assertEquals(updatedEpic, TaskManagerTest.getEpicById(epicId), "Эпики не совпадают.");
    }

    @Test
    void updateSubTaskTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = TaskManagerTest.addEpic(epic);

        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewTask description", NEW, epicId);
        final int subTaskId = TaskManagerTest.addSubTask(subTask);

        SubTask updatedSubTask = new SubTask("Test updatedSubTask", "Test updatedSubTask description", NEW, epicId);

        TaskManagerTest.updateSubTask(subTaskId, updatedSubTask);

        assertEquals(updatedSubTask, TaskManagerTest.getSubTaskById(subTaskId), "Сабтаски не совпадают.");
    }

    @Test
    void deleteTaskTest() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = TaskManagerTest.addTask(task);

        TaskManagerTest.deleteTaskById(taskId);

        assertNull(TaskManagerTest.getTaskById(taskId), "Задача не удалена.");
    }
    @Test
    void deleteTasksTest() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description", NEW);
        Task task2 = new Task("Test addNewTask2", "Test2 addNewTask2 description", NEW);
        TaskManagerTest.deleteTasks();

        assertTrue(TaskManagerTest.getTasks().isEmpty(), "Задачи не удалены.");
    }


    @Test
    void deleteEpicTest() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = TaskManagerTest.addEpic(epic);

        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewTask description", NEW, epicId);
        final int subTaskId = TaskManagerTest.addSubTask(subTask);
        TaskManagerTest.deleteEpicById(epicId);
        assertNull(TaskManagerTest.getEpicById(epicId), "Эпик не удален.");
        assertNull(TaskManagerTest.getSubTaskById(subTaskId), "Сабтаск не удален.");
    }

    @Test
    void deleteEpicsTest() {
        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic1 description");
        final int epicId1 = TaskManagerTest.addEpic(epic1);
        SubTask subTask1 = new SubTask("Test addNewSubTask1", "Test addNewTask1 description", NEW, epicId1);
        final int subTaskId1 = TaskManagerTest.addSubTask(subTask1);

        Epic epic2 = new Epic("Test addNewEpic2", "Test addNewEpic2 description");
        final int epicId2 = TaskManagerTest.addEpic(epic2);
        SubTask subTask2 = new SubTask("Test addNewSubTask", "Test addNewTask2 description", NEW, epicId2);
        final int subTaskId2 = TaskManagerTest.addSubTask(subTask2);

        TaskManagerTest.deleteEpics();
        assertTrue(TaskManagerTest.getEpics().isEmpty(), "Эпик не удален.");
        assertTrue(TaskManagerTest.getSubTasks().isEmpty(), "Сабтаски не удалены.");
    }

    @Test
    void deleteSubTaskById() {
        List<Task> requestedTask = new ArrayList<>();

        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic1 description");
        final int epicId1 = TaskManagerTest.addEpic(epic1);
        SubTask subTask1 = new SubTask("Test addNewSubTask1", "Test addNewTask1 description", NEW, epicId1);
        final int subTaskId1 = TaskManagerTest.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Test addNewSubTask", "Test addNewTask2 description", NEW, epicId1);
        final int subTaskId2 = TaskManagerTest.addSubTask(subTask2);

        requestedTask.add(subTask1);
        TaskManagerTest.deleteSubTaskById(subTaskId2);

        assertEquals(TaskManagerTest.getSubTasksFromEpic(epicId1), requestedTask,
                "Сабтаски из эпика не получены");
    }

    @Test
    void getSubtasksFromEpicTest() {
        List<Task> requestedTask = new ArrayList<>();

        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic1 description");
        final int epicId1 = TaskManagerTest.addEpic(epic1);
        SubTask subTask1 = new SubTask("Test addNewSubTask1", "Test addNewTask1 description", NEW, epicId1);
        final int subTaskId1 = TaskManagerTest.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Test addNewSubTask", "Test addNewTask2 description", NEW, epicId1);
        final int subTaskId2 = TaskManagerTest.addSubTask(subTask2);

        requestedTask.add(subTask1);
        requestedTask.add(subTask2);

        assertEquals(TaskManagerTest.getSubTasksFromEpic(epicId1), requestedTask,
                "Сабтаски из эпика не получены");
    }



}