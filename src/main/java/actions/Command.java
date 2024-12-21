package actions;

public interface Command {

    static final int ZERO_WORD = 0;
    static final int FIRST_WORD = 1;
    static final int SECOND_WORD = 2;
    static final String UNSUPPORTED_OPERATION_MESSAGE = "Enter a valid command.";

    public String execute(UserRecipient userRecipient);

}