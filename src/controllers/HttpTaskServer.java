package controllers;

import HttpTaskHandlers.HttpEpicHandler;
import HttpTaskHandlers.HttpTasksHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;

import static model.Status.NEW;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void start() throws IOException {
        TaskManager tm = new InMemoryTaskManager();
        int id = tm.addTask(new Task("Task1 test name", "Task1 test description", NEW, 5));
        int idEpic = tm.addEpic(new Epic("Epic test name", "Epic test description"));
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new HttpTasksHandler(tm));
        httpServer.createContext("/epics", new HttpEpicHandler(tm));
        httpServer.start();
    }

    public static void main(String[] args) throws IOException {
        start();
    }
}