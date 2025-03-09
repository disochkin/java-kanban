package controllers;

import model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task viewedTask);

    void remove(int id);

    void remove(List<Integer> listOfId);

    List<String> getHistory();
}
