package controllers;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static model.Status.DONE;
import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Добавление тасок в TaskManager")
abstract class TaskManagerTest<T extends TaskManager> {
    protected Task testTaskWithStartTime1;
    protected SubTask testSubtaskWithStartTime1;
    protected Epic testEpic1;
    protected T taskManagerTest;
    int testTaskIdWithStartTime1;
    int testSubTaskIdWithStartTime1;
    int testEpicId1;
    LocalDateTime startTime = LocalDateTime.parse("2025-05-24T16:06:14.648865600");

    void initTestData() throws IOException {
        testTaskWithStartTime1 = new Task("Task1 test name", "Task1 test description", NEW, 5);
        testTaskWithStartTime1.setStartTime(startTime);
        testTaskIdWithStartTime1 = taskManagerTest.addTask(testTaskWithStartTime1);

        testEpic1 = new Epic("Test epic1 name", "Test epic1 description");
        testEpicId1 = taskManagerTest.addEpic(testEpic1);

        testSubtaskWithStartTime1 = new SubTask("Test subtask1 name", "Test subtask1 description", NEW, testEpicId1, 5);
        testSubtaskWithStartTime1.setStartTime(startTime.plusMinutes(30));
        testSubTaskIdWithStartTime1 = taskManagerTest.addSubTask(testSubtaskWithStartTime1);
    }

    @Test
    void TaskManagerInitTest() {
        assertNotNull(taskManagerTest, "ошибка создания TaskManager");
    }

    @Test
    void addNewTaskTest() throws IOException {
        Task additionalTask = new Task("AdditionalTask name", "AdditionalTask description", NEW, 10);
        final int taskId = taskManagerTest.addTask(additionalTask);
        final Task savedTask = taskManagerTest.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(additionalTask, savedTask, "Задачи не совпадают.");

        final List<Task> storedTasks = taskManagerTest.getTasks();
        assertNotNull(storedTasks, "Задачи не возвращаются.");
        assertEquals(2, storedTasks.size(), "Неверное количество задач.");
        assertEquals(Arrays.asList(testTaskWithStartTime1, additionalTask), storedTasks, "Задачи не совпадают.");

        final IOException exception = Assertions.assertThrows(IOException.class, () -> {
            additionalTask.setStartTime(taskManagerTest.getSubTasks().get(0).getStartTime().plusMinutes(1));
            taskManagerTest.addTask(additionalTask);
            System.out.println();
        });
        Assertions.assertEquals("Пересечение по времени выполнения c задачей - Test subtask1 name",
                exception.getMessage(), "сообщение об исключении не совпало");
    }

    @Test
    void addNewEpicTest() throws IOException {
        Epic additionalEpic = new Epic(4, "AdditionalEpic name", "AdditionalEpic description", NEW, 40);
        final int epicId = taskManagerTest.addEpic(additionalEpic);
        final Epic savedEpic = taskManagerTest.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(additionalEpic, savedEpic, "Эпики не совпадают.");

        final List<Epic> storedEpics = taskManagerTest.getEpics();
        assertNotNull(storedEpics, "Эпики не возвращаются.");
        assertEquals(2, storedEpics.size(), "Неверное количество эпиков.");
        assertEquals(Arrays.asList(testEpic1, additionalEpic), storedEpics, "Эпики не совпадают.");
    }

    @Test
    void addNewSubTaskTest() throws IOException {
        SubTask additionalSubTask = new SubTask("Test addNewTask", "Test addNewTask description", NEW, 2, 30);
        final int subTaskId = taskManagerTest.addSubTask(additionalSubTask);
        final SubTask savedTask = taskManagerTest.getSubTaskById(subTaskId);
        assertNotNull(savedTask, "Подзадача не найдена.");
        assertEquals(additionalSubTask, savedTask, "Подзадачи не совпадают.");
        final List<Task> storedSubTasks = taskManagerTest.getSubTasks();
        assertNotNull(storedSubTasks, "Подзадачи не возвращаются.");
        assertEquals(2, storedSubTasks.size(), "Неверное количество подзадач.");
        assertEquals(Arrays.asList(testSubtaskWithStartTime1, additionalSubTask), storedSubTasks, "Подзадачи не совпадают.");
    }

    @Test
    void updateTaskTest() throws IOException {
        Task additionalTask = new Task("Additional task name", "Additional task description", NEW, 5);
        additionalTask.setStartTime(LocalDateTime.now().plusMinutes(30));
        int taskId = taskManagerTest.addTask(additionalTask);
        Task updatedTask = new Task("Test Updated", "Updated task description", DONE, 10);
        updatedTask.setStartTime(LocalDateTime.now().plusMinutes(40));
        taskManagerTest.updateTask(taskId, updatedTask);
        System.out.println(taskManagerTest.getTaskById(taskId));
        assertEquals(updatedTask, taskManagerTest.getTaskById(taskId), "Задачи не совпадают.");
    }

    @Test
    void updateEpicTest() throws IOException {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManagerTest.addEpic(epic);
        Epic updatedEpic = new Epic("Epic Updated", "Updated epic description");
        taskManagerTest.updateEpic(epicId, updatedEpic);
        assertEquals(updatedEpic, taskManagerTest.getEpicById(epicId), "Эпики не совпадают.");
    }

    @Test
    void updateSubTaskTest() throws IOException {
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewTask description", NEW, testEpicId1, 5);
        subTask.setStartTime(LocalDateTime.now().plusMinutes(30));
        final int subTaskId = taskManagerTest.addSubTask(subTask);
        SubTask updatedSubTask = new SubTask("Test updatedSubTask", "Test updatedSubTask description", NEW, testEpicId1, 10);
        updatedSubTask.setStartTime(subTask.getStartTime());
        taskManagerTest.updateSubTask(subTaskId, updatedSubTask);
        assertEquals(updatedSubTask, taskManagerTest.getSubTaskById(subTaskId), "Сабтаски не совпадают.");
    }

