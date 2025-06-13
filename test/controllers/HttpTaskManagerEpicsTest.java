package controllers;

import Adapters.DurationDeserializer;
import Adapters.DurationSerializer;
import Adapters.LocalDateTimeDeserializer;
import Adapters.LocalDateTimeSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Epic;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerEpicsTest {

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
    public void addEpicsTest() throws IOException, InterruptedException {
        String epicJson = "{\"name\":\"Test epic1 name\", \"description\":\"Test epic1 description\"}";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем тест, отвечающий за создание эпиков
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        // проверяем, что создался один эпик с корректным именем
        List<Epic> epicsFromManager = tm.getEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test epic1 name", epicsFromManager.get(0).getName(), "Некорректное имя эпика");
        assertEquals("Test epic1 description", epicsFromManager.get(0).getDescription(), "Некорректное описание эпика");
    }

    @Test
    public void getEpicByIdTest() throws IOException, InterruptedException {
        String epicJson = "{\"name\":\"Test epic1 name\", \"description\":\"Test epic1 description\"}";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        // создали эпик
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // извлекаем id созданного эпика из ответа
        int id = extractId(response.body());
        assertEquals(201, response.statusCode());

        // проверка запроса эпика с id
        url = URI.create(String.format("http://localhost:8080/epics/%s", id));
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task updatedEpicFromTM = taskDeserializer.fromJson(response.body(), Epic.class);

        assertEquals("Test epic1 name", updatedEpicFromTM.getName());
        assertEquals("Test epic1 description", updatedEpicFromTM.getDescription());

        // проверка запроса несуществующей задачи
        url = URI.create(String.format("http://localhost:8080/tasks/%s", 1000));
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void getSubtaskFromEpic() throws IOException, InterruptedException {
        List<Task> requestedTask = new ArrayList<>();

        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic1 description");
        final int epicId1 = tm.addEpic(epic1);
        SubTask subTask1 = new SubTask("Test addNewSubTask1", "Test addNewTask1 description", NEW, epicId1, 5);
        final int subTaskId1 = tm.addSubTask(subTask1);
        subTask1.setStartTime(LocalDateTime.now().plusMinutes(5));
        SubTask subTask2 = new SubTask("Test addNewSubTask", "Test addNewTask2 description", NEW, epicId1, 5);
        subTask2.setStartTime(LocalDateTime.now().plusMinutes(15));
        final int subTaskId2 = tm.addSubTask(subTask2);

        // проверка запроса сабтасок из эпика с id
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(String.format("http://localhost:8080/epics/%s/subtasks", epicId1));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }


    @Test
    public void deleteEpicTest() throws IOException, InterruptedException {
        String epicJson = "{\"name\":\"Test epic1 name\", \"description\":\"Test epic1 description\"}";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        // создали эпик
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // извлекаем id созданного эпика из ответа
        int id = extractId(response.body());
        assertEquals(201, response.statusCode());

        // удаление эпика с id
        url = URI.create(String.format("http://localhost:8080/epics/%s", id));
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        // проверка запроса уже несуществующей задачи
        url = URI.create(String.format("http://localhost:8080/tasks/%s", id));
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }


}