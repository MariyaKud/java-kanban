package service;

import model.Issue;
import model.Node;

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
    private final Map<Integer, Node<Issue>> historyMap = new HashMap<>();

    /**
     * Список для хранения порядка вызова задач
     * Если какая-либо задача просматривалась несколько раз,
     * в истории должен отобразиться только последний просмотр.
     */
    private final CustomLinkedList<Issue> historyList = new CustomLinkedList<>();

    /**
     * Добавить задачу в конец очереди
     */
    @Override
    public void add(Issue issue) {

        if (issue != null) {
            if (historyMap.containsKey(issue.getId())) {
                //Если в хранилище уже есть id, значит есть и в списке.
                //Удаляем из списка задачу, т.к. нас интересует только последний просмотр
                historyList.removeNode(historyMap.get(issue.getId()));
            }

            //Добавляем в конец двусвязного списка
            Node<Issue> node = historyList.linkLast(issue);

            //Добавляем в HashMap
            historyMap.put(issue.getId(), node);
        }
    }

    /**
     * Удалить задачу в очереди по id
     */
    @Override
    public void remove(int id) {
        if (historyList.removeNode(historyMap.get(id))) {
            historyMap.remove(id);
        }
    }

    /**
     * Получить список задач в очереди
     */
    @Override
    public List<Issue> getHistory() {
        return historyList.getTasks();
    }

    /**
     * Двусвязный список задач
     * <p> Доступные операции:
     *  <p> - Добавить сущность в конец списка
     *  <p> - Собрать все сущности из него в обычный ArrayList
     *  <p> - Удаление произвольного узла списка
     */
    private static class CustomLinkedList<T> {

        /**
         * Указатель на первый элемент списка. Он же first
         */
        public Node<T> head = null;

        /**
         * Указатель на последний элемент списка. Он же last
         */
        public Node<T> tail = null;

        /**
         * Размер списка
         */
        private int size = 0;

        /**
         * Конструктор пустого списка
         */
        public CustomLinkedList() {
        }

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

            if (last == null)
                head = node;
            else
                last.next = node;

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
                //Если node<>first в списке, то надо удалить ссылку у предыдущего элемента
                if (node.prev != null && node.next != null) {
                    //привязываем ссылку предыдущего элемента к следующему за удаляемым элементом
                    node.prev.next = node.next;
                    node.next.prev = node.prev;
                } else if (node.prev == null && node.next == null) {
                    //в списке один элемент и мы его удаляем
                    head = null;
                    tail = null;
                    size = 0;
                } else if (node.prev == null) {
                    //Удалили first, значит новый first тот, что был следующим
                    head = node.next;
                    if (node.next != null) {
                        node.next.prev = null;
                    }
                } else {
                    //Удалили last, значит новый last тот, что был предыдущим
                    tail = node.prev;
                    if (node.prev != null) {
                        node.prev.next = null;
                    }
                }
                size--;
                return true;
            }
            return false;
        }

    }
}
