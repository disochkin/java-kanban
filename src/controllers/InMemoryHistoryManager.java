package controllers;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;


public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> taskLinkedMapIndex = new HashMap<>();
    private Node<Task> firstItem;
    private Node<Task> lastItem;


    private void linkLast(Node<Task> newNode) {
        if (lastItem == null)
            firstItem = lastItem = newNode;
        else {
            lastItem.next = newNode;
            newNode.prev = lastItem;
            lastItem = newNode;
        }
    }

    private void removeNode(Node<Task> node) {
        if (node.prev != null && node.next != null) {
            Node<Task> previousNode = node.prev;
            Node<Task> nextNode = node.next;
            previousNode.next = nextNode;
            nextNode.prev = previousNode;
        } else if (node.prev == null && node.next == null) {
            firstItem = lastItem = null;
        } else if (node.next == null) {
            Node<Task> previousNode = node.prev;
            previousNode.next = null;
            lastItem = previousNode;
        } else {
            Node<Task> nextNode = node.next;
            nextNode.prev = null;
            firstItem = nextNode;
        }
    }

    @Override
    public List<String> getHistory() {
        List<String> flatList = new ArrayList<>();
        Node<Task> current = firstItem;
        while (current != null) {
            flatList.add(current.data.toString());
            current = current.next;
        }
        return flatList;
    }

    @Override
    public void add(Task viewedTask) {
        if (!isNull(viewedTask)) {
            int id = viewedTask.getId();
            if (taskLinkedMapIndex.containsKey(id)) {
                removeNode(taskLinkedMapIndex.get(id));
            }
            Node<Task> newNode = new Node<>(null, viewedTask, null);
            linkLast(newNode);
            taskLinkedMapIndex.put(id, newNode);
        }
    }

    @Override
    public void remove(int id) {
        if (taskLinkedMapIndex.containsKey(id)) {
            removeNode(taskLinkedMapIndex.get(id));
            taskLinkedMapIndex.remove(id);
        }
    }

    @Override
    public void remove(List<Integer> listIdToRemove) {
        for (int idToRemove : listIdToRemove) {
            remove(idToRemove);
        }
    }

    static class Node<T> {

        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}

