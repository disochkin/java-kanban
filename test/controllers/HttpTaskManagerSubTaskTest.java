package controllers;

import Adapters.DurationDeserializer;
import Adapters.DurationSerializer;
import Adapters.LocalDateTimeDeserializer;
import Adapters.LocalDateTimeSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerSubTaskTest {

    Gson taskDeserializer;
    Gson taskSerializer;
    HttpTaskServer httpTaskServer;
    TaskManager tm;

    public static Integer extractId(String input) {
        if (input == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("id=([\\d]+)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    @BeforeEach
    public void setUp() throws IOException {
        tm = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(tm);
        httpTaskServer.start();
        taskDeserializer = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(Duration.class, new DurationDeserializer())
                .create();
        taskSerializer = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(Duration.class, new DurationSerializer())
                .create();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void addSubTaskTest() throws IOException, InterruptedException {
        // создаем эпик для теста
        String epicJson = "{\"name\":\"Test epic1 name\", \"description\":\"Test epic1 description\"}";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        int epicId = extractId(response.body());
        LocalDateTime currentTime = LocalDateTime.now();
        SubTask subTask1 = new SubTask(0, "Test Subtask1", "Testing Subtask1 descr", Status.NEW, epicId, currentTime, 5);
        String subTaskJson1 = taskSerializer.toJson(subTask1);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson1)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());


        SubTask subTask2 = new SubTask(0, "Test Subtask2", "Testing Subtask2 descr", Status.NEW, epicId, currentTime.plusMinutes(30), 5);
        String subTaskJson2 = taskSerializer.toJson(subTask2);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson2)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());


        // проверяем, что создалась сабтаски
        List<Task> subTasksFromManager = tm.getSubTasks();
        assertNotNull(subTasksFromManager, "Сабтаски не возвращаются");

        assertEquals(2, subTasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test Subtask1", subTasksFromManager.get(0).getName(), "Некорректное имя задачи");
        assertEquals("Testing Subtask1 descr", subTasksFromManager.get(0).getDescription(), "Некорректное описание сабтаски");
        assertEquals(Status.NEW, subTasksFromManager.get(0).getStatus(), "Некорректный статус сабтаски");
        assertEquals(currentTime, subTasksFromManager.get(0).getStartTime(), "Некорректное время начала сабтаски");
        assertEquals(5, subTasksFromManager.get(0).getDuration(), "Некорректная продолжительность сабтаски");

        assertEquals("Test Subtask2", subTasksFromManager.get(1).getName(), "Некорректное имя задачи");
        assertEquals("Testing Subtask2 descr", subTasksFromManager.get(1).getDescription(), "Некорректное описание сабтаски");
        assertEquals(Status.NEW, subTasksFromManager.get(1).getStatus(), "Некорректный статус сабтаски");
        assertEquals(currentTime.plusMinutes(30), subTasksFromManager.get(1).getStartTime(), "Некорректное время начала сабтаски");
        assertEquals(5, subTasksFromManager.get(1).getDuration(), "Некорректная продолжительность сабтаски");

        // повторно добавляем сабтаску
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void deleteSubTaskTest() throws IOException, InterruptedException {
        // создаем эпик для теста
        String epicJson = "{\"name\":\"Test epic1 name\", \"description\":\"Test epic1 description\"}";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        int epicId = extractId(response.body());
        LocalDateTime currentTime = LocalDateTime.now();
        SubTask subTask1 = new SubTask(0, "Test Subtask1", "Testing Subtask1 descr", Status.NEW, epicId, currentTime, 5);
        String subTaskJson1 = taskSerializer.toJson(subTask1);
        url = URI.create("http://localhost:8080/subtasks");
        HttpRequest requestAddSubTask = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson1)).build();
        HttpResponse<String> responseAddSubTask = client.send(requestAddSubTask, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        int subTaskId = extractId(responseAddSubTask.body());

        // удаляем сабтаску
        url = URI.create(String.format("http://localhost:8080/subtasks/%s", subTaskId));
        HttpRequest requestToDelete = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> responseToDelete = client.send(requestToDelete, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseToDelete.statusCode());
        assertEquals(String.format("Подзадача удалена. id=%s", subTaskId), responseToDelete.body());

    }
}