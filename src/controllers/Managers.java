package controllers;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        HistoryManager historyManager = getDefaultHistory();
        return new InMemoryTaskManager(historyManager);
    }

    public static TaskManager getFileBackedFM(File file) {
        HistoryManager historyManager = getDefaultHistory();
        return new FileBackedTaskManager(historyManager, file);
    }


    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}



