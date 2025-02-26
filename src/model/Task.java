package model;

import static model.TypeTask.TASK;

public class Task {


    protected Status status;
    private int id;
    private String name;
    private String description;

    protected TypeTask type;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.type = TASK;
        this.description = description;
        this.status = status;
    }

    public Task(Integer id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.type = TASK;
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
        return String.valueOf(getId())+","+ type + ","+ getName() +","+ getStatus() + ","+getDescription() + ",";
    }

    public String getCsvRow(char delimiter) {
        return String.valueOf(getId())+delimiter+ type + delimiter + getName() + delimiter + getStatus() + delimiter +getDescription() + delimiter;
    }
}




