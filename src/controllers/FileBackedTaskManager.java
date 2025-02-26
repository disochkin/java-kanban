package controllers;

import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;

public class FileBackedTaskManager extends InMemoryTaskManager {

    final String rowDelimiter = System.lineSeparator();
    final char columnDelimiter = ',';
    final File file;
    String[] csvFileHeader = {"id", "type", "name", "status", "description", "epic"};
    ManagerSaveException e;
    private BufferedWriter writer;


    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
        if (file.exists() && !file.isDirectory()) {
            loadFromFile();
        }
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
                System.out.println("Произошла ошибка записи в файл.");
            }
        } catch (ManagerSaveException e) {
            System.out.println("Произошла ошибка записи в файл. Ошибка: " + e.getMessages());
        }

    }

    private void addId(Task task, Integer id) {
        task.setId(id);
    }


    private void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int lastId = 0;
            List<String> fileRows = new ArrayList<>();
            while (br.ready()) {
                fileRows.add(br.readLine());
            }
            for (int i = 1; i < fileRows.size(); i++) {
                HashMap<String, String> properties = mapCsvRowToProperties(fileRows.get(i));
                switch (typeTask.valueOf(properties.get("type"))) {
                    case EPIC: {
                        lastId = restoreEpic(properties);
                        break;
                    }
                    case TASK: {
                        lastId = restoreTask(properties);
                        break;
                    }
                    case SUBTASK: {
                        lastId = restoreSubTask(properties);
                        break;
                    }
                }
                if (lastId > generatorId) {
                    generatorId = lastId;
                }
            }
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (NumberFormatException ex) {
            System.out.println(ex);
        }
    }

    public int restoreTask(HashMap<String, String> properties) {
        Task task = new Task(parseInt(properties.get("id")), properties.get("name"), properties.get("description"), Status.valueOf(properties.get("status")));
        tasks.put(parseInt(properties.get("id")), task);
        return parseInt(properties.get("id"));
    }

    public int restoreEpic(HashMap<String, String> properties) {
        Epic epic = new Epic(parseInt(properties.get("id")), properties.get("name"), properties.get("description"), Status.valueOf(properties.get("status")));
        epics.put(parseInt(properties.get("id")), epic);
        return parseInt(properties.get("id"));
    }

    public int restoreSubTask(HashMap<String, String> properties) {
        SubTask subTask = new SubTask(parseInt(properties.get("id")), properties.get("name"), properties.get("description"),
                Status.valueOf(properties.get("status")), parseInt(properties.get("epic")));
        subTasks.put(parseInt(properties.get("id")), subTask);
        return parseInt(properties.get("id"));
    }

    public int addTask(Task newTask) {
        super.addTask(newTask);
        save();
        return newTask.getId();
    }

    @Override
    public int addSubTask(SubTask newSubTask) {
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
