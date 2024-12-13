package data;

public class Task {


    protected Status status;
    private int id;
    private String name;
    private String description;

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }


    public int getTaskId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }


    @Override
    public String toString() {
        return "Task={'id':" + "'" + getTaskId() + "'"
                + ", 'name':" + "'" + getName() + "'"
                + ", 'description':" + "'" + getDescription() + "'"
                + ", 'status':" + "'" + getStatus() + "'"
                + '}';
    }

}




