package controllers;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected int generatorId = 0;
    protected HistoryManager historyManager = Managers.getDefaultHistory();


    private void addId(Task task) {
        final int id = ++generatorId;
        task.setId(id);
    }


    @Override
    public int addTask(Task newTask) {
        addId(newTask);
        tasks.put(newTask.getId(), newTask);
        return newTask.getId();
    }

    @Override
    public int addSubTask(SubTask newSubTask) {
        addId(newSubTask);
        subTasks.put(newSubTask.getId(), newSubTask);
        epics.get(newSubTask.getEpicId()).getSubTasksIdList().add(newSubTask.getId());
        epics.get(newSubTask.getEpicId()).addSubTasksId(newSubTask.getId());
        updateEpicStatus(newSubTask.getEpicId());
        return newSubTask.getId();
    }

    @Override
    public int addEpic(Epic newEpic) {
        addId(newEpic);
        epics.put(newEpic.getId(), newEpic);
        return newEpic.getId();
    }

    @Override
    public Epic getEpicById(int epicId) {
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public Task getTaskById(int taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        historyManager.add(subTasks.get(subTaskId));
        return subTasks.get(subTaskId);
    }

    @Override
    public void deleteTasks() {
        historyManager.remove(new ArrayList<>(tasks.keySet()));
        tasks.clear();
    }

    @Override
    public void deleteTaskById(Integer taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            historyManager.remove(taskId);
        }
    }

    @Override
    public void deleteSubTaskById(Integer subTaskId) {
        if (subTasks.containsKey(subTaskId)) {
            int epicId = subTasks.get(subTaskId).getEpicId();
            epics.get(epicId).getSubTasksIdList().remove(subTaskId);
            subTasks.remove(subTaskId);
            updateEpicStatus(epicId);
            historyManager.remove(subTaskId);
        }
    }

    @Override
    public void deleteEpicById(Integer epicId) {
        if (epics.containsKey(epicId)) {
            for (int subTaskId : epics.get(epicId).getSubTasksIdList()) {
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        historyManager.remove(new ArrayList<>(subTasks.keySet()));
        subTasks.clear();
    }

    @Override
    public void deleteEpics() {
        historyManager.remove(new ArrayList<>(epics.keySet()));
        epics.clear();
        historyManager.remove(new ArrayList<>(subTasks.keySet()));
        subTasks.clear();
    }


    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Task> getSubTasksFromEpic(int epicId) {
        ArrayList<Task> subTasksInEpic = new ArrayList<>();
        for (int id : epics.get(epicId).getSubTasksIdList()) {
            subTasksInEpic.add(subTasks.get(id));
        }
        return subTasksInEpic;
    }

    @Override
    public ArrayList<Task> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void updateTask(int id, Task updatedTask) {
        updatedTask.setId(id);
        tasks.put(id, updatedTask);
    }

    @Override
    public void updateEpic(int id, Epic updatedEpic) {
        updatedEpic.setId(id);
        epics.put(id, updatedEpic);
    }

    @Override
    public void updateSubTask(int id, SubTask updatedSubTask) {
        updatedSubTask.setId(id);
        subTasks.put(id, updatedSubTask);
        updateEpicStatus(updatedSubTask.getEpicId());
    }


    private void updateEpicStatus(Integer epicId) {
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

    @Override
    public List<String> getHistory() {
        return historyManager.getHistory();
    }
}