    @Test
    void deleteTaskTest() throws IOException {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW, 10);
        task.setStartTime(LocalDateTime.now().plusMinutes(30));
        final int taskId = taskManagerTest.addTask(task);
        int initialPrioritizedTasks = taskManagerTest.getPrioritizedTasks().size();
        taskManagerTest.deleteTaskById(taskId);
        int finalPrioritizedTasks = taskManagerTest.getPrioritizedTasks().size();
        Exception exception = assertThrows(java.io.IOException.class, () -> {
            taskManagerTest.getTaskById(taskId);
        });
        String expectedMessage = String.format("Задача с id=%s не найдена", taskId);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        assertEquals(initialPrioritizedTasks, finalPrioritizedTasks + 1, "Задача не удалена из приоритетного списка.");
    }

    @Test
    void deleteTasksTest() throws IOException {
        taskManagerTest.deleteTasks();
        assertTrue(taskManagerTest.getTasks().isEmpty(), "Задачи не удалены.");
        assertEquals(0, taskManagerTest.getPrioritizedTasks().size(), "Задачи не удалены из приоритетного списка.");
    }


    @Test
    void deleteEpicTest() throws IOException {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManagerTest.addEpic(epic);
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewTask description", NEW, epicId, 10);
        subTask.setStartTime(LocalDateTime.now().plusMinutes(30));
        final int subTaskId = taskManagerTest.addSubTask(subTask);
        int initialPrioritizedTasks = taskManagerTest.getPrioritizedTasks().size();

        taskManagerTest.deleteEpicById(epicId);
        Exception exception = assertThrows(java.io.IOException.class, () -> {
            taskManagerTest.getEpicById(epicId);
        });
        String expectedMessage = String.format("Эпик с id=%s не найден", epicId);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        assertNull(taskManagerTest.getSubTaskById(subTaskId), "Сабтаск не удален.");
        int finalPrioritizedTasks = taskManagerTest.getPrioritizedTasks().size();
        assertEquals(initialPrioritizedTasks, finalPrioritizedTasks + 1, "Задачи не удалены из приоритетного списка.");
    }

    @Test
    void deleteEpicsTest() throws IOException {
        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic1 description");
        final int epicId1 = taskManagerTest.addEpic(epic1);
        SubTask subTask1 = new SubTask("Test addNewSubTask1", "Test addNewTask1 description", NEW, epicId1, 10);
        final int subTaskId1 = taskManagerTest.addSubTask(subTask1);

        Epic epic2 = new Epic("Test addNewEpic2", "Test addNewEpic2 description");
        final int epicId2 = taskManagerTest.addEpic(epic2);
        SubTask subTask2 = new SubTask("Test addNewSubTask", "Test addNewTask2 description", NEW, epicId2, 10);
        final int subTaskId2 = taskManagerTest.addSubTask(subTask2);

        taskManagerTest.deleteEpics();
        assertTrue(taskManagerTest.getEpics().isEmpty(), "Эпик не удален.");
        assertTrue(taskManagerTest.getSubTasks().isEmpty(), "Сабтаски не удалены.");
    }

    @Test
    void deleteSubTaskByIdTest() throws IOException {
        List<Task> requestedTask = new ArrayList<>();
        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic1 description");
        final int epicId1 = taskManagerTest.addEpic(epic1);
        SubTask subTask1 = new SubTask("Test addNewSubTask1", "Test addNewTask1 description", NEW, epicId1, 10);
        subTask1.setStartTime(LocalDateTime.now().plusMinutes(30));
        final int subTaskId1 = taskManagerTest.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("Test addNewSubTask", "Test addNewTask2 description", NEW, epicId1, 10);
        subTask2.setStartTime(LocalDateTime.now().plusMinutes(40));
        final int subTaskId2 = taskManagerTest.addSubTask(subTask2);

        int initialPrioritizedTasks = taskManagerTest.getPrioritizedTasks().size();

        requestedTask.add(subTask1);
        taskManagerTest.deleteSubTaskById(subTaskId2);
        int finalPrioritizedTasks = taskManagerTest.getPrioritizedTasks().size();

        assertEquals(taskManagerTest.getSubTasksFromEpic(epicId1), requestedTask,
                "Сабтаски из эпика не получены");
        assertEquals(initialPrioritizedTasks, finalPrioritizedTasks + 1, "Подзадачи не удалены из приоритетного списка.");

    }

    @Test
    void getSubtasksFromEpicTest() throws IOException {
        List<Task> requestedTask = new ArrayList<>();

        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic1 description");
        final int epicId1 = taskManagerTest.addEpic(epic1);
        SubTask subTask1 = new SubTask("Test addNewSubTask1", "Test addNewTask1 description", NEW, epicId1, 5);
        final int subTaskId1 = taskManagerTest.addSubTask(subTask1);
        subTask1.setStartTime(LocalDateTime.now().plusMinutes(5));
        SubTask subTask2 = new SubTask("Test addNewSubTask", "Test addNewTask2 description", NEW, epicId1, 5);
        subTask2.setStartTime(LocalDateTime.now().plusMinutes(15));
        final int subTaskId2 = taskManagerTest.addSubTask(subTask2);

        requestedTask.add(subTask1);
        requestedTask.add(subTask2);

        assertEquals(taskManagerTest.getSubTasksFromEpic(epicId1), requestedTask,
                "Сабтаски из эпика не получены");
    }

}