package example.patterns.flyweight.repository;

import example.patterns.flyweight.models.Task;

import java.util.ArrayList;
import java.util.List;

public class FakeTaskRepository implements TaskRepository {
    private final List<Task> list = new ArrayList<>();

    public Task save(Task task) {
        if (task.getId().isPresent()) {
            list.set(task.getId().get(), task);
            return task;
        }

        Task newTask = task.copy();
        newTask.setId(list.size());
        list.add(newTask);
        return newTask;
    }
}
