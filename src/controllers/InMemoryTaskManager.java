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
import java.util.stream.Collectors;

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

//    private int getSubTaskListSizeInEpic(int epicId) {
//        return
//    }

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
        int epicId = subTasks.get(newSubTask.getId()).getEpicId();
        epics.get(epicId).addSubTasksId(newSubTask.getId());
        updateEpicStatus(epicId);
        updateEpicExecTime(epicId);
        return newSubTask.getId();
    }

    @Override
    public int addEpic(Epic newEpic) {
        addId(newEpic);
        epics.put(newEpic.getId(), newEpic);
        return newEpic.getId();
    }

    @Override
    public Epic getEpicById(Integer epicId) throws IOException {
        if (epics.containsKey(epicId)) {
            historyManager.add(epics.get(epicId));
            return epics.get(epicId);
        } else {
            throw new IOException(String.format("Эпик с id=%s не найден", epicId));
        }
    }


    @Override
    public Task getTaskById(Integer taskId) throws IOException {
        if (tasks.containsKey(taskId)) {
            historyManager.add(tasks.get(taskId));
            return tasks.get(taskId);
        } else {
            throw new IOException(String.format("Задача с id=%s не найдена", taskId));
        }
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        historyManager.add(subTasks.get(subTaskId));
        return subTasks.get(subTaskId);
    }

    @Override
    public void deleteTasks() {
        for (Integer key : tasks.keySet()) {
            historyManager.remove(key);
            sortTaskTime.remove(tasks.get(key).getStartTime());
        }
        tasks.clear();
        historyManager.remove(new ArrayList<>(tasks.keySet()));
        tasks.clear();
    }

    @Override
    public void deleteTaskById(Integer taskId) {
        if (tasks.containsKey(taskId)) {
            sortTaskTime.remove(tasks.get(taskId).getStartTime());
            tasks.remove(taskId);
            historyManager.remove(taskId);
        }
    }

    @Override
    public void deleteSubTaskById(Integer subTaskId) {
        if (subTasks.containsKey(subTaskId)) {
            int epicId = subTasks.get(subTaskId).getEpicId();
            epics.get(epicId).getSubTasksIdList().remove(subTaskId);
            sortTaskTime.remove(subTasks.get(subTaskId).getStartTime());
            subTasks.remove(subTaskId);
            updateEpicStatus(epicId);
            historyManager.remove(subTaskId);
        }
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

    protected void cleanEpicEntity(Integer epicId) {
        if (epics.containsKey(epicId) && epics.get(epicId).getSubTasksIdList() != null) {
            for (int subTaskId : epics.get(epicId).getSubTasksIdList()) {
                sortTaskTime.remove(subTasks.get(subTaskId).getStartTime());
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
        }
        historyManager.remove(epicId);
    }

    @Override
    public void deleteEpicById(Integer epicId) {
        cleanEpicEntity(epicId);
        epics.remove(epicId);
    }


    @Override
    public void deleteEpics() {
        for (int epicId : epics.keySet()) {
            cleanEpicEntity(epicId);
        }
        epics.clear();
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
    public List<Task> getSubTasksFromEpic(int epicId) {
        return epics.get(epicId)
                .getSubTasksIdList()
                .stream()
                .map(subTasks::get)
                .collect(Collectors.toList());
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
        long newSubtaskCount = epics.get(epicId)
                .getSubTasksIdList()
                .stream()
                .map(subTasks::get)
                .filter(subTask -> subTask.getStatus() == Status.NEW)
                .count();
        long doneSubtaskCount = epics.get(epicId)
                .getSubTasksIdList()
                .stream()
                .map(subTasks::get)
                .filter(subTask -> subTask.getStatus() == Status.DONE)
                .count();
        final List<Integer> subtasksIds = epics.get(epicId).getSubTasksIdList();
        if (newSubtaskCount == subtasksIds.size()) {
            epics.get(epicId).setStatus(Status.NEW);
        } else if (doneSubtaskCount == subtasksIds.size()) {
            epics.get(epicId).setStatus(Status.DONE);
        } else {
            epics.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }

    public void updateEpicExecTime(int epicId) {
        LocalDateTime startEpicTime = null;
        LocalDateTime endEpicTime = null;
        long durationEpic = 0;
        Epic currentEpic = epics.get(epicId);
        for (int checkedSubTaskId : currentEpic.getSubTasksIdList()) {
            SubTask currentSubTask = subTasks.get(checkedSubTaskId);
            if (currentSubTask.getStartTime() != null) {
                if (startEpicTime == null) {
                    startEpicTime = currentSubTask.getStartTime();
                    endEpicTime = currentSubTask.getEndTime();
                }
                if (startEpicTime != null && currentSubTask.getStartTime().isBefore(startEpicTime)) {
                    startEpicTime = currentSubTask.getStartTime();
                }
                if (endEpicTime != null && currentSubTask.getEndTime().isAfter(endEpicTime)) {
                    endEpicTime = currentSubTask.getEndTime();
                }
                durationEpic += durationEpic + currentSubTask.getDuration();
            }
        }
        currentEpic.setDuration(durationEpic);
        if (startEpicTime != null) {
            currentEpic.setStartTime(startEpicTime);
            currentEpic.setEndTime(startEpicTime.plusMinutes(durationEpic));
        }
    }


    @Override
    public List<String> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new LinkedList<>(sortTaskTime.values());
    }
}
