package Inventory;

import Characters.Hero;
import Characters.Position;
import Inventory.ManaPotion;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class ManaPotionTest {

    /**
     * This class tests the `use` method of the `ManaPotion` class.
     * <p>
     * The `use` method is responsible for adding a specified number of mana points to the hero.
     * If the mana points are successfully added, it returns a success message.
     * If unsuccessful (e.g., hero cannot take mana), it returns `null`.
     */

    @Test
    public void testUseSuccessfulManaAddition() {
        // Arrange
        Hero mockHero = Mockito.mock(Hero.class);
        Position position = new Position(1, 2);
        ManaPotion manaPotion = new ManaPotion("Small Mana Potion", 50, 10, position);

        when(mockHero.takeMana(50)).thenReturn(true);

        // Act
        String result = manaPotion.use(mockHero);

        // Assert
        assertEquals("50 mana points added to your hero!", result);
        verify(mockHero, times(1)).takeMana(50);
    }

    @Test
    public void testUseUnsuccessfulManaAddition() {
        // Arrange
        Hero mockHero = Mockito.mock(Hero.class);
        Position position = new Position(1, 2);
        ManaPotion manaPotion = new ManaPotion("Small Mana Potion", 50, 10, position);

        when(mockHero.takeMana(50)).thenReturn(false);

        // Act
        String result = manaPotion.use(mockHero);

        // Assert
        assertNull(result);
        verify(mockHero, times(1)).takeMana(50);
    }
}