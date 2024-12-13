package data;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();


    public void addTask(Task newTask) {
        tasks.put(newTask.getTaskId(), newTask);
    }

    public void addSubTask(SubTask newSubTask) {
        subTasks.put(newSubTask.getTaskId(), newSubTask);
        epics.get(newSubTask.getEpicId()).getSubTasksIdList().add(newSubTask.getTaskId());
        epics.get(newSubTask.getEpicId()).addSubTasksId(newSubTask.getTaskId());
        updateEpicStatus(newSubTask.getEpicId());
    }

    public void addEpic(Epic newEpic) {
        epics.put(newEpic.getTaskId(), newEpic);
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }


    public void deleteAllTasks() {
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

    public void updateTask(Task newTask) {
        int id = newTask.getTaskId();
        tasks.put(id, newTask);
    }

    public void updateEpic(Epic newEpic) {
        int id = newEpic.getTaskId();
        tasks.put(id, newEpic);
    }

    public void updateSubTask(SubTask newSubTask) {
        if (epics.containsKey(newSubTask.getEpicId())) {
            subTasks.put(newSubTask.getTaskId(), newSubTask);
            updateEpicStatus(newSubTask.getEpicId());
        }
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
