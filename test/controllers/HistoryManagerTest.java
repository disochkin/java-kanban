package controllers;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Добавление тасок в историю задач")
class HistoryManagerTest {
    TaskManager TaskManagerTest;
    ArrayList<String> requestedTask;
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
    @DisplayName("Добавить в историю")
    void getHistoryTest() {
        TaskManagerTest.getTaskById(taskId);
        requestedTask.add(task.toString());
        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Задачи не добавляются.");

        TaskManagerTest.getEpicById(epicId);
        requestedTask.add(epic1.toString());
        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Эпики не добавляются.");

        TaskManagerTest.getSubTaskById(subTaskId1);
        requestedTask.add(subTask1.toString());

        TaskManagerTest.getSubTaskById(subTaskId2);
        requestedTask.add(subTask2.toString());

        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Сабтаски не добавляются");
    }

    @Test
    void historyCountTestTask() {
        TaskManagerTest.getTaskById(taskId);
        requestedTask.add(task.toString());
        TaskManagerTest.getTaskById(taskId);
        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Дубликаты задач не удаляются");
    }

    @Test
    void historyCountTestEpic() {
        TaskManagerTest.getEpicById(epicId);
        requestedTask.add(epic1.toString());
        TaskManagerTest.getEpicById(epicId);
        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Дубликаты эпиков не удаляются");
    }


    @Test
    void historyCountTestSubTask() {
        TaskManagerTest.getSubTaskById(subTaskId1);
        requestedTask.add(subTask1.toString());
        TaskManagerTest.getSubTaskById(subTaskId1);
        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Дубликаты подзадач не удаляются");
    }

    @Test
    void historyManagerRemoveTaskInMiddle() {
        TaskManagerTest.getTaskById(taskId);
        requestedTask.add(task.toString());

        TaskManagerTest.getSubTaskById(subTaskId1);

        TaskManagerTest.getEpicById(epicId);
        requestedTask.add(epic1.toString());

        TaskManagerTest.getSubTaskById(subTaskId1);
        requestedTask.add(subTask1.toString());

        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Дубликат задач не удаляется из середины списка");
    }

    @Test
    void historyManagerRemoveTaskFromStart() {
        TaskManagerTest.getTaskById(taskId);

        TaskManagerTest.getSubTaskById(subTaskId1);
        requestedTask.add(subTask1.toString());

        TaskManagerTest.getEpicById(epicId);
        requestedTask.add(epic1.toString());

        TaskManagerTest.getTaskById(taskId);
        requestedTask.add(task.toString());

        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Дубликат задач не удаляется из начала списка");
    }

    @Test
    void historyManagerRemoveTaskFromEnd() {
        TaskManagerTest.getTaskById(taskId);
        requestedTask.add(task.toString());

        TaskManagerTest.getSubTaskById(subTaskId1);
        requestedTask.add(subTask1.toString());

        TaskManagerTest.getEpicById(epicId);

        TaskManagerTest.getEpicById(epicId);
        requestedTask.add(epic1.toString());

        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Дубликат задач не удаляется из конца списка");
    }

    @Test
    void historyManagerUpdateAfterDeleteTask() {
        TaskManagerTest.getTaskById(taskId);
        TaskManagerTest.deleteTaskById(taskId);

        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Задача не удаляется из истории просмотра после удаления");
    }

    @Test
    void historyManagerUpdateAfterDeleteEpic() {
        TaskManagerTest.getEpicById(taskId);
        TaskManagerTest.deleteEpicById(taskId);

        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Эпик не удаляется из истории просмотра после удаления");
    }

    @Test
    void historyManagerUpdateAfterDeleteSubtasks() {
        TaskManagerTest.getSubTaskById(subTaskId1);
        requestedTask.add(subTask1.toString());

        TaskManagerTest.getSubTaskById(subTaskId2);
        requestedTask.add(subTask2.toString());

        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Сабтаски не добавлены в историю просмотра");

        TaskManagerTest.deleteSubtasks();
        requestedTask.clear();

        assertEquals(requestedTask, TaskManagerTest.getHistory(), "История просмотра задач работает некорректно." +
                "Сабтаски не удаляется из истории просмотра после удаления");
    }

}


