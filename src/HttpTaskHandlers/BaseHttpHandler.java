package HttpTaskHandlers;
import adapters.DurationDeserializer;
import adapters.DurationSerializer;
import adapters.LocalDateTimeDeserializer;
import adapters.LocalDateTimeSerializer;
import com.google.gson.*;
import model.Endpoint;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {
    protected TaskManager tm;
    Gson taskDeserializer;
    Gson taskSerializer;

    public BaseHttpHandler(TaskManager tm) {
        this.tm = tm;
        taskDeserializer = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(Duration.class, new DurationDeserializer())
                .create();
        taskSerializer = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(Duration.class, new DurationSerializer())
                .create();
    }

    protected void sendText(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected String getJsonBody(HttpExchange httpExchange) throws IOException {
        // Читаем тело запроса
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            sb.append(inputLine);
        }
        reader.close();
        return sb.toString();
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
        return Endpoint.UNKNOWN;
    }
}