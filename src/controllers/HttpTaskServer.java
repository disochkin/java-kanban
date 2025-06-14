package controllers;

import com.sun.net.httpserver.HttpServer;
import httptaskhandlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final int port = 8080;
    private final TaskManager tm;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager tm) {
        this.tm = tm;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.start();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(port), 0);
        httpServer.createContext("/tasks", new HttpTasksHandler(tm));
        httpServer.createContext("/epics", new HttpEpicHandler(tm));
        httpServer.createContext("/subtasks", new HttpSubTaskHandler(tm));
        httpServer.createContext("/history", new HttpHistoryHandler(tm));
        httpServer.createContext("/prioritized", new HttpPrioritizedTaskHandler(tm));

        httpServer.start();
        System.out.println("Server STARTED on port: " + port);
    }

    public void stop() {
        httpServer.stop(1);
        System.out.println("Server STOPPED on port: " + port);
    }
}