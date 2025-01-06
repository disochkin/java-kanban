package controllers;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    final int maxHistoryLength = 10;
    private List<Task> historyList = new ArrayList<>(10) {
    };


    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) historyList;
    }

    @Override
    public void updateHistory(Task viewedTask) {
        if (historyList.size() > maxHistoryLength-1) {
            historyList.remove(0);
        }
        historyList.add(viewedTask);
    }
}
