package example.patterns.flyweight.repository;

import example.patterns.flyweight.models.TaskContext;

public interface TaskContextRepository {
    TaskContext save(TaskContext taskContext);
}
