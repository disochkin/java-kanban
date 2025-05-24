package model;

import java.time.LocalDateTime;

import static model.TypeTask.SUBTASK;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description, Status status, int epicId, long duration) {
        super(name, description, status, duration);
        this.epicId = epicId;
        this.type = SUBTASK;
    }

    public SubTask(int id, String name, String description, Status status, int epicId, long duration) {
        super(id, name, description, status, duration);
        this.epicId = epicId;
        this.type = SUBTASK;
    }


    public SubTask(int id, String name, String description, Status status, int epicId, LocalDateTime startTime, long duration) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
        this.type = SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getCsvRow(char delimiter) {
        return super.getCsvRow(delimiter) +epicId;
    }
}
