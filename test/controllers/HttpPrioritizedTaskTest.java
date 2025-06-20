package controllers;

import adapters.DurationDeserializer;
import adapters.DurationSerializer;
import adapters.LocalDateTimeDeserializer;
import adapters.LocalDateTimeSerializer;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpPrioritizedTaskTest {
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
    public void prioritizedTaskTest() throws IOException, InterruptedException {
        LocalDateTime currentTime = LocalDateTime.parse("2020-01-01T00:00:00.000000000");

        Task task1 = new Task("Test 1", "Testing task 1", Status.NEW, currentTime, 5);
        String taskJson1 = taskSerializer.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson1)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int id1 = extractId(response.body());

        Task task2 = new Task("Test 2", "Testing task 2", Status.NEW, currentTime.plusMinutes(10), 5);
        String taskJson2 = taskSerializer.toJson(task2);
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson2)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int id2 = extractId(response.body());

        // добавим задачу без указания времени начала
        Task task3 = new Task("Test 3", "Testing task 3", Status.NEW, 5);
        String taskJson3 = taskSerializer.toJson(task3);
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson3)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int id3 = extractId(response.body());


        // запрашиваем список приоритетных задач
        // задача 3 в список не попала
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/prioritized");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("[{\"name\":\"Test 1\",\"description\":\"Testing task 1\",\"status\":\"NEW\",\"type\":\"TASK\",\"duration\":5,\"startTime\":\"2020-01-01T00:00:00.000000000\",\"id\":1}," +
                        "{\"name\":\"Test 2\",\"description\":\"Testing task 2\",\"status\":\"NEW\",\"type\":\"TASK\",\"duration\":5,\"startTime\":\"2020-01-01T00:10:00.000000000\",\"id\":2}]",
                response.body());

        //удаляем задачу 2
        client = HttpClient.newHttpClient();
        url = URI.create(String.format("http://localhost:8080/tasks/%s", id2));
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(String.format("Задача удалена. id=%s", id2), response.body());

        //снова запрашиваем список приоритетных задач
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/prioritized");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("[{\"name\":\"Test 1\",\"description\":\"Testing task 1\",\"status\":\"NEW\",\"type\":\"TASK\",\"duration\":5,\"startTime\":\"2020-01-01T00:00:00.000000000\",\"id\":1}]",
                response.body());
    }
}
