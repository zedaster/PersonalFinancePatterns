package example.patterns.flyweight.repository;

import example.patterns.flyweight.models.User;

import java.util.ArrayList;
import java.util.List;

public class FakeUserRepository implements UserRepository {
    private final List<User> list = new ArrayList<>();

    public User save(User user) {
        if (user.getId().isPresent()) {
            list.set(user.getId().get(), user);
            return user;
        }

        User newUser = user.copy();
        newUser.setId(list.size());
        list.add(newUser);
        return newUser;
    }
}
