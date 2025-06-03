package model;

import java.time.Duration;
import java.time.LocalDateTime;

import static model.TypeTask.TASK;

public class Task {


    protected Status status;
    protected TypeTask type;
    protected LocalDateTime startTime;
    private int id;
    private final String name;
    private final String description;
    protected Duration duration;

    public Task(String name, String description, Status status, long duration) {
        this.name = name;
        this.type = TASK;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(duration);
    }

    public Task(Integer id, String name, String description, Status status, long duration) {
        this.id = id;
        this.name = name;
        this.type = TASK;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(duration);
    }

    public Task(Integer id, String name, String description, Status status, LocalDateTime startTime, long duration) {
        this.id = id;
        this.name = name;
        this.type = TASK;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
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
        return getId() + "," + type + "," + getName() + "," + getStatus() + "," + getDescription() + ","
                + getDuration() + "," + startTime;
    }

    public String getCsvRow(char delimiter) {
        return String.valueOf(getId()) + delimiter + type + delimiter + getName() + delimiter + getStatus() + delimiter +
                getDescription() + delimiter + duration.toMinutes() + delimiter + startTime + delimiter;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Long getDuration() {
        return duration.toMinutes();
    }

    public void setDuration(long duration) {
        this.duration = Duration.ofMinutes(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

}




