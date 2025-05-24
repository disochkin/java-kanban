package controllers;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.ArrayList;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Добавление тасок в историю задач")
class HistoryManagerTest {
    TaskManager taskManagerTest;
    ArrayList<String> requestedTask;
    int taskId, epicId, subTaskId1, subTaskId2;
    Task task;
    Epic epic1;
    SubTask subTask1, subTask2;

    @BeforeEach
    void setupClass() throws IOException {
        taskManagerTest = Managers.getDefault();
        requestedTask = new ArrayList<>();

        task = new Task("Test addNewTask", "Test addNewTask description", NEW, 5);
        taskId = taskManagerTest.addTask(task);

        epic1 = new Epic("Test NewEpic1", "Test NewEpic1 description");
        epicId = taskManagerTest.addEpic(epic1);

        subTask1 = new SubTask("Test addNewSubTask1", "Test addNewTask description1", NEW, epicId, 5);
        subTaskId1 = taskManagerTest.addSubTask(subTask1);

        subTask2 = new SubTask("Test addNewSubTask2", "Test addNewTask description2", NEW, epicId, 5);
        subTaskId2 = taskManagerTest.addSubTask(subTask2);
    }

    @Test
    @DisplayName("Добавить в историю")
    void getHistoryTest() {
        taskManagerTest.getTaskById(taskId);
        requestedTask.add(task.toString());
        assertEquals(requestedTask, taskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Задачи не добавляются.");

        taskManagerTest.getEpicById(epicId);
        requestedTask.add(epic1.toString());
        assertEquals(requestedTask, taskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Эпики не добавляются.");

        taskManagerTest.getSubTaskById(subTaskId1);
        requestedTask.add(subTask1.toString());

        taskManagerTest.getSubTaskById(subTaskId2);
        requestedTask.add(subTask2.toString());

        assertEquals(requestedTask, taskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Сабтаски не добавляются");
    }

    @Test
    void historyCountTestTask() {
        taskManagerTest.getTaskById(taskId);
        requestedTask.add(task.toString());
        taskManagerTest.getTaskById(taskId);
        assertEquals(requestedTask, taskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Дубликаты задач не удаляются");
    }

    @Test
    void historyCountTestEpic() {
        taskManagerTest.getEpicById(epicId);
        requestedTask.add(epic1.toString());
        taskManagerTest.getEpicById(epicId);
        assertEquals(requestedTask, taskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Дубликаты эпиков не удаляются");
    }


    @Test
    void historyCountTestSubTask() {
        taskManagerTest.getSubTaskById(subTaskId1);
        requestedTask.add(subTask1.toString());
        taskManagerTest.getSubTaskById(subTaskId1);
        assertEquals(requestedTask, taskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Дубликаты подзадач не удаляются");
    }

    @Test
    void historyManagerRemoveTaskInMiddle() {
        taskManagerTest.getTaskById(taskId);
        requestedTask.add(task.toString());

        taskManagerTest.getSubTaskById(subTaskId1);

        taskManagerTest.getEpicById(epicId);
        requestedTask.add(epic1.toString());

        taskManagerTest.getSubTaskById(subTaskId1);
        requestedTask.add(subTask1.toString());

        assertEquals(requestedTask, taskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Дубликат задач не удаляется из середины списка");
    }

    @Test
    void historyManagerRemoveTaskFromStart() {
        taskManagerTest.getTaskById(taskId);

        taskManagerTest.getSubTaskById(subTaskId1);
        requestedTask.add(subTask1.toString());

        taskManagerTest.getEpicById(epicId);
        requestedTask.add(epic1.toString());

        taskManagerTest.getTaskById(taskId);
        requestedTask.add(task.toString());

        assertEquals(requestedTask, taskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Дубликат задач не удаляется из начала списка");
    }

    @Test
    void historyManagerRemoveTaskFromEnd() {
        taskManagerTest.getTaskById(taskId);
        requestedTask.add(task.toString());

        taskManagerTest.getSubTaskById(subTaskId1);
        requestedTask.add(subTask1.toString());

        taskManagerTest.getEpicById(epicId);

        taskManagerTest.getEpicById(epicId);
        requestedTask.add(epic1.toString());

        assertEquals(requestedTask, taskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Дубликат задач не удаляется из конца списка");
    }

    @Test
    void historyManagerUpdateAfterDeleteTask() {
        taskManagerTest.getTaskById(taskId);
        taskManagerTest.deleteTaskById(taskId);

        assertEquals(requestedTask, taskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Задача не удаляется из истории просмотра после удаления");
    }

    @Test
    void historyManagerUpdateAfterDeleteEpic() {
        taskManagerTest.getEpicById(taskId);
        taskManagerTest.deleteEpicById(taskId);

        assertEquals(requestedTask, taskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Эпик не удаляется из истории просмотра после удаления");
    }

    @Test
    void historyManagerUpdateAfterDeleteSubtasks() {
        taskManagerTest.getSubTaskById(subTaskId1);
        requestedTask.add(subTask1.toString());

        taskManagerTest.getSubTaskById(subTaskId2);
        requestedTask.add(subTask2.toString());

        assertEquals(requestedTask, taskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Сабтаски не добавлены в историю просмотра");

        taskManagerTest.deleteSubtasks();
        requestedTask.clear();

        assertEquals(requestedTask, taskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Сабтаски не удаляется из истории просмотра после удаления");
    }

}


