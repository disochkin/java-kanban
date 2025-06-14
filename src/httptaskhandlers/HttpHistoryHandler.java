package httptaskhandlers;

import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;

import java.io.IOException;

public class HttpHistoryHandler extends BaseHttpHandler {

    public HttpHistoryHandler(TaskManager tm) {
        super(tm);

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, taskSerializer.toJson(tm.getHistory()), 200);
    }
}
