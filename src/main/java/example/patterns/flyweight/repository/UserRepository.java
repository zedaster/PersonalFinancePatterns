package example.patterns.flyweight.repository;

import example.patterns.flyweight.models.User;

import java.util.ArrayList;
import java.util.List;

public interface UserRepository {
    default List<User> saveMany(User... users) {
        List<User> addedUsers = new ArrayList<>();
        for (User user : users) {
            User newUser = this.save(user);
            addedUsers.add(newUser);
        }
        return addedUsers;
    }

    User save(User user);
}
