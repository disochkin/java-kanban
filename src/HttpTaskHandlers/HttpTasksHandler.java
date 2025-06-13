package HttpTaskHandlers;

import Adapters.DurationDeserializer;
import Adapters.DurationSerializer;
import Adapters.LocalDateTimeDeserializer;
import Adapters.LocalDateTimeSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import model.Endpoint;
import model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class HttpTasksHandler extends BaseHttpHandler {
    Gson taskDeserializer;
    Gson taskSerializer;
    public HttpTasksHandler(TaskManager tm) {
        super(tm);
        taskDeserializer = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(Duration.class, new DurationDeserializer())
                .create();
        taskSerializer = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(Duration.class, new DurationSerializer())
                .create();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());

        switch (endpoint) {
            case GET_ALL_ENTITY: {
                handleGetAllTasks(httpExchange);
                break;
            }
            case GET_ENTITY_BY_ID: {
                handleGetTaskById(httpExchange);
                break;
            }
            case UPDATE_ENTITY: {
                handleUpdateTaskById(httpExchange);
                break;
            }
            case CREATE_ENTITY: {
                handleCreateTask(httpExchange);
                break;
            }
            case DELETE_ENTITY: {
                handleDeleteTask(httpExchange);
                break;
            }
            default:
                sendText(httpExchange, "Такого эндпоинта не существует", 405);
        }
    }

    private void handleGetAllTasks(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange,  taskSerializer.toJson(tm.getTasks()), 200);
    }

    private void handleGetTaskById(HttpExchange httpExchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(httpExchange);
        if (taskIdOpt.isEmpty()) {
            sendText(httpExchange, "Некорректный идентификатор задачи", 404);
            return;
        }
        try {
            int taskId = taskIdOpt.get();
            sendText(httpExchange, taskSerializer.toJson(tm.getTaskById(taskId)), 200);
        } catch (IOException e) {
            sendText(httpExchange, e.toString(), 404);
        }
    }

    private void handleUpdateTaskById(HttpExchange httpExchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(httpExchange);
        if (taskIdOpt.isEmpty()) {
            sendText(httpExchange, "Некорректный идентификатор задачи", 201);
            return;
        }
        try {
        int taskId = taskIdOpt.get();
        Task taskDTO = taskDeserializer.fromJson(getJsonBody(httpExchange), Task.class);
        tm.updateTask(taskId, taskDTO);
        sendText(httpExchange, "Задача обновлена", 201);}
        catch (IOException e) {
            sendText(httpExchange, e.toString(), 406);
        }
    }

    private void handleCreateTask(HttpExchange httpExchange) throws IOException {
        try {
            Task taskDTO = taskDeserializer.fromJson(getJsonBody(httpExchange), Task.class);
            int taskId = tm.addTask(taskDTO);
            sendText(httpExchange, String.format("Задача создана. id=%s", taskId), 201);
        } catch (Exception e) {
            sendText(httpExchange, e.toString(), 406);
        }
    }

    private void handleDeleteTask(HttpExchange httpExchange) throws IOException {
        Optional<Integer> taskIdOpt = getTaskId(httpExchange);
        if (taskIdOpt.isEmpty()) {
            sendText(httpExchange, "Некорректный идентификатор задачи", 404);
            return;
        }
        int taskId = taskIdOpt.get();
        try {
            tm.deleteTaskById(taskId);
            sendText(httpExchange, String.format("Задача удалена. id=%s", taskId), 200);
        } catch (IOException e) {
            sendText(httpExchange, e.toString(), 502);
        }
    }

    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[pathParts.length - 1]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}


