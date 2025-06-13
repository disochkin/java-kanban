package HttpTaskHandlers;
import model.Endpoint;
import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;
import model.Epic;

import java.io.IOException;
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
            case GET_ALL_SUBENTITIES: {
                handleGetSubTaskFromEpic(httpExchange);
                break;
            }
            default:
                sendText(httpExchange, "Такого эндпоинта не существует", 405);
        }
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2 && requestMethod.equals("GET")) {
            return Endpoint.GET_ALL_ENTITY;
        }
        if (pathParts.length == 3 && requestMethod.equals("GET")) {
            return Endpoint.GET_ENTITY_BY_ID;
        }
        if (pathParts.length == 2 && requestMethod.equals("POST")) {
            return Endpoint.CREATE_ENTITY;
        }
        if (pathParts.length == 3 && requestMethod.equals("POST")) {
            return Endpoint.UPDATE_ENTITY;
        }
        if (pathParts.length == 3 && requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_ENTITY;
        }
        if (pathParts.length == 4 && requestMethod.equals("GET")) {
            return Endpoint.GET_ALL_SUBENTITIES;}
        return Endpoint.UNKNOWN;
    }

    private void handleGetAllEpics(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange,  taskSerializer.toJson(tm.getEpics()), 200);
    }

    private void handleGetEpicById(HttpExchange httpExchange) throws IOException {
        Optional<Integer> epicIdOpt = getEpicId(httpExchange);
        if (epicIdOpt.isEmpty()) {
            sendText(httpExchange, "Некорректный идентификатор эпика", 404);
            return;
        }
        try {
            int epicId = epicIdOpt.get();
            sendText(httpExchange,  taskSerializer.toJson(tm.getEpicById(epicId)), 200);
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
        Epic epicDTO = taskDeserializer.fromJson(getJsonBody(httpExchange), Epic.class);
        tm.updateEpic(epicId, epicDTO);
        sendText(httpExchange, "Эпик обновлен", 200);
    }

    private void handleCreateEpic(HttpExchange httpExchange) throws IOException {
        try {
            Epic epicDTO = taskDeserializer.fromJson(getJsonBody(httpExchange), Epic.class);
            int epicId = tm.addEpic(epicDTO);
            sendText(httpExchange, String.format("Эпик создан. id=%s", epicId), 201);
        } catch (IOException e) {
            sendText(httpExchange, e.toString(), 406);
        }
    }

    private void handleDeleteEpic(HttpExchange httpExchange) throws IOException {
        Optional<Integer> epicIdOpt = getEpicId(httpExchange);
        if (epicIdOpt.isEmpty()) {
            sendText(httpExchange, "Некорректный идентификатор эпика", 404);
            return;
        }
        int epicId = epicIdOpt.get();
        try {
            tm.deleteEpicById(epicId);
            sendText(httpExchange, String.format("Задача удалена. id=%s", epicId), 200);
        } catch (IOException e) {
            sendText(httpExchange, e.toString(), 502);
        }
    }

    private void handleGetSubTaskFromEpic(HttpExchange httpExchange) throws IOException {
        Optional<Integer> epicIdOpt = getEpicId(httpExchange);
        if (epicIdOpt.isEmpty()) {
            sendText(httpExchange, "Некорректный идентификатор эпика", 404);
            return;
        }
        int epicId = epicIdOpt.get();
        try {
            sendText(httpExchange, taskSerializer.toJson(tm.getSubTasksFromEpic(epicId)), 200);
        } catch (IOException e) {
            sendText(httpExchange, e.toString(), 502);
        }
    }

    private Optional<Integer> getEpicId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}




