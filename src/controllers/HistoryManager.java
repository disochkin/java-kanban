package controllers;

import model.Task;

import java.util.List;

public interface HistoryManager {
    void updateHistory(Task viewedTask);
    void updateHistory(List<Task> viewedTaskList);

    void remove(int id);

    void remove(List<Integer> listOfId);

    List<String> getHistory();
}
