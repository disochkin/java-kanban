package model;

import java.util.HashSet;
import static model.TypeTask.EPIC;

public class Epic extends Task {

    private HashSet<Integer> subTasksIdList;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.type = EPIC;
        subTasksIdList = new HashSet<>();
    }

    public Epic(Integer id, String name, String description, Status epicStatus) {
        super(id, name, description, epicStatus);
        this.type = EPIC;
        subTasksIdList = new HashSet<>();
    }

    public void addSubTasksId(Integer tasksId) {
        subTasksIdList.add(tasksId);
    }

    public HashSet<Integer> getSubTasksIdList() {
        return subTasksIdList;
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
}
