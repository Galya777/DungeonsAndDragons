package actions;

public enum CommandType {
    BACKPACK,
    BATTLE,
    CHECK,
    COLLECT,
    GIVE,
    PLAY,
    QUIT,
    THROW,
    USE
    ;

    public static CommandType getCommandAsEnum(String command) {
        CommandType firstCommandAsEnum = null;
        try {
            firstCommandAsEnum = CommandType.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }

        return firstCommandAsEnum;
    }
}
