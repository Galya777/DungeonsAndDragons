package actions;

import Characters.Hero;

public abstract class CommandImpl implements Command {

    protected Hero hero;
    protected String[] splitCommand;

    protected CommandImpl(Hero hero, String[] splitCommand) {
        this.hero = hero;
        this.splitCommand = splitCommand;
    }

}