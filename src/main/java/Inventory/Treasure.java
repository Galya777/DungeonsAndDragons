package Inventory;

import Characters.Hero;
import Characters.Position;

public interface Treasure {

    Position getPosition();
    void setPosition(Position position);

    String INVALID_ARGUMENTS = "Invalid arguments.";

    String getName();

    String use(Hero hero);

    String getTreasureStats();

    int giveExperienceWhenCollected();
}
