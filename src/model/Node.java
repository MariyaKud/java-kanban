package model;

/**
 * Узел двусвязного списка истории просмотров
 */
public class Node <T> {

   public T issue;
   public Node<T> next;
   public Node<T> prev;

    public Node(T issue, Node<T> next, Node<T> prev) {
        this.issue = issue;
        this.next = next;
        this.prev = prev;
    }
}
