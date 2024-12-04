package example.patterns.state;

import example.patterns.state.state.WaitingCommandState;
import example.patterns.state.state.UserState;

public class User {
    private final String username;

    private UserState state = WaitingCommandState.getInstance();

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }
}
