package example.patterns.state.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandHolder {
    private static final CommandHolder instance = new CommandHolder();

    private final Map<String, Command> commandMap;

    private CommandHolder() {
        this.commandMap = new HashMap<>();
        this.commandMap.put("create_budget", new CreateBudgetCommand());
        // we can add more commands here
    }

    public static CommandHolder getInstance() {
        return instance;
    }

    public Optional<Command> getCommand(String commandName) {
        return Optional.ofNullable(this.commandMap.get(commandName));
    }
}
