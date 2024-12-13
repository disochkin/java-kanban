package data;

public class SubTask extends Task {

    int epicId;

    public SubTask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask={'id':" + "'" + getTaskId() + "'"
                + ", 'name':" + "'" + getName() + "'"
                + ", 'description':" + "'" + getDescription() + "'"
                + ", 'status':" + "'" + getStatus() + "'"
                + ", 'epicID':" + "'" + getEpicId() + "'"
                + '}';
    }
}
