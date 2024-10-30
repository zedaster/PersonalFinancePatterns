package example.patterns.flyweight.models;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

// Паттерн Flyweight
public class Task {
    private Integer id;
    private final String title;
    private final String description;
    private final ZonedDateTime deadline;

    public Task(String title, String description, ZonedDateTime deadline) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
    }

    public Optional<Integer> getId() {
        return Optional.ofNullable(id);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ZonedDateTime getDeadline() {
        return deadline;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Task copy() {
        return new Task(this.title, this.description, this.deadline);
    }

    @Override
    public boolean equals(Object o) {
        if (id == null) {
            throw new UnsupportedOperationException("Id is null!");
        }

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            throw new UnsupportedOperationException("Id is null!");
        }

        return Objects.hash(id);
    }
}
