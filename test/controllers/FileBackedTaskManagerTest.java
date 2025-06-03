package controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("unit тесты FileBackedTaskManagerTest")
class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {
    private TaskManager TaskManagerTest;
    private File database_file;

    @BeforeEach
    void setupClass() throws IOException {
        try {
            database_file = File.createTempFile("fb-taskmanager-unit-test", "csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskManagerTest = Managers.getFileBackedFM(database_file);
        initTasks();
    }


    @Test
    void TaskManagerInitTest() {
        assertNotNull(taskManagerTest, "ошибка создания FileBackedTaskManagerTest");
    }


    @Test
    void loadFromFileTest() throws IOException {
        LocalDateTime currentTime = LocalDateTime.now();
        try {
            database_file = File.createTempFile("fb-taskmanager-unit-test", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> databaseFileContent = new ArrayList<>();
        databaseFileContent.add("id,type,name,status,description,duration,startTime,epic");
        databaseFileContent.add(String.format("1,TASK,Test addNewTask,NEW,Test addNewTask description,5,%s,", currentTime));
        databaseFileContent.add(String.format("2,EPIC,Test addNewEpic1,NEW,Test addNewEpic1 description,5,%s,", currentTime));
        databaseFileContent.add(String.format("3,SUBTASK,Test addNewSubTask,NEW,Test addSubNewTask description,5,%s,2", currentTime));
        BufferedWriter writer = new BufferedWriter(new FileWriter(database_file));
        for (String row : databaseFileContent) {
            writer.write(row + System.lineSeparator());
        }
        writer.flush();
        taskManagerTest = FileBackedTaskManager.loadFromFile(database_file);
        assertEquals(String.format("1,TASK,Test addNewTask,NEW,Test addNewTask description,5,%s", currentTime), taskManagerTest.getTasks().get(0).toString(),
                "Ошибка восстановления Задач из файла (TASK)");
        assertEquals(String.format("2,EPIC,Test addNewEpic1,NEW,Test addNewEpic1 description,5,%s", currentTime), taskManagerTest.getEpics().get(0).toString(),
                "Ошибка восстановления Эпиков из файла (EPIC)");
        assertEquals(String.format("3,SUBTASK,Test addNewSubTask,NEW,Test addSubNewTask description,5,%s", currentTime),
                taskManagerTest.getSubTasks().get(0).toString(), "Ошибка восстановления Подзадач из файла (SUBTASKS)");

    }

    @Test
    void saveToFileTest() throws IOException {
        ArrayList<String> expectedResult = new ArrayList<>();
        expectedResult.add("id,type,name,status,description,duration,startTime,epic");
        expectedResult.add(String.format("1,TASK,Task1 test name,NEW,Task1 test description,5,%s,", startTime));
        expectedResult.add(String.format("2,EPIC,Test epic1 name,NEW,Test epic1 description,5,%s,", startTime.plusMinutes(30)));
        expectedResult.add(String.format("3,SUBTASK,Test subtask1 name,NEW,Test subtask1 description,5,%s,2", startTime.plusMinutes(30)));
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
    void addNewEpicTest() throws IOException {
        super.addNewEpicTest();
    }

    @Test
    void addNewSubTaskTest() throws IOException {
        super.addNewSubTaskTest();
    }

    @Test
    void updateTaskTest() throws IOException {
        super.updateTaskTest();
    }

    @Test
    void updateEpicTest() throws IOException {
        super.updateEpicTest();
    }

    @Test
    void updateSubTaskTest() throws IOException {
        super.updateSubTaskTest();
    }

    @Test
    void deleteTaskTest() throws IOException {
        super.deleteTaskTest();
    }

    @Test
    void deleteTasksTest() throws IOException {
        super.deleteTasksTest();
    }


    @Test
    void deleteEpicTest() throws IOException {
        super.deleteEpicTest();
    }

    @Test
    void deleteEpicsTest() throws IOException {
        super.deleteEpicsTest();
    }

    @Test
    void deleteSubTaskById() throws IOException {
        super.deleteSubTaskByIdTest();
    }

    @Test
    void getSubtasksFromEpicTest() throws IOException {
        super.getSubtasksFromEpicTest();
    }
}