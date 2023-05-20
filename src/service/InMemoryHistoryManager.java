package service;

import model.Epic;
import model.Issue;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Представитель контракта истории просмотров {@link HistoryManager} задач {@link Task}, {@link SubTask}, {@link Epic}
 * для менеджера, реализующего интерфейс {@link TaskManager}
 */

public class InMemoryHistoryManager implements HistoryManager {

    /**
     *  HashMap - истории просмотров задач
     *  Ключ: id просмотренной задачи
     *  Значением: узел связного списка, хранящего последовательно истории просмотров задач
     */
    private final Map<Integer, Node<Issue>> historyStorage = new HashMap<>();

    /**
     * Связный список для хранения порядка просмотра задач
     * Если какая-либо задача просматривалась несколько раз, то
     * в истории должен отобразиться только последний просмотр.
     */
    private final CustomLinkedList<Issue> historyQueue = new CustomLinkedList<>();

    /**
     * Добавить задачу в конец очереди
     * @param issue - добавляемая задача
     */
    @Override
    public void add(Issue issue) {

        if (issue != null) {
            if (historyStorage.containsKey(issue.getId())) {
                //Если в хранилище уже есть id, значит есть и в списке.
                //Удаляем из списка задачу, т.к. нас интересует только последний просмотр
                historyQueue.removeNode(historyStorage.get(issue.getId()));
            }

            //Добавить в конец LinkedList
            Node<Issue> node = historyQueue.linkLast(issue);

            //Добавить в HashMap
            historyStorage.put(issue.getId(), node);
        }
    }

    /**
     * Удалить задачу в очереди по id
     * @param id - идентификатор удаляемой задачи
     */
    @Override
    public void remove(int id) {
        if (historyQueue.size() > 0) {
            if (historyQueue.removeNode(historyStorage.get(id)) != null) {
                historyStorage.remove(id);
            }
        }
    }

    /**
     * Получить историю просмотров задач
     * @return - список просмотренных задач
     */
    @Override
    public List<Issue> getHistory() {
        return historyQueue.getTasks();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Узел CustomLinkedList истории просмотров задач
     * @param <T> - тип хранимых задач в связном списке
     */
    private static class Node<T> {
        private final T issue;
        private Node<T> next;
        private Node<T> prev;

        public Node(T issue, Node<T> next, Node<T> prev) {
            this.issue = issue;
            this.next = next;
            this.prev = prev;
        }
    }

    /**
     * Внутренний класс для хранения последовательности обращений к задачам.
     *  <p> Доступные методы:
     *  <p> - Добавить сущность в конец списка
     *  <p> - Собрать все сущности из него в обычный ArrayList
     *  <p> - Удаление произвольного узла списка
     * @param <T> - тип хранимых задач в связном списке
     */
    private static class CustomLinkedList<T> {

        //Указатель на первый элемент списка. Он же first
        private Node<T> head = null;

        //Указатель на последний элемент списка. Он же last
        private Node<T> tail = null;

        //Размер списка
        private int size = 0;

        public int size() {
            return size;
        }

        /**
         * Добавлять задачу в конец списка
         * @param issue - добавляемая в историю задача
         * @return узел истории просмотров с добавленной задачей
         */
        public Node<T> linkLast(T issue) {
            final Node<T> last = tail;
            final Node<T> node = new Node<>(issue, null, last);
            tail = node;
            if (last == null) {
                head = node;
            } else {
                last.next = node;
            }
            size++;
            return node;
        }

        /**
         * Собирает все задачи из связного списка в обычный ArrayList
         * @return - список просмотренных задач
         */
        public List<T> getTasks() {
            List<T> list = new ArrayList<>();
            for (Node<T> x = head; x != null; x = x.next) {
                list.add(x.issue);
            }
            return list;
        }

        /**
         * Удаляет узел из списка
         * @param node удаляемый узел
         * @return задача из удаленного узла
         */
        public T removeNode(Node<T> node) {
            if (node != null) {
                final Node<T> prev = node.prev;
                final Node<T> next = node.next;

                if (prev == null && next == null) {
                    //Удалить в списке последний элемент
                    head = null;
                    tail = null;
                } else if (prev != null && next != null) {
                    //Связать предыдущий и следующий элементы
                    prev.next = next;
                    next.prev = prev;
                } else if (prev == null) {
                    //Удалили first, значит новый first тот, что был следующим
                    head = next;
                    next.prev = null;
                } else {
                    //Удалили last, значит новый last тот, что был предыдущим
                    tail = prev;
                    prev.next = null;
                }
                size--;
            } else {
                return null;
            }

            return node.issue;
        }
    }
}
