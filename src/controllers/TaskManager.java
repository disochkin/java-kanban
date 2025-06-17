package controllers;

import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    int addTask(Task newTask) throws IOException;

    int addSubTask(SubTask newSubTask) throws IOException;

    int addEpic(Epic newEpic);

    Epic getEpicById(Integer epicId) throws IOException;

    Task getTaskById(Integer taskId) throws IOException;

    SubTask getSubTaskById(int subTaskId);

    void deleteTasks();

    void deleteTaskById(Integer taskId);

    void deleteSubTaskById(Integer subTaskId);

    void deleteEpicById(Integer epicId);

    void deleteSubtasks();

    void deleteEpics();

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    List<Task> getSubTasksFromEpic(int epicId);

    ArrayList<Task> getSubTasks();

    void updateTask(int id, Task newTask) throws IOException;

    void updateEpic(int id, Epic newEpic) throws IOException;

    void updateSubTask(int id, SubTask newSubTask) throws IOException;

    List<String> getHistory();

    List<Task> getPrioritizedTasks();

}
