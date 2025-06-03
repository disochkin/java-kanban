package controllers;

import model.Epic;
import model.Status;
import model.SubTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static model.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Обновление статуса эпика")
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

    void generateSubtask(Status[] statuses) throws IOException {
        for (Status status : statuses) {
            TaskManagerTest.addSubTask(new SubTask("Test addNewSubTask", "Test addNewTask description", status, epicId, 5));
        }
    }

    @Test
    void epicUpdateStatusTestOnlyNew() throws IOException {
        generateSubtask(new Status[]{NEW, NEW, NEW, NEW});
        assertEquals(NEW, epic1.getStatus(), "Статус эпика вычисляется некорректно");
    }

    @Test
    void epicUpdateStatusOnlyDone() throws IOException {
        generateSubtask(new Status[]{DONE, DONE, DONE, DONE});
        assertEquals(DONE, epic1.getStatus(), "Статус эпика вычисляется некорректно");
    }

    @Test
    void epicUpdateStatusWithINPROGRESSNEWDONE() throws IOException {
        generateSubtask(new Status[]{IN_PROGRESS, NEW, DONE, DONE});
        assertEquals(IN_PROGRESS, epic1.getStatus(), "Статус эпика вычисляется некорректно");
    }


    @Test
    void epicUpdateStatusWithINPROGRESS() throws IOException {
        generateSubtask(new Status[]{IN_PROGRESS, DONE, DONE, DONE});
        assertEquals(IN_PROGRESS, epic1.getStatus(), "Статус эпика вычисляется некорректно");
    }


}
