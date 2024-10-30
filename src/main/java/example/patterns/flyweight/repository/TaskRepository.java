package example.patterns.flyweight.repository;

import example.patterns.flyweight.models.Task;

public interface TaskRepository {
    Task save(Task task);
}
