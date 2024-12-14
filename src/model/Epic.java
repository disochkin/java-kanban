package model;

import java.util.HashSet;

public class Epic extends Task {

    private HashSet<Integer> subTasksIdList;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
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
        return "Epic={'id':" + "'" + getId() + "'"
                + ", 'name':" + "'" + getName() + "'"
                + ", 'description':" + "'" + getDescription() + "'"
                + ", 'status':" + "'" + getStatus() + "'"
                + '}';
    }

}
