package controllers;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected int generatorId = 0;
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected TreeMap<LocalDateTime, Task> sortTaskTime = new TreeMap<>((o1, o2) -> {
        if (o1 == null) {
            return 1;
        } else if (o2 == null) {
            return -1;
        } else {
            return o1.compareTo(o2);
        }
    });


    private long getMilliseconds(LocalDateTime localDateTime) {
        return ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    private boolean checkTaskTimeCollision(Task task1, Task task2) {
        long overlap = Math.min(getMilliseconds(task1.getEndTime()), getMilliseconds(task2.getEndTime())) -
                Math.max(getMilliseconds(task1.getStartTime()), getMilliseconds(task2.getStartTime()));
        return overlap > 0;
    }


    private void intersectionsOfTime(Task taskToCheck) throws IOException {
        if (taskToCheck.getStartTime() != null) {
            Task collisionTask = sortTaskTime.values().stream()
                    .filter(task -> checkTaskTimeCollision(task, taskToCheck)).findFirst().orElse(null);
            if (collisionTask != null) {
                throw new IOException("Пересечение по времени выполнения c задачей - " + collisionTask.getName());
            }
        }
    }

    private void putToSortTimeTask(Task task) {
        if (task.getStartTime() != null) {
            sortTaskTime.put(task.getStartTime(), task);
        }
    }


    private void addId(Task task) {
        final int id = ++generatorId;
        task.setId(id);
    }


    @Override
    public int addTask(Task newTask) throws IOException {
        intersectionsOfTime(newTask);
        addId(newTask);
        tasks.put(newTask.getId(), newTask);
        putToSortTimeTask(newTask);
        return newTask.getId();
    }

    @Override
    public int addSubTask(SubTask newSubTask) throws IOException {
        intersectionsOfTime(newSubTask);
        addId(newSubTask);
        putToSortTimeTask(newSubTask);
        subTasks.put(newSubTask.getId(), newSubTask);

        //epics.get(newSubTask.getEpicId()).getSubTasksIdList().add(newSubTask.getId());
        epics.get(newSubTask.getEpicId()).addSubTasksId(newSubTask.getId());


        updateEpicStatus(newSubTask.getEpicId());
        updateEpicExecTime(newSubTask.getEpicId());
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
    public void updateTask(int id, Task updatedTask) throws IOException {
        if (tasks.containsKey(id) && updatedTask.getId() == 0) {
            sortTaskTime.remove(tasks.get(id).getStartTime());
            intersectionsOfTime(updatedTask);
            updatedTask.setId(id);
            sortTaskTime.put(updatedTask.getStartTime(), updatedTask);
            tasks.put(id, updatedTask);
        }
    }

    @Override
    public void updateEpic(int id, Epic updatedEpic) throws IOException {
        updatedEpic.setId(id);
        epics.put(id, updatedEpic);
    }

    @Override
    public void updateSubTask(int id, SubTask updatedSubTask) throws IOException {
        updatedSubTask.setId(id);
        subTasks.put(id, updatedSubTask);
        updateEpicStatus(updatedSubTask.getEpicId());
        updateEpicExecTime(updatedSubTask.getEpicId());
    }


    private void updateEpicStatus(int epicId) {
        int newSubtaskCount = 0;
        int doneSubtaskCount = 0;
        for (int index : epics.get(epicId).getSubTasksIdList()) {
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

    public void updateEpicExecTime(int epicId) {
        LocalDateTime startEpicTime = null;
        LocalDateTime endEpicTime = null;
        long durationEpic = 0;
        for (int checkedSubTaskId : epics.get(epicId).getSubTasksIdList()) {
            if (subTasks.get(checkedSubTaskId).getStartTime() != null) {
                if (startEpicTime == null) {
                    startEpicTime = subTasks.get(checkedSubTaskId).getStartTime();
                    endEpicTime = subTasks.get(checkedSubTaskId).getEndTime();
                }
                if (startEpicTime != null && subTasks.get(checkedSubTaskId).getStartTime().isBefore(startEpicTime)) {
                    startEpicTime = subTasks.get(checkedSubTaskId).getStartTime();
                }
                if (endEpicTime != null && subTasks.get(checkedSubTaskId).getEndTime().isAfter(endEpicTime)) {
                    endEpicTime = subTasks.get(checkedSubTaskId).getEndTime();
                }
                durationEpic += durationEpic + subTasks.get(checkedSubTaskId).getDuration();
            }
        }
        epics.get(epicId).setDuration(durationEpic);
        if (startEpicTime != null) {
            epics.get(epicId).setStartTime(startEpicTime);
            epics.get(epicId).setEndTime(endEpicTime);
            epics.get(epicId).setEndTime(startEpicTime.plusMinutes(durationEpic));
        }
    }


    @Override
    public List<String> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() { return new LinkedList<>(sortTaskTime.values()); }
}
