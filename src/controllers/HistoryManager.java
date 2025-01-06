package controllers;

import model.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void updateHistory(Task viewedTask);

    ArrayList<Task> getHistory();
}
