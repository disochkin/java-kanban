package controllers;

import Adapters.DurationDeserializer;
import Adapters.DurationSerializer;
import Adapters.LocalDateTimeDeserializer;
import Adapters.LocalDateTimeSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Status;
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

public class HttpTaskManagerTasksTest {

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
    public void addTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now(), 5);
        String taskJson = taskSerializer.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем тест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = tm.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");

        // повторно добавляем задачу
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void getTaskByIdTest() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now(), 5);
        String taskJson = taskSerializer.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        // создали задачу
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // извлекаем id созданной задачи из ответа
        int id = extractId(response.body());
        assertEquals(201, response.statusCode());

        // проверка запроса задачи с id
        url = URI.create(String.format("http://localhost:8080/tasks/%s", id));
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task updatedTaskFromTM = taskDeserializer.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(task.getName(), updatedTaskFromTM.getName());
        assertEquals(task.getDescription(), updatedTaskFromTM.getDescription());
        assertEquals(task.getStartTime(), updatedTaskFromTM.getStartTime());
        assertEquals(task.getStatus(), updatedTaskFromTM.getStatus());
        assertEquals(task.getDuration(), updatedTaskFromTM.getDuration());

        // проверка запроса несуществующей задачи
        url = URI.create(String.format("http://localhost:8080/tasks/%s", 1000));
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now(), 5);
        String taskJson = taskSerializer.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        // создали задачу
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // извлекаем id созданной задачи из ответа
        int id = extractId(response.body());
        assertEquals(201, response.statusCode());

        // удаление задачи с id
        url = URI.create(String.format("http://localhost:8080/tasks/%s", id));
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверка запроса уже несуществующей задачи
        url = URI.create(String.format("http://localhost:8080/tasks/%s", id));
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void updateTaskTest() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, LocalDateTime.now(), 5);
        String taskJson = taskSerializer.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        // создали задачу
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // извлекаем id созданной задачи из ответа
        int id = extractId(response.body());
        assertEquals(201, response.statusCode());

        //создали обновленную задачу
        Task updatedTask = new Task("UPD Test 2", "UPD Testing task 2", Status.NEW, task.getStartTime().plusMinutes(10),
                10);
        taskJson = taskSerializer.toJson(updatedTask);
        url = URI.create(String.format("http://localhost:8080/tasks/%s", id));
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        // запрашиваем обновленную задачу
        url = URI.create(String.format("http://localhost:8080/tasks/%s", id));
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task updatedTaskFromTM = taskDeserializer.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(updatedTask.getName(), updatedTaskFromTM.getName());
        assertEquals(updatedTask.getDescription(), updatedTaskFromTM.getDescription());
        assertEquals(updatedTask.getStartTime(), updatedTaskFromTM.getStartTime());
        assertEquals(updatedTask.getStatus(), updatedTaskFromTM.getStatus());
        assertEquals(updatedTask.getDuration(), updatedTaskFromTM.getDuration());
    }

}