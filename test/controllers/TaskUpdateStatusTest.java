package controllers;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskUpdateStatusTest {
    Epic epic1;
    int epicId;
    TaskManager TaskManagerTest;

    @BeforeEach
    void setupClass() {
        TaskManagerTest = Managers.getDefault();
        epic1 = new Epic("Test NewEpic1", "Test NewEpic1 description");
        epicId = TaskManagerTest.addEpic(epic1);
    }

    void generateSubtask(Status[] statuses) {
        for (Status status:statuses) {
            TaskManagerTest.addSubTask(new SubTask("Test addNewSubTask", "Test addNewTask description", status, epicId));
        }
    }
    @Test
    void epicUpdateStatusTestOnlyNew() {
        generateSubtask(new Status[]{NEW, NEW, NEW, NEW});
        assertEquals(epic1.getStatus(), NEW, "Статус эпика вычисляется некорректно");
    }

    @Test void epicUpdateStatusOnlyDone() {
        generateSubtask(new Status[]{DONE, DONE, DONE, DONE});
        assertEquals(epic1.getStatus(), DONE, "Статус эпика вычисляется некорректно");
    }

    @Test void epicUpdateStatusWithINPROGRESS() {
        generateSubtask(new Status[]{IN_PROGRESS, DONE, DONE, DONE});
        assertEquals(epic1.getStatus(), IN_PROGRESS, "Статус эпика вычисляется некорректно");
    }


}
