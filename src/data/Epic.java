package data;

import java.util.HashSet;

public class Epic extends Task {

    private HashSet<Integer> subTasksIdList;

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
        subTasksIdList = new HashSet<>();
    }

    public void addSubTasksId(Integer tasksId) {
        subTasksIdList.add(tasksId);
    }

    public HashSet<Integer> getSubTasksIdList() {
        return subTasksIdList;
    }

    void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Epic={'id':" + "'" + getTaskId() + "'"
                + ", 'name':" + "'" + getName() + "'"
                + ", 'description':" + "'" + getDescription() + "'"
                + ", 'status':" + "'" + getStatus() + "'"
                + '}';
    }

}
