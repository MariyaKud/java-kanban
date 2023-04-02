package service;

import model.Issue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Представитель контракта истории просмотров задач {Task, SubTask, Epic}
 */
public class InMemoryHistoryManager implements HistoryManager {

    /**
     *  HashMap для хранения истории просмотров.
     *  Её ключом будет id задачи, просмотр которой требуется удалить,
     *  а значением — место просмотра этой задачи в списке, то есть узел связного списка.
     */
    private final Map<Integer, Node<Issue>> historyStorage = new HashMap<>();

    /**
     * Список для хранения порядка вызова задач
     * Если какая-либо задача просматривалась несколько раз,
     * в истории должен отобразиться только последний просмотр.
     */
    private final CustomLinkedList<Issue> historyQueue = new CustomLinkedList<>();

    /**
     * Добавить задачу в конец очереди
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
     */
    @Override
    public void remove(int id) {
        if (historyQueue.removeNode(historyStorage.get(id))) {
            historyStorage.remove(id);
        }
    }

    /**
     * Получить список задач в очереди
     */
    @Override
    public List<Issue> getHistory() {
        return historyQueue.getTasks();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Узел CustomLinkedList истории просмотров задач
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
     * <p> Доступные методы:
     *  <p> - Добавить сущность в конец списка
     *  <p> - Собрать все сущности из него в обычный ArrayList
     *  <p> - Удаление произвольного узла списка
     */
    private static class CustomLinkedList<T> {
        /**
         * Указатель на первый элемент списка. Он же first
         */
        private Node<T> head = null;

        /**
         * Указатель на последний элемент списка. Он же last
         */
        private Node<T> tail = null;

        /**
         * Размер списка
         */
        private int size = 0;

        public int size() {
            return size;
        }

        /**
         * Добавлять задачу в конец списка
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
         * Собирает все задачи из списка в обычный ArrayList
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
         */
        public boolean removeNode(Node<T> node) {
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
                return true;
            }
            return false;
        }
    }
}
