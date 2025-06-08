package HttpTaskHandlers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controllers.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class BaseHttpHandler implements HttpHandler {
    protected TaskManager tm;

    public BaseHttpHandler(TaskManager tm) {
        this.tm = tm;
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

    enum Endpoint {GET_ALL_ENTITY, GET_ENTITY_BY_ID, CREATE_ENTITY, UPDATE_ENTITY, DELETE_ENTITY, UNKNOWN}

    class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
                return LocalDateTime.parse(json.getAsString(), formatter);
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
        }
    }

    class DurationAdapter implements JsonDeserializer<Duration> {
        @Override
        public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Duration.ofMinutes(json.getAsLong());
        }
    }


}