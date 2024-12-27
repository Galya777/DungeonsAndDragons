package Inventory;

import Characters.Hero;
import Characters.Position;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TreasureTest {

    /**
     * The `Treasure` class represents a collectible item in the game that can be used by a `Hero`.
     * The `use` method is responsible for defining the behavior of the treasure when it's used by the hero.
     * This test class validates the behavior of the `use` method in a variety of scenarios.
     */

    @Test
    public void testUseTreasureSuccess() {
        // Arrange
        Treasure treasure = Mockito.mock(Treasure.class);
        Hero hero = Mockito.mock(Hero.class);
        String expectedOutput = "Treasure used successfully.";

        when(treasure.use(hero)).thenReturn(expectedOutput);

        // Act
        String result = treasure.use(hero);

        // Assert
        assertEquals(expectedOutput, result);
        verify(treasure, times(1)).use(hero);
    }

    @Test
    public void testUseTreasureWithNullHero() {
        // Arrange
        Treasure treasure = Mockito.mock(Treasure.class);
        Hero hero = null;
        String expectedOutput = Treasure.INVALID_ARGUMENTS;

        when(treasure.use(hero)).thenReturn(expectedOutput);

        // Act
        String result = treasure.use(hero);

        // Assert
        assertEquals(expectedOutput, result);
        verify(treasure, times(1)).use(hero);
    }

    @Test
    public void testUseTreasureWithHeroThatCannotUseIt() {
        // Arrange
        Treasure treasure = Mockito.mock(Treasure.class);
        Hero hero = Mockito.mock(Hero.class);
        String expectedOutput = "Hero could not use the treasure.";

        when(treasure.use(hero)).thenReturn(expectedOutput);

        // Act
        String result = treasure.use(hero);

        // Assert
        assertEquals(expectedOutput, result);
        verify(treasure, times(1)).use(hero);
    }

    @Test
    public void testUseTreasureMultipleTimes() {
        // Arrange
        Treasure treasure = Mockito.mock(Treasure.class);
        Hero hero = Mockito.mock(Hero.class);
        String expectedOutput = "Treasure used successfully.";

        when(treasure.use(hero)).thenReturn(expectedOutput);

        // Act
        String result1 = treasure.use(hero);
        String result2 = treasure.use(hero);

        // Assert
        assertEquals(expectedOutput, result1);
        assertEquals(expectedOutput, result2);
        verify(treasure, times(2)).use(hero);
    }
}