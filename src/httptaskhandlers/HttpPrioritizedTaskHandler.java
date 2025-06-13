package httptaskhandlers;

import com.sun.net.httpserver.HttpExchange;
import controllers.TaskManager;

import java.io.IOException;

public class HttpPrioritizedTaskHandler extends BaseHttpHandler {

    public HttpPrioritizedTaskHandler(TaskManager tm) {
        super(tm);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, taskSerializer.toJson(tm.getPrioritizedTasks()), 200);
    }
}


