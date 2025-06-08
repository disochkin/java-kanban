package controllers;

import model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;


public class FileBackedTaskManager extends InMemoryTaskManager {

    final String rowDelimiter = System.lineSeparator();
    final char columnDelimiter = ',';
    final File file;
    private final String[] csvFileHeader = {"id", "type", "name", "status", "description", "duration", "startTime", "epic"};
    private ManagerSaveException e;
    private BufferedWriter writer;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int lastId = 0;
            List<String> fileRows = new ArrayList<>();
            while (br.ready()) {
                fileRows.add(br.readLine());
            }
            for (int i = 1; i < fileRows.size(); i++) {
                HashMap<String, String> properties = fileBackedTaskManager.mapCsvRowToProperties(fileRows.get(i));
                switch (TypeTask.valueOf(properties.get("type"))) {
                    case EPIC: {
                        lastId = fileBackedTaskManager.restoreEpic(properties);
                        break;
                    }
                    case TASK: {
                        lastId = fileBackedTaskManager.restoreTask(properties);
                        break;
                    }
                    case SUBTASK: {
                        lastId = fileBackedTaskManager.restoreSubTask(properties);
                        break;
                    }
                }
                if (lastId > fileBackedTaskManager.generatorId) {
                    fileBackedTaskManager.generatorId = lastId;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в  файл: " + e.getMessage());
        }
        return fileBackedTaskManager;
    }

    private HashMap<String, String> mapCsvRowToProperties(String row) {
        HashMap<String, String> properties = new HashMap<>();
        for (int index = 0; index < csvFileHeader.length; index++) {
            properties.put(csvFileHeader[index], row.split(Character.toString(columnDelimiter), -1)[index]);
        }
        return properties;
    }

    private void storeToFile(String row) throws IOException {
        writer.write(row + rowDelimiter);
        writer.flush();
    }

    public void save() {
        try {
            writer = new BufferedWriter(new FileWriter(file, false));
            storeToFile(String.join(Character.toString(columnDelimiter), csvFileHeader));
            for (Task task : getTasks()) {
                storeToFile(task.getCsvRow(columnDelimiter));
            }
            for (Task epic : getEpics()) {
                storeToFile(epic.getCsvRow(columnDelimiter));
                for (Task subTask : getSubTasksFromEpic(epic.getId())) {
                    storeToFile(subTask.getCsvRow(columnDelimiter));
                }
            }
            writer.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в  файл: " + e.getMessage());
        }
    }

    private void addId(Task task, Integer id) {
        task.setId(id);
    }

    public int restoreTask(HashMap<String, String> properties) {
        Task task = new Task(parseInt(properties.get("id")), properties.get("name"), properties.get("description"),
                Status.valueOf(properties.get("status")), parseInt(properties.get("duration")));
        task.setStartTime(LocalDateTime.parse(properties.get("startTime")));
        tasks.put(parseInt(properties.get("id")), task);
        return parseInt(properties.get("id"));
    }

    public int restoreEpic(HashMap<String, String> properties) {
        Epic epic = new Epic(parseInt(properties.get("id")), properties.get("name"), properties.get("description"),
                Status.valueOf(properties.get("status")), parseInt(properties.get("duration")));
        epics.put(parseInt(properties.get("id")), epic);
        epic.setStartTime(LocalDateTime.parse(properties.get("startTime")));
        return parseInt(properties.get("id"));
    }

    public int restoreSubTask(HashMap<String, String> properties) {
        SubTask subTask = new SubTask(parseInt(properties.get("id")), properties.get("name"), properties.get("description"),
                Status.valueOf(properties.get("status")), parseInt(properties.get("epic")),
                LocalDateTime.parse(properties.get("startTime")), parseInt(properties.get("duration")));
        subTasks.put(parseInt(properties.get("id")), subTask);
        return parseInt(properties.get("id"));
    }


    public int addTask(Task newTask) throws IOException {
        super.addTask(newTask);
        save();
        return newTask.getId();
    }

    @Override
    public int addSubTask(SubTask newSubTask) throws IOException {
        super.addSubTask(newSubTask);
        save();
        return newSubTask.getId();
    }

    @Override
    public int addEpic(Epic newEpic) {
        super.addEpic(newEpic);
        save();
        return newEpic.getId();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteTaskById(Integer taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteSubTaskById(Integer subTaskId) {
        super.deleteSubTaskById(subTaskId);
        save();
    }

    @Override
    public void deleteEpicById(Integer epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }
}
