package example.patterns.flyweight.repository;

import example.patterns.flyweight.models.TaskContext;

import java.util.HashSet;
import java.util.Set;

public class FakeTaskContextRepository implements TaskContextRepository {
    private final Set<TaskContext> set = new HashSet<>();

    public TaskContext save(TaskContext taskContext) {
        set.add(taskContext);
        return taskContext;
    }

}
