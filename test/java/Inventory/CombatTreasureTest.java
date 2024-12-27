package Inventory;

import Characters.Hero;
import Characters.Position;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombatTreasureTest {

    /**
     * Class Description: The `CombatTreasure` class represents treasures or items found within the game
     * that provide benefits for combat-related actions. This class implements the `Treasure` interface
     * and defines methods to retrieve treasure properties (e.g., attack value, level, etc.) and interact
     * with hero characters, such as its `use` method.
     * <p>
     * Method Description: The `use(Hero hero)` method is intended to define how the treasure interacts
     * with a hero when the treasure is used. The return value is expected to provide a description of
     * the action or the impact of using the treasure.
     */

    @Test
    void testUse_ReturnsExpectedStringWhenUsedOnHero() {
        // Arrange
        Position position = new Position(2, 3);
        CombatTreasure treasure = Mockito.spy(new CombatTreasure("Sword of Power", 5, 50, 100, position));
        Hero hero = Mockito.mock(Hero.class);

        Mockito.doReturn("You feel empowered using the Sword of Power!")
                .when(treasure).use(hero);

        // Act
        String result = treasure.use(hero);

        // Assert
        assertEquals("You feel empowered using the Sword of Power!", result);
    }

    @Test
    void testUse_EmptyStringReturnedByDefault() {
        // Arrange
        Position position = new Position(1, 1);
        CombatTreasure treasure = new CombatTreasure("Unused Treasure", 2, 20, 50, position);
        Hero hero = Mockito.mock(Hero.class);

        // Act
        String result = treasure.use(hero);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testUse_HeroInteractionOccurs() {
        // Arrange
        Position position = new Position(0, 0);
        CombatTreasure treasure = Mockito.spy(new CombatTreasure("Mysterious Treasure", 3, 30, 70, position));
        Hero hero = Mockito.mock(Hero.class);

        Mockito.doReturn("The hero interacts with the Mysterious Treasure.")
                .when(treasure).use(hero);

        // Act
        String result = treasure.use(hero);

        // Assert
        assertEquals("The hero interacts with the Mysterious Treasure.", result);

        // Verify that `use` was called with the mock hero
        Mockito.verify(treasure).use(hero);
    }
}