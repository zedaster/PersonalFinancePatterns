package example.patterns.flyweight;

import example.patterns.flyweight.models.Task;
import example.patterns.flyweight.models.TaskContext;
import example.patterns.flyweight.models.User;
import example.patterns.flyweight.repository.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new FakeUserRepository();
        TaskRepository taskRepository = new FakeTaskRepository();
        TaskContextRepository taskContextRepository = new FakeTaskContextRepository();

        List<User> users = userRepository.saveMany(
                new User("Anton"),
                new User("Boris"),
                new User("Yaroslav"));

        // Добавляем шаблонную задачу
        String dormTitle = "Заселиться в общежитие";
        String dormDesc = "Тут должно быть очень длинное описание с MD форматированием";
        ZonedDateTime dormDeadline = getEkbTime(2025, 8, 25, 16, 0);
        Task dormTask = new Task(dormTitle, dormDesc, dormDeadline);

        // Сохраняем одно описание задачи, но множество состояний для него
        Task dormTaskWithId = taskRepository.save(dormTask);
        users.forEach(user -> taskContextRepository.save(new TaskContext(user, dormTaskWithId, false)));

        // Делаем пользовательскую задачу (Картошку едет копать только Антон)
        String userTaskTitle = "Идти капать картошку";
        String userTaskDesc = "Точный адрес поля с картошкой: ....";
        ZonedDateTime userTaskDeadline = getEkbTime(2025, 1, 30, 23, 0);
        Task userTask = new Task(userTaskTitle, userTaskDesc, userTaskDeadline);

        // Сохраняем одно описание и одно состояние для задачи
        Task userTaskWithId = taskRepository.save(userTask);
        taskContextRepository.save(new TaskContext(users.get(0), userTaskWithId, false));
    }

    private static ZonedDateTime getEkbTime(int year, int month, int day, int hour, int mins) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalTime time = LocalTime.of(hour, mins);
        return ZonedDateTime.of(date, time, ZoneId.of("GMT+5"));
    }
}
