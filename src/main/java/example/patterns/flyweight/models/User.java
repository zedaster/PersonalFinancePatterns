package example.patterns.flyweight.models;

import java.util.Objects;
import java.util.Optional;

public class User {
    private Integer id;
    private final String name;
    public User(String name) {
        this.name = name;
    }

    private User(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Optional<Integer> getId() {
        return Optional.ofNullable(id);
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User copy() {
        return new User(this.name);
    }

    @Override
    public boolean equals(Object o) {
        if (id == null) {
            throw new UnsupportedOperationException("Id is null!");
        }

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            throw new UnsupportedOperationException("Id is null!");
        }

        return Objects.hash(id);
    }
}
