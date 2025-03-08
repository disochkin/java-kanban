package controllers;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("unit тесты FileBackedTaskManagerTest")
class FileBackedTaskManagerTest {
    private TaskManager TaskManagerTest;
    private File database_file;

    //@BeforeEach
    void setupClass() {
        try {
            database_file = File.createTempFile("fb-taskmanager-unit-test", "csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TaskManagerTest = Managers.getFileBackedFM(database_file);
    }


    @Test
    void TaskManagerInitTest() {
        setupClass();
        assertNotNull(TaskManagerTest, "ошибка создания FileBackedTaskManagerTest");
    }

    @Test
    void saveToFileTest() {

        setupClass();
        ArrayList<String> expectedResult = new ArrayList<>();
        expectedResult.add("id,type,name,status,description,epic");
        expectedResult.add("1,TASK,Test addNewTask,NEW,Test addNewTask description,");
        expectedResult.add("2,EPIC,Test addNewEpic1,NEW,Test addNewEpic1 description,");
        expectedResult.add("3,SUBTASK,Test addNewSubTask,NEW,Test addSubNewTask description,2");


        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = TaskManagerTest.addTask(task);
        Epic epic = new Epic("Test addNewEpic1", "Test addNewEpic1 description");
        final int epicId = TaskManagerTest.addEpic(epic);
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addSubNewTask description", NEW, epicId);
        final int subTaskId = TaskManagerTest.addSubTask(subTask);

        ArrayList<String> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(database_file))) {
            while (br.ready()) {
                result.add(br.readLine());
            }
            assertEquals(expectedResult, result);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    @Test
    void loadFromFileTest() throws IOException {
        try {
            database_file = File.createTempFile("fb-taskmanager-unit-test", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> databaseFileContent = new ArrayList<>();
        databaseFileContent.add("id,type,name,status,description,epic");
        databaseFileContent.add("1,TASK,Test addNewTask,NEW,Test addNewTask description,");
        databaseFileContent.add("2,EPIC,Test addNewEpic1,NEW,Test addNewEpic1 description,");
        databaseFileContent.add("3,SUBTASK,Test addNewSubTask,NEW,Test addSubNewTask description,2");

        BufferedWriter writer = new BufferedWriter(new FileWriter(database_file));
        for (String row : databaseFileContent) {
            writer.write(row + System.lineSeparator());
        }
        writer.flush();
        TaskManagerTest = FileBackedTaskManager.loadFromFile(database_file);
        assertEquals("1,TASK,Test addNewTask,NEW,Test addNewTask description,", TaskManagerTest.getTasks().get(0).toString(),
                "Ошибка восстановления Задач из файла (TASK)");
        assertEquals("2,EPIC,Test addNewEpic1,NEW,Test addNewEpic1 description,", TaskManagerTest.getEpics().get(0).toString(),
                "Ошибка восстановления Эпиков из файла (EPIC)");
        assertEquals("3,SUBTASK,Test addNewSubTask,NEW,Test addSubNewTask description,2", TaskManagerTest.getSubTasks().get(0).toString(),
                "Ошибка восстановления Подзадач из файла (SUBTASKS)");

    }

    @Test
    void addNewEpicTest() {
        setupClass();
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
        setupClass();
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
        setupClass();
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = TaskManagerTest.addTask(task);

        Task updatedTask = new Task("Test Updated", "Updated task description", NEW);
        TaskManagerTest.updateTask(taskId, updatedTask);

        assertEquals(updatedTask, TaskManagerTest.getTaskById(taskId), "Задачи не совпадают.");
    }

    @Test
    void updateEpicTest() {
        setupClass();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = TaskManagerTest.addEpic(epic);

        Epic updatedEpic = new Epic("Epic Updated", "Updated epic description");
        TaskManagerTest.updateEpic(epicId, updatedEpic);

        assertEquals(updatedEpic, TaskManagerTest.getEpicById(epicId), "Эпики не совпадают.");
    }

    @Test
    void updateSubTaskTest() {
        setupClass();
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
        setupClass();
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = TaskManagerTest.addTask(task);

        TaskManagerTest.deleteTaskById(taskId);

        assertNull(TaskManagerTest.getTaskById(taskId), "Задача не удалена.");
    }

    @Test
    void deleteTasksTest() {
        setupClass();
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description", NEW);
        Task task2 = new Task("Test addNewTask2", "Test2 addNewTask2 description", NEW);
        TaskManagerTest.deleteTasks();

        assertTrue(TaskManagerTest.getTasks().isEmpty(), "Задачи не удалены.");
    }


    @Test
    void deleteEpicTest() {
        setupClass();
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
        setupClass();
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
        setupClass();
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
        setupClass();
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