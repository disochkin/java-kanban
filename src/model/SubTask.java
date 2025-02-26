package model;

import static model.typeTask.SUBTASK;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
        this.type = SUBTASK;
    }

    public SubTask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
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
        return super.toString() + getEpicId();
    }

    @Override
    public String getCsvRow(char delimiter) {
        return super.getCsvRow(delimiter) + getEpicId();
    }}
