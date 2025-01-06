package controllers;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    static ArrayList<Task> historyList = new ArrayList<Task>();

    int addTask(Task newTask);

    int addSubTask(SubTask newSubTask);

    int addEpic(Epic newEpic);

    Epic getEpicById(int epicId);

    Task getTaskById(int taskId);

    SubTask getSubTaskById(int subTaskId);

    void deleteTasks();

    void deleteTaskById(Integer taskId);

    void deleteSubTaskById(Integer subTaskId);

    void deleteEpicById(Integer epicId);

    void deleteSubtasks();

    void deleteEpics();

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Task> getSubTasksFromEpic(int epicId);

    ArrayList<Task> getSubTasks();

    void updateTask(int id, Task newTask);

    void updateEpic(int id, Epic newEpic);

    void updateSubTask(int id, SubTask newSubTask);

    ArrayList<Task> getHistory();
}
