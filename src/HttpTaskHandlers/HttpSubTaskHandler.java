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
import model.SubTask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class HttpSubTaskHandler extends BaseHttpHandler {
    Gson taskDeserializer;
    Gson taskSerializer;

    public HttpSubTaskHandler(TaskManager tm) {
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
                handleGetAllSubTasks(httpExchange);
                break;
            }
            case GET_ENTITY_BY_ID: {
                handleGetSubTaskById(httpExchange);
                break;
            }
            case UPDATE_ENTITY: {
                handleUpdateSubTaskById(httpExchange);
                break;
            }
            case CREATE_ENTITY: {
                handleCreateSubTask(httpExchange);
                break;
            }
            case DELETE_ENTITY: {
                handleDeleteSubTask(httpExchange);
                break;
            }
            default:
                sendText(httpExchange, "Такого эндпоинта не существует", 405);
        }
    }

    private void handleGetAllSubTasks(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, taskSerializer.toJson(tm.getSubTasks()), 200);
    }

    private void handleGetSubTaskById(HttpExchange httpExchange) throws IOException {
        Optional<Integer> subTaskIdOpt = getSubTaskId(httpExchange);
        if (subTaskIdOpt.isEmpty()) {
            sendText(httpExchange, "Некорректный идентификатор подзадачи", 404);
            return;
        }
        try {
            int subTaskId = subTaskIdOpt.get();
            sendText(httpExchange, taskSerializer.toJson(tm.getSubTaskById(subTaskId)), 200);
        } catch (IOException e) {
            sendText(httpExchange, e.toString(), 404);
        }
    }

    private void handleUpdateSubTaskById(HttpExchange httpExchange) throws IOException {
        Optional<Integer> subTaskIdOpt = getSubTaskId(httpExchange);
        if (subTaskIdOpt.isEmpty()) {
            sendText(httpExchange, "Некорректный идентификатор подзадачи", 201);
            return;
        }
        try {
            int subTaskId = subTaskIdOpt.get();
            SubTask taskDTO = taskDeserializer.fromJson(getJsonBody(httpExchange), SubTask.class);
            tm.updateSubTask(subTaskId, taskDTO);
            sendText(httpExchange, String.format("Подзадача обновлена. id=%s", subTaskId), 200);
        } catch (IOException e) {
            sendText(httpExchange, e.toString(), 406);
        }

    }

    private void handleCreateSubTask(HttpExchange httpExchange) throws IOException {
        try {
            SubTask taskDTO = taskDeserializer.fromJson(getJsonBody(httpExchange), SubTask.class);
            int subTaskId = tm.addSubTask(taskDTO);
            sendText(httpExchange, String.format("Подзадача создана. id=%s", subTaskId), 201);
        } catch (IOException e) {
            sendText(httpExchange, e.toString(), 406);
        }
    }

    private void handleDeleteSubTask(HttpExchange httpExchange) throws IOException {
        Optional<Integer> subTaskIdOpt = getSubTaskId(httpExchange);
        if (subTaskIdOpt.isEmpty()) {
            sendText(httpExchange, "Некорректный идентификатор подзадачи", 404);
            return;
        }
        int subTaskId = subTaskIdOpt.get();
        try {
            tm.deleteSubTaskById(subTaskId);
            sendText(httpExchange, String.format("Подзадача удалена. id=%s", subTaskId), 200);
        } catch (IOException e) {
            sendText(httpExchange, e.toString(), 502);
        }
    }

    // TO DO
    private Optional<Integer> getSubTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[pathParts.length - 1]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
