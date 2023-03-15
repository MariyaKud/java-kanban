package model;

/**
 * Подзадача - класс для разбиения масштабных задач
 * Для каждой подзадачи известно, в рамках какого эпика она выполняется.
 */
public class SubTask extends Issue {

    /**
     * Владелец текущего экземпляра класса {@code SubTask}
     * Хранить id вместо самого эпика считаю очень интересным предложением, но
     * в разрезе работы с базами данных. На текущем этапе, считаю преждевременным усложнением.
     * Пока же мы изучаем классы и хочется максимально "наиграться" с классами.
     * Но в следующем спринте сделаю, даже если баз данных еще не будет.
     */
    private Epic parent;

    public SubTask(int id, String title, String description, Epic parent, IssueStatus status) {
        super(id,title,description);
        this.parent = parent;
        this.setStatus(status);
    }

    /**
     * Получить родителя экземпляра класса {@code SubTask}
     * @return - владелец экземпляр класса {@link Epic}
     */
    public Epic getParent() {
        return parent;
    }

    public void setParent(Epic parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "SubTask{"  +
                "id=" + getId() +
                ", parentId=" + parent.getId() +
                ", status="   + getStatus() +
                ", title='"   + getTitle() + '\'' +
                ", hash='"    + hashCode() + '\'' +
                ", parent.hash='" + parent.hashCode() + '\'' +
                '}';
    }
}
