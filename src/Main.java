import data.*;

public class Main {

    public static void main(String[] args) {
        TaskManager tm = new TaskManager();
        Task task1 = new Task(1, "задача№1", "деплой", Status.NEW);
        Task task2 = new Task(2, "задача№2", "тест", Status.NEW);
        tm.addTask(task1);
        tm.addTask(task2);
        System.out.println("Задачи:");
        System.out.println(tm.getTasks());

        Epic epic1 = new Epic(3, "Эпик1", "Большой эпик");
        Epic epic2 = new Epic(4, "Эпик2", "Очень большой эпик");
        tm.addEpic(epic1);
        tm.addEpic(epic2);
        System.out.println("Эпики:");
        System.out.println(tm.getEpics());

        SubTask subTask1 = new SubTask(5, "Подзадача 1", "Описание подзадачи 1", Status.NEW, 3);
        SubTask subTask2 = new SubTask(6, "Подзадача 2", "Описание подзадачи 2", Status.NEW, 3);
        SubTask subTask3 = new SubTask(7, "Подзадача 3", "Описание подзадачи 3", Status.NEW, 4);

        tm.addSubTask(subTask1);
        tm.addSubTask(subTask2);
        tm.addSubTask(subTask3);

        System.out.println("Сабтаски:");
        System.out.println(tm.getSubTasksFromEpic(3));
        System.out.println(tm.getSubTasksFromEpic(4));

        Task editedTask = new Task(1, "Имя задачи изменено", "Описание задачи изменено", Status.IN_PROGRESS);
        tm.updateTask(editedTask);

        System.out.println(tm.getTasks());

        SubTask editedSubTask1 = new SubTask(5, "Подзадача 1 изм", "Описание подзадачи 1", Status.DONE, 3);
        SubTask editedSubTask2 = new SubTask(6, "Подзадача 2 изм ", "Описание подзадачи изменено", Status.DONE, 3);
        SubTask editedSubTask3 = new SubTask(7, "Подзадача 3 изм", "Описание подзадачи изменено", Status.IN_PROGRESS, 4);

        tm.updateSubTask(editedSubTask1);
        tm.updateSubTask(editedSubTask2);
        tm.updateSubTask(editedSubTask3);

        System.out.println(tm.getEpics());

        tm.deleteTaskById(2);
        System.out.println(tm.getTasks());

        System.out.println("Сабтаск удален");
        tm.deleteSubTaskById(5);
        System.out.println(tm.getSubTasks());
        System.out.println(tm.getEpics());

        System.out.println("Эпик удален");
        tm.deleteEpicById(3);
        System.out.println(tm.getEpics());
        System.out.println(tm.getSubTasks());


    }
}
