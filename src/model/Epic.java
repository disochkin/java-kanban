package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static model.TypeTask.EPIC;

public class Epic extends Task {

    private final ArrayList<Integer> subTasksIdList;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW, 0);
        this.type = EPIC;
        subTasksIdList = new ArrayList<>();
    }

    public Epic(Integer id, String name, String description, Status epicStatus, long duration) {
        super(id, name, description, epicStatus, duration);
        this.type = EPIC;
        subTasksIdList = new ArrayList<>();
    }

    public void addSubTasksId(Integer tasksId) {
        subTasksIdList.add(tasksId);
    }

    public ArrayList<Integer> getSubTasksIdList() {
        return subTasksIdList;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime(LocalDateTime endTime) {
        return endTime;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void cleanSubtaskIds() {
        subTasksIdList.clear();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getCsvRow(char delimiter) {
        return super.getCsvRow(delimiter);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
