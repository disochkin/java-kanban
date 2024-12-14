package controllers;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int generatorId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();


    private void addId(Task task) {
        final int id = ++generatorId;
        task.setId(id);
    }


    public int addTask(Task newTask) {
        addId(newTask);
        tasks.put(newTask.getId(), newTask);
        return newTask.getId();
    }

    public int addSubTask(SubTask newSubTask) {
        addId(newSubTask);
        subTasks.put(newSubTask.getId(), newSubTask);
        epics.get(newSubTask.getEpicId()).getSubTasksIdList().add(newSubTask.getId());
        epics.get(newSubTask.getEpicId()).addSubTasksId(newSubTask.getId());
        updateEpicStatus(newSubTask.getEpicId());
        return newSubTask.getId();
    }

    public int addEpic(Epic newEpic) {
        addId(newEpic);
        epics.put(newEpic.getId(), newEpic);
        return newEpic.getId();
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteTaskById(Integer taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
        }
    }

    public void deleteSubTaskById(Integer subTaskId) {
        if (subTasks.containsKey(subTaskId)) {
            int epicId = subTasks.get(subTaskId).getEpicId();
            epics.get(epicId).getSubTasksIdList().remove(subTaskId);
            subTasks.remove(subTaskId);
            updateEpicStatus(epicId);
        }
    }

    public void deleteEpicById(Integer epicId) {
        if (epics.containsKey(epicId)) {
            for (int subTaskId : epics.get(epicId).getSubTasksIdList()) {
                subTasks.remove(subTaskId);
            }
        }
        epics.remove(epicId);
    }

    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        subTasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subTasks.clear();
    }


    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getSubTasksFromEpic(int epicId) {
        ArrayList<SubTask> subTasksInEpic = new ArrayList<>();
        for (int id : epics.get(epicId).getSubTasksIdList()) {
            subTasksInEpic.add(subTasks.get(id));
        }
        return subTasksInEpic;
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void updateTask(int id, Task newTask) {
        newTask.setId(id);
        tasks.put(id, newTask);
    }

    public void updateEpic(int id, Epic newEpic) {
        newEpic.setId(id);
        tasks.put(id, newEpic);
    }

    public void updateSubTask(int id, SubTask newSubTask) {
        newSubTask.setId(id);
        subTasks.put(id, newSubTask);
        updateEpicStatus(newSubTask.getEpicId());
    }

    public void updateEpicStatus(Integer epicId) {
        int newSubtaskCount = 0;
        int doneSubtaskCount = 0;
        for (Integer index : epics.get(epicId).getSubTasksIdList()) {
            if (subTasks.get(index).getStatus() == Status.NEW) {
                newSubtaskCount++;
            } else if (subTasks.get(index).getStatus() == Status.DONE) {
                doneSubtaskCount++;
            }
        }
        if (epics.get(epicId).getSubTasksIdList().size() == newSubtaskCount) {
            epics.get(epicId).setStatus(Status.NEW);
        } else if (epics.get(epicId).getSubTasksIdList().size() == doneSubtaskCount) {
            epics.get(epicId).setStatus(Status.DONE);
        } else {
            epics.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }
}
