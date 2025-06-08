package HttpTaskHandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import model.Epic;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class HttpEpicHandler extends BaseHttpHandler {
    public HttpEpicHandler(TaskManager tm) {
        super(tm);

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());
        switch (endpoint) {
            case GET_ALL_ENTITY: {
                handleGetAllEpics(httpExchange);
                break;
            }
            case GET_ENTITY_BY_ID: {
                handleGetEpicById(httpExchange);
                break;
            }
            case UPDATE_ENTITY: {
                handleUpdateEpicById(httpExchange);
                break;
            }
            case CREATE_ENTITY: {
                handleCreateEpic(httpExchange);
                break;
            }
            case DELETE_ENTITY: {
                handleDeleteEpic(httpExchange);
                break;
            }
            default:
                sendText(httpExchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetAllEpics(HttpExchange httpExchange) throws IOException {
        String text = tm.getEpics().toString();
        sendText(httpExchange, text, 200);
    }

    private void handleGetEpicById(HttpExchange httpExchange) throws IOException {
        Optional<Integer> epicIdOpt = getEpicId(httpExchange);
        if (epicIdOpt.isEmpty()) {
            sendText(httpExchange, "Некорректный идентификатор эпика", 404);
            return;
        }
        try {
            int epicId = epicIdOpt.get();
            String text = tm.getEpicById(epicId).toString();
            sendText(httpExchange, text, 200);
        } catch (IOException e) {
            sendText(httpExchange, e.toString(), 404);
        }
    }

    private void handleUpdateEpicById(HttpExchange httpExchange) throws IOException {
        Optional<Integer> epicIdOpt = getEpicId(httpExchange);
        if (epicIdOpt.isEmpty()) {
            sendText(httpExchange, "Некорректный идентификатор эпика", 201);
            return;
        }
        int epicId = epicIdOpt.get();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        Epic epicDTO = gson.fromJson(getJsonBody(httpExchange), Epic.class);
        tm.updateTask(epicId, epicDTO);
        sendText(httpExchange, "Эпик обновлен", 200);
    }

    private void handleCreateEpic(HttpExchange httpExchange) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        try {
            Epic epicDTO = gson.fromJson(getJsonBody(httpExchange), Epic.class);
            int taskId = tm.addTask(epicDTO);
            sendText(httpExchange, String.format("Эпик создан. id=%s", taskId), 200);
        } catch (IOException e) {
            sendText(httpExchange, e.toString(), 406);
        }
    }

    private void handleDeleteEpic(HttpExchange httpExchange) throws IOException {
        Optional<Integer> epicIdOpt = getEpicId(httpExchange);
        if (epicIdOpt.isEmpty()) {
            sendText(httpExchange, "Некорректный идентификатор задачи", 404);
            return;
        }
        int epicId = epicIdOpt.get();
        try {
            tm.deleteTaskById(epicId);
            sendText(httpExchange, String.format("Задача удалена. id=%s", epicId), 200);
        } catch (IOException e) {
            sendText(httpExchange, e.toString(), 502);
        }
    }

    private Optional<Integer> getEpicId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[pathParts.length - 1]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}




