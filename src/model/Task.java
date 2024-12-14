package model;

public class Task {


    protected Status status;
    private int id;
    private String name;
    private String description;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return "Task={'id':" + "'" + getId() + "'"
                + ", 'name':" + "'" + getName() + "'"
                + ", 'description':" + "'" + getDescription() + "'"
                + ", 'status':" + "'" + getStatus() + "'"
                + '}';
    }
}




