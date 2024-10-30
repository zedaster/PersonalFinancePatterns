package example.patterns.flyweight.models;

import java.util.Objects;

// Паттерн Flyweight
public class TaskContext {
    private final User user;
    private final Task task;
    private final boolean isDone;

    public TaskContext(User user, Task task, boolean isDone) {
        this.user = user;
        this.task = task;
        this.isDone = isDone;
    }

    public User getUser() {
        return user;
    }

    public Task getTask() {
        return task;
    }

    public boolean isDone() {
        return isDone;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskContext that = (TaskContext) o;
        return Objects.equals(user, that.user) && Objects.equals(task, that.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, task);
    }
}
